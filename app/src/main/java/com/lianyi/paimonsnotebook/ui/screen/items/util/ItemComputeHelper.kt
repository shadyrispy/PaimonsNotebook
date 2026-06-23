package com.lianyi.paimonsnotebook.ui.screen.items.util

import com.lianyi.paimonsnotebook.common.database.cultivate.dao.CultivateEntityDao
import com.lianyi.paimonsnotebook.common.database.cultivate.dao.CultivateItemMaterialsDao
import com.lianyi.paimonsnotebook.common.database.cultivate.dao.CultivateItemsDao
import com.lianyi.paimonsnotebook.common.database.cultivate.data.CultivateEntityType
import com.lianyi.paimonsnotebook.common.database.cultivate.data.CultivateItemType
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateEntity
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateItemMaterials
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateItems
import com.lianyi.paimonsnotebook.common.extension.list.takeFirstIf
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.event.calculate.BatchCalculatePromotionDetail
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.event.calculate.BatchComputeData

/*
* 养成计算结果存储助手
* 将角色/武器养成计算结果的构建与存储逻辑从 ViewModel 中提取
* */
object ItemComputeHelper {

    /*
    * 保存角色养成计算结果到数据库
    *
    * @return 角色id,如果无需保存则返回 null
    * */
    suspend fun saveAvatarComputeResult(
        result: BatchComputeData,
        promotionDetail: BatchCalculatePromotionDetail,
        projectId: Int,
        itemAlreadyAdded: Boolean,
        cultivateEntityDao: CultivateEntityDao,
        cultivateItemsDao: CultivateItemsDao,
        cultivateItemMaterialsDao: CultivateItemMaterialsDao
    ): Int? {
        if (result.items.isEmpty()) return null

        /*
        * TODO 支持一次性添加多名角色
        * 获取第一个角色id不为空的数据
        * */
        val avatarPromotion = promotionDetail.items.takeFirstIf { it.avatar_id != null } ?: return null
        val avatarId = avatarPromotion.avatar_id ?: return null

        val firstResult = result.items.first()

        val overallMaterials = buildAvatarOverallMaterials(result, avatarId, projectId)
        val avatarMaterials = buildAvatarBreakthroughMaterials(firstResult, avatarId, projectId)
        val avatarSkillMaterials = buildAvatarSkillMaterials(firstResult, projectId)

        if (overallMaterials.isEmpty() && avatarMaterials.isEmpty() && avatarSkillMaterials.isEmpty()) {
            error("当前角色养成配置没有所需的养成材料")
        }

        /*
        * 如果已经添加过了,需要更新数据库
        * 为避免不同等级产生不同数量的材料(lv1跟lv10所需要的材料数量是不同的)
        * 这里直接把原来的给删了,重新添加
        * 外键约束会自动删除引用的表的数据
        * */
        if (itemAlreadyAdded) {
            cultivateEntityDao.deleteEntityByItemIdAndProjectId(avatarId, projectId)
        }

        val avatarEntity = CultivateEntity(
            itemId = avatarId,
            projectId = projectId,
            type = CultivateEntityType.Avatar,
            status = 0
        )
        cultivateEntityDao.insert(avatarEntity)

        val (overallItems, avatarItem, avatarSkillItems) = buildAvatarCultivateItems(
            avatarPromotion,
            avatarId,
            projectId
        )

        cultivateItemsDao.insert(overallItems)
        cultivateItemsDao.insert(avatarItem)
        cultivateItemsDao.insert(avatarSkillItems)

        cultivateItemMaterialsDao.insert(overallMaterials)
        cultivateItemMaterialsDao.insert(avatarMaterials)
        cultivateItemMaterialsDao.insert(avatarSkillMaterials)

        return avatarId
    }

    /*
    * 保存武器养成计算结果到数据库
    *
    * @return 武器id,如果无需保存则返回 null
    * */
    suspend fun saveWeaponComputeResult(
        result: BatchComputeData,
        promotionDetail: BatchCalculatePromotionDetail,
        projectId: Int,
        itemAlreadyAdded: Boolean,
        cultivateEntityDao: CultivateEntityDao,
        cultivateItemsDao: CultivateItemsDao,
        cultivateItemMaterialsDao: CultivateItemMaterialsDao
    ): Int? {
        if (result.items.isEmpty()) return null

        val weapon = promotionDetail.items.takeFirstIf { it.weapon != null }?.weapon ?: return null

        val weaponMaterials = result.overall_consume.map {
            CultivateItemMaterials(
                itemId = it.id,
                cultivateItemId = -weapon.id,
                projectId = projectId,
                count = it.num,
                lackCount = it.lack_num,
                status = if (it.lack_num <= 0) {
                    1
                } else {
                    0
                }
            )
        }

        //检查材料是否为空
        if (weaponMaterials.isEmpty()) {
            error("当前武器养成配置没有所需的养成材料")
        }

        if (itemAlreadyAdded) {
            cultivateEntityDao.deleteEntityByItemIdAndProjectId(weapon.id, projectId)
        }

        val weaponEntity = CultivateEntity(
            itemId = weapon.id,
            projectId = projectId,
            type = CultivateEntityType.Weapon,
            status = 0
        )
        cultivateEntityDao.insert(weaponEntity)

        val overallItem = CultivateItems(
            itemId = -weapon.id,
            entityItemId = weapon.id,
            projectId = projectId,
            itemType = CultivateItemType.Overall,
            fromLevel = 0,
            toLevel = 0,
            status = 0
        )

        val weaponItem = CultivateItems(
            itemId = weapon.id,
            entityItemId = weapon.id,
            projectId = projectId,
            itemType = CultivateItemType.Weapon,
            fromLevel = weapon.level_current,
            toLevel = weapon.level_target,
            status = 0
        )

        cultivateItemsDao.insert(overallItem)
        cultivateItemsDao.insert(weaponItem)

        cultivateItemMaterialsDao.insert(weaponMaterials)

        return weapon.id
    }

    /*
    * 构建角色总览材料
    * cultivateItemId = 负的角色id
    * */
    private fun buildAvatarOverallMaterials(
        result: BatchComputeData,
        avatarId: Int,
        projectId: Int
    ): List<CultivateItemMaterials> {
        return result.overall_consume.map {
            CultivateItemMaterials(
                itemId = it.id,
                cultivateItemId = -avatarId,
                projectId = projectId,
                count = it.num,
                lackCount = it.lack_num,
                status = if (it.lack_num > 0) {
                    0
                } else {
                    1
                }
            )
        }
    }

    /*
    * 构建角色突破所需材料
    * item id = 材料id
    * cultivateItemId = 角色id
    * */
    private fun buildAvatarBreakthroughMaterials(
        firstResult: BatchComputeData.Item,
        avatarId: Int,
        projectId: Int
    ): List<CultivateItemMaterials> {
        return firstResult.avatar_consume.map {
            CultivateItemMaterials(
                itemId = it.id,
                cultivateItemId = avatarId,
                projectId = projectId,
                count = it.num,
                lackCount = 0,
                status = 0
            )
        }
    }

    /*
    * 构建技能突破所需材料
    * 约束的id始终为当前角色的元素爆发技能id
    * itemId = 材料id
    * cultivateItemId = 角色技能id
    * */
    private fun buildAvatarSkillMaterials(
        firstResult: BatchComputeData.Item,
        projectId: Int
    ): List<CultivateItemMaterials> {
        return firstResult.skills_consume.map { skillConsume ->
            skillConsume.consume_list.map { consume ->
                CultivateItemMaterials(
                    itemId = consume.id,
                    cultivateItemId = skillConsume.skill_info.id.toInt(),
                    projectId = projectId,
                    count = consume.num,
                    lackCount = 0,
                    status = 0
                )
            }
        }.flatten()
    }

    /*
    * 构建角色养成计算项(总览项、角色项、技能项)
    * */
    private fun buildAvatarCultivateItems(
        avatarPromotion: BatchCalculatePromotionDetail.Item,
        avatarId: Int,
        projectId: Int
    ): Triple<CultivateItems, CultivateItems, List<CultivateItems>> {
        /*
        * 全部材料计算项(存储全部材料的数量与所需个数)
        * */
        val overallItems = CultivateItems(
            itemId = -avatarId,
            entityItemId = avatarId,
            projectId = projectId,
            itemType = CultivateItemType.Overall,
            fromLevel = 0,
            toLevel = 0,
            status = 0
        )

        /*
        * 角色养成计算项
        * */
        val avatarItem = CultivateItems(
            itemId = avatarId,
            entityItemId = avatarId,
            projectId = projectId,
            itemType = CultivateItemType.Avatar,
            fromLevel = avatarPromotion.avatar_level_current ?: 0,
            toLevel = avatarPromotion.avatar_level_target ?: 0,
            status = 0
        )

        /*
        * 角色技能计算项
        * */
        val skillList = avatarPromotion.skill_list ?: listOf()
        val avatarSkillItems = skillList.map {
            CultivateItems(
                itemId = it.id,
                entityItemId = avatarId,
                projectId = projectId,
                itemType = CultivateItemType.Skill,
                fromLevel = it.level_current,
                toLevel = it.level_target,
                status = 0
            )
        }

        return Triple(overallItems, avatarItem, avatarSkillItems)
    }
}

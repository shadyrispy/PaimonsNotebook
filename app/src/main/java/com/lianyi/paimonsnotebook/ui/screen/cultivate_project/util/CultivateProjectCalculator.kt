package com.lianyi.paimonsnotebook.ui.screen.cultivate_project.util

import androidx.compose.ui.util.fastMap
import com.lianyi.paimonsnotebook.common.database.cultivate.data.CultivateEntityType
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateEntity
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateItemMaterials
import com.lianyi.paimonsnotebook.common.database.cultivate.entity.CultivateItems
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.avatar.AvatarData
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.item.Material
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.item.Materials
import com.lianyi.paimonsnotebook.common.web.hutao.genshin.weapon.WeaponData
import com.lianyi.paimonsnotebook.ui.screen.cultivate_project.data.EntityBaseInfo
import com.lianyi.paimonsnotebook.ui.screen.cultivate_project.data.MaterialBaseInfo
import kotlin.math.abs

/*
* 养成计划材料总览计算器
* 将材料总览分组的计算逻辑从 ViewModel 中提取,便于独立测试与维护
* */
object CultivateProjectCalculator {

    /*
    * 计算结果
    * */
    data class OverallResult(
        val materialBaseInfoGroups: List<List<MaterialBaseInfo>>,
        val materialBaseInfoGroupsFlatten: List<MaterialBaseInfo>,
        val entityBaseInfoMap: Map<Int, MutableList<EntityBaseInfo>>,
        val sortedEntityItemsPairList: List<Pair<CultivateEntity, List<CultivateItems>>>
    )

    /*
    * 计算材料总览分组
    *
    * @param cultivateEntityMapList 养成实体与对应养成项的映射
    * @param itemsMaterialsMap 计算项所需材料的映射
    * @param sortByEntityType 是否按实体类型排序
    * @param getMaterialData 通过材料id获取材料数据
    * @param getAvatarData 通过角色id获取角色数据
    * @param getWeaponData 通过武器id获取武器数据
    * @return 计算结果,如果中途因数据缺失中止则返回 null
    * */
    fun calculateMaterialOverallGroup(
        cultivateEntityMapList: Map<CultivateEntity, List<CultivateItems>>,
        itemsMaterialsMap: Map<Int, List<CultivateItemMaterials>>,
        sortByEntityType: Boolean,
        getMaterialData: (Int) -> Material?,
        getAvatarData: (Int) -> AvatarData?,
        getWeaponData: (Int) -> WeaponData?
    ): OverallResult? {
        //五星突破水晶id
        val gemIds = Materials.AvatarPromotionGemSimpleItems

        //按指定方式排序养成实体
        val sortedPairList = cultivateEntityMapList.toList().sortedBy {
            if (sortByEntityType) {
                it.first.type.ordinal.toLong()
            } else {
                it.first.addTime
            }
        }

        //按材料id分组并计算材料基础信息,再按rank分组
        val materialBaseInfoGroups = groupMaterialsByRank(
            sortedPairList = sortedPairList,
            itemsMaterialsMap = itemsMaterialsMap,
            getMaterialData = getMaterialData,
            gemIds = gemIds
        )

        //构建实体与材料的关联
        val entityBaseInfoMap = buildEntityMaterialRelation(
            sortedPairList = sortedPairList,
            itemsMaterialsMap = itemsMaterialsMap,
            materialBaseInfoGroups = materialBaseInfoGroups,
            getAvatarData = getAvatarData,
            getWeaponData = getWeaponData
        ) ?: return null

        //分组排序: 长度最长且未完成数量最多的排前面
        sortGroups(materialBaseInfoGroups)

        //展平并排序
        val flattened = flattenAndSort(materialBaseInfoGroups)

        return OverallResult(
            materialBaseInfoGroups = materialBaseInfoGroups,
            materialBaseInfoGroupsFlatten = flattened,
            entityBaseInfoMap = entityBaseInfoMap,
            sortedEntityItemsPairList = sortedPairList
        )
    }

    /*
    * 将材料按rank分组
    * 相同组别(可合成高一级)的材料分为同一组
    * */
    private fun groupMaterialsByRank(
        sortedPairList: List<Pair<CultivateEntity, List<CultivateItems>>>,
        itemsMaterialsMap: Map<Int, List<CultivateItemMaterials>>,
        getMaterialData: (Int) -> Material?,
        gemIds: Set<Int>
    ): MutableList<List<MaterialBaseInfo>> {
        var currentGroup = mutableListOf<MaterialBaseInfo>()
        val groups = mutableListOf<List<MaterialBaseInfo>>()

        sortedPairList.asSequence().map { (cultivateEntity, _) ->
            itemsMaterialsMap[-cultivateEntity.itemId] ?: listOf()
        }.flatten()
            .groupBy { it.itemId }
            .mapNotNull { map ->
                val material = getMaterialData(map.key) ?: return@mapNotNull null
                //最小的缺少材料的数量
                val minLackCountMaterials = map.value.minBy { it.lackCount }

                val lackCount = minLackCountMaterials.lackCount
                val count = minLackCountMaterials.count

                //所需的材料数量
                val totalMaterialsCount = map.value.sumOf { it.count }

                /*
                * 获取材料可用数量
                * lackCount小于0代表这个材料多余所需数量
                * 大于0代表持有数量少于所需数量
                * */
                val availableCount = if (lackCount < 0) {
                    abs(lackCount - count)
                } else {
                    count - lackCount
                }

                MaterialBaseInfo(
                    material = material,
                    count = totalMaterialsCount,
                    availableCount = availableCount
                )
            }.sortedBy { it.material.Id }.toList()
            .forEach { materialBaseInfo ->
                if (currentGroup.isEmpty()) {
                    currentGroup += materialBaseInfo
                } else {
                    val first = currentGroup.first().material
                    val last = currentGroup.last().material
                    val material = materialBaseInfo.material
                    //智识之冕(104319)单独进行判断,其次添加至组别的材料只能为五星以下的材料,如果为五星材料则需要判断第一个添加的材料是否是二星
                    if ((material.RankLevel < 5 || first.RankLevel == 2 || gemIds.contains(material.Id)) && last.Id + 1 == material.Id && last.RankLevel + 1 == material.RankLevel && material.Id != 104319) {
                        currentGroup += materialBaseInfo
                    } else {
                        groups += currentGroup
                        currentGroup = mutableListOf(materialBaseInfo)
                    }
                }
            }
        if (currentGroup.isNotEmpty()) {
            groups += currentGroup
        }

        return groups
    }

    /*
    * 构建实体与材料的关联
    * 返回 null 表示因数据缺失中止计算
    * */
    private fun buildEntityMaterialRelation(
        sortedPairList: List<Pair<CultivateEntity, List<CultivateItems>>>,
        itemsMaterialsMap: Map<Int, List<CultivateItemMaterials>>,
        materialBaseInfoGroups: List<List<MaterialBaseInfo>>,
        getAvatarData: (Int) -> AvatarData?,
        getWeaponData: (Int) -> WeaponData?
    ): MutableMap<Int, MutableList<EntityBaseInfo>>? {
        //养成实体所需的材料分类后再将对应材料的id取出
        val materialGroupListItemIdsList = materialBaseInfoGroups.map {
            it.map { baseInfo ->
                baseInfo.material.Id
            }.toSet()
        }

        val entityBaseInfoMap = mutableMapOf<Int, MutableList<EntityBaseInfo>>()

        for ((cultivateEntity, _) in sortedPairList) {
            //获取养成材料总览材料列表id集合
            val itemIds =
                (itemsMaterialsMap[-cultivateEntity.itemId] ?: continue).fastMap { it.itemId }

            //遍历set判断养成实体是否需要对应的材料
            for (baseInfoMaterialIdsSet in materialGroupListItemIdsList) {
                //判断养成材料id集合与分组材料集合是否有交际
                val add = itemIds.any { it in baseInfoMaterialIdsSet }

                var list = entityBaseInfoMap[baseInfoMaterialIdsSet.first()]

                if (list == null) {
                    list = mutableListOf()
                    entityBaseInfoMap[baseInfoMaterialIdsSet.first()] = list
                }

                if (add) {
                    if (cultivateEntity.type == CultivateEntityType.Avatar) {
                        val avatar = getAvatarData(cultivateEntity.itemId) ?: return null
                        list.add(
                            EntityBaseInfo(
                                id = avatar.id,
                                name = avatar.name,
                                iconUrl = avatar.iconUrl,
                                star = avatar.quality
                            )
                        )
                    }

                    if (cultivateEntity.type == CultivateEntityType.Weapon) {
                        val weapon = getWeaponData(cultivateEntity.itemId) ?: return null
                        list.add(
                            EntityBaseInfo(
                                id = weapon.id,
                                name = weapon.name,
                                iconUrl = weapon.iconUrl,
                                star = weapon.rankLevel
                            )
                        )
                    }
                }
            }
        }

        return entityBaseInfoMap
    }

    /*
    * 分组排序
    * 长度最长且未完成数量最多的集合排到最前面
    * */
    private fun sortGroups(
        groups: MutableList<List<MaterialBaseInfo>>
    ) {
        groups.sortWith(
            compareBy<List<MaterialBaseInfo>> {
                it.all { baseInfo -> baseInfo.lackCount <= 0 }
            }.thenByDescending { it.size }
                .thenBy { it.count { baseInfo -> baseInfo.lackCount <= 0 } }
        )
    }

    /*
    * 展平并排序
    * */
    private fun flattenAndSort(
        groups: List<List<MaterialBaseInfo>>
    ): List<MaterialBaseInfo> {
        return groups.flatten()
            .sortedBy {
                it.lackCount <= 1
            }
    }
}

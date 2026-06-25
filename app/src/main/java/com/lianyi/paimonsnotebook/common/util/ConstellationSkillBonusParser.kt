package com.lianyi.paimonsnotebook.common.util

import android.util.Log
import com.lianyi.paimonsnotebook.common.web.hoyolab.takumi.game_record.character.CharacterDetailData

/**
 * 命座影响的技能类型
 */
enum class ConstellationSkillTarget {
    NormalAttack,    // 普通攻击
    ElementalSkill,  // 元素战技
    ElementalBurst   // 元素爆发
}

/**
 * 从米游社 Constellation.effect 文本中解析命座为技能带来的等级加成。
 *
 * 数据约定：
 * - 米游社 API 返回的 `CharacterDetailData.skills[].level` 是「含命座加成」后的显示值
 * - 米游社 batch_compute 期望的 `level_current` 是「基础级」（不含命座加成）
 * - 本工具通过解析 `constellations[].effect` 文本来还原基础级
 *
 * 文本模式（中文国服）：
 *   "普通攻击的等级提高 3 级。"
 *   "元素战技 等级提高3级"
 *   "元素爆发的等级提高 3 级。"
 */
object ConstellationSkillBonusParser {

    private const val TAG = "ConstellationBonus"

    // 匹配: "普通攻击[的]?\\s*等级提高\\s*[1-9]\\s*级"
    // 分组1: 技能类型, 分组2: 加成等级数
    // 允许: 「的」可选；技能名与「等级」之间允许空格
    private val pattern = Regex(
        "(普通攻击|元素战技|元素爆发)的?\\s*等级提高\\s*([1-9])\\s*级"
    )

    /**
     * 解析单条命座 effect 文本
     * @return null if 文本不含技能加成；否则 (target, bonus)
     */
    fun parse(effect: String): Pair<ConstellationSkillTarget, Int>? {
        val match = pattern.find(effect) ?: return null
        val target = when (match.groupValues[1]) {
            "普通攻击" -> ConstellationSkillTarget.NormalAttack
            "元素战技" -> ConstellationSkillTarget.ElementalSkill
            "元素爆发" -> ConstellationSkillTarget.ElementalBurst
            else -> return null
        }
        val bonus = match.groupValues[2].toIntOrNull() ?: return null
        return target to bonus
    }

    /**
     * 汇总所有激活的命座，得到每个技能目标的总加成。
     *
     * 规则：
     * - 仅统计 `is_actived = true` 的命座
     * - 同技能多命座叠加时取最大值（防御性：正常不会出现同一技能被多条命座加成）
     *
     * @return Map<技能目标, 加成等级>，可能为空
     */
    fun summarize(
        constellations: List<CharacterDetailData.Constellation>
    ): Map<ConstellationSkillTarget, Int> {
        if (constellations.isEmpty()) return emptyMap()

        val bonuses = mutableMapOf<ConstellationSkillTarget, Int>()
        constellations.filter { it.is_actived }.forEach { c ->
            val parsed = parse(c.effect)
            if (parsed == null) {
                Log.v(TAG, "pos=${c.pos} effect=\"${c.effect}\" -> no skill bonus")
                return@forEach
            }
            val (target, bonus) = parsed
            val existing = bonuses[target] ?: 0
            if (bonus > existing) {
                bonuses[target] = bonus
                Log.d(TAG, "pos=${c.pos} target=$target bonus=$bonus")
            }
        }
        return bonuses
    }
}

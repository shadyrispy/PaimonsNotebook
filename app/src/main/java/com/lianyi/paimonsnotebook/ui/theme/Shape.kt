package com.lianyi.paimonsnotebook.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

// 圆角 token（供 Modifier.radius() 使用，避免硬编码 dp 值）
/** 小图标按钮圆角 */
val RadiusSmall = 2.dp
/** 中等元素圆角（卡片、列表项） */
val RadiusMedium = 4.dp
/** 大卡片圆角 */
val RadiusLarge = 8.dp
/** 特殊圆角（极少使用） */
val RadiusExtraSmall = 1.dp
val RadiusSmall3 = 3.dp
val RadiusMedium6 = 6.dp
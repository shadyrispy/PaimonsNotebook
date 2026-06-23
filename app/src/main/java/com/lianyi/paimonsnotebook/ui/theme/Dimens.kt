package com.lianyi.paimonsnotebook.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 通用尺寸常量集合
 *
 * 抽取项目中高频重复的 dp/sp 值，供后续逐步替换硬编码值时使用。
 * 现有代码无需立即替换，新代码建议优先使用此处常量。
 *
 * 分类说明：
 * - Spacing: 通用间距（padding / spacer / arrangedBy）
 * - Corner: 圆角半径
 * - Icon: 图标尺寸
 * - Text: 文字大小
 * - Card / Component: 卡片及常用组件尺寸
 * - Divider / Border: 分割线与边框
 */
object Dimens {

    // ==================== Spacing 间距 ====================
    /** 极小间距，用于紧凑布局的内边距 */
    val spacing_xs: Dp = 2.dp
    /** 小间距，常用于图标内边距、细小间隔 */
    val spacing_small: Dp = 4.dp
    /** 中等偏小间距，用于组件内部紧凑间隔 */
    val spacing_small_medium: Dp = 6.dp
    /** 中等间距，用于常规组件间隔 */
    val spacing_medium: Dp = 8.dp
    /** 中等偏大间距，用于列表项间隔 */
    val spacing_medium_large: Dp = 10.dp
    /** 大间距，用于卡片内边距、分组间隔 */
    val spacing_large: Dp = 12.dp
    /** 超大间距，用于页面级内边距、分组间隔 */
    val spacing_xlarge: Dp = 16.dp
    /** 页面级水平边距，用于屏幕左右两侧留白 */
    val spacing_page: Dp = 12.dp
    /** 超大间距，用于区块间分隔 */
    val spacing_xxlarge: Dp = 20.dp
    /** 巨大间距，用于页面顶部/底部留白 */
    val spacing_huge: Dp = 24.dp

    // ==================== Corner 圆角 ====================
    /** 极小圆角，用于标签、小按钮 */
    val corner_xs: Dp = 2.dp
    /** 小圆角，用于小卡片、标签、按钮 */
    val corner_small: Dp = 4.dp
    /** 中等圆角，用于普通卡片、弹窗内组件 */
    val corner_medium: Dp = 6.dp
    /** 大圆角，用于卡片、容器、弹窗 */
    val corner_large: Dp = 8.dp
    /** 超大圆角，用于特殊强调容器 */
    val corner_xlarge: Dp = 12.dp
    /** 圆形/胶囊形组件使用，配合 clip(CircleShape) */
    val corner_pill: Dp = 9999.dp

    // ==================== Icon 图标尺寸 ====================
    /** 极小图标，用于状态标记、角标 */
    val icon_xs: Dp = 12.dp
    /** 小图标，用于列表项次要图标 */
    val icon_small: Dp = 16.dp
    /** 中等图标，用于按钮、列表项主图标 */
    val icon_medium: Dp = 20.dp
    /** 大图标，用于工具栏、卡片主图标 */
    val icon_large: Dp = 24.dp
    /** 超大图标，用于空状态、引导页 */
    val icon_xlarge: Dp = 48.dp
    /** 占位图尺寸，用于空页面占位图 */
    val icon_placeholder: Dp = 120.dp

    // ==================== Text 文字大小 ====================
    /** 极小文字，用于角标、徽章数字 */
    val text_caption_xs: TextUnit = 10.sp
    /** 说明文字，用于辅助提示、标签 */
    val text_caption: TextUnit = 12.sp
    /** 次要正文，用于列表项副标题 */
    val text_small: TextUnit = 13.sp
    /** 正文，用于常规内容文字 */
    val text_body: TextUnit = 14.sp
    /** 主要正文，用于按钮、卡片标题 */
    val text_body_large: TextUnit = 15.sp
    /** 标题，用于卡片标题、弹窗标题 */
    val text_title: TextUnit = 16.sp
    /** 输入框文字，用于表单输入 */
    val text_input: TextUnit = 18.sp
    /** 大标题，用于页面标题、弹窗主标题 */
    val text_title_large: TextUnit = 20.sp
    /** 超大标题，用于弹窗主标题强调 */
    val text_title_xlarge: TextUnit = 22.sp
    /** h3 级别标题 */
    val text_h3: TextUnit = 24.sp
    /** h2 级别标题 */
    val text_h2: TextUnit = 26.sp
    /** h1 级别标题 */
    val text_h1: TextUnit = 30.sp

    // ==================== Card / Component 卡片及组件尺寸 ====================
    /** 小型卡片高度 */
    val card_small: Dp = 48.dp
    /** 通知项图标尺寸 */
    val card_notify_icon: Dp = 26.dp
    /** 通知项水平内边距 */
    val card_notify_padding: Dp = 12.dp
    /** 通知项垂直内边距 */
    val card_notify_padding_vertical: Dp = 8.dp
    /** 弹窗默认宽度 */
    val popup_width_default: Dp = 180.dp
    /** 分隔文字组件宽度 */
    val divider_text_width: Dp = 20.dp
    /** 指示器默认宽度 */
    val indicator_width: Dp = 24.dp
    /** 指示器默认高度 */
    val indicator_height: Dp = 8.dp

    // ==================== Divider / Border 分割线与边框 ====================
    /** 细分割线宽度 */
    val divider_thin: Dp = 1.dp
    /** 边框默认宽度 */
    val border_thin: Dp = 1.dp
    /** 模糊半径默认值 */
    val blur_default: Dp = 4.dp
}

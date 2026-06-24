package com.lianyi.paimonsnotebook.ui.screen.home.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.lianyi.paimonsnotebook.common.components.loading.ContentLoadingLayout
import com.lianyi.paimonsnotebook.common.components.loading.LoadingAnimationPlaceholder
import com.lianyi.paimonsnotebook.common.components.placeholder.EmptyPlaceholder
import com.lianyi.paimonsnotebook.common.components.placeholder.ErrorPlaceholder
import com.lianyi.paimonsnotebook.ui.screen.home.viewmodel.PostDetailViewModel
import com.lianyi.paimonsnotebook.ui.theme.PaimonsNotebookTheme

class AnnouncementScreen : ComponentActivity() {
    private val viewModel by lazy { ViewModelProvider(this)[PostDetailViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PaimonsNotebookTheme {
                ContentLoadingLayout(
                    loadingState = viewModel.postLoadingState,
                    loadingContent = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingAnimationPlaceholder()
                        }
                    },
                    errorContent = {
                        ErrorPlaceholder("文章获取失败")
                    },
                    emptyContent = {
                        EmptyPlaceholder()
                    },
                    noDataContent = {
                        EmptyPlaceholder()
                    },
                    defaultContent = {
                        EmptyPlaceholder()
                    }
                ) {
                    LoadingSuccessScreen()
                }

            }
        }
    }

    @Composable
    fun LoadingSuccessScreen() {
//        viewModel.postFullData?.post?.post?.apply {
//            HtmlTextLazyColumn(
//                htmlText = content,
//                videoCover = viewModel.videoCover,
//                onVideoClick = {
//
//                },
//                onHyperlinkClick = {
//                    viewModel.hyperlinkNavigate( it)
//                },
//                diskCacheTemplate = viewModel.getHtmlImageDiskCacheData(this)
//            ) {
//                TitleText(text = subject, fontSize = 18.sp)
//
//                DividerText(
//                    text = "文章发表:${
//                        TimeHelper.getTime(
//                            created_at.toLong() * 1000L,
//                            TimeStampType.MM_DD
//                        ).replace(" ", "-")
//                    }", modifier = Modifier
//                        .padding(0.dp, 8.dp)
//                        .fillMaxWidth()
//                )
//            }
//        }
    }
}
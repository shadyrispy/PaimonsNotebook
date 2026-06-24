package com.lianyi.paimonsnotebook.common.core.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lianyi.paimonsnotebook.common.extension.scope.launchIO
import com.lianyi.paimonsnotebook.common.extension.scope.launchMain
import com.lianyi.paimonsnotebook.common.extension.string.errorNotify
import com.lianyi.paimonsnotebook.common.util.enums.LoadingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

/**
 * ViewModel 基类，封装通用的协程调度、Flow 收集、错误处理与加载状态管理。
 *
 * 现有 ViewModel 无需强制继承，可逐步迁移。
 */
abstract class BaseViewModel : ViewModel() {

    /** 通用加载状态，子类可直接使用或自行维护独立状态 */
    var loadingState by mutableStateOf(LoadingState.Loading)
        protected set

    /** 全局错误处理器，子类可重写以自定义错误提示策略 */
    protected open fun onError(error: Throwable) {
        error.printStackTrace()
        error.message?.errorNotify()
    }

    /**
     * 在 IO 线程执行挂起任务，自动捕获异常并调用 [onError]。
     *
     * @param setLoading 是否在执行前后自动切换 loadingState（默认 false）
     */
    fun launchIOSafe(
        setLoading: Boolean = false,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launchIO {
        if (setLoading) loadingState = LoadingState.Loading
        try {
            block()
            if (setLoading) loadingState = LoadingState.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (setLoading) loadingState = LoadingState.Error
            onError(e)
        }
    }

    /**
     * 在 IO 线程收集 Flow，每条数据切回主线程执行 [onEach]。
     */
    fun <T> Flow<T>.collectOnMain(onEach: suspend (T) -> Unit): Job = viewModelScope.launchIO {
        collect { value -> launchMain { onEach(value) } }
    }

    /**
     * collectLatest 版本，在 IO 线程收集，主线程处理。
     */
    fun <T> Flow<T>.collectLatestOnMain(onEach: suspend (T) -> Unit): Job = viewModelScope.launchIO {
        collectLatest { value -> launchMain { onEach(value) } }
    }

    /**
     * 在 IO 线程收集并处理，不切换线程。
     */
    fun <T> Flow<T>.collectIO(onEach: suspend (T) -> Unit): Job = viewModelScope.launchIO {
        collect { onEach(it) }
    }

    /** 在主线程切换加载状态 */
    fun updateLoadingState(state: LoadingState) {
        viewModelScope.launchMain { loadingState = state }
    }

    /** 标记为空数据状态 */
    fun setEmptyState() = updateLoadingState(LoadingState.Empty)

    /** 标记为成功状态 */
    fun setSuccessState() = updateLoadingState(LoadingState.Success)

    /** 标记为错误状态 */
    fun setErrorState() = updateLoadingState(LoadingState.Error)
}

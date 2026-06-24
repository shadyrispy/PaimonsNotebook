package com.lianyi.paimonsnotebook.common.core.base

import com.lianyi.paimonsnotebook.common.util.enums.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `launchIOSafe executes block successfully`() = runTest(testDispatcher) {
        val vm = object : BaseViewModel() {}
        var executed = false
        // launchIOSafe 内部使用 Dispatchers.IO(真实线程池),advanceUntilIdle 只推进测试调度器,
        // 因此用 join() 等待真实 IO 线程上的协程完成
        vm.launchIOSafe { executed = true }.join()
        assert(executed)
    }

    @Test
    fun `launchIOSafe catches exception and calls onError`() = runTest(testDispatcher) {
        val vm = object : BaseViewModel() {
            var caughtError: Throwable? = null
            override fun onError(error: Throwable) {
                caughtError = error
            }
        }
        vm.launchIOSafe { throw RuntimeException("test error") }.join()
        assertEquals("test error", vm.caughtError?.message)
    }

    @Test
    fun `launchIOSafe with setLoading updates loadingState`() = runTest(testDispatcher) {
        val vm = object : BaseViewModel() {}
        vm.launchIOSafe(setLoading = true) { /* success */ }.join()
        assertEquals(LoadingState.Success, vm.loadingState)
    }

    @Test
    fun `launchIOSafe with setLoading sets Error on failure`() = runTest(testDispatcher) {
        val vm = object : BaseViewModel() {
            override fun onError(error: Throwable) {
                // 重写以避免调用 errorNotify() 依赖 Android Context
            }
        }
        vm.launchIOSafe(setLoading = true) { throw RuntimeException("fail") }.join()
        assertEquals(LoadingState.Error, vm.loadingState)
    }

    @Test
    fun `loadingState defaults to Loading`() {
        val vm = object : BaseViewModel() {}
        assertEquals(LoadingState.Loading, vm.loadingState)
    }
}

package coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    runBlocking {
        executeThread()
    }
}

// 1. thread 和 coroutine
private fun executeThread() {
    val threadExecuteTime = measureTimeMillis {
        repeat(100_000) {
            Thread {

            }.start()
        }
    }
    println("thread: $threadExecuteTime")
}

private fun executeCoroutine() {
    val coroutineExecuteTime = measureTimeMillis {
        // 建立新的CoroutineScope，會把現在的thread block住
        // to be used in main functions and in tests
        runBlocking {
            repeat(100_000) {
                launch {

                }
            }
        }
    }
    println("thread: $coroutineExecuteTime")
}

// 2. launch / coroutineScope / async / join
// runBlocking - 建立一個新的coroutine scope，並block住當前的thread，把內部程式碼執行完才算結束
// launch - 建立子coroutine，回傳Job，沒有回傳值，執行完就結束了
private fun launchTest() {
    runBlocking {
        launch {
            println("launch 1 start")
            delay(1000)
            println("launch 1 end")
        }
        launch {
            println("launch 2 start")
            delay(500)
            println("launch 2 end")
        }
        println("runBlocking End")
    }
    println("Done")
}

// coroutineScope - 在現有 suspend fun 中建立 scope,內部的 coroutine 都執行完才會繼續執行下面的程式碼
private suspend fun coroutineScopeTest() {
    coroutineScope {
        launch {
            println("launch 1 start")
            delay(1000)
            println("launch 1 end")
        }
        launch {
            println("launch 2 start")
            delay(500)
            println("launch 2 end")
        }
    }
    coroutineScope {
        launch {
            println("launch 3 start")
            delay(1000)
            println("launch 3 end")
        }
        launch {
            println("launch 4 start")
            delay(500)
            println("launch 4 end")
        }
    }
    println("Done")
}

private fun asyncTest() {
    runBlocking {
        // 建立子coroutine，回傳deferred，等於有回傳值的Job
        val deferred = async {
            println("launch start")
            delay(500)
            println("launch end")
            2
        }
        println("${deferred.await()}")
        println("Done")
    }
}

private fun joinTest() {
    runBlocking {
        val job = launch {
            println("launch start")
            delay(500)
            println("launch end")
            2
        }
        job.join()// 等待job執行結束才會執行下面的程式碼
        println("Done")
    }
}

// 3. sharedFlow / stateFlow / combine / shareIn / stateIn
suspend fun sharedFlowTest() = coroutineScope {
    val dataUseCase = DataUseCase()
    val sharedFlow = dataUseCase.getSharedFlow()
    launch {
        delay(1000)
        sharedFlow.collect {
            println("(1): $it")
        }
    }
    launch {
        sharedFlow.collect {
            println("(2): $it")
        }
    }
    println("done")
}

suspend fun stateFlowTest() = coroutineScope {
    val dataUseCase = DataUseCase()
    val stateFlow = dataUseCase.getStateFlow()
    launch {
        stateFlow.collect {
            println("stateFlow (1): $it")
        }
    }
    launch {
        delay(2000)
        stateFlow.collect {
            println("stateFlow (2): $it")
        }
    }
    println("done")
}

class DataUseCase {
    val scope = CoroutineScope(SupervisorJob())
    private val sharedFlow = MutableSharedFlow<String>(replay = 1)
    private val stateFlow = MutableStateFlow<String>("")

    suspend fun getSharedFlow(): Flow<Int> = flow {
        println("shared Flow started")
        repeat(6) {
            delay(100)
            emit(it)
        }
    }.shareIn(
        scope,
        replay = 1,
        started = SharingStarted.Eagerly
    )

    suspend fun getStateFlow(): Flow<Int> = flow {
        println("state Flow started")
        repeat(8) {
            delay(500)
            emit(it)
        }
    }.stateIn(
        scope
    )


    suspend fun combine(): Flow<Int> =
        combine(getSharedFlow(), getStateFlow()) { a, b ->
            println("$a, $b")
            a + b
        }


}
/**
 * cold flow - callbackFlow, channelFlow
 * hot flow - stateFlow, sharedFlow
 */


// 參考連結
// [官方推荐 Flow 取代 LiveData, 有必要吗？](https://xie.infoq.cn/article/04e927ce35d5f9b66e5152c9e)
// [StateFlow或SharedFlow？](https://shenzhen2017.github.io/blog/2020/12/translate-substituing-liveData.html)
// [Coroutine 停看聽](https://ithelp.ithome.com.tw/users/20129264/ironman/3966)
// [谁能取代Android的LiveData- StateFlow or SharedFlow](https://blog.csdn.net/eclipsexys/article/details/122227697)
// [A safer way to collect flows from Android UIs](https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda)
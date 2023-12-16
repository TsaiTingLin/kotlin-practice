package coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    stateFlowTest()
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
// runBlocking - new 一個新的coroutine，並block住當前的thread，把內部程式碼執行完才算結束
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

// coroutineScope - 分隔程式區塊，不會建立新的coroutine，繼承外面的coroutine
private fun coroutineScopeTest() {
    runBlocking {
        coroutineScope {
            println("launch 1 start")
            delay(1000)
            println("launch 1 end")
        }
        coroutineScope {
            println("launch 2 start")
            delay(500)
            println("launch 2 end")
        }
//        println("Done $data1 , $data2")
        println("Done")
    }
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
        // 建立子coroutine，回傳deferred，等於有回傳值的Job
        val job = launch {
            println("launch start")
            delay(500)
            println("launch end")
            2
        }
//        job.join()
        println("Done")
    }
}




// 3. sharedFlow / stateFlow
fun sharedFlowTest() = runBlocking {
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

fun stateFlowTest() = runBlocking {
    val dataUseCase = DataUseCase()
    val stateFlow = dataUseCase.stateFlow()
    launch {
        stateFlow.collect {
            println("stateFlow (1): $it")
        }
    }
//    yield()
    launch {
        delay(2000)
        stateFlow.collect {
            println("stateFlow (2): $it")
        }
    }
    println("done")
}

class DataUseCase {
    val scope = CoroutineScope(Job())
    suspend fun getSharedFlow(): Flow<Int> = flow {
        println("shared Flow started")
        repeat(6) {
            delay(100)
            emit(it)
        }
    }.shareIn(
        scope,
        replay = 2,
        started = SharingStarted.WhileSubscribed()
    )


    suspend fun stateFlow(): Flow<Int> = flow {
        println("state Flow started")
        repeat(8) {
            delay(500)
            emit(it)
        }
    }.stateIn(
        scope
    )
}

// 參考連結
// [官方推荐 Flow 取代 LiveData, 有必要吗？](https://xie.infoq.cn/article/04e927ce35d5f9b66e5152c9e)
// [StateFlow或SharedFlow？](https://shenzhen2017.github.io/blog/2020/12/translate-substituing-liveData.html)
// [Coroutine 停看聽](https://ithelp.ithome.com.tw/users/20129264/ironman/3966)
// [谁能取代Android的LiveData- StateFlow or SharedFlow](https://blog.csdn.net/eclipsexys/article/details/122227697)
// [A safer way to collect flows from Android UIs](https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda)





















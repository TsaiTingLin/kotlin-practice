package coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*


/**
 * [reference_1](https://blog.csdn.net/unicorn97/article/details/105209834)
 * [reference_2](https://blog.csdn.net/rikkatheworld/article/details/125646615)
 * [reference_3](https://blog.csdn.net/qq_30382601/article/details/121825461)
 * flatMapConcat, flatMapMerge, flatMapLatest
 */
@OptIn(FlowPreview::class)
fun main() = runBlocking<Unit> {
    val persistentFlow = MutableSharedFlow<Int>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val instantFlow = MutableSharedFlow<Int>()
    launch {
        listOf(88888,99999).forEach {
            delay(500)
            persistentFlow.emit(it)
        }
    }

    launch {
        (1..100).forEach {
            instantFlow.emit(it)
            delay(100)
        }
    }

//    launch {
//        flowRepeat(10, 10)
//            .flatMapMerge(2){ flowRepeat(2, 3).processData(it) }
//            .collect{
//                println(it)
//            }
//    }
    launch {
        flowOf(persistentFlow, flowRepeat(2, 2), flowRepeat(3,2), flowRepeat(4, 2))
            .flattenMerge(1)
            .collect { println(it) }
    }
}


fun <T> flowRepeat(value: T, times: Int) = flow {
    repeat(times) {
        emit(value)
        delay(10)
    }
}.flowOn(Dispatchers.Default)


fun Flow<Int>.processData(item: Int) = this.map {
    "$item $it"
}

fun requestFlow(i: Int) = flow<String> {
    emit("$i:First")
    delay(500)
    emit("$i:Second")
}

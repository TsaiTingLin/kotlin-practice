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
fun main() = runBlocking<Unit> {
    val delayedDataFlow = MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val realtimeDataFlow = MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    launch {
        listOf("delayed-data_1", "delayed-data_2").forEach {
            delay(500)
            delayedDataFlow.emit(it)
        }
    }
    launch {
        (1..100).forEach {
            realtimeDataFlow.emit("real-time-data_$it")
            delay(100)
        }
    }
    val flow = flow {
        repeat(2) {
            emit("delayed-data_$it")
            delay(10)
        }
    }
    // Situation A
    launch {
        realtimeDataFlow
            .flatMapConcat{
                delayedDataFlow.processData(it)
            }
            .collect {
                println(it)
            }
    }

    // Situation B
    launch {
        realtimeDataFlow
            .flatMapConcat{
                flow.processData(it)
            }
            .collect {
                println(it)
            }
    }
}

fun <T> flowRepeat(value: T, times: Int) = flow {
    repeat(times) {
        emit(value)
        delay(10)
    }
}.flowOn(Dispatchers.Default)

fun Flow<String>.processData(item: String) = this.map {
    "$item $it"
}



// break down the source code
fun Flow<String>.flatMapConcat(transform:suspend (value:String)->Flow<String>):Flow<String>{
    return this.map { transform(it) }.flattenConcat()
}
fun Flow<Flow<String>>.flattenConcat(): Flow<String> = flow {
    collect { subFlow ->
        println(subFlow.hashCode())
        subFlow.collect(this)
    }
}

fun flatMapConcatExample() {
    GlobalScope.launch {
        launch {
            flowRepeat(10, 10)
                .flatMapConcat { flowRepeat(2, 3).processData(it) }
                .collect {
                    println(it)
                }
        }
    }
}

fun test() {
    GlobalScope.launch {
        flowOf(flowRepeat(2, 2), flowRepeat(2, 2), flowRepeat(3, 2), flowRepeat(4, 2))
            .flattenMerge(1)
            .collect { println(it) }
    }
}







fun Flow<Int>.processData(item: Int) = this.map {
    "$item $it"
}

fun requestFlow(i: Int) = flow<String> {
    emit("$i:First")
    delay(500)
    emit("$i:Second")
}

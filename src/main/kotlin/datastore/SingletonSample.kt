package datastore

import kotlinx.coroutines.*
import kotlinx.coroutines.internal.synchronized
import javax.naming.Context
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

// [參考連結](https://www.796t.com/article.php?id=230059)

fun main(args: Array<String>) {
//    runBlocking {
//        launch {
//            (0..3).forEach { _ ->
//                delay(150)
//                println(SingletonSample.getInstance())
//            }
//        }
//        launch(Dispatchers.IO) {
//            (0..3).forEach { _ ->
//                delay(150)
//                println(SingletonSample.getInstance())
//            }
//        }
//    }
    reflection2()
}


class SingletonSample private constructor() {

    companion object {
        @Volatile
        private var instance: SingletonSample? = null

//        @Synchronized
//        fun getInstance(): SingletonSample {
//            if (instance == null) {
//                println("instance==null")
//                instance = SingletonSample()
//            }
//            return instance ?: SingletonSample()
//        }













        // 1. 上鎖+volatile
//        @Synchronized
//        fun getInstance(): SingletonSample {
//            if (instance == null) {
//                println("instance==null")
//                instance = SingletonSample()
//            }
//            return instance ?: SingletonSample()
//        }


        // 有缺點嗎？？






        // 2. 雙重鎖
        @OptIn(InternalCoroutinesApi::class)
//        fun getInstance():SingletonSample{
//            val obj = instance
//            if(obj!=null){
//                return obj
//            }
//            return synchronized(this){
//                val realOne = instance
//                if(realOne!=null){
//                    realOne
//                }else{
//                    val tempCreated = SingletonSample()
//                    instance = tempCreated
//                    tempCreated
//                }
//            }
//        }
        // 也可以寫成這樣
//        fun getInstance(): SingletonSample =
//            instance ?: synchronized(this) {
//                instance ?: SingletonSample().also {
//                    println("instance==null")
//                    instance = it
//                }
//            }






        // 有沒有發現哪裏怪怪的??



        // 3. byLazy 已幫你實現類似雙重鎖的概念
//        val getInstance by lazy { SingletonSample() }
//        val sameSame:SingletonSample by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SingletonSample() }





        // 4. 靜態內部類單例
        private class LazyHolder {
            companion object{
                val realSingleObject = SingletonSample()
            }
        }
        fun getInstance():SingletonSample{
            return LazyHolder.realSingleObject
        }


        // 我其實不知道和這個有甚麼不一樣
//        private val realOne = SingletonSample()
//        fun getInstance():SingletonSample{
//            return realOne
//        }




        // 雙重鎖、靜態內部類、byLazy都可以實現執行緒安全的單例模式，但是不能防止用反射的方式打破單例
        // 反射->物件繞過Java控制檢查的許可權

    }
}


fun reflection1(){
    val obj1 = SingletonSample.getInstance()
    val obj2 = SingletonSample.getInstance()
    val cons = SingletonSample::class.primaryConstructor
    cons?.isAccessible = true
    val reflectionObj = cons?.call()

    println("$obj1")
    println("$obj2")
    println("$reflectionObj")
}

fun reflection2(){
    // enum防止反射的方式打破單例
    val obj1 = PerfectEnumSingleton
    val obj2 = PerfectEnumSingleton
    val cons = PerfectEnumSingleton::class.primaryConstructor
    cons?.isAccessible = true
    val reflectionObj = cons?.call()

    println("$obj1")
    println("$obj2")
    println("$reflectionObj")
}

object PerfectEnumSingleton{}

// 完美列舉實現單例模式
//enum class PerfectEnumSingleton {
//    INSTANCE;
//
//    fun test(){
//        println("test")
//    }
//}

/**
 * 結語:
 *
 * 那sharedPreference到底要用哪種方式?? 需要context拿到getSharedPreferences方法
 * 1. by lazy + 傳入androidContext(全方位 NormalSharedPreferencesManager)
 * 2. koin (小畢) -> dataModule + sharedPreferenceBase+
 *
 */






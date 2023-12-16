package datastore

import kotlin.reflect.full.*

/**
 * 甚麼是反射?
 * 藉由class對像取得類別的屬性和方法
 *
 * build原理
 * 1. 原始檔：編寫Java原始檔（我們也稱之為原始碼檔案），它的副檔名為.java；
 * 2. 編譯：然後通過編譯器把原始檔編譯成位元組碼檔案，位元組碼副檔名為.class；
 * 3. 執行：最後使用直譯器來執行位元組碼檔案。
 */
// [一張圖稿懂反射](https://blog.csdn.net/sinat_21843047/article/details/80066333)
// [今日分享內容](https://zhuanlan.zhihu.com/p/395105807)

abstract class Father{
    fun fatherFunc(){}
    open fun String.test(){
    }
}
class Item(var name: String): Father() {
    var price = 0.0
    var vipId = 0

    constructor() : this("未知商品") {
        this.price = 0.0
    }

    constructor(name: String, price: Double) : this(name) {
        this.price = price
        this.name = name
    }

    constructor(name: String, price: Double, vipId:Int) : this(name) {
        this.name = name
        this.price = price
        this.vipId = vipId
    }
    fun setData() {
        this.price = 0.0
        this.name = "未知商品"
        println("执行空参数的test方法: $name, $price")
    }
    fun setData1() {
        this.price = 0.0
        this.name = "未知商品"
        println("111111111111111111111: $name, $price")
    }
    fun setData(name: String){
        this.price = price
        println("执行带String参数的方法：$name, $price")
    }
    fun setData(number: Int){
        this.price = number.toDouble()
        println("number：$name, $price")
    }
    fun setData(name: String, price: Double){
        this.price = price
        this.name = name
        println("执行带String，Double参数的方法：$name, $price")
    }

    override fun String.test(){
    }
}

fun main(args: Array<String>) {
    declaredFunctions()
}

// 1. 創建實例
// createInstance()方法调用无参数的构造器创建实例
fun createInstance(){
    val clazz = Item::class// 先拿到KClass<T>
    val inst1 = clazz.createInstance()// 獲取Item這個類別的實體
    val inst2 = clazz.createInstance()
    println(inst1.name)
    println(inst1.price)
    println("$inst1, $inst2")
}

// KClass.primaryConstructor 是主构造函数，如果有参数，可以调用它的 call 方法传入参数
// 所有构造器和方法都属于KFunction的实例，可以通过call()方法来调用。
fun primaryConstructor(){
    val clazz = Item::class
    val con = clazz.primaryConstructor
    val inst1 = con?.call("111")
    println(inst1?.name)
    println(inst1?.price)
}

fun createByDifferentCons(){
    val clazz = Item::class
    //获取所有构造器
    val cons = clazz.constructors
    cons.forEach {
        println("建構子$it")
        if (it.parameters.size == 2) {
            //调用带两个参数的构造器创建实例
            //所有构造器和方法都属于KFunction的实例，可以通过call()方法来调用。
            val inst2 = it.call("商品1", 45.6)
            println(inst2.name)
            println(inst2.price)
            println(inst2.vipId)
        }
        if(it.parameters.size==3){
            val inst3 = it.call("商品2", 87.87,1111)
            println(inst3.name)
            println(inst3.price)
            println(inst3.vipId)
        }
        println("------end-------")
    }
}

// 只適用不帶參數傳遞
fun itemFactory(createItem:()-> Item){
    val item: Item = createItem()
    println(item.name)
    println(item.price)
    println(item.vipId)
}


// 2. 獲取實例function
fun compareFunctions(){
    val clazz = Item::class
    val ins = clazz.createInstance()
    val functions = clazz.functions// 本身及父类的函数 KClass.functions
    val declaredFunctions = clazz.declaredFunctions// 本身声明的函数 KClass.declaredFunctions
    val memberExtensionFunctions = clazz.memberExtensionFunctions// 拓展的函数 KClass.memberExtensionFunctions
    println("-------functions---------")
    functions.forEach {
        println(it)
    }
    println("------declaredFunctions--------")
    declaredFunctions.forEach {
        println(it)
    }
    println("------memberExtensionFunctions--------")
    memberExtensionFunctions.forEach {
        println(it)
    }
}

// 設定函數參數
fun declaredFunctions(){
    val clazz = Item::class
    val ins = clazz.createInstance()
    val funcs = clazz.declaredFunctions
    for(f in funcs){
        if (f.parameters.size == 1) {
            //调用带0个参数的函数
            f.call(ins)
        }
        //如果函数具有2个参数
        if (f.parameters.size == 2) {
            //调用3个参数的函数
            f.call(ins, "aaa")
        }

        //如果函数具有3个参数
        if (f.parameters.size == 3) {
            //调用带2个参数的函数
            f.call(ins, "bbb", 45.6)
        }
    }
}

// Kotlin可以获取函数的引用，把函数当成参数传入另一个函数中。
fun isSmall(i: Int) = i < 5
fun isSmall(s: String) = s.length < 5
fun overloadFunctionSample(){
    val list = listOf(20, 30, 100, 4, -3, 2, -12)

    val resultList = list.filter(::isSmall)
    println(resultList)

    val strList = listOf("Java", "Kotlin", "Swift", "Go", "Erlang")

    val resultStrList = strList.filter(::isSmall)
    println(resultStrList)

    var f: (String) -> Boolean = ::isSmall
    println(f("Lua"))
}






// [參考連結](https://www.jianshu.com/p/63da6197913b)


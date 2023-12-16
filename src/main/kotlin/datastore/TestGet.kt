package datastore

class TestGet {
    object SharedPreference{
        var a:Int = 0
    }

    companion object{
        private val test1
            get() = SharedPreference.a

        private val test2:Int
            get() { return SharedPreference.a }
        @JvmStatic
        fun main(args:Array<String>){
            println("$test1, $test2")
            SharedPreference.a++
            println("$test1, $test2")
        }
    }
}
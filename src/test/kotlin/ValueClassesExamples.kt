import org.junit.jupiter.api.Test

class ValueClassesExamples {

    @JvmInline
    value class DisplayBrightness(val value: Int)


    @JvmInline
    value class Pair1(val value: Pair<String, String>)

    @JvmInline
    value class Pair2(val value: Pair<String, String>){
        val elem1: String
            get() = this.value.first

        val elem2: String
            get() = this.value.second
    }


    fun doSth(p: Pair1, p2: Pair2): DisplayBrightness {
        val strings = p.value.toList() + (p2.elem1 + p2.elem1)
        return DisplayBrightness(strings.size)
    }

    @Test
    fun doSthTest() {
        val result = doSth(Pair1("foo" to "bar"), Pair2("oneb" to "biii"))

        println(result)

    }
}


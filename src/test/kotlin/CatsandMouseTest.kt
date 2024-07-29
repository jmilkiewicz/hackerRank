import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class CatsandMouseTest {

    @ParameterizedTest(name = "{index} for {0} biggest is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun biggerIsGreaterTest(catA: Int, catB:Int, mouse:Int, expected: String) {
        assertThat(catAndMouse(catA, catB, mouse), Matchers.`is`(expected))
    }

    private fun catAndMouse(catA: Int, catB: Int, mouse: Int): String {
        val cats = listOf(catA to "Cat A", catB to "Cat B")


        val map = cats.map { p -> Math.abs(p.first - mouse) to p.second }
        val max = map.minByOrNull { p -> p.first }!!.first

        val result = map.filter { it.first == max }

        return when {
            result.size == 1 -> result[0].second
            else -> "Mouse C"
        }
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(1, 2, 3, "Cat B"),
                Arguments.of(1, 3, 2, "Mouse C"),
            );
        }
    }

}

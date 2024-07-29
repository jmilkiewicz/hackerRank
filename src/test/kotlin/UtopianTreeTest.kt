import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class UtopianTreeTest {

    @ParameterizedTest(name = "{index} for growth cycles  {0}, hieght is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun test(cycles: Int,  expected: Int) {
        assertThat(utopianTree(cycles), Matchers.`is`(expected))
    }

    sealed interface SeasonGrowth {
        fun grow(): SeasonGrowth
        fun getValue(): Int
    }

    class SpringGrowth(val current: Int) : SeasonGrowth {
        override fun grow(): SeasonGrowth = SummerGrowth(current * 2)
        override fun getValue(): Int = current

    }

    class SummerGrowth(val current: Int) : SeasonGrowth {
        override fun grow(): SeasonGrowth = SpringGrowth(current + 1)
        override fun getValue(): Int = current
    }


    fun utopianTree(n: Int): Int {
        val last = generateSequence(SpringGrowth(1) as SeasonGrowth) { thisTree -> thisTree.grow() }.take(n+1).last()
        return last.getValue()
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    0,
                    1,
                ),
                Arguments.of(
                    1,
                    2,
                ),
                Arguments.of(
                    5,
                    14,
                ),
                Arguments.of(
                    4, 7
                ),
            );
        }

    }

}

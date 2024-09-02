import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class StaircaseTest {

    @ParameterizedTest(name = "{index} for {0} biggest is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun test(depth: Int, expected: List<String>) {
        assertThat(app2(depth), Matchers.`is`(expected))
    }

    fun staircase(n: Int): Unit {
        app(n).forEach { println(it) }
    }

    fun app(n: Int): List<String> = (1..n)
        .map {
            val howManySpaces = (n - it)
            " ".repeat(howManySpaces) + "#".repeat(n - howManySpaces)
        }


    fun app2(n: Int): List<String> {
        val sequence = generateSequence(" ".repeat(n-1) + "#") { prev -> prev.drop(1) + "#" }
        return sequence.take(n).toList()

    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    6,
                    listOf("     #", "    ##", "   ###", "  ####", " #####", "######"),
                ),
                Arguments.of(
                    1,
                    listOf("#"),
                ),
            );
        }
    }

}
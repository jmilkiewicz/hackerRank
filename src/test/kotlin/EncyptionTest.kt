import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class EncyptionTest {

    @ParameterizedTest(name = "{index} for s {0} ,encrypted is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun test(input: String, output: String) {
        assertThat(encryption(input), Matchers.`is`(output))
    }


    fun encryption(s: String): String {
        val noSpaces = s.toCharArray().filter { !it.isWhitespace() }
        val sqrt = Math.sqrt(noSpaces.count().toDouble())
        val rows = Math.floor(sqrt).toInt()
        val columns = Math.ceil(sqrt).toInt()

        val chunks = noSpaces.chunked(columns)
        val fold = chunks.fold(emptyList<String>()) { acc, row ->
            val asStrings = row.map { it.toString() }
            if (acc.isEmpty()) {
                asStrings
            } else {

                val padNumber = columns - asStrings.size
                val padded = asStrings + List(padNumber) { "" }
                acc.zip(padded) { st1, st2 -> st1 + st2 }
            }
        }
        return fold.joinToString(" ")


    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(

                Arguments.of(
                    "haveaniceday",
                    "hae and via ecy",
                ),
                Arguments.of(
                    "feedthedog",
                    "fto ehg ee dd",
                ),

            );
        }

    }

}

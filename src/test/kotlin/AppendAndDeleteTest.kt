import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class AppendAndDeleteTest {

    @ParameterizedTest(name = "{index} for s {0}, t {1} , moves {2}, possible: {3}")
    @MethodSource("provideStringsForIsBlank")
    fun test(s: String, t: String, k: Int, boolean: Boolean) {
        assertThat(appendAndDeleteNice(s, t, k), Matchers.`is`(boolean))
    }

    private fun appendAndDeleteNice(s: String, t: String, k: Int): Boolean {

        val commonPrefixLength = s.zip(t).takeWhile { (a1, a2) -> a1 == a2 }.size

        val sWithoutPrefix = s.substring(commonPrefixLength)

        val tWithoutPrefix = t.substring(commonPrefixLength)

        return when {
            s.length + t.length <= k -> true
            s.isEmpty() -> k - t.length >= 0
            s == t -> k % 2 == 0
            tWithoutPrefix.isEmpty() -> (k - sWithoutPrefix.length) % 2 == 0
            sWithoutPrefix.isEmpty() -> (k - tWithoutPrefix.length) % 2 == 0
            else -> k - sWithoutPrefix.length - tWithoutPrefix.length == 0
        }
    }

    private fun appendAndDelete(s: String, t: String, k: Int): String {
        return if (appendAndDeleteNice(s, t, k)) {
            "Yes"
        } else {
            "No"
        }


    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(

                Arguments.of(
                    "aba",
                    "aba",
                    5, false
                ),

                Arguments.of(
                    "asdfqwertyuighjkzxcvasdfqwertyuighjkzxcvasdfqwertyuighjkzxcvasdfqwertyuighjkzxcvasdfqwertyuighjkzxcv",
                    "asdfqwertyuighjkzxcvasdfqwertyuighjkzxcvasdfqwertyuighjkzxcvasdfqwertyuighjkzxcvasdfqwertyuighjkzxcv",
                    20, true
                ),
                Arguments.of(
                    "zzzzz",
                    "zzzzzzz",
                    4, true
                ),


                Arguments.of(
                    "aaaaaaaaaa",
                    "aaaaa",
                    7, true
                ),
                Arguments.of(
                    "abcd",
                    "abcdert",
                    10, false
                ),
                Arguments.of(
                    "ax",
                    "aa",
                    3, false
                ),


                Arguments.of(
                    "abcd",
                    "abcdert",
                    11, true
                ),
                Arguments.of(
                    "abcd",
                    "abcdert",
                    12, true
                ),

                Arguments.of(
                    "",
                    "aa",
                    3, true
                ),


                Arguments.of(
                    "ashley",
                    "ash",
                    2, false
                ),
                Arguments.of(
                    "aba",
                    "aba",
                    7, true
                ),
                Arguments.of(
                    "abc",
                    "def",
                    6, true
                ),
                Arguments.of(
                    "aab",
                    "aac",
                    2, true
                ),

                Arguments.of(
                    "aab",
                    "aacd",
                    3, true
                ),
                Arguments.of(
                    "aabac",
                    "aac",
                    4, true
                ),

                Arguments.of(
                    "aabd",
                    "aaef",
                    2, false
                ),

                Arguments.of(
                    "",
                    "aa",
                    2, true
                ),
                Arguments.of(
                    "hackerhappy",
                    "hackerrank",
                    9, true
                )

            );
        }

    }

}

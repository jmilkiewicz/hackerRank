import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class DesignerPDFViewerTest {

    @ParameterizedTest(name = "{index} for rank {0}, scores {1} , result is {2}")
    @MethodSource("provideStringsForIsBlank")
    fun test(heights: Array<Int>, word:String, expected: Int) {
        assertThat(designerPdfViewer(heights, word), Matchers.`is`(expected))
    }


    fun designerPdfViewer(h: Array<Int>, word: String): Int {
        val toMap = ('a'..'z').zip(h).toMap()
        val theTallest = word.map { toMap[it]!! }.max()
        return theTallest * word.length

    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf(1, 3, 1, 3, 1, 4, 1, 3, 2, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5).toTypedArray(),
                    "abc",
                    9,
                ),
                Arguments.of(
                    listOf(1,3,1,3,1,4,1,3,2,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,7).toTypedArray(),
                    "zaba",
                    28,
                ),
            );
        }

    }

}

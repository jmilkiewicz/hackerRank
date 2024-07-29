import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class AcmTeamTest {

    @ParameterizedTest(name = "{index} for sss {0}, scores {1}")
    @MethodSource("provideStringsForIsBlank")
    fun test(st: Array<String>, expected: Array<Int>) {
        assertThat(acmTeam(st), Matchers.`is`(expected))
    }


    fun acmTeam(topics: Array<String>): Array<Int> {
        val pairTopics = topics.flatMapIndexed { index, topic ->
            topics.slice((index + 1).rangeTo(topics.size-1)).map { merge(topic, it) }
        }

         val max = pairTopics.max()
        val howManyPairs = pairTopics.count { it == max }
        return arrayOf(max, howManyPairs)

    }

    private fun merge(topic: String, topic2: String) =
        topic.zip(topic2).count { (c1, c2) -> c1 == '1' || c2 == '1' }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    arrayOf(
                        "10101",
                        "11100",
                        "11010", "00101"
                    )
                    ,
                    arrayOf(5,2)
                ),
            );
        }

    }

}

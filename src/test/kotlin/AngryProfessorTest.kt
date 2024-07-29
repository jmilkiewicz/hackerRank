import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class AngryProfessorTest {

    @ParameterizedTest(name = "{index} for rank {0}, scores {1} , result is {2}")
    @MethodSource("provideStringsForIsBlank")
    fun test(st: Array<Int>, k:Int, expected:String) {
        assertThat(angryProfessor(k, st), Matchers.`is`(expected))
    }


    fun angryProfessor(k: Int, a: Array<Int>): String {
        return if(a.filter { it <=0 }.size < k){
            "YES"
        }else {
            "NO"
        }
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf(-1,-3,4,2).toTypedArray(),
                    3,
                    "YES",
                ),
                Arguments.of(
                    listOf(0,-1,2,1).toTypedArray(),
                    2,
                    "NO"
                ),
            );
        }

    }

}

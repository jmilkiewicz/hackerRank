import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class SavePrisonerTest {

    @ParameterizedTest(name = "{index} for  {0}, t {1} , moves {2}, possible: {3}")
    @MethodSource("provideStringsForIsBlank")
    fun test(numOfPrisoners: Int, candies: Int, startPosition: Int, expected: Int) {
        assertThat(savePrisoner(numOfPrisoners, candies, startPosition), Matchers.`is`(expected))
    }

    private fun savePrisoner2(numOfPrisoners: Int, candies: Int, startPosition: Int): Int {
        fun <T> Sequence<T>.repeat(): Sequence<T> =
            generateSequence(this) { this }.flatten()

        val aaaa = (1..numOfPrisoners).asSequence().repeat().drop(startPosition - 1).drop(candies - 1).take(1).toList()
        return aaaa.last()


    }

    private fun savePrisoner(numOfPrisoners: Int, candies: Int, startPosition: Int): Int {
        val restOfCandies = candies  % numOfPrisoners

        return if(restOfCandies == 0) {
            if(startPosition == 1){
                numOfPrisoners
            }else{
                startPosition - 1
            }
        }else{
            val x = startPosition + restOfCandies -1
            if(x > numOfPrisoners)
                x - numOfPrisoners
            else
                x
        }





    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(

                Arguments.of(
                    10,
                    16,
                    8,
                    3
                ),
                Arguments.of(
                    4,
                    6,
                    2,
                    3
                ),
                Arguments.of(
                    10,
                    15,
                    4,
                    8
                ),
                Arguments.of(
                    5,
                    2,
                    1,
                    2
                ),
                Arguments.of(
                    5,
                    2,
                    2,
                    3
                ),
                Arguments.of(
                    7,
                    19,
                    2,
                    6
                ),
                Arguments.of(
                    3,
                    7,
                    3,
                    3
                ),

                Arguments.of(
                    559033664,
                    822024089,
                    131746755,
                    394737179
                ),
                Arguments.of(
                    4,
                    5,
                    4,
                    4
                ),

                Arguments.of(
                    3,
                    6,
                    3,
                    2
                ),

                Arguments.of(
                    4,
                    4,
                    1,
                    4
                ),


                );
        }

    }

}

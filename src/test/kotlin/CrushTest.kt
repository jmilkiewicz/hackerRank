import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class CrushTest {

    @ParameterizedTest(name = "{index} for array of size {0} ,   queries {1} , biggest is encrypted is {2}")
    @MethodSource("provideStringsForIsBlank")
    fun test(inputArraySize: Int, queries: Array<Array<Int>>, output: Long) {
        assertThat(arrayManipulation(inputArraySize, queries), Matchers.`is`(output))
    }


    fun arrayManipulation(inputArraySize: Int, queries: Array<Array<Int>>): Long {
        val creatInitialRange = creatInitialRange(inputArraySize)

        val fold = queries.fold(listOf(creatInitialRange)) { acc, ints -> acc.flatMap { it.merge(ints) } }

        return fold.maxOf { it.value }


    }

    private fun creatInitialRange(inputArraySize: Int) = MyRange(1..inputArraySize)

    data class MyRange(val range: IntRange, val value: Long = 0) {
        fun merge(ints: Array<Int>): List<MyRange> {
            val from = ints[0]
            val to = ints[1]
            val newValue = ints[2]
            return if (areSeparated(from, to)) {
                listOf(this) + MyRange(from..to, newValue.toLong())
            } else {
                handle(from, to, newValue)
            }
        }

        private fun handle(from: Int, to: Int, newValue: Int): List<MyRange> {

           val l =  when {
//                opcja 2
                from <= range.first && to >= range.last -> {

                    val l = listOf(
                        MyRange(from.rangeTo(range.first-1), newValue.toLong()),
                        MyRange(range, this.value + newValue.toLong()),
                        MyRange((range.last + 1).rangeTo(to), newValue.toLong())
                    )
                    l
                }

                //opcja 3
                range.contains(from) && range.contains(to) -> {
                    val l = listOf(
                        MyRange(range.first.rangeTo(from-1), this.value),
                        MyRange(from.rangeTo(to), this.value + newValue),
                        MyRange((to + 1).rangeTo(range.last), this.value)
                    )
                    l
                }
//                opcja 4
                from > range.first && to > range.last -> {

                    val l = listOf(
                        MyRange(range.first.rangeTo(from-1), value),
                        MyRange(from.rangeTo(range.last), this.value + newValue.toLong()),
                        MyRange((range.last + 1).rangeTo(to-1), newValue.toLong())
                    )
                    l
                }
//                opcja 5
                from < range.first && to >= range.first -> {

                    val l = listOf(
                        MyRange(from.rangeTo(range.first-1), newValue.toLong()),
                        MyRange(range.first.rangeTo(to), this.value + newValue.toLong()),
                        MyRange((to + 1).rangeTo(range.last), value)
                    )
                    l
                }

                else -> throw IllegalArgumentException("Invalid input")
            }
            return  l.filter { !it.range.isEmpty() }.filter { it.value > 0 }
        }

        private fun areSeparated(from: Int, to: Int): Boolean =
            (from < range.first && to < range.first()) || (from > range.last && to > range.last)


    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(

                Arguments.of(
                    10,
                    arrayOf(arrayOf(1, 5, 3), arrayOf(4, 8, 7), arrayOf(6, 9, 1)),
                    10
                ),

                Arguments.of(
                    5,
                    arrayOf(arrayOf(1, 2, 100), arrayOf(2, 5, 100), arrayOf(3, 4, 100)),
                    200L
                ),

                Arguments.of(
                    10,
                    arrayOf(arrayOf(1, 5, 3), arrayOf(6, 8, 5), arrayOf(9, 10, 1)),
                    5L
                ),

                Arguments.of(
                    10,
                    arrayOf(arrayOf(2, 5, 3), arrayOf(2, 8, 5)),
                    8L
                ),
                Arguments.of(
                    10,
                    arrayOf(arrayOf(1, 5, 3), arrayOf(3, 6, 6)),
                    9L
                ),
                Arguments.of(
                    10,
                    arrayOf(arrayOf(1, 5, 3), arrayOf(5, 6, 6)),
                    9L
                ),


                Arguments.of(
                    10,
                    arrayOf(arrayOf(3, 6, 3), arrayOf(1, 7, 6)),
                    9L
                ),


                );
        }

    }

}

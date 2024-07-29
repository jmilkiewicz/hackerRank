import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class ClimbingLeaderBoard {

    @ParameterizedTest(name = "{index} for rank {0}, scores {1} , result is {2}")
    @MethodSource("provideStringsForIsBlank")
    fun climbingLeaderBoardTest(ranked: Array<Int>, player: Array<Int>, expected: List<Int>) {
        assertThat(climbing3(ranked, player), Matchers.`is`(expected))
    }


    private data class RankAndRanked(val ranked: List<MyRange>, val ranks: List<Int>)


    fun climbing3(scores: Array<Int>, alice: Array<Int>): List<Int> {
        val scoresList = scores.distinct()
        return alice.map {
            val searchResult = scoresList.binarySearch(it, reverseOrder())
            if (searchResult < 0) -searchResult else searchResult + 1
        }
    }

    private fun climbing2(ranked: Array<Int>, player: Array<Int>): Array<Int> {

        val ranges = buildRanges(ranked)

        val comparisonBuilder: (Int) -> ((MyRange) -> Int) = { toSearch: Int ->
            { myRange ->
                if (myRange.covers(toSearch)) {
                    0
                } else
                    if (myRange.isGreater(toSearch)) {
                        -1
                    } else {
                        +1
                    }
            }
        }
        val result = player.fold(RankAndRanked(ranges, emptyList())) { acc, result ->
            val indexOfFound = acc.ranked.binarySearch(0, acc.ranked.size, comparisonBuilder(result))
            val rank = acc.ranked[indexOfFound].rank

            RankAndRanked(acc.ranked.subList(0, indexOfFound+1), listOf(rank) + acc.ranks)
        }

        return result.ranks.reversed().toTypedArray()
    }


    private fun climbing(ranked: Array<Int>, player: Array<Int>): Array<Int> {
        val fold = buildRanges(ranked)
        val comparisonBuilder: (Int) -> ((MyRange) -> Int) = { toSearch: Int ->
            { myRange ->
                if (myRange.covers(toSearch)) {
                    0
                } else
                    if (myRange.isGreater(toSearch)) {
                        -1
                    } else {
                        +1
                    }
            }
        }


        return player.map { result ->
            val index = fold.binarySearch(0, fold.size, comparisonBuilder(result))
            fold[index].rank
        }.toTypedArray()


    }

    private fun buildRanges(ranked: Array<Int>): List<MyRange> {
        val fold = ranked.fold(emptyList<MyRange>()) { acc, value ->
            if (acc.isEmpty()) {
                listOf(MyRange(Int.MAX_VALUE, value, 1))
            } else {
                val last = acc.last()
                if (last.downTo == value) {
                    acc
                } else {
                    acc + last.newRange(value)
                }
            }

        }

        val last = fold.last()
        return fold + MyRange(last.downTo, 0, last.rank + 1)
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf(100, 100, 50, 40, 40, 20, 10).toTypedArray(),
                    listOf(5, 25, 50, 120).toTypedArray(),
                    listOf(6, 4, 2, 1),
                ),
                Arguments.of(
                    listOf(
                        997,
                        981,
                        957,
                        933,
                        930,
                        927,
                        926,
                        920,
                        916,
                        896,
                        887,
                        874,
                        863,
                        863,
                        858,
                        847,
                        815,
                        809,
                        803,
                        794,
                        789,
                        785,
                        783,
                        778,
                        764,
                        755,
                        751,
                        740,
                        737,
                        730,
                        691,
                        677,
                        652,
                        650,
                        587,
                        585,
                        583,
                        568,
                        546,
                        541,
                        540,
                        538,
                        531,
                        527,
                        506,
                        493,
                        457,
                        435,
                        430,
                        427,
                        422,
                        422,
                        414,
                        404,
                        400,
                        394,
                        387,
                        384,
                        374,
                        371,
                        369,
                        369,
                        368,
                        365,
                        363,
                        337,
                        336,
                        328,
                        325,
                        316,
                        314,
                        306,
                        282,
                        277,
                        230,
                        227,
                        212,
                        199,
                        179,
                        173,
                        171,
                        168,
                        136,
                        125,
                        124,
                        95,
                        92,
                        88,
                        85,
                        70,
                        68,
                        61,
                        60,
                        59,
                        44,
                        43,
                        28,
                        23,
                        13,
                        12
                    ).toTypedArray(),
                    listOf(12,20,30,32,35,37,63,72,83,85,96,98,98,118,122,125,129,132,140,144,150,164,184,191,194,198,200,220,228,229,229,236,238,246,259,271,276,281,283,287,300,302,306,307,312,318,321,325,341,341,341,344,349,351,354,356,366,369,370,379,380,380,396,405,408,417,423,429,433,435,438,441,442,444,445,445,452,453,465,466,467,468,469,471,475,482,489,491,492,493,498,500,501,504,506,508,523,529,530,539,543,551,552,556,568,569,571,587,591,601,602,606,607,612,614,619,620,623,625,625,627,638,645,653,661,662,669,670,676,684,689,690,709,709,710,716,724,726,730,731,733,737,744,744,747,757,764,765,765,772,773,774,777,787,794,796,797,802,805,811,814,819,819,829,830,841,842,847,857,857,859,860,866,872,879,882,895,900,900,903,905,915,918,918,922,925,927,928,929,931,934,937,955,960,966,974,982,988,996,996).toTypedArray(),
                    emptyList<Int>()

                )
            );
        }

        private data class MyRange(val from: Int, val downTo: Int, val rank: Int = 0) {

            fun newRange(to: Int): MyRange {
                return MyRange(this.downTo, to, this.rank + 1)
            }

            fun covers(toSearch: Int): Boolean {
                return this.from > toSearch && toSearch >= this.downTo
            }

            fun isGreater(toSearch: Int): Boolean {
                return !covers(toSearch) && toSearch < this.downTo
            }
        }
    }

}

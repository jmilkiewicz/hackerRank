import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test


class Merge2Sorted {


    @Test
    fun name() {
        val left = listOf(4, 5, 6, 7, 8)
        val right = listOf(1, 2, 3, 4, 5)

        val meged = mergeSorted2(left, right)

        assertThat(meged, Matchers.`is`(listOf(1, 2, 3, 4, 4, 5, 5, 6, 7, 8)))
    }

    private fun mergeSorted(left: List<Int>, right: List<Int>): List<Int> {
        return if (left.isEmpty() || right.isEmpty()) {
            left + right
        } else {
            val headLeft = left.first()
            val headRight = right.first()
            if (headRight > headLeft) {
                val start = left.takeWhile { it <= headRight }
                start + mergeSorted(left.drop(start.size), right)
            } else {
                val start = right.takeWhile { it <= headRight }
                start + mergeSorted(left, right.drop(start.size))
            }
        }
    }

    private fun mergeSorted2(left: List<Int>, right: List<Int>): List<Int> {
        tailrec fun go(left: List<Int>, right: List<Int>, result: List<Int>): List<Int> {
            return if (left.isEmpty() || right.isEmpty()) {
                result + left + right
            } else {
                val headLeft = left.first()
                val headRight = right.first()
                if (headRight > headLeft) {
                    val start = left.takeWhile { it <= headRight }
                    go(left.drop(start.size), right, result + start)
                } else {
                    val start = right.takeWhile { it <= headRight }
                    go(left, right.drop(start.size), result + start)
                }
            }
        }

        return go(left, right, emptyList())
    }
}

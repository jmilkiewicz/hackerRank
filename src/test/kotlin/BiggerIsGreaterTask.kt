import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class BiggerIsGreaterTask {

    @ParameterizedTest(name = "{index} for {0} biggest is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun biggerIsGreaterTest(input: String, expected: String) {
        assertThat(biggerIsGreater(input), Matchers.`is`(expected))
    }


    private fun biggerIsGreater(input: String): String {
        val charArray = input.toCharArray()
        return when (val switchOperation = switchOperation(charArray)) {
            null -> "no answer"
            else -> doSwitch(charArray, switchOperation)
        }


    }

    private fun doSwitch(charArray: CharArray, switchOperation: Pair<Int, Int>): String {
        val (indexFrom, indexTo) = switchOperation
        val tmp = charArray[indexFrom]
        charArray[indexFrom] = charArray[indexTo]
        charArray[indexTo] = tmp

        val slice = charArray.slice(indexTo + 1..<charArray.size).sorted()
        val result = charArray.slice(0..indexTo) + slice
        return result.joinToString(separator = "")
    }


    private fun switchOperation(input: CharArray): Pair<Int, Int>? {
        var result: Pair<Int, Int>? = null
        for (i in input.size - 1 downTo 1) {
            val newResult = switchOperation(input, i)

            if (result == null) {
                result = newResult
            } else {
                result = selectBetterSwitch(newResult, result)
            }

        }
        return result
    }

    private fun selectBetterSwitch(
        newResult: Pair<Int, Int>?,
        oldResult: Pair<Int, Int>
    ): Pair<Int, Int> {
        return if (newResult != null && newResult.second > oldResult.second) {
            newResult
        } else {
            oldResult
        }
    }


    private fun switchOperation(input: CharArray, start: Int): Pair<Int, Int>? {
        val last = input[start]
        var index = start
        while (index >= 0 && last <= input[index]) {
            index--
        }
        return if (index == -1) {
            null
        } else {
            Pair(start, index)
        }

    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("hefg", "hegf"),
                Arguments.of("dkhc", "hcdk"),
                Arguments.of("abdc", "acbd"),
                Arguments.of("fedcbabcd", "fedcbabdc"),
                Arguments.of("dcba", "no answer"),
                Arguments.of("aegc", "agce"),
                Arguments.of("lmno", "lmon"),
                Arguments.of("abdc", "acbd"),
                Arguments.of("abcd", "abdc"),
                Arguments.of("dhck", "dhkc"),
                );
        }
    }

}

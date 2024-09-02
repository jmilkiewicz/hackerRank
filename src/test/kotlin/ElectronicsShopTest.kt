import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ElectronicsShopTest {
    @ParameterizedTest(name = "{index} for {0} biggest is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun electronicsShop(
        keyboards: Array<Int>,
        usbDrives: Array<Int>,
        budget: Int,
        expected: Int,
    ) {
        assertThat(electronicsShop(keyboards, usbDrives, budget), Matchers.`is`(expected))
    }

    private fun electronicsShop(
        keyboards: Array<Int>,
        usbDrives: Array<Int>,
        budget: Int,
    ): Int {
        val maxByOrNull =
            keyboards
                .flatMap { keyboard -> usbDrives.map { drive -> keyboard + drive } }
                .filter { it <= budget }
                .maxOrNull()

        return maxByOrNull ?: -1
    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    listOf(3, 1).toTypedArray(),
                    listOf(5, 2, 8).toTypedArray(),
                    10,
                    9,
                ),
            )
    }
}

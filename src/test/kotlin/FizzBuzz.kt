import org.junit.jupiter.api.Test

class FizzBuzz {
    @Test
    fun test() {
        val fizzes = listOf("", "", "fizz").asSequence().indefinite()
        val buzzes = listOf("", "", "", "", "buzz").asSequence().indefinite()
        val fizzBuzzes = fizzes.zip(buzzes).map { "${it.first}${it.second}" }

        val x =
            generateSequence(1) { it + 1 }
                .zip(fizzBuzzes)
                .map {
                    it.second.ifEmpty {
                        it.first.toString()
                    }
                }

        x.take(100).forEach { println(it) }
    }
}

private fun <T> Sequence<T>.indefinite(): Sequence<T> = generateSequence(this) { this }.flatten()

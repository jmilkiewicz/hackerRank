import org.junit.jupiter.api.Test

class TennisGameTest {
    enum class GemScore {
        ZERO,
        FIFTEEN,
        THIRTY,
        FORTY,
    }

    data class SetScore(
        val playerOne: Int,
        val playerTwo: Int,
    ) {
        fun playerOneWinsGem(): SetScore = SetScore(playerOne + 1, playerTwo)

        fun playerTwoWinsGem(): SetScore = SetScore(playerOne, playerTwo + 1)

        fun isTieBreak(): Boolean = playerTwo == 6 && playerOne == 6

        fun isSetWonByPlayerOne(): Boolean =
            (playerOne == 6 && playerTwo < 5) ||
                (playerOne == 7)

        fun isSetWonByPlayerTwo(): Boolean =
            (playerTwo == 6 && playerOne < 5) ||
                (playerTwo == 7)
    }

    sealed class GameResult

    data class NormalGameResult(
        val setScore: SetScore,
        val playerOne: GemScore,
        val playerTwo: GemScore,
    ) : GameResult()

    data class Deuce(
        val setScore: SetScore,
    ) : GameResult()

    data class TieBreak(
        val setScore: SetScore,
        val playerOne: Int,
        val playerTwo: Int,
    ) : GameResult()

    data class PlayerOneAdvantage(
        val setScore: SetScore,
    ) : GameResult()

    data class PlayerTwoAdvantage(
        val setScore: SetScore,
    ) : GameResult()

    data class Finish(
        val setScore: SetScore,
    ) : GameResult()

    abstract class GameState {
        abstract fun playerOneWonABall(): GameState

        abstract fun playerTwoWonABall(): GameState

        abstract fun getResult(): GameResult
    }

    class GemWonState(
        score: SetScore,
    ) : GameState() {
        private val currentState: GameState =
            when {
                score.isTieBreak() -> TieBreakGame(score)
                score.isSetWonByPlayerOne() -> FinishState(score)
                score.isSetWonByPlayerTwo() -> FinishState(score)
                else -> NormalGame(score)
            }

        override fun playerOneWonABall(): GameState = currentState.playerOneWonABall()

        override fun playerTwoWonABall(): GameState = currentState.playerTwoWonABall()

        override fun getResult(): GameResult = currentState.getResult()
    }

    class PlayerOneAdvantageGame(
        private val setScore: SetScore,
    ) : GameState() {
        override fun playerOneWonABall(): GameState = GemWonState(setScore.playerOneWinsGem())

        override fun playerTwoWonABall(): GameState = DeuceGame(setScore)

        override fun getResult(): GameResult = PlayerOneAdvantage(setScore)
    }

    class PlayerTwoAdvantageGame(
        private val setScore: SetScore,
    ) : GameState() {
        override fun playerOneWonABall(): GameState = DeuceGame(setScore)

        override fun playerTwoWonABall(): GameState = GemWonState(setScore.playerTwoWinsGem())

        override fun getResult(): GameResult = PlayerTwoAdvantage(setScore)
    }

    class DeuceGame(
        private val setScore: SetScore,
    ) : GameState() {
        override fun playerOneWonABall(): GameState = PlayerOneAdvantageGame(setScore)

        override fun playerTwoWonABall(): GameState = PlayerTwoAdvantageGame(setScore)

        override fun getResult(): GameResult = Deuce(setScore)
    }

    class TieBreakGame(
        private val setScore: SetScore,
        private val playerOne: Int = 0,
        private val playerTwo: Int = 0,
    ) : GameState() {
        override fun playerOneWonABall(): GameState = TieBreakGame(setScore, playerOne + 1, playerTwo)

        override fun playerTwoWonABall(): GameState = TieBreakGame(setScore, playerOne, playerTwo + 1)

        override fun getResult(): GameResult = TieBreak(setScore, playerOne, playerTwo)
    }

    class FinishState(
        private val setScore: SetScore,
    ) : GameState() {
        override fun playerOneWonABall(): GameState = this

        override fun playerTwoWonABall(): GameState = this

        override fun getResult(): GameResult = Finish(setScore)
    }

    class NormalGame(
        private val setScore: SetScore,
        private val playerOne: GemScore = GemScore.ZERO,
        private val playerTwo: GemScore = GemScore.ZERO,
    ) : GameState() {
        override fun playerOneWonABall(): GameState =
            when (playerOne) {
                GemScore.ZERO, GemScore.FIFTEEN, GemScore.THIRTY -> {
                    val newScore = GemScore.entries[playerOne.ordinal + 1]
                    if (isDeuce(newScore, playerTwo)) {
                        DeuceGame(setScore)
                    } else {
                        NormalGame(setScore, newScore, playerTwo)
                    }
                }

                GemScore.FORTY -> GemWonState(setScore.playerOneWinsGem())
            }

        private fun isDeuce(
            playerOne: GemScore,
            playerTwo: GemScore,
        ): Boolean = playerOne == GemScore.FORTY && playerTwo == GemScore.FORTY

        override fun playerTwoWonABall(): GameState =
            when (playerTwo) {
                GemScore.ZERO, GemScore.FIFTEEN, GemScore.THIRTY -> {
                    val newScore = GemScore.entries[playerTwo.ordinal + 1]
                    if (isDeuce(playerOne, newScore)) {
                        DeuceGame(setScore)
                    } else {
                        NormalGame(setScore, playerOne, newScore)
                    }
                }

                GemScore.FORTY -> GemWonState(setScore.playerTwoWinsGem())
            }

        override fun getResult(): GameResult = NormalGameResult(setScore, playerOne, playerTwo)
    }

    class TennisGame(
        private val gameState: GameState = NormalGame(SetScore(0, 0)),
    ) {
        fun playerOneWonABall(): TennisGame {
            val newState = gameState.playerOneWonABall()
            return TennisGame(newState)
        }

        fun playerTwoWonABall(): TennisGame {
            val newState = gameState.playerTwoWonABall()
            return TennisGame(newState)
        }

        fun getResult(): GameResult = this.gameState.getResult()
    }

    @Test
    fun test() {
        val game = TennisGame()
        val result =
            game
                .playerOneWonABall()
                .playerOneWonABall()
                .playerOneWonABall()
                .playerTwoWonABall()
                .playerTwoWonABall()
                .playerTwoWonABall()
                .playerOneWonABall()
                .playerOneWonABall()
                .playerOneWonABall()
                .playerOneWonABall()
                .getResult()
        println(result)
    }

    @Test
    fun testTiebrek() {
        val game = TennisGame(NormalGame(SetScore(5, 6)))
        val result =
            game
                .playerOneWonABall()
                .playerOneWonABall()
                .playerOneWonABall()
                .playerOneWonABall()
                .playerOneWonABall()
                .getResult()
        println(result)
    }

    @Test
    fun testSetEnd() {
        val game = TennisGame(NormalGame(SetScore(6, 5)))
        val result =
            game
                .playerOneWonABall()
                .playerOneWonABall()
                .playerOneWonABall()
                .playerOneWonABall()
                .getResult()
        println(result)
    }
}

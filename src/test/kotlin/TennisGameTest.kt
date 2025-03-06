class TennisGameTest {
    enum class GameScore {
        ZERO,
        FIFTEEN,
        THIRTY,
        FORTY,
    }

    data class SetResult(
        val playerOne: Int = 0,
        val playerTwo: Int = 0,
    ) {
        fun playerOneWinsGame(): SetResult = SetResult(playerOne + 1, playerTwo)

        fun playerTwoWinsGame(): SetResult = SetResult(playerOne, playerTwo + 1)

        fun isTieBreak(): Boolean = playerTwo == 6 && playerOne == 6

        fun isSetWonByPlayerOne(): Boolean =
            (playerOne == 6 && playerTwo < 5) ||
                (playerOne == 7)

        fun isSetWonByPlayerTwo(): Boolean =
            (playerTwo == 6 && playerOne < 5) ||
                (playerTwo == 7)

        fun isSetCompleted(): Boolean = isSetWonByPlayerTwo() || isSetWonByPlayerOne()
    }

    data class CompletedSets(
        val sets: List<SetResult> = emptyList(),
    ) {
        fun append(currentSet: SetResult): CompletedSets = this.copy(sets = sets + currentSet)
    }

    data class MatchResult(
        val completedSets: CompletedSets = CompletedSets(),
        val currentSet: SetResult = SetResult(0, 0),
        val currentGame: GameResult = GamePointsResult(GameScore.ZERO, GameScore.ZERO),
    )

    sealed class GameResult

    data class GamePointsResult(
        val playerOne: GameScore,
        val playerTwo: GameScore,
    ) : GameResult()

    object Deuce : GameResult()

    data class TieBreak(
        val playerOne: Int,
        val playerTwo: Int,
    ) : GameResult()

    object PlayerOneAdvantage : GameResult()

    object PlayerTwoAdvantage : GameResult()

    object Finish : GameResult()

    abstract class GameState {
        abstract fun playerOneWonABall(): GameState

        abstract fun playerTwoWonABall(): GameState

        abstract fun getResult(): MatchResult
    }

    class GameWonState(
        private val completedSets: CompletedSets,
        private val currentSet: SetResult,
    ) : GameState() {
        private val currentState: GameState =
            when {
                currentSet.isTieBreak() -> TieBreakState(completedSets)
                currentSet.isSetCompleted() -> onSetCompleted(completedSets, currentSet)
                else -> NormalGameState(completedSets, currentSet)
            }

        private fun onSetCompleted(
            completedSets: CompletedSets,
            currentSet: SetResult,
        ): GameState =
            when {
                isMatchCompleted() -> FinishState(completedSets, currentSet)
                else ->
                    NormalGameState(
                        completedSets.append(currentSet),
                        SetResult(0, 0),
                        GameScore.ZERO,
                        GameScore.ZERO,
                    )
            }

        private fun isMatchCompleted(): Boolean {
            val allSets = completedSets.sets + currentSet
            val setsWonByP1 = (allSets).count { it.isSetWonByPlayerOne() }
            val setsWonByP2 = (allSets).count { it.isSetWonByPlayerTwo() }
            return setsWonByP2 == 2 || setsWonByP1 == 2
        }

        override fun playerOneWonABall(): GameState = currentState.playerOneWonABall()

        override fun playerTwoWonABall(): GameState = currentState.playerTwoWonABall()

        override fun getResult(): MatchResult = currentState.getResult()
    }

    class PlayerOneAdvantageState(
        private val completedSets: CompletedSets,
        private val currentSet: SetResult,
    ) : GameState() {
        override fun playerOneWonABall(): GameState = GameWonState(completedSets, currentSet.playerOneWinsGame())

        override fun playerTwoWonABall(): GameState = DeuceState(completedSets, currentSet)

        override fun getResult(): MatchResult =
            MatchResult(
                completedSets,
                currentSet,
                PlayerOneAdvantage,
            )
    }

    class PlayerTwoAdvantageState(
        private val completedSets: CompletedSets,
        private val currentSet: SetResult,
    ) : GameState() {
        override fun playerOneWonABall(): GameState = DeuceState(completedSets, currentSet)

        override fun playerTwoWonABall(): GameState = GameWonState(completedSets, currentSet.playerTwoWinsGame())

        override fun getResult(): MatchResult =
            MatchResult(
                completedSets,
                currentSet,
                PlayerTwoAdvantage,
            )
    }

    class DeuceState(
        private val completedSets: CompletedSets,
        private val currentSet: SetResult,
    ) : GameState() {
        override fun playerOneWonABall(): GameState = PlayerOneAdvantageState(completedSets, currentSet)

        override fun playerTwoWonABall(): GameState = PlayerTwoAdvantageState(completedSets, currentSet)

        override fun getResult(): MatchResult = MatchResult(completedSets, currentSet, Deuce)
    }

    class TieBreakState(
        private val completedSets: CompletedSets,
        private val playerOne: Int = 0,
        private val playerTwo: Int = 0,
    ) : GameState() {
        override fun playerOneWonABall(): GameState =
            if (playerOne >= 6 && playerOne - playerTwo >= 1) {
                GameWonState(completedSets, SetResult(7, 6))
            } else {
                TieBreakState(completedSets, playerOne + 1, playerTwo)
            }

        override fun playerTwoWonABall(): GameState =
            if (playerTwo >= 6 && playerTwo - playerOne >= 1) {
                GameWonState(completedSets, SetResult(6, 7))
            } else {
                TieBreakState(completedSets, playerOne, playerTwo + 1)
            }

        override fun getResult(): MatchResult =
            MatchResult(
                completedSets,
                SetResult(6, 6),
                TieBreak(playerOne, playerTwo),
            )
    }

    class FinishState(
        private val completedSets: CompletedSets,
        private val currentSet: SetResult,
    ) : GameState() {
        override fun playerOneWonABall(): GameState = this

        override fun playerTwoWonABall(): GameState = this

        override fun getResult(): MatchResult = MatchResult(completedSets, currentSet, Finish)
    }

    class NormalGameState(
        private val completedSets: CompletedSets,
        private val currentSet: SetResult,
        private val playerOne: GameScore = GameScore.ZERO,
        private val playerTwo: GameScore = GameScore.ZERO,
    ) : GameState() {
        override fun playerOneWonABall(): GameState =
            when (playerOne) {
                GameScore.ZERO, GameScore.FIFTEEN, GameScore.THIRTY -> {
                    val newScore = GameScore.entries[playerOne.ordinal + 1]
                    if (isDeuce(newScore, playerTwo)) {
                        DeuceState(completedSets, currentSet)
                    } else {
                        NormalGameState(completedSets, currentSet, newScore, playerTwo)
                    }
                }

                GameScore.FORTY -> GameWonState(completedSets, currentSet.playerOneWinsGame())
            }

        private fun isDeuce(
            playerOne: GameScore,
            playerTwo: GameScore,
        ): Boolean = playerOne == GameScore.FORTY && playerTwo == GameScore.FORTY

        override fun playerTwoWonABall(): GameState =
            when (playerTwo) {
                GameScore.ZERO, GameScore.FIFTEEN, GameScore.THIRTY -> {
                    val newScore = GameScore.entries[playerTwo.ordinal + 1]
                    if (isDeuce(playerOne, newScore)) {
                        DeuceState(completedSets, currentSet)
                    } else {
                        NormalGameState(completedSets, currentSet, playerOne, newScore)
                    }
                }

                GameScore.FORTY -> GameWonState(completedSets, currentSet.playerTwoWinsGame())
            }

        override fun getResult(): MatchResult =
            MatchResult(
                completedSets,
                currentSet,
                GamePointsResult(playerOne, playerTwo),
            )
    }

    class TennisGame(
        private val gameState: GameState = NormalGameState(CompletedSets(), SetResult(), GameScore.ZERO, GameScore.ZERO),
    ) {
        fun playerOneWonABall(): TennisGame {
            val newState = gameState.playerOneWonABall()
            return TennisGame(newState)
        }

        fun playerTwoWonABall(): TennisGame {
            val newState = gameState.playerTwoWonABall()
            return TennisGame(newState)
        }

        fun getResult(): MatchResult = this.gameState.getResult()
    }
}

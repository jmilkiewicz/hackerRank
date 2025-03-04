import org.junit.jupiter.api.Test

class TennisGameStateTest {
    class State<S, A>(
        val run: (S) -> Pair<A, S>,
    ) {
        fun <B> map(f: (A) -> B): State<S, B> =
            State { s ->
                val (a, newState) = run(s)
                Pair(f(a), newState)
            }

        fun <B> flatMap(f: (A) -> State<S, B>): State<S, B> =
            State { s ->
                val (a, newState) = run(s)
                f(a).run(newState)
            }
    }

    enum class GemScore {
        ZERO,
        FIFTEEN,
        THIRTY,
        FORTY,
    }

    data class SetScore(
        val playerOne: Int = 0,
        val playerTwo: Int = 0,
    ) {
        fun playerOneWinsGem(): SetScore = SetScore(playerOne + 1, playerTwo)

        fun playerTwoWinsGem(): SetScore = SetScore(playerOne, playerTwo + 1)

        fun isForTieBreak(): Boolean = playerTwo == 6 && playerOne == 6

        fun isSetWonByPlayerOne(): Boolean =
            (playerOne == 6 && playerTwo < 5) ||
                (playerOne == 7)

        fun isSetWonByPlayerTwo(): Boolean =
            (playerTwo == 6 && playerOne < 5) ||
                (playerTwo == 7)

        fun isSetCompleted() = isSetWonByPlayerTwo() || isSetWonByPlayerOne()
    }

    data class CompletedSets(
        val completedSets: List<SetScore> = emptyList(),
    ) {
        val setsWonByPlayerOne = completedSets.count { it.isSetWonByPlayerOne() }
        val setsWonByPlayerTwo = completedSets.count { it.isSetWonByPlayerTwo() }

        fun append(newCompletedSet: SetScore): CompletedSets = this.copy(completedSets + newCompletedSet)

        fun isMatchCompleted(): Boolean = setsWonByPlayerOne == 2 || setsWonByPlayerTwo == 2

        fun getWinner(): String = if (setsWonByPlayerOne == 2) "p1" else "p2"
    }

    data class MatchResult(
        val completedSets: CompletedSets = CompletedSets(),
        val currentSetResult: GameResult = NormalGameResult(GemScore.ZERO, GemScore.ZERO),
    )

    sealed class GameResult(
        open val currentSetScore: SetScore,
    )

    data class TieBreak(
        override val currentSetScore: SetScore,
        val playerOne: Int,
        val playerTwo: Int,
    ) : GameResult(currentSetScore) {
        fun isCompleted(): Boolean = Math.abs(playerOne - playerTwo) > 1 && (playerOne >= 7 || playerTwo >= 7)

        fun playerOneWinsPoint(): TieBreak = this.copy(playerOne = playerOne + 1)

        fun playerTwoWinsPoint(): TieBreak = this.copy(playerTwo = playerTwo + 1)
    }

    data class NormalGameResult(
        val playerOne: GemScore,
        val playerTwo: GemScore,
        override val currentSetScore: SetScore = SetScore(),
    ) : GameResult(currentSetScore)

    data class Deuce(
        override val currentSetScore: SetScore,
    ) : GameResult(currentSetScore)

    data class PlayerOneAdvantage(
        override val currentSetScore: SetScore,
    ) : GameResult(currentSetScore)

    data class PlayerTwoAdvantage(
        override val currentSetScore: SetScore,
    ) : GameResult(currentSetScore)

    // TODO ten finish mi się nie podoba:
    // bierze completedSets tak jak w MatchResult
    // musi przekazać SetScore do GameResult
    class Finish(
        val completedSets: CompletedSets,
    ) : GameResult(completedSets.completedSets.last()) {
        fun getWinner(): String = completedSets.getWinner()
    }

    abstract class GameState {
        abstract fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState>

        abstract fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState>

        fun onGemWon(
            completedSets: CompletedSets,
            currentSetScore: SetScore,
        ): Pair<MatchResult, GameState> =
            when {
                currentSetScore.isForTieBreak() ->
                    MatchResult(
                        completedSets,
                        TieBreak(currentSetScore, 0, 0),
                    ) to TieBreakGame()

                currentSetScore.isSetCompleted() ->
                    onSetWon(
                        completedSets.append(currentSetScore),
                    )

                else ->
                    MatchResult(
                        completedSets,
                        NormalGameResult(GemScore.ZERO, GemScore.ZERO, currentSetScore),
                    ) to NormalGame()
            }

        fun onSetWon(completedSets: CompletedSets): Pair<MatchResult, GameState> =
            when {
                completedSets.isMatchCompleted() ->
                    MatchResult(
                        completedSets = completedSets,
                        Finish(completedSets),
                    ) to FinishState()

                else ->
                    MatchResult(
                        completedSets,
                        NormalGameResult(GemScore.ZERO, GemScore.ZERO, SetScore()),
                    ) to NormalGame()
            }
    }

    class PlayerOneAdvantageGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            onGemWon(gameResult.completedSets, gameResult.currentSetResult.currentSetScore.playerOneWinsGem())

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentSetResult = Deuce(gameResult.currentSetResult.currentSetScore)) to DeuceGame()
    }

    class PlayerTwoAdvantageGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentSetResult = Deuce(gameResult.currentSetResult.currentSetScore)) to DeuceGame()

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            onGemWon(
                gameResult.completedSets,
                gameResult.currentSetResult.currentSetScore.playerTwoWinsGem(),
            )
    }

    class DeuceGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentSetResult = PlayerOneAdvantage(gameResult.currentSetResult.currentSetScore)) to PlayerOneAdvantageGame()

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentSetResult = PlayerTwoAdvantage(gameResult.currentSetResult.currentSetScore)) to PlayerTwoAdvantageGame()
    }

    class FinishState : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> = gameResult to this

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> = gameResult to this
    }

    class NormalGame : GameState() {
        // TODO  p1WonBall i p2WonBall mnóstow duplikacji

        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            when (gameResult.currentSetResult) {
                is NormalGameResult -> {
                    val currentSetResult: NormalGameResult = gameResult.currentSetResult
                    when (currentSetResult.playerOne) {
                        GemScore.ZERO, GemScore.FIFTEEN, GemScore.THIRTY -> {
                            val newScore = GemScore.entries[currentSetResult.playerOne.ordinal + 1]
                            if (isDeuce(newScore, currentSetResult.playerTwo)) {
                                gameResult.copy(currentSetResult = Deuce(gameResult.currentSetResult.currentSetScore)) to DeuceGame()
                            } else {
                                gameResult.copy(
                                    currentSetResult =
                                        NormalGameResult(
                                            newScore,
                                            currentSetResult.playerTwo,
                                            currentSetResult.currentSetScore,
                                        ),
                                ) to this
                            }
                        }

                        GemScore.FORTY ->
                            onGemWon(
                                gameResult.completedSets,
                                currentSetResult.currentSetScore.playerOneWinsGem(),
                            )
                    }
                }

                else -> gameResult to this
            }

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            when (gameResult.currentSetResult) {
                is NormalGameResult -> {
                    val currentSetResult: NormalGameResult = gameResult.currentSetResult
                    when (currentSetResult.playerTwo) {
                        GemScore.ZERO, GemScore.FIFTEEN, GemScore.THIRTY -> {
                            val newScore = GemScore.entries[currentSetResult.playerTwo.ordinal + 1]
                            if (isDeuce(currentSetResult.playerOne, newScore)) {
                                gameResult.copy(currentSetResult = Deuce(gameResult.currentSetResult.currentSetScore)) to DeuceGame()
                            } else {
                                gameResult.copy(
                                    currentSetResult =
                                        NormalGameResult(
                                            currentSetResult.playerOne,
                                            newScore,
                                            currentSetResult.currentSetScore,
                                        ),
                                ) to this
                            }
                        }

                        GemScore.FORTY ->
                            onGemWon(
                                gameResult.completedSets,
                                currentSetResult.currentSetScore.playerTwoWinsGem(),
                            )
                    }
                }

                else -> gameResult to this
            }

        private fun isDeuce(
            playerOne: GemScore,
            playerTwo: GemScore,
        ): Boolean = playerOne == GemScore.FORTY && playerTwo == GemScore.FORTY
    }

    class TieBreakGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            handle(
                gameResult,
                { tieBreak -> tieBreak.playerOneWinsPoint() },
                { setScore -> setScore.playerOneWinsGem() },
            )

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            handle(
                gameResult,
                { tieBreak -> tieBreak.playerTwoWinsPoint() },
                { setScore -> setScore.playerTwoWinsGem() },
            )

        private fun handle(
            gameResult: MatchResult,
            tiesBreak: (TieBreak) -> TieBreak,
            gemWinnerIncreaser: (SetScore) -> SetScore,
        ) = when (gameResult.currentSetResult) {
            is TieBreak -> {
                val updatedTiebreakResult =
                    tiesBreak(gameResult.currentSetResult)

                if (updatedTiebreakResult.isCompleted()) {
                    onGemWon(gameResult.completedSets, gemWinnerIncreaser(gameResult.currentSetResult.currentSetScore))
                } else {
                    gameResult.copy(currentSetResult = updatedTiebreakResult) to this
                }
            }

            else -> gameResult to this
        }
    }

    fun p1WinsABall(gameResult: MatchResult): State<GameState, MatchResult> = State { s -> s.p1WonBall(gameResult) }

    fun p2WinsABall(gameResult: MatchResult): State<GameState, MatchResult> = State { s -> s.p2WonBall(gameResult) }

    fun initGame(gameResult: MatchResult): State<GameState, MatchResult> = State { s -> Pair(gameResult, s) }

    fun program1(gameResult: MatchResult): State<GameState, MatchResult> =
        p1WinsABall(gameResult)
            .flatMap { res1 ->
                p1WinsABall(res1)
            }.flatMap { res2 ->
                p2WinsABall(res2)
            }

    fun program2(gameResult: MatchResult): State<GameState, MatchResult> =
        p2WinsABall(gameResult)
            .flatMap { res1 ->
                p2WinsABall(res1)
            }.flatMap { res2 ->
                p1WinsABall(res2)
            }

    @Test
    fun testSetEnd() {
        val initialState = NormalGame()

        val (result, finalState) =
            program1(
                MatchResult(),
            ).run(initialState)

        val (gameResultStep2, gameStateStep2) = program2(result).run(finalState)

        println("Final Result: $gameResultStep2") // Output: 1
        println("Final State: $gameStateStep2") // Output: 1

        val (gameResultStep1, gameStateStep1) = program1(result).run(finalState)

        println("Final Result: $gameResultStep1") // Output: 1
        println("Final State: $gameStateStep1") // Output: 1
    }

    @Test
    fun testSetEnd3() {
        val initialState = NormalGame()

        val (gameResult, gameState) =
            initGame(MatchResult())
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .run(initialState)

        println("Final Result: $gameResult") // Output: 1
        println("Final State: $gameState") // Output: 1
    }

    @Test
    fun testSetEndTiebreak() {
        val initialState = TieBreakGame()

        val (gameResult, gameState) =
            initGame(MatchResult().copy(currentSetResult = TieBreak(SetScore(6, 6), 5, 5)))
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .run(initialState)

        println("Final Result: $gameResult") // Output: 1
        println("Final State: $gameState") // Output: 1
    }
}

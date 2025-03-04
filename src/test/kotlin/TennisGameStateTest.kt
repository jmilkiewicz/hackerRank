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
        private val setsWonByPlayerOne = completedSets.count { it.isSetWonByPlayerOne() }
        private val setsWonByPlayerTwo = completedSets.count { it.isSetWonByPlayerTwo() }

        fun append(newCompletedSet: SetScore): CompletedSets = this.copy(completedSets + newCompletedSet)

        fun isMatchCompleted(): Boolean = setsWonByPlayerOne == 2 || setsWonByPlayerTwo == 2

        fun getWinner(): String = if (setsWonByPlayerOne == 2) "p1" else "p2"
    }

    data class MatchResult(
        val completedSets: CompletedSets = CompletedSets(),
        val currentSetScore: SetScore = SetScore(),
        val currentGemResult: GameResult = NormalGameResult(GemScore.ZERO, GemScore.ZERO),
    )

    sealed class GameResult

    data class TieBreak(
        val playerOne: Int,
        val playerTwo: Int,
    ) : GameResult() {
        fun isCompleted(): Boolean = Math.abs(playerOne - playerTwo) > 1 && (playerOne >= 7 || playerTwo >= 7)

        fun playerOneWinsPoint(): TieBreak = this.copy(playerOne = playerOne + 1)

        fun playerTwoWinsPoint(): TieBreak = this.copy(playerTwo = playerTwo + 1)
    }

    data class NormalGameResult(
        val playerOne: GemScore,
        val playerTwo: GemScore,
    ) : GameResult()

    object Deuce : GameResult()

    object PlayerOneAdvantage : GameResult()

    object PlayerTwoAdvantage : GameResult()

    object Finish : GameResult()

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
                        currentSetScore,
                        TieBreak(0, 0),
                    ) to TieBreakGame()

                currentSetScore.isSetCompleted() ->
                    onSetWon(
                        completedSets.append(currentSetScore),
                    )

                else ->
                    MatchResult(
                        completedSets,
                        currentSetScore,
                        NormalGameResult(GemScore.ZERO, GemScore.ZERO),
                    ) to NormalGame()
            }

        fun onSetWon(completedSets: CompletedSets): Pair<MatchResult, GameState> =
            when {
                completedSets.isMatchCompleted() ->
                    MatchResult(
                        completedSets = completedSets,
                        // TODO to jest trochę bezsensu
                        currentSetScore = SetScore(0, 0),
                        Finish,
                    ) to FinishState()

                else ->
                    MatchResult(
                        completedSets,
                        SetScore(),
                        NormalGameResult(GemScore.ZERO, GemScore.ZERO),
                    ) to NormalGame()
            }
    }

    class PlayerOneAdvantageGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            onGemWon(gameResult.completedSets, gameResult.currentSetScore.playerOneWinsGem())

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentGemResult = Deuce) to DeuceGame()
    }

    class PlayerTwoAdvantageGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentGemResult = Deuce) to DeuceGame()

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            onGemWon(
                gameResult.completedSets,
                gameResult.currentSetScore.playerTwoWinsGem(),
            )
    }

    class DeuceGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentGemResult = PlayerOneAdvantage) to PlayerOneAdvantageGame()

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentGemResult = PlayerTwoAdvantage) to PlayerTwoAdvantageGame()
    }

    class FinishState : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> = gameResult to this

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> = gameResult to this
    }

    class NormalGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            aaa(
                gameResult,
                { normalGameResult -> normalGameResult.playerOne },
                { normalGameResult -> normalGameResult.playerTwo },
                { winnerNewResult, looserResult -> NormalGameResult(winnerNewResult, looserResult) },
                { setScore -> setScore.playerOneWinsGem() },
            )

        // TODO to jest b skomplikowane. Problemem jest że muismy wskazać winner i (explicite) loosera oraz powtórzyć to
        // dla newGemResult(bo to potrzbuje kolejności: player1 ,player2)  oraz increaseGem
        private fun aaa(
            gameResult: MatchResult,
            ballWinnerCurrentResult: (NormalGameResult) -> GemScore,
            ballLooserCurrentResult: (NormalGameResult) -> GemScore,
            newGemResult: (winnerResult: GemScore, looserResult: GemScore) -> NormalGameResult,
            increaseGem: (SetScore) -> SetScore,
        ) = when (gameResult.currentGemResult) {
            is NormalGameResult -> {
                val currentSetResult: NormalGameResult = gameResult.currentGemResult
                when (val ballWinnerCurrentResult1 = ballWinnerCurrentResult(currentSetResult)) {
                    GemScore.ZERO, GemScore.FIFTEEN, GemScore.THIRTY -> {
                        val newScore = GemScore.entries[ballWinnerCurrentResult1.ordinal + 1]
                        if (isDeuce(newScore, ballLooserCurrentResult(currentSetResult))) {
                            gameResult.copy(currentGemResult = Deuce) to DeuceGame()
                        } else {
                            gameResult.copy(
                                currentGemResult =
                                    newGemResult(newScore, ballLooserCurrentResult(currentSetResult)),
                            ) to this
                        }
                    }

                    GemScore.FORTY ->
                        onGemWon(
                            gameResult.completedSets,
                            increaseGem(gameResult.currentSetScore),
                        )
                }
            }

            else -> gameResult to this
        }

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            aaa(
                gameResult,
                { normalGameResult -> normalGameResult.playerTwo },
                { normalGameResult -> normalGameResult.playerOne },
                { winnerNewResult, looserResult -> NormalGameResult(looserResult, winnerNewResult) },
                { setScore -> setScore.playerTwoWinsGem() },
            )

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
        ) = when (gameResult.currentGemResult) {
            is TieBreak -> {
                val updatedTiebreakResult =
                    tiesBreak(gameResult.currentGemResult)

                if (updatedTiebreakResult.isCompleted()) {
                    onGemWon(gameResult.completedSets, gemWinnerIncreaser(gameResult.currentSetScore))
                } else {
                    gameResult.copy(currentGemResult = updatedTiebreakResult) to this
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
            // TODO trzeba ustawić gemy na 6:6 a później wynik w Tiebreak - czy można to jakoś wymusić?
            initGame(MatchResult().copy(currentSetScore = SetScore(6, 6), currentGemResult = TieBreak(5, 5)))
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .run(initialState)

        println("Final Result: $gameResult") // Output: 1
        println("Final State: $gameState") // Output: 1
    }
}

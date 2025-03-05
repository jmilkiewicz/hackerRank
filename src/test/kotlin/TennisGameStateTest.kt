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

    enum class GameScore {
        ZERO,
        FIFTEEN,
        THIRTY,
        FORTY,
    }

    data class SetScore(
        val playerOne: Int = 0,
        val playerTwo: Int = 0,
    ) {
        fun playerOneWinsGame(): SetScore = SetScore(playerOne + 1, playerTwo)

        fun playerTwoWinsGame(): SetScore = SetScore(playerOne, playerTwo + 1)

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
        val currentGameResult: GameResult = NormalGameResult(GameScore.ZERO, GameScore.ZERO),
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
        val playerOne: GameScore,
        val playerTwo: GameScore,
    ) : GameResult()

    object Deuce : GameResult()

    object PlayerOneAdvantage : GameResult()

    object PlayerTwoAdvantage : GameResult()

    object Finish : GameResult()

    abstract class GameState {
        abstract fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState>

        abstract fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState>

        fun onGameWon(
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
                        NormalGameResult(GameScore.ZERO, GameScore.ZERO),
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
                        NormalGameResult(GameScore.ZERO, GameScore.ZERO),
                    ) to NormalGame()
            }
    }

    class PlayerOneAdvantageGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            onGameWon(gameResult.completedSets, gameResult.currentSetScore.playerOneWinsGame())

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentGameResult = Deuce) to DeuceGame()
    }

    class PlayerTwoAdvantageGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentGameResult = Deuce) to DeuceGame()

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            onGameWon(
                gameResult.completedSets,
                gameResult.currentSetScore.playerTwoWinsGame(),
            )
    }

    class DeuceGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentGameResult = PlayerOneAdvantage) to PlayerOneAdvantageGame()

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            gameResult.copy(currentGameResult = PlayerTwoAdvantage) to PlayerTwoAdvantageGame()
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
                { setScore -> setScore.playerOneWinsGame() },
            )

        // TODO to jest b skomplikowane. Problemem jest że muismy wskazać winner i (explicite) loosera oraz powtórzyć to
        // dla newGemResult(bo to potrzbuje kolejności: player1 ,player2)  oraz increaseGem
        private fun aaa(
            gameResult: MatchResult,
            ballWinnerCurrentResult: (NormalGameResult) -> GameScore,
            ballLooserCurrentResult: (NormalGameResult) -> GameScore,
            newGameResult: (winnerResult: GameScore, looserResult: GameScore) -> NormalGameResult,
            increaseGame: (SetScore) -> SetScore,
        ) = when (gameResult.currentGameResult) {
            is NormalGameResult -> {
                val currentSetResult: NormalGameResult = gameResult.currentGameResult
                when (val ballWinnerCurrentResult1 = ballWinnerCurrentResult(currentSetResult)) {
                    GameScore.ZERO, GameScore.FIFTEEN, GameScore.THIRTY -> {
                        val newScore = GameScore.entries[ballWinnerCurrentResult1.ordinal + 1]
                        if (isDeuce(newScore, ballLooserCurrentResult(currentSetResult))) {
                            gameResult.copy(currentGameResult = Deuce) to DeuceGame()
                        } else {
                            gameResult.copy(
                                currentGameResult =
                                    newGameResult(newScore, ballLooserCurrentResult(currentSetResult)),
                            ) to this
                        }
                    }

                    GameScore.FORTY ->
                        onGameWon(
                            gameResult.completedSets,
                            increaseGame(gameResult.currentSetScore),
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
                { setScore -> setScore.playerTwoWinsGame() },
            )

        private fun isDeuce(
            playerOne: GameScore,
            playerTwo: GameScore,
        ): Boolean = playerOne == GameScore.FORTY && playerTwo == GameScore.FORTY
    }

    class TieBreakGame : GameState() {
        override fun p1WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            handle(
                gameResult,
                { tieBreak -> tieBreak.playerOneWinsPoint() },
                { setScore -> setScore.playerOneWinsGame() },
            )

        override fun p2WonBall(gameResult: MatchResult): Pair<MatchResult, GameState> =
            handle(
                gameResult,
                { tieBreak -> tieBreak.playerTwoWinsPoint() },
                { setScore -> setScore.playerTwoWinsGame() },
            )

        private fun handle(
            gameResult: MatchResult,
            tiesBreak: (TieBreak) -> TieBreak,
            gameWinnerIncrement: (SetScore) -> SetScore,
        ) = when (gameResult.currentGameResult) {
            is TieBreak -> {
                val updatedTiebreakResult =
                    tiesBreak(gameResult.currentGameResult)

                if (updatedTiebreakResult.isCompleted()) {
                    onGameWon(gameResult.completedSets, gameWinnerIncrement(gameResult.currentSetScore))
                } else {
                    gameResult.copy(currentGameResult = updatedTiebreakResult) to this
                }
            }

            else -> gameResult to this
        }
    }

    fun p1WinsABall(gameResult: MatchResult): State<GameState, MatchResult> = State { s -> s.p1WonBall(gameResult) }

    fun p2WinsABall(gameResult: MatchResult): State<GameState, MatchResult> = State { s -> s.p2WonBall(gameResult) }

    fun initGame(gameResult: MatchResult): State<GameState, MatchResult> = State { s -> Pair(gameResult, s) }

    fun startBrandNewGame(): State<GameState, MatchResult> =
        State { _ ->
            Pair(
                MatchResult(
                    completedSets = CompletedSets(),
                    currentSetScore = SetScore(0, 0),
                    NormalGameResult(GameScore.ZERO, GameScore.ZERO),
                ),
                NormalGame(),
            )
        }

    fun initTieBreakGame(
        completedSets: CompletedSets = CompletedSets(),
        tieBreak: TieBreak = TieBreak(0, 0),
    ): State<GameState, MatchResult> =
        State { _ ->
            MatchResult(
                completedSets,
                currentSetScore = SetScore(6, 6),
                currentGameResult = tieBreak,
            ) to TieBreakGame()
        }

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
    fun startBrandNewGameTest() {
        val (gameResult, gameState) =
            startBrandNewGame()
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
//                ten FinishState() będzie ignorowany...
                .run(FinishState())

        println("Final Result: $gameResult") // Output: 1
        println("Final State: $gameState") // Output: 1
    }

    @Test
    fun initTiebreak() {
        val willBeIgnoredAtAll = NormalGame()

        val (gameResult, gameState) =
            initTieBreakGame()
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .run(willBeIgnoredAtAll)

        println("Final Result: $gameResult") // Output: 1
        println("Final State: $gameState") // Output: 1
    }

    @Test
    fun testSetEndTiebreak() {
        val initialState = TieBreakGame()

        val (gameResult, gameState) =
            // TODO trzeba ustawić gemy na 6:6 a później wynik w Tiebreak - czy można to jakoś wymusić?
            initGame(MatchResult().copy(currentSetScore = SetScore(6, 6), currentGameResult = TieBreak(5, 5)))
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .run(initialState)

        println("Final Result: $gameResult") // Output: 1
        println("Final State: $gameState") // Output: 1

        // ODPOWIEDZ CHAT GPT
        /*
        This issue is not a problem with your design but rather a fundamental characteristic (or limitation) of the State Monad when used in scenarios like yours.

            Why Does This Happen?
            The State Monad is designed to separate state (S) from computation results (A).

            This means you must always explicitly keep S and A in sync, which is not enforced by the monad itself.
            If you forget to set both correctly (e.g., forgetting to update currentGameResult when setting TieBreakGame), the application state becomes invalid.
            Is This a Design Problem or Just a Monad Quirk?
            🔹 Not a problem with your design → You identified the issue correctly and solved it with the State Pattern.
            🔹 A fundamental characteristic of the State Monad → Because it separates S (state) from A (result), it does not inherently guarantee consistency between them.

            Why Does State Pattern Work Better for You?
            In the State Pattern, GameState directly encodes the match state (e.g., TieBreakState includes set scores).
            This naturally ensures consistency because state is not separated from game logic.
            The State Monad, however, forces this separation, which requires extra effort to keep things correct.
            Bottom Line
            You are not doing anything wrong. The State Monad just does not enforce the guarantees you need—so the State Pattern is a better fit for your case.

            If you want to stick with a monadic approach, you need additional structures (like wrapping GameState and MatchResult together), but at that point, it’s essentially mimicking the State Pattern anyway. 😃
         */
    }
}

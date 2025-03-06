import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not
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

    fun initDeuce(
        completedSets: CompletedSets = CompletedSets(),
        currentSetScore: SetScore = SetScore(0, 0),
    ): State<GameState, MatchResult> =
        State { _ ->
            MatchResult(
                completedSets,
                currentSetScore = currentSetScore,
                currentGameResult = Deuce,
            ) to DeuceGame()
        }

    @Test
    fun forkTest() {
        val initialState = NormalGame()

        val initialMatchResult = MatchResult()
        val (intermediateResult, intermediateState) =
            initGame(
                initialMatchResult,
            ).flatMap { p1WinsABall(it) }
                .flatMap { p1WinsABall(it) }
                .flatMap { p1WinsABall(it) }
                .run(initialState)

        val (forkResult1, forkState1) =
            p1WinsABall(intermediateResult).run(intermediateState)

        assertThat(
            forkResult1,
            equalTo(
                initialMatchResult.copy(
                    currentSetScore = SetScore(1, 0),
                    currentGameResult = NormalGameResult(GameScore.ZERO, GameScore.ZERO),
                ),
            ),
        )

        assertThat(
            forkState1,
            instanceOf(NormalGame::class.java),
        )

        val (forkResult2, forkState2) =
            p2WinsABall(intermediateResult)
                .flatMap { p2WinsABall(it) }
                .run(intermediateState)

        assertThat(
            forkResult2,
            equalTo(
                initialMatchResult.copy(
                    currentGameResult =
                        NormalGameResult(
                            GameScore.FORTY,
                            GameScore.THIRTY,
                        ),
                ),
            ),
        )

        assertThat(
            forkState2,
            instanceOf(NormalGame::class.java),
        )

        assertThat(forkResult1, not(equalTo(forkResult2)))
    }

    @Test
    fun deuceTest() {
        val willBeIgnored = NormalGame()

        val completedSets = CompletedSets()
        val currentSetScore = SetScore(0, 0)
        val (matchResult, gameState) =
            initDeuce(completedSets, currentSetScore)
                .flatMap { p1WinsABall(it) }
                .run(willBeIgnored)

        assertThat(
            matchResult,
            equalTo(
                MatchResult(
                    completedSets = completedSets,
                    currentSetScore = currentSetScore,
                    currentGameResult = PlayerOneAdvantage,
                ),
            ),
        )

        assertThat(
            gameState,
            instanceOf(PlayerOneAdvantageGame::class.java),
        )
    }

    @Test
    fun startBrandNewGameTest() {
        val (matchResult, gameState) =
            startBrandNewGame()
                .flatMap { res -> p1WinsABall(res) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
                .flatMap { r2 -> p2WinsABall(r2) }
//                this FinishState() will be ignored
                .run(FinishState())

        assertThat(
            matchResult,
            equalTo(
                MatchResult(
                    completedSets = CompletedSets(emptyList()),
                    currentSetScore = SetScore(0, 1),
                    currentGameResult =
                        NormalGameResult(
                            GameScore.ZERO,
                            GameScore.ZERO,
                        ),
                ),
            ),
        )
    }

    @Test
    fun initTiebreakTest() {
        val willBeIgnoredAtAll = NormalGame()

        val completedSets = CompletedSets()
        val (matchResult, gameState) =
            initTieBreakGame(completedSets = completedSets)
                .flatMap { p1WinsABall(it) }
                .flatMap { p1WinsABall(it) }
                .flatMap { p2WinsABall(it) }
                .flatMap { p2WinsABall(it) }
                .flatMap { p2WinsABall(it) }
                .flatMap { p2WinsABall(it) }
                .run(willBeIgnoredAtAll)

        assertThat(
            matchResult,
            equalTo(
                MatchResult(
                    completedSets = completedSets,
                    currentSetScore = SetScore(6, 6),
                    currentGameResult =
                        TieBreak(2, 4),
                ),
            ),
        )

        assertThat(
            gameState,
            instanceOf(
                TieBreakGame::class.java,
            ),
        )
    }

    @Test
    fun testSetEndTiebreak() {
        val tieBreak = TieBreak(5, 5)

        // TODO Very error prone: in current set we need to set 6:6, set TieBreak for a current game, and set initial State to TieBreakGame()
        initGame(MatchResult().copy(currentSetScore = SetScore(6, 6), currentGameResult = tieBreak))
            .flatMap { p1WinsABall(it) }
            .flatMap { p2WinsABall(it) }
            .flatMap { p2WinsABall(it) }
            .flatMap { p2WinsABall(it) }
            .run(TieBreakGame())

        val (matchResult, gameState) =
            initTieBreakGame(completedSets = CompletedSets(), tieBreak = tieBreak)
                .flatMap { p1WinsABall(it) }
                .flatMap { p2WinsABall(it) }
                .flatMap { p2WinsABall(it) }
                .flatMap { p2WinsABall(it) }
                .run(FinishState())

        assertThat(
            matchResult,
            equalTo(
                MatchResult(
                    completedSets = CompletedSets().append(SetScore(6, 7)),
                    currentSetScore = SetScore(0, 0),
                    currentGameResult =
                        NormalGameResult(GameScore.ZERO, GameScore.ZERO),
                ),
            ),
        )

        assertThat(
            gameState,
            instanceOf(
                NormalGame::class.java,
            ),
        )
    }
}

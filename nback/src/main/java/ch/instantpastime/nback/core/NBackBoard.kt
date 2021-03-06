package ch.instantpastime.nback.core

class NBackBoard(nbLetters: Int, nBackLevel: Int, val uiControl: INBackController) : INBackBoard {

    companion object {
        const val DEFAULT_NB_TRIALS: Int = 20
    }

    private val play: NBackPlay = NBackPlay(this)
    private var gameRun: NBackRun = NBackRun(nbLetters = nbLetters, nBackLevel = nBackLevel)
    private val score: NBackScore = NBackScore()
    var lastDraw: NBackTrial? = null
        private set

    /**
     * True when the user says it is the same location, otherwise false.
     */
    var mAnswerSameLocation: Boolean = false

    /**
     * True when the user says it is the same letter, otherwise false.
     */
    var mAnswerSameLetter: Boolean = false

    var goal: Int = DEFAULT_NB_TRIALS
        private set

    private val actualGoal: Int
        get() = goal + Level

    /**
     * Number of trials that have been drawn (i.e. generated and shown to the user).
     */
    var drawCount: Int = 0
        private set

    /**
     * Number of trials that have been shown to the user and for which they
     * hade the possibility to answer.
     */
    val answerableCount: Int
        get() {
            val q = drawCount - Level
            return if (q > 0) q else 0
        }

    /**
     * Number of trials that have been corrected.
     */
    val checkCount: Int
        get() = score.TotalCount

    /**
     * Number of trials that have been checked as correct.
     */
    val CorrectCount: Int
        get() = score.CorrectCount

    val Level: Int
        get() = gameRun._level

    val expectAnswer: Boolean
        get() = drawCount > Level

    val instantScore: Int
        get() = (NBackScore.SCORE_MAX * score.CorrectCount) / goal

    fun toggleLocationAnswer(): Boolean {
        val it = !mAnswerSameLocation
        mAnswerSameLocation = it
        return it
    }

    fun toggleLetterAnswer(): Boolean {
        return (!mAnswerSameLetter).also {
            mAnswerSameLetter = it
        }
    }

    fun reset() {
        score.reset()
        gameRun.reset()
        play.raiseReset()
        lastDraw = null
        drawCount = 0
        mAnswerSameLocation = false
        mAnswerSameLetter = false
    }

    fun getNextTrial(): NBackTrial {
        return gameRun.getNextTrial().also {
            drawCount += 1
            lastDraw = it
        }
    }

    fun checkCurrentAnswer(): Pair<NBackScore.Correctness?, NBackScore.Correctness?> {
        val correctnesses = checkAnswer(
            locationAnswer = mAnswerSameLocation,
            letterAnswer = mAnswerSameLetter
        )
        // Reset user's answers.
        mAnswerSameLocation = false
        mAnswerSameLetter = false
        return correctnesses
    }

    private fun checkAnswer(locationAnswer: Boolean, letterAnswer: Boolean)
            : Pair<NBackScore.Correctness?, NBackScore.Correctness?> {
        val locationCorrectness =
            NBackScore.getCorrectness(
                answer = locationAnswer,
                // True when it is actually the same location, false when different,
                // null when there isn't enough elements to compare.
                actual = lastDraw?.location?.isSame
            )
        val letterCorrectness =
            NBackScore.getCorrectness(
                answer = letterAnswer,
                // True when it is actually the same letter, false when different,
                // null when there isn't enough elements to compare.
                actual = lastDraw?.symbol?.isSame
            )
        score.updateScore(locationCorrectness)
        score.updateScore(letterCorrectness)
        return Pair(locationCorrectness, letterCorrectness)
    }

    fun drawNext() {
        if (drawCount < actualGoal) {
            play.raiseDraw()
        } else {
            play.raiseTotalReached()
        }
    }

    fun tick() {
        if (drawCount <= Level) {
            play.raiseDraw()
        } else {
            play.raiseTick()
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onEnterBlank() {
    }

    /**
     * {@inheritDoc}
     */
    override fun onEnterDrawing() {
        val last = lastDraw
        val next = getNextTrial()
        play.raiseDrawn(last = last, next = next)
    }

    /**
     * {@inheritDoc}
     */
    override fun onEnterWaiting(last: NBackTrial?, next: NBackTrial) {
        uiControl.onNextTrial(last = last, next = next)
    }

    /**
     * {@inheritDoc}
     */
    override fun onEnterCorrecting() {
        val (locationCorrectness, letterCorrectness) = checkCurrentAnswer()
        uiControl.onCorrectResult(locationCorrectness, letterCorrectness)
    }

    /**
     * {@inheritDoc}
     */
    override fun onEnterFinished() {
        uiControl.onFinished(CorrectCount, checkCount)
    }
}

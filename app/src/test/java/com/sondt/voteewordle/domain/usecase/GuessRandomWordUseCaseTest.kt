package com.sondt.voteewordle.domain.usecase

import com.sondt.voteewordle.data.local.entity.WordEntity
import com.sondt.voteewordle.domain.entity.GuessResult
import com.sondt.voteewordle.domain.entity.GuessResultType
import com.sondt.voteewordle.domain.repository.WordDictRepository
import com.sondt.voteewordle.domain.repository.WordleRepository
import io.reactivex.Maybe
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn

@RunWith(MockitoJUnitRunner::class)
class GuessRandomWordUseCaseTest {
    @Mock
    lateinit var wordleRepository: WordleRepository
    @Mock
    lateinit var wordDictRepository: WordDictRepository

    /**
     * a        b        c        d        e         <- row 1
     * correct  present  absent   absent   absent
     *
     * glob: \[a] <- 1st letter, must be A
     *       [^bcde] <- 2nd letter, must not be B (present), C D E (absent)
     *       [^cde] <- 3rd letter, must not be C D E (can be B)
     *       [^cde] <- 4th letter, must not be C D E (can be B)
     *       [^cde] <- 5th letter, must not be C D E (can be B)
     *
     * afghz <- db query 1, should not be selected because not contains 'b' value
     * aijbb <- db query 2, should not be selected because having duplicated letter (consider next value)
     * aijkb <- db query 3, expect to be selected
     * aijlb <- db query 4, should not be selected because of selecting first matching value
     */
    @Test
    fun run_GivenMultipleWords_ReturnsCorrect() {
        val seed = 12345
        val lastResult: List<List<GuessResult>> = listOf(listOf(
            GuessResult(0, "a", GuessResultType.CORRECT),
            GuessResult(1, "b", GuessResultType.PRESENT),
            GuessResult(2, "c", GuessResultType.ABSENT),
            GuessResult(3, "d", GuessResultType.ABSENT),
            GuessResult(4, "e", GuessResultType.ABSENT),
        ))
        val dictWords = listOf(
            WordEntity("afghz"),
            WordEntity("aijbb"),
            WordEntity("aijkb"),
            WordEntity("aijlb"),
        )
        val nextResult = listOf(
            GuessResult(0, "a", GuessResultType.CORRECT),
            GuessResult(1, "i", GuessResultType.CORRECT),
            GuessResult(2, "j", GuessResultType.CORRECT),
            GuessResult(3, "k", GuessResultType.CORRECT),
            GuessResult(4, "b", GuessResultType.CORRECT),
        )
        val expectedGlob = "[a][^bcde][^cde][^cde][^cde]"

        doReturn(Maybe.just(dictWords)).`when`(wordDictRepository).searchWords(expectedGlob)
        doReturn(Maybe.just(nextResult)).`when`(wordleRepository).guessRandom("aijkb", 5, seed)


        GuessRandomWordUseCase(wordleRepository, wordDictRepository).invoke(seed, lastResult)
            .test()
            .assertValue(nextResult)
            .dispose()
    }

    /**
     * a        b        c        d        e         <- row 1
     * correct  present  present  absent   absent
     *
     * glob: \[a] <- 1st letter, must be A
     *       [^bde] <- 2nd letter, must not be B (present), D E (absent), (can be C)
     *       [^cde] <- 3rd letter, must not be C (present), D E (absent), (can be B)
     *       [^de] <- 4th letter, must not be D E (can be B C)
     *       [^de] <- 5th letter, must not be D E (can be B C)
     *
     * axyzb <- db query 1, should not be selected because not contains 'c' value
     * afghc <- db query 2, should not be selected because not contains 'b' value
     */
    @Test
    fun run_GivenNoAnyMatchedWords_ReturnsNoValue() {
        val seed = 12345
        val lastResult: List<List<GuessResult>> = listOf(listOf(
            GuessResult(0, "a", GuessResultType.CORRECT),
            GuessResult(1, "b", GuessResultType.PRESENT),
            GuessResult(2, "c", GuessResultType.PRESENT),
            GuessResult(3, "d", GuessResultType.ABSENT),
            GuessResult(4, "e", GuessResultType.ABSENT),
        ))
        val dictWords = listOf(
            WordEntity("axyzb"),
            WordEntity("afghc"),
        )
        val expectedGlob = "[a][^bde][^cde][^de][^de]"

        doReturn(Maybe.just(dictWords)).`when`(wordDictRepository).searchWords(expectedGlob)


        GuessRandomWordUseCase(wordleRepository, wordDictRepository).invoke(seed, lastResult)
            .test()
            .assertNoValues()
            .assertComplete()
            .dispose()
    }
}

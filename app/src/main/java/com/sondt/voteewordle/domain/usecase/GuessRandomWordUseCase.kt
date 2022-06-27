package com.sondt.voteewordle.domain.usecase

import com.sondt.voteewordle.domain.entity.GuessResult
import com.sondt.voteewordle.domain.entity.GuessResultType
import com.sondt.voteewordle.domain.repository.WordDictRepository
import com.sondt.voteewordle.domain.repository.WordleRepository
import io.reactivex.Maybe

class GuessRandomWordUseCase(
    private val wordleRepository: WordleRepository,
    private val wordDictRepository: WordDictRepository,
) {
    operator fun invoke(seed: Int, lastResults: List<List<GuessResult>>): Maybe<List<GuessResult>> {
        // glob pattern for every single letter of word
        val pattern1 = getRegexPatternOfLetter(lastResults, 0)
        val pattern2 = getRegexPatternOfLetter(lastResults, 1)
        val pattern3 = getRegexPatternOfLetter(lastResults, 2)
        val pattern4 = getRegexPatternOfLetter(lastResults, 3)
        val pattern5 = getRegexPatternOfLetter(lastResults, 4)
        val glob = "$pattern1$pattern2$pattern3$pattern4$pattern5"
        print("glob: $glob")

        // use above pattern to query from SQLite
        return wordDictRepository.searchWords(glob)
            .flatMap { list ->
                val potentialWords = list.map { it.word }
                print("list: $potentialWords")

                val presentLetters = lastResults.flatten().filter { it.result == GuessResultType.PRESENT }.map { it.guess }
                // filter words which contains Present letter and have as few duplicates as possible
                val word = getFirstWord(potentialWords, 0, presentLetters)
                    ?: getFirstWord(potentialWords, 1, presentLetters)
                    ?: getFirstWord(potentialWords, 2, presentLetters)
                    ?: getFirstWord(potentialWords, 3, presentLetters)
                    ?: return@flatMap Maybe.empty()
                print("word: $word")

                wordleRepository.guessRandom(word, 5, seed)
            }
    }

    private fun getRegexPatternOfLetter(allResults: List<List<GuessResult>>, letterIndex: Int): String {
        val letterResults = allResults.map { it[letterIndex] }
        val pattern = letterResults.firstOrNull { it.result == GuessResultType.CORRECT }?.guess ?: run {
            "^" + allResults.map {
                    it.filterIndexed { index, guessResult ->
                        guessResult.result == GuessResultType.ABSENT || (guessResult.result == GuessResultType.PRESENT && index == letterIndex)
                    }
                }.flatten().joinToString(separator = "") { it.guess }
        }
        return "[$pattern]"
    }

    private fun getFirstWord(potentialWords: List<String>, acceptNumberOfDuplication: Int = 0, presentLetters: List<String>): String? {
        potentialWords.filter { word ->
            presentLetters.all { word.contains(it) }
        }.forEach { word ->
            // group by letter, sum the letters and subtract 1 to remove those that are themselves
            val numOfDup = word.groupingBy { it }.eachCount().values.sumOf { it - 1 }
            if (numOfDup == acceptNumberOfDuplication) {
                return word
            }
        }
        return null
    }
}

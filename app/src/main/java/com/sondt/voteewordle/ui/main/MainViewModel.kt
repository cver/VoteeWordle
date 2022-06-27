package com.sondt.voteewordle.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sondt.voteewordle.domain.entity.GuessResult
import com.sondt.voteewordle.domain.entity.GuessResultType
import com.sondt.voteewordle.domain.usecase.*
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(
    private val guessRandomFirstWordUseCase: GuessRandomFirstWordUseCase,
    private val guessRandomWordUseCase: GuessRandomWordUseCase,
    private val syncWordDictionaryUseCase: SyncWordDictionaryUseCase,
    private val countWordDictionaryUseCase: CountWordDictionaryUseCase,
): ViewModel() {
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val wordDictLength: MutableLiveData<Int> = MutableLiveData(0)
    val guessResult: MutableLiveData<GuessResultsEvent> = MutableLiveData(GuessResultsEvent.None())
    private val compositeDisposable = CompositeDisposable()

    fun syncIfNeed() {
        // if word dict is empty, load word dict (https://pypi.org/project/english-words/)
        countWordDictionaryUseCase()
            .flatMap {
                if (it == 0) {
                    syncWordDictionaryUseCase()
                        .andThen(countWordDictionaryUseCase())
                } else {
                    Maybe.just(it)
                }
            }
            .doOnSubscribe {
                isLoading.postValue(true)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                wordDictLength.postValue(it)
                isLoading.postValue(false)
            }, {
                it.printStackTrace()
                isLoading.postValue(false)
            })
            .also { compositeDisposable.add(it) }
    }

    fun sync() {
        syncWordDictionaryUseCase()
            .andThen(countWordDictionaryUseCase())
            .doOnSubscribe {
                isLoading.postValue(true)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                wordDictLength.postValue(it)
                isLoading.postValue(false)
            }, {
                it.printStackTrace()
                isLoading.postValue(false)
            })
            .also { compositeDisposable.add(it) }
    }

    fun clear() {
        guessResult.postValue(GuessResultsEvent.None())
    }

    fun guess(seed: Int, startWord: String) {
        val operator = guessResult.value?.takeIf { it.data.isNotEmpty() }?.let { results ->

            // reset if already won
            if (results.data.last().all { it.result == GuessResultType.CORRECT }) {
                guessResult.postValue(GuessResultsEvent.None())
                guessRandomFirstWordUseCase(seed, startWord)
            } else {
                guessRandomWordUseCase(seed, results.data)
            }

        } ?: run {
            guessRandomFirstWordUseCase(seed, startWord)
        }

        operator
            .doOnSubscribe {
                isLoading.postValue(true)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val value = guessResult.value?.data?.toMutableList() ?: mutableListOf()
                value.add(it)
                guessResult.postValue(GuessResultsEvent.Success(value))
                isLoading.postValue(false)
            }, {
                it.printStackTrace()
                isLoading.postValue(false)
                val currentData = guessResult.value?.data ?: emptyList()
                if (it.message == "invalid_word") {
                    guessResult.postValue(GuessResultsEvent.InvalidStartWord(currentData))
                } else {
                    guessResult.postValue(GuessResultsEvent.Failed(currentData))
                }
            }, {
                val currentData = guessResult.value?.data ?: emptyList()
                guessResult.postValue(GuessResultsEvent.NotMatch(currentData))
                isLoading.postValue(false)
            })
            .also { compositeDisposable.add(it) }
    }

    sealed class GuessResultsEvent(val data: List<List<GuessResult>>) {
        class None(data: List<List<GuessResult>> = emptyList()): GuessResultsEvent(data)
        class Success(data: List<List<GuessResult>> = emptyList()): GuessResultsEvent(data)
        class InvalidStartWord(data: List<List<GuessResult>> = emptyList()): GuessResultsEvent(data)
        class Failed(data: List<List<GuessResult>> = emptyList()): GuessResultsEvent(data)
        class NotMatch(data: List<List<GuessResult>> = emptyList()): GuessResultsEvent(data)
    }
}

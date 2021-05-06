package com.touchsides.wordreader.ui.wordreader

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.touchsides.wordreader.entities.FileStats
import com.touchsides.wordreader.entities.ScrabbleLetterScores
import java.io.*

class WordsReaderViewModel @ViewModelInject constructor(
) : ViewModel() {
    val fileStats: FileStats = FileStats()
    private var _doneReading: MutableLiveData<Boolean> = MutableLiveData(false)
    val doneReading: LiveData<Boolean> = _doneReading
    private var _doneComputing: MutableLiveData<Boolean> = MutableLiveData(false)
    val doneComputing: LiveData<Boolean> = _doneComputing
    private var fileText: MutableLiveData<String>? = null
    private val wordsMap: HashMap<String, Int> = HashMap()

    fun readFromFile(context: Context, fileName: String) {
        try {
            val inputStream: InputStream? = context.assets.open(fileName)
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String?
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }

                val str = stringBuilder.toString()

                for (word in str.split(" ")) {
                    var count = 1
                    if (wordsMap[word] != null) {
                        count += wordsMap[word] ?: 0
                    }
                    wordsMap[word] = count
                }

                inputStream.close()
                fileText?.postValue(stringBuilder.toString())
                _doneReading.value = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun computeMostFrequentWord() {
        val result = wordsMap.toList().sortedByDescending { (_, value) -> value}.toMap()
        val multipleEntriesOfMostFrequentWord = result.filter {it.key.isNotBlank() && it.value == result.entries.first().value}
        val freq = StringBuilder()
        for ((key) in multipleEntriesOfMostFrequentWord) {
            freq.append("[${key}]")
        }
        fileStats.mostFrequentWord = freq.toString()
        fileStats.mostFrequentWordCount = result.entries.first().value!!
    }

    fun computeMostFrequent7CharacterWord() {
        val result = wordsMap.entries.filter { it.key.length != 7 && it.key.isNotBlank()}
        if (result.isNotEmpty()){
            result.toList().sortedByDescending { (_, value) -> value}
            val multipleEntriesOfMostFrequent7CharacterWord = result.filter {it.key.isNotBlank() && it.value == result.first().value}
            val freq = StringBuilder()
            for ((key) in multipleEntriesOfMostFrequent7CharacterWord) {
                freq.append("[${key}]")
            }
            fileStats.mostFrequent7CharacterWord = freq.toString()
            fileStats.mostFrequent7CharacterWordCount = result.first().value!!
        }
    }

    fun computeHighestScoringWordInScrabble() {
        val scrabbleWords: HashMap<String, Int> = HashMap()
        var scrabbleLetterScores = ScrabbleLetterScores()

        wordsMap.forEach { word ->
            scrabbleLetterScores.inputWord = word.key
            var count = scrabbleLetterScores.score
            if (scrabbleWords[word.key] != null) {
                count += wordsMap[word.key] ?: 0
            }
            scrabbleWords[word.key] = count
        }

        val result = scrabbleWords.toList().sortedByDescending { (_, value) -> value}.toMap()
        val multipleEntriesOfMostFrequentWord = result.filter {it.key.isNotBlank() && it.value == result.entries.first().value}
        val freq = StringBuilder()
        for ((key) in multipleEntriesOfMostFrequentWord) {
            freq.append("[${key}]")
        }

        fileStats.highestScoringWordInScrabble = freq.toString()
        fileStats.highestScoringWordInScrabbleCount = result.entries.first().value
        _doneComputing.value = true
    }
}

package com.touchsides.wordreader.entities

class ScrabbleLetterScores {

    var inputWord: String? = null

    companion object {

        private val letterScore: HashMap<Int, Int> = HashMap('Z' - 'A')

        private fun mapToScore(letters: String, score: Int) {
            letters.chars().forEach { letter: Int ->
                letterScore[letter] = score
            }
        }

        init {
            mapToScore("AEIOULNRST", 1)
            mapToScore("DG", 2)
            mapToScore("BCMP", 3)
            mapToScore("FHVWY", 4)
            mapToScore("K", 5)
            mapToScore("JX", 8)
            mapToScore("QZ", 10)
        }
    }

    val score: Int
        get() = inputWord?.toUpperCase()?.chars()?.map { c: Int ->
            letterScore.getOrDefault(
                c,
                0
            )
        }?.reduce(0) { a: Int, b: Int -> a + b }
            ?: 0
}
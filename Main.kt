package flashcards

import java.io.File

data class Card(val term: String, val def: String) {
    var errors = 0

    override fun toString() = term
}

class FlashCards {
    private val log = mutableListOf<String>()

    private val deck = mutableListOf<Card>()
    private operator fun List<Card>.contains(term: String) = any { it.term == term }
    private infix fun List<Card>.has(def: String) = any { it.def == def }

    private fun println(s: String = "") {
        log.add(s)
        kotlin.io.println(s)
    }

    private fun readln(): String {
        val s = kotlin.io.readln()
        log.add(s)
        return s
    }

    init {
        run()
    }

    private fun addCard() {
        println("The card:")
        val term = readln()
        if (term in deck) {
            println("The card \"$term\" already exists.")
            return
        }

        println("The definition of the card:")
        val def = readln()
        if (deck has def) {
            println("The definition \"$def\" already exists.")
            return
        }

        deck += Card(term, def)
        println("The pair (\"$term\":\"$def\") has been added.")
    }

    private fun removeCard() {
        println("Which card?")
        val term = readln()

        if (term in deck) {
            deck.remove(deck.find { it.term == term })
            println("The card has been removed.")
        } else println("Can't remove \"$term\": there is no such card.")
    }

    private fun import() {
        println("File name:")
        val file = File(readln())

        if (!file.exists()) {
            println("File not found.")
            return
        }

        val cards = file.readLines()
        cards.forEach {
            val (term, definition, mistakes) = it.split('=')

            if (term in deck) {
                deck.remove(deck.find { it.term == term })
            }

            deck += Card(term, definition).apply {
                this.errors = mistakes.toInt()
            }
        }
        println("${cards.size} cards have been loaded.")
    }

    private fun export() {
        println("File name:")
        val filename = readln()

        var output = ""
        deck.forEach {
            output += it.term + "=" + it.def + "=" + it.errors + "\n"
        }

        File(filename).writeText(output)
        println("${deck.size} cards have been saved.")
    }

    private fun takeQuiz() {
        println("How many times to ask?")
        repeat(readln().toInt()) {
            val card = deck.random()

            println("Print the definition of \"${card.term}\":")
            val answer = readln()

            if (card.def != answer) card.errors++
            println(when {
                card.def == answer -> "Correct!"
                deck has answer -> "Wrong. The right answer is \"${card.def}\", but your " +
                        "definition is correct for \"${deck.find { it.def == answer }?.term}\"."
                else -> "Wrong. The right answer is \"${card.def}\"."
            })
        }
    }

    private fun saveLog() {
        println("File name:")
        val filename = readln()

        File(filename).writeText(log.joinToString("\n"))
        println("The log has been saved.")
    }

    private fun showHardestCard() {
        // get the set of cards with the largest amount of errors
        val terms = deck.groupBy { it.errors }.maxByOrNull { it.key }?.value

        // if the error amount is 0, or we didn't find anything, return an empty list
            ?.let { if (it[0].errors > 0) it else null } ?: listOf()

        println(when {
            terms.isEmpty() -> "There are no cards with errors."
            terms.size == 1 -> "The hardest card is \"${terms[0].term}\". You have ${terms[0].errors} errors answering it."
            else -> "The hardest cards are \"" + terms.joinToString("\", \"") +
                    "\". You have ${terms[0].errors} errors answering them"
        })
    }

    private fun resetStats() {
        for (card in deck) {
            card.errors = 0
        }
        println("Card statistics have been reset.")
    }

    private fun run() {
        while (true) {
            println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
            when (readln()) {
                "add" -> addCard()
                "remove" -> removeCard()
                "import" -> import()
                "export" -> export()
                "ask" -> takeQuiz()
                "exit" -> return
                "log" -> saveLog()
                "hardest card" -> showHardestCard()
                "reset stats" -> resetStats()
            }
            println()
        }
    }
}

fun main() {
    FlashCards()
    println("Bye bye!")
}
package flashcards

import java.io.File
import kotlin.random.Random

class CardBox {
    private val cards = mutableMapOf<String, String>()
    private val stats = mutableMapOf<String, Int>()
    private val log = mutableListOf<String>()

    init {
        run()
    }

    private fun getKey(value: String): String = cards.filterValues { it == value }.keys.first()

    private fun <K, V> Map<K, V>.random(): Map.Entry<K, V> = entries.elementAt(Random.nextInt(size))

    private fun addCard() {
        logPrintln("The card:")
        val term = logReadln()
        if (term in cards) {
            logPrintln("The card \"$term\" already exists.")
            return
        }

        logPrintln("The definition of the card:")
        val definition = logReadln()
        if (definition in cards.values) {
            logPrintln("The definition \"$definition\" already exists.")
            return
        }

        cards[term] = definition
        stats[term] = 0
        logPrintln("The pair (\"$term\":\"$definition\") has been added.")
    }

    private fun removeCard() {
        logPrintln("Which card?")
        val term = logReadln()
        if (term in cards) {
            cards.remove(term)
            stats.remove(term)
            logPrintln("The card has been removed.")
        } else logPrintln("Can't remove \"$term\": there is no such card.")
    }

    private fun takeQuiz() {
        logPrintln("How many times to ask?")
        repeat(logReadln().toInt()) {
            val card = cards.random()
            logPrintln("Print the definition of \"${card.key}\":")
            val answer = logReadln()
            if (card.value != answer) stats[card.key] = stats[card.key]!! + 1
            logPrintln(
                when {
                    card.value == answer -> "Correct!"
                    answer in cards.values -> "Wrong. The right answer is \"${card.value}\", but your definition is correct for \"${
                        getKey(
                            answer
                        )
                    }\"."
                    else -> "Wrong. The right answer is \"${card.value}\"."
                }
            )
        }
    }

    private fun import() {
        logPrintln("File name:")
        val file = File(logReadln())

        if (!file.exists()) {
            logPrintln("File not found.")
            return
        }

        val lines = file.readLines()
        lines.forEach {
            val (key, value, count) = it.split('=')
            cards[key] = value
            stats[key] = count.toInt()
        }
        logPrintln("${lines.size} cards have been loaded.")
    }

    private fun export() {
        logPrintln("File name:")
        val filename = logReadln()

        var output = ""
        cards.entries.forEach {
            output += it.key + "=" + it.value + "=" + stats[it.key] + "\n"
        }

        File(filename).writeText(output)
        logPrintln("${cards.size} cards have been saved.")
    }

    private fun logPrintln(s: String = "") {
        log.add(s)
        println(s)
    }

    private fun logReadln() : String {
        val s = readln()
        log.add(s)
        return s
    }    

    private fun saveLog() {
        logPrintln("File name:")
        val filename = logReadln()

        File(filename).writeText(log.joinToString("\n"))
        logPrintln("The log has been saved.")
    }


    private fun showHardestCard() {
        val terms = stats.filterValues { it > 0 && it == stats.maxByOrNull { it.value }?.value }.keys.toList()
        logPrintln(
            when {
                terms.size == 1 -> "The hardest card is \"${terms[0]}\". You have ${stats[terms[0]]} errors answering it."
                terms.size > 1 -> "The hardest cards are " + terms.joinToString("\", \"", "\"", "\"") +
                        ". You have ${stats[terms[0]]} errors answering them"
                else -> "There are no cards with errors."
            }
        )
    }

    private fun resetStats() {
        for (term in stats) {
            stats[term.key] = 0
        }
        logPrintln("Card statistics have been reset.")
    }

    private fun run() {
        while (true) {
            logPrintln("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
            when (logReadln()) {
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
            logPrintln()
        }
    }
}

fun main() {
    CardBox()
    println("Bye bye!")
}

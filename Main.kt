package flashcards

import java.io.File
import kotlin.random.Random

class CardBox {
    private val cards = mutableMapOf<String, String>()

    init {
        run()
    }

    private fun getKey(value: String): String = cards.filterValues { it == value }.keys.first()

    fun <K,V> Map<K,V>.random(): Map.Entry<K,V> = entries.elementAt(Random.nextInt(size))

    fun addCard() {
        println("The card:")
        var term = readln()
        if (term in cards) {
            println("The card \"$term\" already exists.")
            return
        }

        println("The definition of the card:")
        var definition = readln()
        if (definition in cards.values) {
            println("The definition \"$definition\" already exists.")
            return
        }

        cards[term] = definition
        println("The pair (\"$term\":\"$definition\") has been added.")
    }

    fun removeCard() {
        println("Which card?")
        var term = readln()
        if (term in cards) {
            cards.remove(term)
            println("The card has been removed.")
        } else println("Can't remove \"$term\": there is no such card.")
    }

    fun takeQuiz() {
        println("How many times to ask?")
        repeat(readln().toInt()) {
            val card = cards.random()
            println("Print the definition of \"${card.key}\":")
            var answer = readln()
            println(when {
                card.value == answer -> "Correct!"
                answer in cards.values -> "Wrong. The right answer is \"${card.value}\", but your definition is correct for \"${getKey(answer)}\"."
                else -> "Wrong. The right answer is \"${card.value}\"."
            })
        }
    }

    fun import() {
        println("File name:")
        val file = File(readln())

        if (!file.exists()) {
            println("File not found.")
            return
        }

        val lines = file.readLines()
        lines.forEach {
            val (key, value) = it.split('=')
            cards[key] = value
        }
        println("${lines.size} cards have been loaded.")
    }

    fun export() {
        println("File name:")
        val filename = readln()

        File(filename).writeText(cards.entries.joinToString("\n"))
        println("${cards.size} cards have been saved.")
    }

    fun run() {
        while (true) {
            println("Input the action (add, remove, import, export, ask, exit):")
            val action = readln()
            when (action) {
                "add" -> addCard()
                "remove" -> removeCard()
                "import" -> import()
                "export" -> export()
                "ask" -> takeQuiz()
                "exit" -> return
            }
            println()
        }
    }
}

fun main() {
    CardBox()
    println("Bye bye!");
}

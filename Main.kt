package flashcards

class CardBox {
    val cards = mutableMapOf<String, String>()

    fun inputCards() {
        println("Input the number of cards:")
        val count = readln().toInt()

        for (i in 1..count) {
            println("Card #$i:")
            var term = readln()
            while (term in cards) {
                println("The term \"$term\" already exists. Try again:")
                term = readln()
            }

            println("The definition for card #$i:")
            var definition = readln()
            while (definition in cards.values) {
                println("The definition \"$definition\" already exists. Try again:")
                definition = readln()
            }

            cards[term] = definition
        }
    }

    fun getKey(value: String): String = cards.filterValues { it == value }.keys.first()

    fun takeQuiz() {
        cards.forEach {
            println("Print the definition of \"${it.key}\":")
            var answer = readln()
            println(when {
                it.value == answer -> "Correct!"
                answer in cards.values -> "Wrong. The right answer is \"${it.value}\", but your definition is correct for \"${getKey(answer)}\"."
                else -> "Wrong. The right answer is \"${it.value}\"."
            })
        }
    }
}

fun main() {
    val box = CardBox()
    box.inputCards()
    box.takeQuiz()
}

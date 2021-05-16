package setgame

const val NUMBER_OF_FACE_UP_CARDS = 12
const val SET_SIZE = 3
const val MAX_FACE_UP_CARDS = 18

enum class Shape { Oval, Squiggle, Diamond }
enum class Color { Red, Purple, Green }
enum class SetNumber { One, Two, Three }
enum class Shading { Solid, Striped, Outlined }

fun generateShuffledSetDeck(): MutableList<SetCard> {
    val deck = mutableListOf<SetCard>()
    for (shape in Shape.values()) {
        for (color in Color.values()) {
            for (number in SetNumber.values()) {
                for (shading in Shading.values()) {
                    deck.add(SetCard(shape, color, number, shading))
                }
            }
        }
    }
    deck.shuffle()
    return deck
}

class SetGame {
    private val deck: MutableList<SetCard> = generateShuffledSetDeck()
    private val faceUpCards: MutableList<SetCard> = mutableListOf()
    private var setsFound: Int = 0

    init {
        initializeFaceUpCards()
    }

    fun play() {
        printWelcomeMessage()
        while (faceUpCards.size >= NUMBER_OF_FACE_UP_CARDS) {
            printFaceUpCards()
            when (val userInput = promptUserInput()) {
                "draw" -> {
                    drawFromDeck()
                }
                "find" -> {
                    val set = searchSetInFaceUpCards()
                    if (set != null) {
                        println("The computer found the following Set:")
                        println(collectionOfCardsToString(set))
                        handleSetFound(set)
                    } else {
                        println("Could not find a Set! Type \"draw\" or restart the game.")
                    }
                }
                else -> {
                    val indices = extractIndicesFromInput(userInput)
                    if (indices.size == SET_SIZE) {
                        val chosenCards = indices.map { faceUpCards[it - 1] }
                        if (isLegalSet(chosenCards)) {
                            println("You found a Set! Well done.")
                            handleSetFound(chosenCards)
                        } else {
                            println("The following cards do not form a valid Set. Try again :)")
                            println(collectionOfCardsToString(chosenCards))
                        }
                    }
                }
            }
        }
    }

    private fun initializeFaceUpCards() {
        repeat(NUMBER_OF_FACE_UP_CARDS) {
            faceUpCards.add(deck.removeLast())
        }
    }

    private fun drawFromDeck() {
        if (faceUpCards.size < MAX_FACE_UP_CARDS) {
            repeat(SET_SIZE) {
                faceUpCards.add(deck.removeLast())
            }
        } else {
            println("Max number of face up cards reached: $MAX_FACE_UP_CARDS")
        }
    }

    /**
     * If the input is valid, returns a list of indices in the range from 1 to faceUpCards.size
     * If the input is not valid, returns a list with less than SET_SIZE elements.
     */
    private fun extractIndicesFromInput(input: String): List<Int> {
        val rawCardsIndices = input.split("\\s+".toRegex())
        if (rawCardsIndices.toSet().size != SET_SIZE) {
            println("Wrong number of different indices! Please insert $SET_SIZE different indices exactly.")
            return listOf()
        }
        val cardsIndices = mutableListOf<Int>()
        for (rawIndex in rawCardsIndices) {
            try {
                val i = rawIndex.toInt()
                if (i < 1 || i > faceUpCards.size) {
                    println("'$rawIndex' is not a valid index. It is not in the range of 1...${faceUpCards.size}.")
                    break
                }
                cardsIndices.add(i)
            } catch (e: NumberFormatException) {
                println("'$rawIndex' is not a valid index (not integer).")
                break
            }
        }
        return cardsIndices
    }

    private fun searchSetInFaceUpCards(): List<SetCard>? {
        for (i in faceUpCards.indices) {
            for (j in i + 1 until faceUpCards.size) {
                val card1 = faceUpCards[i]
                val card2 = faceUpCards[j]
                val card3 = SetCard(
                    deriveCompletingShape(card1, card2),
                    deriveCompletingColor(card1, card2),
                    deriveCompletingNumber(card1, card2),
                    deriveCompletingShading(card1, card2)
                )
                if (faceUpCards.contains(card3)) {
                    return listOf(card1, card2, card3)
                }
            }
        }
        return null
    }

    private fun deriveCompletingShape(card1: SetCard, card2: SetCard): Shape {
        return if (card1.shape == card2.shape) card1.shape else {
            val values = mutableListOf(*Shape.values())
            values.remove(card1.shape)
            values.remove(card2.shape)
            values[0]
        }
    }

    private fun deriveCompletingColor(card1: SetCard, card2: SetCard): Color {
        return if (card1.color == card2.color) card1.color else {
            val values = mutableListOf(*Color.values())
            values.remove(card1.color)
            values.remove(card2.color)
            values[0]
        }
    }

    private fun deriveCompletingNumber(card1: SetCard, card2: SetCard): SetNumber {
        return if (card1.setNumber == card2.setNumber) card1.setNumber else {
            val values = mutableListOf(*SetNumber.values())
            values.remove(card1.setNumber)
            values.remove(card2.setNumber)
            values[0]
        }
    }

    private fun deriveCompletingShading(card1: SetCard, card2: SetCard): Shading {
        return if (card1.shading == card2.shading) card1.shading else {
            val values = mutableListOf(*Shading.values())
            values.remove(card1.shading)
            values.remove(card2.shading)
            values[0]
        }
    }

    private fun printFaceUpCards() {
        println("Board:")
        for (i in 0 until faceUpCards.size step SET_SIZE) {
            println(collectionOfCardsToString(faceUpCards.subList(i, i + SET_SIZE)))
            for (j in i until i + SET_SIZE) {
                val padding = " ".repeat((CARD_STRING_LINE_LENGTH + j.toString().length) / 2)
                val extraPaddingBetweenCards = " ".repeat(3)
                print("$padding${j + 1}$padding$extraPaddingBetweenCards")
            }
            println()
        }
        println()
    }

    private fun printWelcomeMessage() {
        println(
            """
            Play Set! See instructions at: https://en.wikipedia.org/wiki/Set_(card_game)
            - To select a Set, type the 3 indices matching 3 cards of your choice, for example: "5 2 10".
            - To draw 3 more cards, type "draw".
            - Want the computer to find a Set? Type "find".
            """.trimIndent()
        )
    }

    private fun promptUserInput(): String {
        print("Your input: ")
        return readLine()?.trim()?.toLowerCase() ?: ""
    }

    private fun isLegalSet(cards: List<SetCard>): Boolean {
        if (cards.size != SET_SIZE) return false

        val legalDifferences = setOf(1, SET_SIZE)
        val shapes = cards.map { it.shape }.toSet()
        val colors = cards.map { it.color }.toSet()
        val numbers = cards.map { it.setNumber }.toSet()
        val shadings = cards.map { it.shading }.toSet()

        return legalDifferences.contains(shapes.size) &&
                legalDifferences.contains(colors.size) &&
                legalDifferences.contains(numbers.size) &&
                legalDifferences.contains(shadings.size)
    }

    private fun handleSetFound(set: List<SetCard>) {
        faceUpCards.removeAll(set)
        drawFromDeck()
        setsFound += 1
    }
}
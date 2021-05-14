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
    private var faceUpCards: MutableSet<SetCard> = mutableSetOf()

    init {
        initializeFaceUpCards()
    }

    fun play() {
        printWelcomeMessage()
        while (faceUpCards.size >= NUMBER_OF_FACE_UP_CARDS) {
            printFaceUpCards()
            printInstructions()
            when (readLine()!!.toLowerCase()) {
                "draw" -> {
                    drawFromDeck()
                }
                "help" -> {
                    val set = searchSetInFaceUpCards()
                    if (set != null) {
                        println(collectionOfCardsToString(set))
                        faceUpCards.removeAll(set)
                        drawFromDeck()
                    } else {
                        println("Could not find a Set! Type \"draw\" or restart the game.")
                    }
                }
                else -> {
                    // should print indices of cards in face up cards as numbers
                    // parse and trim white spaces from user input
                    // check the input is valid
                    // check the input is a valid set or not and print a message accordingly.
                }
            }


//            if (deck.size >= SET_SIZE) {
//                drawFromDeck(SET_SIZE)
//            } else {
//                break
//            }
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

    private fun searchSetInFaceUpCards(): List<SetCard>? {
        val faceUpCardsList = faceUpCards.toList()
        for (i in faceUpCardsList.indices) {
            for (j in i + 1 until faceUpCardsList.size) {
                val card1 = faceUpCardsList[i]
                val card2 = faceUpCardsList[j]
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
        val l = faceUpCards.toList()
        for (i in 0..l.size step SET_SIZE) {
            println(collectionOfCardsToString(l.subList(i, kotlin.math.min(i + SET_SIZE, l.size))))
//            println("(A B C)")
        }
    }

    private fun printWelcomeMessage() {
        println("Play Set! See instructions at: https://en.wikipedia.org/wiki/Set_(card_game)")
    }

    private fun printInstructions() {
        println(
            """
            - To select a Set, insert the 3 characters matching your cards.
            - To draw 3 more cards, type "draw".
            - Want the computer to find a set? Type "help".
            Your input:
            """.trimIndent()
        )
    }

    fun isLegalSet(cards: List<SetCard>): Boolean {
        if (cards.isEmpty()) return true
        if (cards.size != SET_SIZE) return false

        val legalDifferences = setOf(1, cards.size)
        val shapes = setOf(cards.map { it.shape })
        val colors = setOf(cards.map { it.color })
        val numbers = setOf(cards.map { it.setNumber })
        val shadings = setOf(cards.map { it.shading })

        return legalDifferences.contains(shapes.size) &&
                legalDifferences.contains(colors.size) &&
                legalDifferences.contains(numbers.size) &&
                legalDifferences.contains(shadings.size)
    }
}

fun main() {
    val setGame = SetGame()
    setGame.play()
}
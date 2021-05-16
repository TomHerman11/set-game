package setgame

import java.lang.StringBuilder

const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_PURPLE = "\u001B[35m"

const val CARD_STRING_LINE_LENGTH = 17 + 5 + 5 // 17 max raw length + 5 on each side

data class SetCard(
    val shape: Shape,
    val color: Color,
    val setNumber: SetNumber,
    val shading: Shading
) {
    private val cardAsString: String

    init {
        cardAsString = generateCardAsString()
    }

    private fun generateCardAsString(): String {
        val singleShapeString = when (shape) {
            Shape.Squiggle -> generateSquiggleString()
            Shape.Diamond -> generateDiamondString()
            Shape.Oval -> generateOvalString()
        }
        val rawCardAsString = concatMultiLineStrings(
            List(parseNumber(setNumber)) { singleShapeString },
            " "
        )
        val paddedCardAsString = padMultilineString(rawCardAsString, CARD_STRING_LINE_LENGTH)
        return applyColorToMultilineString(paddedCardAsString, getAnsiColor())
    }

    override fun toString(): String {
        return cardAsString
    }

    private fun generateSquiggleString(): String {
        val s = getShadingChar()
        return """
        #\$s$s$s\
        #/$s$s$s/
        #\$s$s$s\
        #/$s$s$s/""".trimMargin("#")
    }

    private fun generateDiamondString(): String {
        val s = getShadingChar()
        return """
        # /$s\ 
        #/$s$s$s\
        #\$s$s$s/
        # \$s/ """.trimMargin("#")
    }

    private fun generateOvalString(): String {
        val s = getShadingChar()
        return """
        # *** 
        #|$s$s$s|
        #|$s$s$s|
        # *** """.trimMargin("#")
    }

    private fun getShadingChar(): Char = when (shading) {
        Shading.Striped -> '-'
        Shading.Solid -> '0'
        else -> ' '
    }

    private fun getAnsiColor(): String = when (color) {
        Color.Green -> ANSI_GREEN
        Color.Purple -> ANSI_PURPLE
        Color.Red -> ANSI_RED
    }
}

fun parseNumber(setNumber: SetNumber): Int = when (setNumber) {
    SetNumber.One -> 1
    SetNumber.Two -> 2
    SetNumber.Three -> 3
}

fun collectionOfCardsToString(c: Collection<SetCard>): String {
    return concatMultiLineStrings(
        c.map { it.toString() },
        " ".repeat(2)
    )
}

fun concatMultiLineStrings(strings: List<String>, d: String): String {
    val builder = StringBuilder()
    val linesMatrix = strings.map { it.lines() }
    val minLines = linesMatrix.minByOrNull { it.size }

    if (minLines != null) {
        for (i in minLines.indices) {
            val lineBuilder = StringBuilder()
            for (lines in linesMatrix) {
                lineBuilder.append(lines[i])
                lineBuilder.append(d)
            }
            builder.appendLine(lineBuilder.toString())
        }
    }
    return builder.toString().removeNewLineSuffix()
}

fun padMultilineString(s: String, desiredLineSize: Int): String {
    val builder = StringBuilder()
    val lines = s.lines()

    for (line in lines) {
        val lineBuilder = StringBuilder()
        val pad = " ".repeat(kotlin.math.max(((desiredLineSize - line.length) / 2), 0))
        lineBuilder.append(pad)
        lineBuilder.append(line)
        lineBuilder.append(pad)
        builder.appendLine(lineBuilder.toString())
    }
    return builder.toString().removeNewLineSuffix()
}

fun String.removeNewLineSuffix(): String {
    return Regex("\n\$").replace(this, "") // remove new line ('\n') from final multilineString
}

fun applyColorToMultilineString(s: String, color: String): String {
    val builder = StringBuilder()
    val lines = s.lines()

    for (line in lines) {
        builder.appendLine(color + line + ANSI_RESET)
    }
    return builder.toString().removeNewLineSuffix()
}
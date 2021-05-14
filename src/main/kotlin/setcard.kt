package setgame

import java.lang.StringBuilder

const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_PURPLE = "\u001B[35m"

data class SetCard(
    val shape: Shape,
    val color: Color,
    val setNumber: SetNumber,
    val shading: Shading
) {
    override fun toString(): String {
        val singleShapeString = when (shape) {
            Shape.Squiggle -> generateSquiggleString()
            Shape.Diamond -> generateDiamondString()
            Shape.Oval -> generateOvalString()
        }
        return concatMultiLineStrings(
            List(parseNumber(setNumber)) { singleShapeString },
            " "
        )
    }

    private fun generateSquiggleString(): String {
        val s = getShadingChar()
        val c = getAnsiColor()
        return """
        #$c\$s$s$s\
        #$c/$s$s$s/
        #$c\$s$s$s\
        #$c/$s$s$s/""".trimMargin("#")
    }

    private fun generateDiamondString(): String {
        val s = getShadingChar()
        val c = getAnsiColor()
        return """
        #$c /$s\ 
        #$c/$s$s$s\
        #$c\$s$s$s/
        #$c \$s/ """.trimMargin("#")
    }

    private fun generateOvalString(): String {
        val s = getShadingChar()
        val c = getAnsiColor()
        return """
        #$c *** 
        #$c|$s$s$s|
        #$c|$s$s$s|
        #$c *** """.trimMargin("#")
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
        "           "
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
    return Regex("\n\$").replace(builder.toString(), "") // remove new line ('\n') from final multilineString
}

package mezzari.torres.lucas.core.archive

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
fun String.replaceLineBreaks(replaceable: String = ""): String {
    return this.replace("\n", replaceable)
        .replace("\\n", replaceable)
}
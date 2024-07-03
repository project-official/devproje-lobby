package net.projecttl.lobby.util

fun String.tagReplacer(name: String, repl: String): String {
	val tag = listOf("<$name>", "<$name/>", "<$name />")
	var source = this

	tag.forEach {
		if (source.contains(it)) {
			source = source.replace(it, repl)
		}
	}

	return source
}

fun String.formatter(): String {
	return this.tagReplacer("tab", "    ")
		.tagReplacer("nb", "")
		.tagReplacer("space", " ")
		.tagReplacer("nbsp", "\n")
}

fun String.performance(): String {
	val rt = Runtime.getRuntime()
	return this.tagReplacer("use", String.format("%.2f", (rt.maxMemory() - (rt.maxMemory() - rt.freeMemory())) * 0.001 * 0.001 * 0.001))
		.tagReplacer("max", String.format("%.2f", rt.maxMemory() * 0.001 * 0.001 * 0.001))
}


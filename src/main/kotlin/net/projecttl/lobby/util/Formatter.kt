package net.projecttl.lobby.util

fun String.formatter(): String {
	return this.replace("<tab>", "    ")
		.replace("<nb>", "")
		.replace("<space>", " ")
		.replace("<nbsp>", "\n")
}

fun String.performance(): String {
	val rt = Runtime.getRuntime()
	val tags = listOf(
		// Memory
		"<use>",
		"<max>"
	)

	return this.replace(
		tags[0],
		String.format("%.2f", (rt.maxMemory() - (rt.maxMemory() - rt.freeMemory())) * 0.001 * 0.001 * 0.001)
	).replace(tags[1], String.format("%.2f", rt.maxMemory() * 0.001 * 0.001 * 0.001))
}


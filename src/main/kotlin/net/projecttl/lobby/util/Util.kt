package net.projecttl.lobby.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

fun String.toMini(): Component {
	return MiniMessage.miniMessage().deserialize(this.formatter())
}

fun Component.asString() = PlainTextComponentSerializer.plainText().serialize(this)

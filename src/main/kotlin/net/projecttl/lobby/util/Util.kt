package net.projecttl.lobby.util

import com.google.common.io.ByteStreams
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minestom.server.entity.Player

fun String.toMini(): Component {
	return MiniMessage.miniMessage().deserialize(this.formatter())
}

fun Component.asString() = PlainTextComponentSerializer.plainText().serialize(this)

fun Player.moveServer(server: String) {
	this.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty())
	val out = ByteStreams.newDataOutput()
	out.writeUTF("Connect")
	out.writeUTF(server)

	this.sendPluginMessage("BungeeCord", out.toByteArray())
}

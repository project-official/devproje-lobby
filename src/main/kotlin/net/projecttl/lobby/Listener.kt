package net.projecttl.lobby

import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerCommandEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import net.projecttl.lobby.util.asString
import net.projecttl.net.projecttl.lobby.Config

object Listener {
	fun run(node: EventNode<Event>) {
		val dim = DimensionType.builder()
			.height(416)
			.logicalHeight(200)
			.minY(-64)
			.build()

		MinecraftServer.getDimensionTypeRegistry().register(NamespaceID.from("fullbright"), dim)
		instance = MinecraftServer.getInstanceManager().createInstanceContainer()

		instance.chunkLoader = AnvilLoader(Config.level_name)
		instance.setChunkSupplier(::LightingChunk)

		node.addListener(PlayerCommandEvent::class.java) { event ->
			val name = event.player.name.asString()
			logger.info("$name using command: /${event.command}")
		}

		node.addListener(PlayerSpawnEvent::class.java) { event ->
			event.player.sendMessage("${event.player.displayName}")
		}
	}
}

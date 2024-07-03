package net.projecttl.lobby

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerCommandEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.projecttl.lobby.util.asString

object Listener {
	fun run(node: EventNode<Event>) {
		val spawnPoint = Pos(0.5, 41.0, 0.5)
		instance = MinecraftServer.getInstanceManager().createInstanceContainer()

		instance.chunkLoader = AnvilLoader(Config.level_name)
		instance.setChunkSupplier(::LightingChunk)

		node.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
			event.spawningInstance = instance
		}

		node.addListener(PlayerCommandEvent::class.java) { event ->
			val name = event.player.name.asString()
			logger.info("$name using command: /${event.command}")
		}

		node.addListener(PlayerSpawnEvent::class.java) { event ->
			event.player.teleport(spawnPoint)
			event.player.gameMode = GameMode.ADVENTURE
			event.player.sendMessage("${event.player.username} is joined the server")
		}

		node.addListener(PlayerMoveEvent::class.java) { event ->
			if (event.player.position.y <= 20) {
				event.player.teleport(Config.defaultSpawn)
			}
		}
	}
}

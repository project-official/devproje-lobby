package net.projecttl.lobby

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.*
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.item.ItemComponent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.projecttl.lobby.ui.ServerUI
import net.projecttl.lobby.util.asString
import net.projecttl.lobby.util.toMini

object Listener {
	private val compass = ItemStack.builder(Material.COMPASS).apply {
		set(ItemComponent.ITEM_NAME, "<green>Server Selector".toMini())
	}.build()

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
			event.player.inventory.setItemStack(4, compass)
			event.player.sendMessage("${event.player.username} is joined the server")
		}

		node.addListener(PlayerMoveEvent::class.java) { event ->
			if (event.player.position.y <= 20) {
				event.player.teleport(Config.defaultSpawn)
			}
		}

		node.addListener(ItemDropEvent::class.java) { event ->
			event.isCancelled = true
		}

		node.addListener(PlayerSwapItemEvent::class.java) { event ->
			event.isCancelled = true
		}

		node.addListener(InventoryPreClickEvent::class.java) { event ->
			event.isCancelled = true
		}

		node.addListener(PlayerUseItemEvent::class.java) { event ->
			if (event.player.itemInMainHand.isAir) {
				event.isCancelled = true
				return@addListener
			}

			ServerUI(event.player).build()
		}
	}
}

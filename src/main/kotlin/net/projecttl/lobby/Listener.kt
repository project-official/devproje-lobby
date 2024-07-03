package net.projecttl.lobby

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.*
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.projecttl.lobby.util.asString
import net.projecttl.lobby.util.toMini

object Listener {
	@OptIn(DelicateCoroutinesApi::class)
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

//			event.player.inventory.setItemStack(4, compass)

			if (Config.bossBar.isEmpty()) {
				return@addListener
			}

			GlobalScope.launch {
				var progress = 0F
				var page = 0

				do {
					val bar = BossBar.bossBar(
						Config.bossBar[page].toMini(),
						progress,
						BossBar.Color.GREEN,
						BossBar.Overlay.PROGRESS
					)

					event.player.showBossBar(bar)
					delay(25)

					progress += 0.0025F
					if (progress > 1.0) {
						progress = 0F

						page = if (Config.bossBar.size - 1 <= page) {
							0
						} else {
							page + 1
						}
					}

					event.player.hideBossBar(bar)
				} while (true)
			}

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
	}
}

package net.projecttl.lobby.core

import net.kyori.adventure.key.Key
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandManager
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.extras.MojangAuth
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.velocity.VelocityProxy
import net.minestom.server.utils.NamespaceID
import net.projecttl.lobby.Config
import net.projecttl.lobby.handler.CampfireHandler
import net.projecttl.lobby.handler.SignHandler
import net.projecttl.lobby.handler.SkullHandler
import net.projecttl.lobby.type.ProxyType
import org.jetbrains.exposed.sql.Database
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Kernel {
	val logger: Logger = LoggerFactory.getLogger(this::class.java)
	val database: Database

	val server: MinecraftServer
	val commands: CommandManager
	val handler: GlobalEventHandler

	init {
		logger.info("Minecraft ${MinecraftServer.VERSION_NAME}")

		database = Database.connect(
			url = Config.database_url,
			driver = Config.dbType.driver,
			user = Config.database_username,
			password = Config.database_password
		)

		server = MinecraftServer.init()
		commands = MinecraftServer.getCommandManager()
		handler = MinecraftServer.getGlobalEventHandler()

		when (Config.proxyType) {
			ProxyType.NONE       -> {}
			ProxyType.VELOCITY   -> {
				if (Config.velocity_secret.isNotEmpty()) {
					VelocityProxy.enable(Config.velocity_secret)
					logger.info("Enabled velocity forward option")
				} else {
					logger.info("Velocity secret not set, ignore it")
				}
			}

			ProxyType.BUNGEECORD -> {
				BungeeCordProxy.enable()
				logger.info("Enabled bungeecord forward option")
			}
		}

		if (Config.onlineMode && !BungeeCordProxy.isEnabled() && !VelocityProxy.isEnabled()) {
			MojangAuth.init()
		}

		MinecraftServer.getBlockManager().apply {
			registerHandler(NamespaceID.from(Key.key("minecraft:sign"))) { SignHandler() }
			registerHandler(NamespaceID.from(Key.key("minecraft:skull"))) { SkullHandler() }
			registerHandler(NamespaceID.from(Key.key("minecraft:campfire"))) { CampfireHandler() }
		}
	}
}
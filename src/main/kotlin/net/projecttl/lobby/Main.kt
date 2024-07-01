package net.projecttl.lobby

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kyori.adventure.key.Key
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.MojangAuth
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.velocity.VelocityProxy
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.utils.NamespaceID
import net.projecttl.lobby.command.Fly
import net.projecttl.lobby.command.Teleport
import net.projecttl.lobby.core.Kernel
import net.projecttl.lobby.handler.CampfireHandler
import net.projecttl.lobby.handler.SignHandler
import net.projecttl.lobby.handler.SkullHandler
import net.projecttl.lobby.task.TabList
import net.projecttl.lobby.type.DatabaseType
import net.projecttl.lobby.type.ProxyType
import org.jetbrains.exposed.sql.Database
import org.slf4j.Logger
import java.io.File

lateinit var logger: Logger
lateinit var database: Database
lateinit var instance: InstanceContainer

suspend fun main() {
	val kernel = Kernel()
	logger = kernel.logger

	if (Config.dbType == DatabaseType.SQLITE) {
		val config = File("config")
		if (!config.exists()) {
			config.mkdir()
			val configFile = File("config", "data.db")

			withContext(Dispatchers.IO) {
				configFile.createNewFile()
			}
		}
	}

	val server = MinecraftServer.init()
	val commands = MinecraftServer.getCommandManager()
	val handler = MinecraftServer.getGlobalEventHandler()

	database = Database.connect(
		url = Config.database_url,
		driver = Config.dbType.driver,
		user = Config.database_username,
		password = Config.database_password
	)

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

	Listener.run(handler)
	TabList.run()

	with(commands) {
		register(Fly)
		register(Teleport)
	}

	MinecraftServer.getBlockManager().apply {
		registerHandler(NamespaceID.from(Key.key("minecraft:sign"))) { SignHandler() }
		registerHandler(NamespaceID.from(Key.key("minecraft:skull"))) { SkullHandler() }
		registerHandler(NamespaceID.from(Key.key("minecraft:campfire"))) { CampfireHandler() }
	}

	coroutineScope {
		launch {
			server.start(Config.host, Config.serverPort)
		}
	}

	logger.info("Server started at ${Config.host}:${Config.serverPort}")
}

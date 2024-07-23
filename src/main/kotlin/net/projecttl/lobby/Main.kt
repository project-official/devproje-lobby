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
import net.projecttl.lobby.command.TabListC
import net.projecttl.lobby.command.Teleport
import net.projecttl.lobby.core.Kernel
import net.projecttl.lobby.handler.CampfireHandler
import net.projecttl.lobby.handler.SignHandler
import net.projecttl.lobby.handler.SkullHandler
import net.projecttl.lobby.service.TabListService
import net.projecttl.lobby.task.TabList
import net.projecttl.lobby.type.DatabaseType
import net.projecttl.lobby.type.ProxyType
import net.projecttl.lobby.ui.UIHandler
import org.jetbrains.exposed.sql.Database
import org.slf4j.Logger
import java.io.File

lateinit var logger: Logger
lateinit var database: Database
lateinit var instance: InstanceContainer
lateinit var tabListService: TabListService

suspend fun main() {
	val kernel = Kernel()
	logger = kernel.logger

	if (Config.dbType == DatabaseType.SQLITE) {
		val config = File("config")
		if (!config.exists()) {
			config.mkdir()
			val cnf = File("config", "data.db")

			withContext(Dispatchers.IO) {
				cnf.createNewFile()
			}
		}
	}

	database = kernel.database
	tabListService = TabListService(database)

	UIHandler.run(kernel.handler)
	Listener.run(kernel.handler)
	TabList.run()

	with(kernel.commands) {
		register(Fly)
		register(TabListC)
		register(Teleport)
	}

	coroutineScope {
		launch {
			kernel.server.start(Config.host, Config.serverPort)
		}
	}

	logger.info("Server started at ${Config.host}:${Config.serverPort}")
}

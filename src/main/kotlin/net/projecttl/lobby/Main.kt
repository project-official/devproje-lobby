package net.projecttl.lobby

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.key.Key
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.MojangAuth
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.velocity.VelocityProxy
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.utils.NamespaceID
import net.projecttl.lobby.core.Kernel
import net.projecttl.lobby.handler.CampfireHandler
import net.projecttl.lobby.handler.SignHandler
import net.projecttl.lobby.handler.SkullHandler
import net.projecttl.lobby.type.ProxyType
import net.projecttl.net.projecttl.lobby.Config
import org.slf4j.Logger

lateinit var logger: Logger
lateinit var instance: InstanceContainer

suspend fun main() {
	val kernel = Kernel()
	logger = kernel.logger

	val server = MinecraftServer.init()
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

	coroutineScope {
		launch {
			server.start(Config.host, Config.serverPort)
		}
	}

	logger.info("Server started at ${Config.host}:${Config.serverPort}")
}

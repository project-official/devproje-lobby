package net.projecttl.lobby.core

import net.minestom.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Kernel {
	val logger: Logger = LoggerFactory.getLogger(this::class.java)

	init {
		logger.info("Minecraft ${MinecraftServer.VERSION_NAME}")
	}
}
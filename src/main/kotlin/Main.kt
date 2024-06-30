package net.projecttl

import net.minestom.server.MinecraftServer
import net.minestom.server.extras.MojangAuth
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.velocity.VelocityProxy
import net.projecttl.type.ProxyType

fun main() {
	val server = MinecraftServer.init()
	when (Config.proxyType) {
		ProxyType.NONE 	   -> {}
		ProxyType.VELOCITY -> {
			VelocityProxy.enable(Config.velocity_secret)
		}
		ProxyType.BUNGEECORD -> {
			BungeeCordProxy.enable()
		}
	}

	if (Config.onlineMode && !BungeeCordProxy.isEnabled() && !VelocityProxy.isEnabled()) {
		MojangAuth.init()
	}

	server.start(Config.host, Config.serverPort)
}

package net.projecttl.lobby

import net.minestom.server.coordinate.Pos
import net.projecttl.lobby.type.DatabaseType
import net.projecttl.lobby.type.ProxyType
import net.projecttl.lobby.util.DelegateGenerator
import java.io.File
import java.util.*
import kotlin.reflect.KProperty

object Config {
	private fun <T> useConfig() = ConfigDelegate<T>()

	private fun parseInt(ref: String) = ref.toIntOrNull() ?: 25565

	private fun parseBool(ref: String) = when (ref) {
		"true"  -> true
		"false" -> false
		else    -> throw IllegalStateException("$ref is not a Boolean")
	}

	private fun parseProxyType(ref: String) = when (ref) {
		ProxyType.NONE.type       -> ProxyType.NONE
		ProxyType.VELOCITY.type   -> ProxyType.VELOCITY
		ProxyType.BUNGEECORD.type -> ProxyType.BUNGEECORD
		else                      -> throw IllegalStateException("$ref is not a ProxyType")
	}

	private fun parseDBType(ref: String) = when (ref) {
		DatabaseType.SQLITE.name.lowercase()  -> DatabaseType.SQLITE
		DatabaseType.MARIADB.name.lowercase() -> DatabaseType.MARIADB
		else                                  -> throw IllegalStateException("$ref is not a DatabaseType")
	}

	private fun parsePos(ref: String): Pos {
		val split = ref.split(";")
		if (split.size == 3 || split.size == 5) {
			throw NullPointerException("split format must be to {x};{y};{z}; or {x};{y};{z};{pitch};{yaw};")
		}

		val x = split[0].toDouble()
		val y = split[1].toDouble()
		val z = split[2].toDouble()

		if (split.size == 5) {
			val pitch = split[3].toFloat()
			val yaw = split[4].toFloat()

			return Pos(x, y, z, yaw, pitch)
		}

		return Pos(x, y, z)
	}

	val host: String by useConfig()
	private val port: String by useConfig()
	val serverPort = parseInt(port)

	private val proxy_type: String by useConfig()
	val proxyType = parseProxyType(proxy_type)
	val velocity_secret: String by useConfig()
	private val online_mode: String by useConfig()
	val onlineMode = parseBool(online_mode)

	val level_name: String by useConfig()
	private val default_spawn: String by useConfig()
	val defaultSpawn = parsePos(default_spawn)

	private val database_type: String by useConfig()
	val dbType = parseDBType(database_type)

	val database_url: String by useConfig()
	val database_username: String by useConfig()
	val database_password: String by useConfig()
}

@Suppress("UNCHECKED_CAST")
private class ConfigDelegate<T> : DelegateGenerator<T> {
	override val props = Properties()
	override operator fun getValue(thisRef: Any, property: KProperty<*>): T = props[property.name] as T

	init {
		val file = File("config.properties")
		if (!file.exists()) {
			val stream = javaClass.getResourceAsStream("/config.properties")!!
			stream.use { buf ->
				val buffer = buf.readAllBytes()

				file.createNewFile()
				file.writeBytes(buffer)
			}

			throw NullPointerException("config not found in your service directory. please edit `config.properties` file first.")
		}

		props.load(file.inputStream())
	}
}

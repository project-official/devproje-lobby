package net.projecttl

import net.projecttl.type.ProxyType
import net.projecttl.util.DelegateGenerator
import java.io.File
import java.util.*
import kotlin.reflect.KProperty

object Config {
	private fun <T> useConfig() = ConfigDelegate<T>()

	private fun parseInt(ref: String) = ref.toIntOrNull() ?: 25565

	private fun parseBool(ref: String) = when (ref) {
		"true" 	-> true
		"false" -> false
		else 	-> throw IllegalStateException("$ref is not a Boolean")
	}

	private fun parseProxyType(ref: String) = when (ref) {
		ProxyType.NONE.type 	  -> ProxyType.NONE
		ProxyType.VELOCITY.type   -> ProxyType.VELOCITY
		ProxyType.BUNGEECORD.type -> ProxyType.BUNGEECORD
		else 			  -> throw IllegalStateException("$ref is not a ProxyType")
	}

	val host: String by useConfig()
	val velocity_secret: String by useConfig()

	private val port: String by useConfig()
	private val proxy_type: String by useConfig()
	private val online_mode: String by useConfig()

	val serverPort = parseInt(port)
	val onlineMode = parseBool(online_mode)
	val proxyType = parseProxyType(proxy_type)
}

@Suppress("UNCHECKED_CAST")
private class ConfigDelegate<T> : DelegateGenerator<T> {
	override val props = Properties()
	override operator fun getValue(thisRef: Any, property: KProperty<*>): T = props[property.name] as T

	init {
		val file = File("./config.properties")
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

package net.projecttl.lobby.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import net.projecttl.lobby.tabListService
import net.projecttl.lobby.type.TabListContent
import net.projecttl.lobby.util.formatter
import net.projecttl.lobby.util.toMini

@OptIn(DelicateCoroutinesApi::class)
object TabListC : Command("tablist", "tl") {
	init {
		setDefaultExecutor { sender, _ ->
			sender.sendMessage(
				"""
					<gold>Usage<reset>: /tablist <action> <type> [index] "content" | {empty}
					<gold>Perfix:
					<tab><gold>literal<reset>: <>
					<tab><gold>integer<reset>: []
					<tab><gold>string<reset>: ""
					<tab><gold>bool<reset>: {}
					<gold>action<reset>:
					<tab><gold>-<reset> get
					<tab><gold>-<reset> set
					<tab><gold>-<reset> add (not included index)
					<tab><gold>-<reset> del {empty}
					<gold>type<reset>:
					<tab><gold>-<reset> HEADER
					<tab><gold>-<reset> FOOTER
				""".trimIndent().toMini()
			)
		}

		val getArgument = ArgumentType.Literal("get")
		val setArgument = ArgumentType.Literal("set")
		val addArgument = ArgumentType.Literal("add")
		val delArgument = ArgumentType.Literal("del")

		val typeArgument = ArgumentType.Enum("type", TabListContent::class.java)

		val indexArgument = ArgumentType.Integer("index")
		val contentArgument = ArgumentType.StringArray("content")

		val emptyArgument = ArgumentType.Boolean("empty")

		addSyntax({ sender, ctx ->
			if (sender is Player) {
				if (sender.username != "WH64") {
					return@addSyntax
				}
			}

			GlobalScope.launch {
				val type = ctx.get(typeArgument)
				val header = tabListService.getHeader()
				val footer = tabListService.getFooter()

				@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
				when (type) {
					TabListContent.HEADER -> sender.sendMessage("<gold>HEADER<white>: <reset>$header".toMini())
					TabListContent.FOOTER -> sender.sendMessage("<gold>FOOTER<white>: <reset>$footer".toMini())
				}
			}
		}, getArgument, typeArgument)

		addSyntax({ sender, ctx ->
			if (sender is Player) {
				if (sender.username != "WH64") {
					return@addSyntax
				}
			}

			GlobalScope.launch {
				val type = ctx.get(typeArgument)
				val index = ctx.get(indexArgument)
				val content = ctx.get(contentArgument)
				val str = content.joinToString(separator = " ").formatter()

				try {
					@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
					when (type) {
						TabListContent.HEADER -> tabListService.setHeader(index, str)
						TabListContent.FOOTER -> tabListService.setFooter(index, str)
					}
				} catch (ex: Exception) {
					sender.sendMessage("<red>${type.name.lowercase()} $index line content is not exist (aborted)".toMini())
					return@launch
				}

				sender.sendMessage("Set ${type.name.lowercase()} $index line content: $str".toMini())
			}
		}, setArgument, typeArgument, indexArgument, contentArgument)

		addSyntax({ sender, ctx ->
			if (sender is Player) {
				if (sender.username != "WH64") {
					return@addSyntax
				}
			}

			GlobalScope.launch {
				val type = ctx.get(typeArgument)
				val content = ctx.get(contentArgument)
				val str = content.joinToString(separator = " ").formatter()

				@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
				when (type) {
					TabListContent.HEADER -> tabListService.addHeader(str)
					TabListContent.FOOTER -> tabListService.addFooter(str)
				}

				sender.sendMessage("Add ${type.name.lowercase()} ${tabListService.index(type == TabListContent.HEADER)} line content: $str".toMini())
			}
		}, addArgument, typeArgument, contentArgument)

		addSyntax({ sender, ctx ->
			if (sender is Player) {
				if (sender.username != "WH64") {
					return@addSyntax
				}
			}

			GlobalScope.launch {
				val type = ctx.get(typeArgument)
				val index = ctx.get(indexArgument)
				val empty = ctx.get(emptyArgument)

				try {
					@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
					when (type) {
						TabListContent.HEADER -> tabListService.delHeader(index, empty)
						TabListContent.FOOTER -> tabListService.delFooter(index, empty)
					}
				} catch (ex: Exception) {
					sender.sendMessage("<red>${type.name.lowercase()} $index line content is not exist (aborted)".toMini())
					return@launch
				}

				sender.sendMessage("${if (empty) "Clear" else "Delete"} ${type.name.lowercase()} $index line content".toMini())
			}
		}, delArgument, typeArgument, indexArgument, emptyArgument)
	}
}
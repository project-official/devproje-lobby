package net.projecttl.lobby.command

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.projecttl.lobby.util.toMini

object Teleport : Command("teleport", "tp") {
	private enum class ReasonType {
		NO_ARGUMENT,
		NO_PLAYER,
		NO_PERMISSION;
	}

	private fun fallback(sender: CommandSender, reason: ReasonType) {
		when (reason) {
			ReasonType.NO_ARGUMENT 	 -> sender.sendMessage("<red>Command must be contains x, y, z values</red>".toMini())
			ReasonType.NO_PLAYER   	 -> sender.sendMessage("<red>You must be a player to teleport command!</red>".toMini())
			ReasonType.NO_PERMISSION -> {}
		}
	}

	init {
		setDefaultExecutor { sender, _ ->
			if (sender !is Player) {
				return@setDefaultExecutor fallback(sender, ReasonType.NO_PLAYER)
			}
		}

		val argX = ArgumentType.Double("x")
		val argY = ArgumentType.Double("y")
		val argZ = ArgumentType.Double("z")

		addSyntax({ sender, ctx ->
			if (sender !is Player) {
				return@addSyntax fallback(sender, ReasonType.NO_PLAYER)
			}

			val x = ctx.get(argX) ?: return@addSyntax fallback(sender, ReasonType.NO_ARGUMENT)
			val y = ctx.get(argY) ?: return@addSyntax fallback(sender, ReasonType.NO_ARGUMENT)
			val z = ctx.get(argZ) ?: return@addSyntax fallback(sender, ReasonType.NO_ARGUMENT)

			sender.teleport(Pos(x, y, z))
			sender.sendMessage("Teleported $x $y $z")
		}, argX, argY, argZ)
	}
}
package net.projecttl.lobby.command

import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import net.projecttl.lobby.util.toMini

object Fly : Command("fly") {
	init {
		setDefaultExecutor { sender, _ ->
			if (sender !is Player) {
				return@setDefaultExecutor sender.sendMessage("<red>You're not player!".toMini())
			}

			sender.isAllowFlying = true
			sender.isFlying = !sender.isAllowFlying
		}
	}
}
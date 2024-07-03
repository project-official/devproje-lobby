package net.projecttl.lobby.task

import kotlinx.coroutines.*
import net.projecttl.lobby.instance
import net.projecttl.lobby.tabListService
import net.projecttl.lobby.util.performance
import net.projecttl.lobby.util.toMini

@OptIn(DelicateCoroutinesApi::class)
object TabList {
	fun run() {
		GlobalScope.launch {
			while (isActive) {
				instance.sendPlayerListHeaderAndFooter(
					tabListService.getHeader().performance().toMini(),
					tabListService.getFooter().performance().toMini()
				)

				delay(25)
			}
		}
	}
}
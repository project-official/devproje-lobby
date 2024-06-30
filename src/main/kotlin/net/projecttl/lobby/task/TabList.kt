package net.projecttl.lobby.task

import kotlinx.coroutines.*
import net.projecttl.lobby.instance
import net.projecttl.lobby.util.toMini

@OptIn(DelicateCoroutinesApi::class)
object TabList {
	fun run() {
		GlobalScope.launch {
			while (isActive) {
				val rt = Runtime.getRuntime()
				instance.sendPlayerListHeaderAndFooter(
					("\n <gradient:#00ffff:#0091ff><bold>PROJECT_TL'S OFFICIAL SERVER \n" +
						" <reset><white>프로젝트의 마크 서버에 오신걸 환영합니다! \n").toMini(),
					("\n <gray>Using memory: ${String.format("%.2f", (rt.maxMemory() - (rt.maxMemory() - rt.freeMemory())) * 0.001 * 0.001 * 0.001)}GB/${String.format("%.2f", rt.maxMemory() * 0.001 * 0.001 * 0.001)}GB \n" +
						" Lobby Server powered by <bold><gradient:#ff6c32:#ff76b6>Minestom \n").toMini()
				)

				delay(25)
			}
		}
	}
}
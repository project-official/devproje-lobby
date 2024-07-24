package net.projecttl.lobby.ui

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemComponent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.HeadProfile
import net.projecttl.lobby.logger
import net.projecttl.lobby.util.asString
import net.projecttl.lobby.util.moveServer
import net.projecttl.lobby.util.toMini

// TODO: this is temp class
private class ItemData(val item: ItemStack, val exec: (Player) -> Unit)

// Server List
// TODO: create server list for use database
@OptIn(DelicateCoroutinesApi::class)
private val default = ItemData(ItemStack.builder(Material.PLAYER_HEAD).apply {
	set(ItemComponent.ITEM_NAME, "<aqua>프젝 놀이터".toMini())
	set(ItemComponent.PROFILE, HeadProfile(PlayerSkin.fromUsername("Project_IO")!!))
	lore(listOf(
		"<yellow>Comming Soon!".toMini()
	))
}.build()) {
//	GlobalScope.launch {
//		var bar: BossBar
//		var progress = 0.0f
//		for (i in 3 downTo 1) {
//			if (!it.isOnline) {
//				return@launch
//			}
//
//			bar = BossBar.bossBar(
//				"${i}초 후에 <aqua>\"프젝 놀이터\"<reset>서버로 이동 합니다".toMini(),
//				progress,
//				BossBar.Color.GREEN,
//				BossBar.Overlay.PROGRESS
//			)
//
//			it.showBossBar(bar)
//			delay(1000)
//			progress += 0.5f
//			it.hideBossBar(bar)
//		}

//		it.sendMessage("<aqua>\"프젝 놀이터\"<reset>로 전송 중...".toMini())
//		it.moveServer("playground")

//		for (i in 0..10) {
//			if (!it.isOnline) {
//				logger.info("${it.username} is sent for <aqua>\"프젝 놀이터\"<reset> server")
//				return@launch
//			}
//
//			delay(1000)
//		}
//
//		it.sendMessage("""
//			<red>현재 <aqua>"프젝 놀이터" <red>서버에 문제가 발생 하였습니다.
//			문제가 지속될 경우 디스코드 <yellow>wh64<red>로 문의 바랍니다.
//		""".trimIndent().toMini())
//	}

	it.sendMessage("<red>준비중인 서버예요. 오픈까지 기다려 주세요!".toMini())
	it.closeInventory()
}

@OptIn(DelicateCoroutinesApi::class)
private val gonnyon = ItemData(
	ItemStack.builder(Material.PLAYER_HEAD).apply {
		set(ItemComponent.ITEM_NAME, "<green>곤뇬 야생서버".toMini())
		set(ItemComponent.PROFILE, HeadProfile(PlayerSkin.fromUuid("94dd9aea-fc4f-4ee5-a592-a7ee1ab10b4d")!!))
		lore(listOf(
			"<yellow>곤뇬 박물관 전용 마인크레프트 서버다".toMini(),
			"<red>Whitelist Required".toMini()
		))
	}.build()) {
	GlobalScope.launch {
		var bar: BossBar
		var progress = 0.0f
		for (i in 3 downTo 1) {
			if (!it.isOnline) {
				return@launch
			}

			bar = BossBar.bossBar(
				"${i}초 후에 <green>\"곤뇬 야생서버\"<reset>서버로 이동 합니다".toMini(),
				progress,
				BossBar.Color.GREEN,
				BossBar.Overlay.PROGRESS
			)

			it.showBossBar(bar)
			delay(1000)
			progress += 0.5f
			it.hideBossBar(bar)
		}

		it.sendMessage("<green>\"곤뇬 야생서버\"<reset>로 전송 중...".toMini())
		it.moveServer("gonnyon")

		for (i in 0..10) {
			if (!it.isOnline) {
				logger.info("${it.username} is sent for <green>\"프젝 놀이터\"<reset> server")
				return@launch
			}

			delay(1000)
		}

		it.sendMessage("""
			<red>현재 <green>"곤뇬 야생서버" <red>서버에 문제가 발생 하였습니다.
			문제가 지속될 경우 디스코드 <yellow>wh64<red>로 문의 바랍니다.
		""".trimIndent().toMini())
	}

	it.closeInventory()
}

private val discord = ItemData(ItemStack.builder(Material.PLAYER_HEAD).apply {
	set(ItemComponent.ITEM_NAME, "<color:#7289DA>쪼의 평범한 디스코드 서버".toMini())
	set(ItemComponent.PROFILE, HeadProfile(PlayerSkin.fromUsername("wh64")!!))
}.build()) {
	it.sendMessage("<color:#7289DA>쪼의 평화로운 디스코드 서버: ".toMini().append(
		Component.text("https://discord.gg/FpJCWv45FV", NamedTextColor.YELLOW)
			.decoration(TextDecoration.UNDERLINED, true)
			.hoverEvent(HoverEvent.showText(Component.text("링크를 클릭하여 서버에 접속해 보세요!")))
			.clickEvent(ClickEvent.openUrl("https://discord.gg/FpJCWv45FV"))
	))
	it.closeInventory()
}

private val close = ItemData(ItemStack.builder(Material.BARRIER).apply {
	set(ItemComponent.ITEM_NAME, "<red>Close".toMini())
}.build()) {
	it.closeInventory()
}

object UIHandler {
	private val list = listOf(
		default,
		gonnyon,
		discord
	)

	fun run(node: EventNode<Event>) {
		node.addListener(InventoryPreClickEvent::class.java) { event ->
			val name = event.clickedItem.get(ItemComponent.ITEM_NAME)
			if (close.item.get(ItemComponent.ITEM_NAME)?.asString() == name?.asString()) {
				close.exec.invoke(event.player)
				return@addListener
			}

			list.forEach { i ->
				if (i.item.get(ItemComponent.ITEM_NAME)?.asString() == name?.asString()) {
					i.exec.invoke(event.player)
				}
			}
		}
	}
}

class ServerUI(private val player: Player) {
	private val inventory = Inventory(InventoryType.CHEST_2_ROW, "<green>Server List".toMini())
	private val empty = ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE).apply {
		set(ItemComponent.ITEM_NAME, " ".toMini())
	}.build()

	init {
		// TODO
		inventory.setItemStack(0, default.item)
		inventory.setItemStack(1, gonnyon.item)
		for (i in 10..17) {
			inventory.setItemStack(i, empty)
		}

		inventory.setItemStack(9, discord.item)
		inventory.setItemStack(17, close.item)
	}

	fun build() {
		player.openInventory(inventory)
	}
}
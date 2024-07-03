package net.projecttl.lobby.ui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import net.projecttl.lobby.serverListService

class ServerUI(val player: Player) {
	suspend fun render(): List<ItemStack> = serverListService.findAll().map { item ->
		ItemStack.of(Material.PLAYER_HEAD).builder().let {
			it.customName(Component.text(item.obj.name))
			val lore = item.obj.description.split(";")
			if (lore.isNotEmpty()) {
				it.lore(lore.map { l ->
					Component.text(l).decoration(TextDecoration.ITALIC, false)
				}.toList())
			}

			it.setTag(Tag.String("profile"), "WH74")

			it.build()
		}
	}.toList()
}
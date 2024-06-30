package net.projecttl.lobby.handler

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.tag.Tag
import net.minestom.server.utils.NamespaceID

class SkullHandler : BlockHandler {
	override fun getNamespaceId() = NamespaceID.from(Key.key("minecraft:skull"))
	override fun getBlockEntityTags() = mutableListOf(
		Tag.String("ExtraType"),
		Tag.NBT("SkullOwner")
	)
}

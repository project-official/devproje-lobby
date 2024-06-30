package net.projecttl.lobby.handler

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.tag.Tag
import net.minestom.server.utils.NamespaceID

class SignHandler : BlockHandler {
	override fun getNamespaceId() = NamespaceID.from(Key.key("minecraft:sign"))
	override fun getBlockEntityTags() = mutableListOf(
		Tag.String("ExtraType"),
		Tag.NBT("Color"),
		Tag.NBT("GlowingText"),
		Tag.NBT("Text1"),
		Tag.NBT("Text2"),
		Tag.NBT("Text3"),
		Tag.NBT("Text4")
	)
}

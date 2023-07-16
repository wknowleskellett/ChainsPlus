package dev.williamknowleskellett.chains_plus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Material;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.MapColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ChainsPlusMod implements ModInitializer {
	public static final String ID = "chains_plus";
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static boolean isLanternGravity = true;

	public static final LinkBlock LINK_BLOCK = Registry.register(Registries.BLOCK, new Identifier(ID, "link"),
			new LinkBlock(Settings.of(Material.METAL, MapColor.CLEAR).requiresTool().strength(5.0F, 6.0F)
					.sounds(BlockSoundGroup.CHAIN).nonOpaque()));
	public static final LinkItem LINK_ITEM = Registry.register(Registries.ITEM, new Identifier(ID, "link"),
			new LinkItem(LINK_BLOCK, new Item.Settings()));

	@Override
	public void onInitialize() {
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), LINK_BLOCK);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
			content.addAfter(Items.HONEY_BLOCK, LINK_ITEM);
		});
	}
}

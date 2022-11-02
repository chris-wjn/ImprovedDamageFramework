package net.cwjn.idf.block;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.item.IDFItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class IDFBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ImprovedDamageFramework.MOD_ID);

    public static final RegistryObject<Block> BONFIRE_BASE = register("bonfire_base",
            () -> new BonfireBlock(BlockBehaviour.Properties.of(Material.SAND).sound(SoundType.SAND)
                    .strength(6f).instabreak().noOcclusion().noCollission().explosionResistance(3600000f)), CreativeModeTab.TAB_DECORATIONS);

    private static <T extends Block> RegistryObject<T> register(final String name, final Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> returnBlock = BLOCKS.register(name, block);
        registerBlockItem(name, returnBlock, tab);
        return returnBlock;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return IDFItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

}

package net.cwjn.idf.block.entity;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.block.IDFBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IDFBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ImprovedDamageFramework.MOD_ID);

    public static final RegistryObject<BlockEntityType<BonfireBlockEntity>> BONFIRE_BASE =
            BLOCK_ENTITIES.register("bonfire_base", () ->
                    BlockEntityType.Builder.of(BonfireBlockEntity::new,
                            IDFBlocks.BONFIRE_BASE.get()).build(null));

}

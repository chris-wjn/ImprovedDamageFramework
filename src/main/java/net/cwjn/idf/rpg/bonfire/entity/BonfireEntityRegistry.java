package net.cwjn.idf.rpg.bonfire.entity;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.rpg.bonfire.BonfireBlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BonfireEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ImprovedDamageFramework.MOD_ID);

    public static final RegistryObject<BlockEntityType<BonfireBlockEntity>> BONFIRE_BASE =
            BLOCK_ENTITIES.register("bonfire_base", () ->
                    BlockEntityType.Builder.of(BonfireBlockEntity::new,
                            BonfireBlockRegistry.BONFIRE_BASE.get()).build(null));

}

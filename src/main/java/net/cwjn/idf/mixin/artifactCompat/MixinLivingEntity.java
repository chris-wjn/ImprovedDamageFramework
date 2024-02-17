package net.cwjn.idf.mixin.artifactCompat;

import artifacts.common.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Redirect(
            method = "getEquipmentSlotForItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/minecraftforge/common/ToolAction;)Z"
            )
    )
    private static boolean nullGuardForArtifactCompat(ItemStack instance, ToolAction toolAction) {
        if (ModConfig.server == null) return false;
        else return instance.canPerformAction(toolAction);
    }

    @Redirect(
            method = "getEquipmentSlotForItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z",
                    ordinal = 1
            )
    )
    private static boolean elytraCheck(ItemStack instance, Item pItem) {
        return instance.getItem() instanceof ElytraItem;
    }

}

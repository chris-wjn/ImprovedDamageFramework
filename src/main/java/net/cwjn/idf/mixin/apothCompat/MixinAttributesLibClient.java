package net.cwjn.idf.mixin.apothCompat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.apotheosis.core.attributeslib.client.AttributesLibClient;

import java.util.function.Consumer;

@Mixin(AttributesLibClient.class)
public class MixinAttributesLibClient {

    @Inject(method = "applyModifierTooltips", at = @At("HEAD"), cancellable = true, remap = false)
    private static void applyModifierTooltips(Player player, ItemStack stack, Consumer<Component> tooltip, TooltipFlag flag, CallbackInfo callback) {
        callback.cancel();
    }

}

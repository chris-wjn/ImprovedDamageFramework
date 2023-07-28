package net.cwjn.idf.mixin;

import net.cwjn.idf.api.event.OnItemStackCreatedEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.ClientConfig;
import net.cwjn.idf.util.Util;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.UnaryOperator;

import static net.cwjn.idf.data.CommonData.WEAPON_TAG;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Ljava/util/function/UnaryOperator;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent removeRarityStyler(MutableComponent instance, UnaryOperator<Style> p_130939_) {
        if (this.hasTag() && this.getTag().contains(WEAPON_TAG)) {
            if (ClientConfig.DISPLAY_DAMAGE_CLASS_ICON.get()) instance.append(Util.writeIcon(this.getTag().getString(WEAPON_TAG), true));
            instance.append(Component.translatable("idf." + this.getTag().getString(WEAPON_TAG) + "_tooltip"));
        }
        return instance;
    }

    @Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasCustomHoverName()Z"))
    private boolean removeItalicName(ItemStack instance) {
        return false;
    }

    //@Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 1))
    private boolean removeEnchantmentDisplay(int hideFlags, ItemStack.TooltipPart tooltipPart) {
        return false;
    }

    @Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 2))
    private boolean removeColourDisplay(int hideFlags, ItemStack.TooltipPart tooltipPart) {
        return false;
    }

    @Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 3))
    private boolean removeVanillaModifiersTooltip(int hideFlags, ItemStack.TooltipPart tooltipPart) {
        return false;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void constructorInjection(ItemLike item, int amount, CompoundTag capNBT, CallbackInfo callback) {
        OnItemStackCreatedEvent event = new OnItemStackCreatedEvent((ItemStack)(Object)this);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Shadow @Nullable public abstract CompoundTag getTag();
    @Shadow public abstract boolean hasTag();
}


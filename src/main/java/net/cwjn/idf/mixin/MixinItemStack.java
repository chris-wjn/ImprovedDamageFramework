package net.cwjn.idf.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.cwjn.idf.api.event.ReplaceAttributeModifierEvent;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.function.UnaryOperator;

import static net.cwjn.idf.data.CommonData.WEAPON_TAG;
import static net.cwjn.idf.util.Util.offensiveAttribute;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow public abstract Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pSlot);

    private static final Multimap<Attribute, AttributeModifier> emptyMap = HashMultimap.create();

    @Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Ljava/util/function/UnaryOperator;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent removeRarityStyler(MutableComponent instance, UnaryOperator<Style> p_130939_) {
        if (this.hasTag() && this.getTag().contains(WEAPON_TAG)) {
            if (!CommonConfig.LEGENDARY_TOOLTIPS_COMPAT_MODE.get()) instance.append(Util.writeIcon(this.getTag().getString(WEAPON_TAG), true));
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
        MinecraftForge.EVENT_BUS.post(new ReplaceAttributeModifierEvent((ItemStack)(Object)this));
        return false;
    }

    @Shadow @Nullable public abstract CompoundTag getTag();
    @Shadow public abstract boolean hasTag();
}


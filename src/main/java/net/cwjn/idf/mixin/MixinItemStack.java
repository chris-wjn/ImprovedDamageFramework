package net.cwjn.idf.mixin;

import com.google.common.collect.HashMultimap;
import net.cwjn.idf.api.event.OnItemStackCreatedEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.ClientConfig;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.util.Util;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.UnaryOperator;

import static net.cwjn.idf.data.CommonData.RANGED_TAG;
import static net.cwjn.idf.data.CommonData.WEAPON_TAG;
import static net.cwjn.idf.util.Util.offensiveAttribute;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

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
        return false;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void constructorInjection(ItemLike item, int amount, CompoundTag capNBT, CallbackInfo callback) {
        OnItemStackCreatedEvent event = new OnItemStackCreatedEvent((ItemStack)(Object)this);
        MinecraftForge.EVENT_BUS.post(event);
    }

    /*
    In vanilla, there are three kinds of attribute modifiers: Addition, Multiply Base, and Multiply Total.
    Multiply Base works very strangely and doesn't really make sense in the context of this mod. So,
    this method will take the damage, attackspeed, and force of melee weapons and convert them all to an
    addition modifier.
    Furthermore, we do not want bows and crossbows to transfer their damage stats over to the player,
    as they would then be able to use them as melee weapons.
     */
    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void reworkAttributeModifiers(EquipmentSlot pSlot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir, Multimap<Attribute, AttributeModifier> multimap) {
        //first lets instantiate two maps, one to be modified and returned and one to get the original modifiers
        Multimap<Attribute, AttributeModifier> newMap = HashMultimap.create();
        ItemStack item = (ItemStack)(Object)this;
        if (!item.hasTag()) {
            cir.setReturnValue(multimap);
            return;
        }
        if (!item.getTag().contains(WEAPON_TAG)) {
            cir.setReturnValue(multimap);
            return;
        }
        //weapon case
        for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
            String name = entry.getKey().getDescriptionId().toLowerCase();
            if (offensiveAttribute.test(name)) {
                if (entry.getKey() == Attributes.ATTACK_SPEED) {
                    Collection<AttributeModifier> mods = multimap.get(entry.getKey());
                    final double flat = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                    double f1 = flat + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(flat)).sum();
                    double f2 = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                    newMap.put(entry.getKey(), new AttributeModifier(Util.UUID_STAT_CONVERSION[pSlot.getIndex()], "conversion", (f2 * (f1 + 4.0)) - 4.0, ADDITION));
                    continue;
                }
                Collection<AttributeModifier> mods = multimap.get(entry.getKey());
                final double flat = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                double f1 = flat + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(flat)).sum();
                double f2 = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                newMap.put(entry.getKey(), new AttributeModifier(Util.UUID_STAT_CONVERSION[pSlot.getIndex()], "conversion", f1 * f2, ADDITION));
            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        cir.setReturnValue(newMap);
    }

    @Shadow @Nullable public abstract CompoundTag getTag();
    @Shadow public abstract boolean hasTag();
}


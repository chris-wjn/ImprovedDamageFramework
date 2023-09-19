package net.cwjn.idf.mixin.bettercombat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.bettercombat.logic.PlayerAttackHelper;
import net.cwjn.idf.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Map;

import static net.cwjn.idf.data.CommonData.RANGED_TAG;
import static net.cwjn.idf.data.CommonData.WEAPON_TAG;
import static net.cwjn.idf.util.Util.offensiveAttribute;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

@Mixin(PlayerAttackHelper.class)
public class MixinPlayerAttackHelper {

    @Redirect(method = "setAttributesForOffHandAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private static Multimap<Attribute, AttributeModifier> fixDualWielding(ItemStack item, EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> newMap = HashMultimap.create();
        Multimap<Attribute, AttributeModifier> oldMap = item.getAttributeModifiers(slot);
        if (!item.hasTag()) {
            return oldMap;
        }
        if (!item.getTag().contains(WEAPON_TAG)) {
            return oldMap;
        }
        //weapon case
        boolean isRanged = item.getTag().getBoolean(RANGED_TAG);
        for (Map.Entry<Attribute, AttributeModifier> entry : oldMap.entries()) {
            String name = entry.getKey().getDescriptionId().toLowerCase();
            if (offensiveAttribute.test(name)) {
                if (isRanged) continue;
                if (entry.getKey() == Attributes.ATTACK_SPEED) {
                    Collection<AttributeModifier> mods = oldMap.get(entry.getKey());
                    final double flat = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                    double f1 = flat + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(flat)).sum();
                    double f2 = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                    newMap.put(entry.getKey(), new AttributeModifier(Util.UUID_STAT_CONVERSION[slot.getIndex()], "conversion", (f2 * (f1 + 4.0)) - 4.0, ADDITION));
                    continue;
                }
                Collection<AttributeModifier> mods = oldMap.get(entry.getKey());
                final double flat = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                double f1 = flat + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(flat)).sum();
                double f2 = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                newMap.put(entry.getKey(), new AttributeModifier(Util.UUID_STAT_CONVERSION[slot.getIndex()], "conversion", f1 * f2, ADDITION));
            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        return newMap;
    }

}

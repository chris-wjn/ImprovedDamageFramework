package net.cwjn.idf.mixin.bettercombat;

import com.google.common.collect.Multimap;
import net.bettercombat.logic.PlayerAttackHelper;
import net.cwjn.idf.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerAttackHelper.class)
public class MixinPlayerAttackHelper {

    @Redirect(method = "setAttributesForOffHandAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private static Multimap<Attribute, AttributeModifier> fixDualWielding(ItemStack item, EquipmentSlot slot) {
        return Util.getReworkedAttributeMap(item, slot);
    }

}

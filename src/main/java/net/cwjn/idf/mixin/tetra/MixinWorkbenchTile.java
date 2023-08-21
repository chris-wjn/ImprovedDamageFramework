package net.cwjn.idf.mixin.tetra;

import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import se.mickelus.tetra.aspect.ItemAspect;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.effect.CritEffect;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem;
import se.mickelus.tetra.module.ItemModule;

import java.util.Map;

import static net.cwjn.idf.data.CommonData.WEAPON_TAG;

@Mixin(WorkbenchTile.class)
public class MixinWorkbenchTile {

    @Inject(remap = false, method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/util/LazyOptional;ifPresent(Lnet/minecraftforge/common/util/NonNullConsumer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addDamageClass(Player player, CallbackInfo ci, ItemStack targetStack, ItemStack upgradedStack, IModularItem item, BlockState blockState, Map availableTools, ItemStack[] materials, ItemStack[] materialsAltered, ItemStack tempStack) {
        CompoundTag tag = upgradedStack.getTag();
        if (tag == null) return;
        tag.putBoolean(CommonData.EQUIPMENT_TAG, true);
        ItemModule[] modules = item.getMajorModules(upgradedStack);
        String dc = "strike";
        tag.putInt(CommonData.TETRA_CRIT_LEVEL, item.getEffectLevel(upgradedStack, ItemEffect.criticalStrike));
        tag.putFloat(CommonData.TETRA_CRIT_EFFICIENCY, item.getEffectEfficiency(upgradedStack, ItemEffect.criticalStrike));
        if (item instanceof ModularBowItem || item instanceof ModularCrossbowItem) {
            tag.putBoolean(CommonData.RANGED_TAG, true);
            dc = "pierce";
        } else {
            int sls = 0;
            int prc = 0;
            int str = 0;
            boolean thrown = false;
            for (ItemModule module : modules) {
                if (module.getAspects(upgradedStack).contains(ItemAspect.throwable)) thrown = true;
                sls += module.getAspects(upgradedStack).getLevel(ItemAspect.edgedWeapon);
                str += module.getAspects(upgradedStack).getLevel(ItemAspect.bluntWeapon);
                prc += module.getAspects(upgradedStack).getLevel(ItemAspect.pointyWeapon);
            }
            if (thrown) tag.putBoolean(CommonData.THROWN_TAG, true);
            int highest = Math.max(sls, Math.max(prc, str));
            if (highest == sls) dc = "slash";
            else if (highest == prc) dc = "pierce";
        }
        tag.putString(WEAPON_TAG, dc);
        if (CommonConfig.LEGENDARY_TOOLTIPS_COMPAT_MODE.get()) {
            tag.putInt("idf.tooltip_border", Util.getItemBorderType(dc, upgradedStack.getAttributeModifiers(LivingEntity.getEquipmentSlotForItem(upgradedStack))));
        }
    }

}

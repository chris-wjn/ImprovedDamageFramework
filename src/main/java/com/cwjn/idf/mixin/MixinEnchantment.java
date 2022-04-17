package com.cwjn.idf.mixin;

import com.cwjn.idf.Color;
import com.cwjn.idf.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class MixinEnchantment {
    /**
     * @author cwJn
     */
    @Overwrite
    public Component getFullname(int level) {
        TranslatableComponent component = new TranslatableComponent(this.getDescriptionId());
        if (this.isCurse()) {
            component.withStyle(ChatFormatting.RED);
        } else {
            component.withStyle(ChatFormatting.GRAY);
        }

        MutableComponent mutablecomponent;
        double maxLevel = this.getMaxLevel();
        double currentLevel = level;
        double levelAsPercentage = currentLevel/maxLevel;

        if (maxLevel == 1) {
            mutablecomponent = Util.withColor(component, Color.FULLPURPLE);
        } else {
            if (levelAsPercentage <= 0.33) {
                mutablecomponent = Util.withColor(component, Color.ENCHANTINGGRAY).append(" ").append(Util.withColor(new TranslatableComponent("enchantment.level." + level), Color.ENCHANTINGGRAY));
            } else if (levelAsPercentage <= 0.66) {
                mutablecomponent = Util.withColor(component, Color.WEAKPURPLE).append(" ").append(Util.withColor(new TranslatableComponent("enchantment.level." + level), Color.WEAKPURPLE));
            } else if (levelAsPercentage <= 0.99) {
                mutablecomponent = Util.withColor(component, Color.FULLPURPLE).append(" ").append(Util.withColor(new TranslatableComponent("enchantment.level." + level), Color.FULLPURPLE));
            } else {
                mutablecomponent = Util.withColor(component, Color.MAGICBLUE).append(" ").append(Util.withColor(new TranslatableComponent("enchantment.level." + level), Color.MAGICBLUE));
            }
        }
        return mutablecomponent;
    }

    @Shadow
    public String getDescriptionId() {
        throw new IllegalStateException("Mixin failed to shadow getDescriptionId()");
    }
    @Shadow
    public boolean isCurse() {
        throw new IllegalStateException("Mixin failed to shadow isCurse()");
    }
    @Shadow
    public int getMaxLevel() {
        throw new IllegalStateException("Mixin failed to shadow getMaxLevel()");
    }

}

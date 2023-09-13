package net.cwjn.idf.mixin.damage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SweetBerryBushBlock.class)
public class MixinSweetBerryBushBlock {

    @ModifyConstant(method = "entityInside", constant = @Constant(floatValue = 1.0f))
    private float changeDamage(float constant, BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        return pEntity instanceof LivingEntity? ((LivingEntity) pEntity).getMaxHealth()*0.05f : constant;
    }

}

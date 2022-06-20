package net.cwjn.idf.mixin;

import net.cwjn.idf.attribute.AttributeRegistry;
import net.cwjn.idf.capability.AuxiliaryProvider;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import se.mickelus.mutil.util.CastOptional;
import se.mickelus.tetra.effect.EffectHelper;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.effect.ItemEffectHandler;
import se.mickelus.tetra.effect.SweepingEffect;

@Mixin(SweepingEffect.class)
public class MixinSweepingEffect {

    /**
     * @author cwJn
     */
    @Overwrite(remap = false)
    public static void sweepAttack(ItemStack itemStack, LivingEntity target, LivingEntity attacker, int sweepingLevel) {
        boolean trueSweep = EffectHelper.getEffectLevel(itemStack, ItemEffect.truesweep) > 0;
        float damage = (float)Math.max(attacker.getAttributeValue(Attributes.ATTACK_DAMAGE) * (double)((float)sweepingLevel * 0.125F), 1.0);
        float fd = (float) (attacker.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float wd = (float) (attacker.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float ld = (float) (attacker.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float md = (float) (attacker.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float dd = (float) (attacker.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float pen = (float) attacker.getAttributeValue(AttributeRegistry.PENETRATING.get());
        float knockback = trueSweep ? (float)(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, itemStack) + 1) * 0.5F : 0.5F;
        double range = 1.0 + EffectHelper.getEffectEfficiency(itemStack, ItemEffect.sweeping);
        double reach = attacker.getAttributeValue((Attribute) ForgeMod.REACH_DISTANCE.get());
        attacker.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range, 0.25, range)).stream().filter((entity) -> {
            return entity != attacker;
        }).filter((entity) -> {
            return entity != target;
        }).filter((entity) -> {
            return !attacker.isAlliedTo(entity);
        }).filter((entity) -> {
            return attacker.distanceToSqr(entity) < (range + reach) * (range + reach);
        }).forEach((entity) -> {
            entity.knockback((double)knockback, (double) Mth.sin(attacker.getYRot() * 3.1415927F / 180.0F), (double)(-Mth.cos(attacker.getYRot() * 3.1415927F / 180.0F)));
            DamageSource damageSource = attacker instanceof Player ?
                    new IDFEntityDamageSource("player", attacker, fd, wd, ld, md, dd, pen,
                    attacker.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("player has no damage class!")).getDamageClass())
                    :
                    new IDFIndirectEntityDamageSource("mob", attacker, entity, fd, wd, ld, md, dd, pen,
                            attacker.getCapability(AuxiliaryProvider.AUXILIARY_DATA).isPresent() ? attacker.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElse(null).getDamageClass() : "strike");
            if (trueSweep) {
                ItemEffectHandler.applyHitEffects(itemStack, entity, attacker);
                EffectHelper.applyEnchantmentHitEffects(itemStack, entity, attacker);
                causeTruesweepDamage(damageSource, damage, itemStack, attacker, entity);
            } else {
                entity.hurt(damageSource, damage);
            }

        });
        attacker.level.playSound((Player)null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
        CastOptional.cast(attacker, Player.class).ifPresent(Player::sweepAttack);
    }

    /**
     * @author cwJn
     */
    @Overwrite(remap = false)
    public static void truesweep(ItemStack itemStack, LivingEntity attacker) {
        int sweepingLevel = getSweepingLevel(itemStack);
        float damage = (float)Math.max(attacker.getAttributeValue(Attributes.ATTACK_DAMAGE) * (double)((float)sweepingLevel * 0.125F), 1.0);
        float fd = (float) (attacker.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float wd = (float) (attacker.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float ld = (float) (attacker.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float md = (float) (attacker.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float dd = (float) (attacker.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get()) * ((double)sweepingLevel * 0.125F));
        float pen = (float) attacker.getAttributeValue(AttributeRegistry.PENETRATING.get());
        float knockback = 0.5F + (float)EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, itemStack) * 0.5F;
        double range = 2.0 + EffectHelper.getEffectEfficiency(itemStack, ItemEffect.sweeping);
        Vec3 target = Vec3.directionFromRotation(attacker.getXRot(), attacker.getYRot()).normalize().scale(range).add(attacker.getEyePosition(0.0F));
        AABB aoe = new AABB(target, target);
        attacker.level.getEntitiesOfClass(LivingEntity.class, aoe.inflate(range, 1.0, range)).stream().filter((entity) -> {
            return entity != attacker;
        }).filter((entity) -> {
            return !attacker.isAlliedTo(entity);
        }).forEach((entity) -> {
            entity.knockback((double)knockback, (double)Mth.sin(attacker.getYRot() * 3.1415927F / 180.0F), (double)(-Mth.cos(attacker.getXRot() * 3.1415927F / 180.0F)));
            ItemEffectHandler.applyHitEffects(itemStack, entity, attacker);
            EffectHelper.applyEnchantmentHitEffects(itemStack, entity, attacker);
            DamageSource damageSource = attacker instanceof Player ?
                    new IDFEntityDamageSource("player", attacker, fd, wd, ld, md, dd, pen,
                            attacker.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("player has no damage class!")).getDamageClass())
                    :
                    new IDFIndirectEntityDamageSource("mob", attacker, entity, fd, wd, ld, md, dd, pen,
                            attacker.getCapability(AuxiliaryProvider.AUXILIARY_DATA).isPresent() ? attacker.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElse(null).getDamageClass() : "strike");
            causeTruesweepDamage(damageSource, damage, itemStack, attacker, entity);
        });
        attacker.level.playSound((Player)null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
        CastOptional.cast(attacker, Player.class).ifPresent(Player::sweepAttack);
    }

    /*
    boolean isCrit = player.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get())/100 >= Math.random();
        float critMultiplier = 1.0F;
        CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, target, isCrit, isCrit ? 1.5F : 1.0F);
        isCrit = hitResult != null;
        if (isCrit) {
            critMultiplier = hitResult.getDamageModifier();
        }
     */
    /**
     * @author cwJn
     */
    @Overwrite(remap = false)
    private static void causeTruesweepDamage(DamageSource damageSource, float baseDamage, ItemStack itemStack, LivingEntity attacker, LivingEntity target)  {
        float targetModifier = EnchantmentHelper.getDamageBonus(itemStack, target.getMobType());
        boolean isCrit = attacker.getAttributeValue(AttributeRegistry.CRIT_CHANCE.get())/100 >= Math.random();
        float critMultiplier = 1.0F;
        CriticalHitEvent hitResult = ForgeHooks.getCriticalHit((Player)attacker, target, isCrit, isCrit ? 1.5F : 1.0F);
        isCrit = hitResult != null;
        if (isCrit) {
            critMultiplier = hitResult.getDamageModifier();
        }
        target.hurt(damageSource, (baseDamage + targetModifier) * critMultiplier);
        if (targetModifier > 0.0F) {
            CastOptional.cast(attacker, Player.class).ifPresent((player) -> {
                player.magicCrit(target);
            });
        }

        if (critMultiplier > 1.0F) {
            attacker.getCommandSenderWorld().playSound((Player)null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.3F);
            ((Player)attacker).crit(target);
        }
    }

    @Shadow(remap = false)
    public static int getSweepingLevel(ItemStack itemStack) {
        throw new IllegalStateException("Mixin failed to shadow getSweepingLevel(ItemStack itemStack)");
    }

}

package net.cwjn.idf.mixin;

import net.cwjn.idf.Attributes.AttributeRegistry;
import net.cwjn.idf.Attributes.CapabilityProvider;
import net.cwjn.idf.Damage.IDFDamageSource;
import net.cwjn.idf.Damage.IDFEntityDamageSource;
import net.cwjn.idf.Damage.IDFIndirectEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

@Mixin(DamageSource.class)
public class MixinDamageSource {

        /**
         * @author cwJn
         */
        @Overwrite
        public static DamageSource sting(LivingEntity bee) {
            float fire = (float) bee.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
            float water = (float) bee.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
            float lightning = (float) bee.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
            float magic = (float) bee.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
            float dark = (float) bee.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
            float pen = (float) bee.getAttributeValue(AttributeRegistry.PENETRATING.get());
            String damageClass = bee.getCapability(CapabilityProvider.AUXILIARY_DATA).isPresent() ? bee.getCapability(CapabilityProvider.AUXILIARY_DATA).orElse(null).getDamageClass() : "strike";
            return new IDFEntityDamageSource("sting", bee, fire, water, lightning, magic, dark, pen, damageClass);
        }

        /**
         * @author cwJn
         */
        @Overwrite
        public static DamageSource mobAttack(LivingEntity mob) {
            float fire = (float) mob.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
            float water = (float) mob.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
            float lightning = (float) mob.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
            float magic = (float) mob.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
            float dark = (float) mob.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
            float pen = (float) mob.getAttributeValue(AttributeRegistry.PENETRATING.get());
            String damageClass = mob.getCapability(CapabilityProvider.AUXILIARY_DATA).isPresent() ? mob.getCapability(CapabilityProvider.AUXILIARY_DATA).orElse(null).getDamageClass() : "strike";
            return new IDFEntityDamageSource("mob", mob, fire, water, lightning, magic, dark, pen, damageClass);
        }

        /**
         * @author cwJn
         */
        @Overwrite
        public static DamageSource indirectMobAttack(Entity source, @Nullable LivingEntity indirectSource) {
            float fire = 0;
            float water = 0;
            float lightning = 0;
            float magic = 0;
            float dark = 0;
            float pen = 0;
            String damageClass = "strike";
            if (indirectSource != null) {
                fire = (float) indirectSource.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
                water = (float) indirectSource.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
                lightning = (float) indirectSource.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
                magic = (float) indirectSource.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
                dark = (float) indirectSource.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
                pen = (float) indirectSource.getAttributeValue(AttributeRegistry.PENETRATING.get());
                damageClass = indirectSource.getCapability(CapabilityProvider.AUXILIARY_DATA).isPresent() ? source.getCapability(CapabilityProvider.AUXILIARY_DATA).orElse(null).getDamageClass() : "strike";
            }
            return new IDFIndirectEntityDamageSource("mob", source, indirectSource, fire, water, lightning, magic, dark, pen, damageClass);
        }

        /**
         * @author cwJn
         */
        @Overwrite
        public static DamageSource playerAttack(Player player) {
            float fire = (float) player.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
            float water = (float) player.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
            float lightning = (float) player.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
            float magic = (float) player.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
            float dark = (float) player.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
            float pen = (float) player.getAttributeValue(AttributeRegistry.PENETRATING.get());
            String damageClass = player.getCapability(CapabilityProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("player has no damage class!")).getDamageClass();
            return new IDFEntityDamageSource("player", player, fire, water, lightning, magic, dark, pen, damageClass);
        }


}

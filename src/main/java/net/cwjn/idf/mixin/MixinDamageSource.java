package net.cwjn.idf.mixin;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.capability.data.AuxiliaryData;
import net.cwjn.idf.capability.data.ProjectileHelper;
import net.cwjn.idf.capability.provider.ArrowHelperProvider;
import net.cwjn.idf.capability.provider.AuxiliaryProvider;
import net.cwjn.idf.capability.provider.TridentHelperProvider;
import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
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
        float fire = (float) bee.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get());
        float water = (float) bee.getAttributeValue(IDFAttributes.WATER_DAMAGE.get());
        float lightning = (float) bee.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get());
        float magic = (float) bee.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get());
        float dark = (float) bee.getAttributeValue(IDFAttributes.DARK_DAMAGE.get());
        float pen = (float) bee.getAttributeValue(IDFAttributes.PENETRATING.get());
        float lifesteal = (float) bee.getAttributeValue(IDFAttributes.LIFESTEAL.get());
        AuxiliaryData data = bee.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElse(new AuxiliaryData());
        return new IDFEntityDamageSource("sting", bee, fire, water, lightning, magic, dark, pen, lifesteal, data.getDamageClass());
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource mobAttack(LivingEntity mob) {
        final float fire = (float) mob.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get());
        final float water = (float) mob.getAttributeValue(IDFAttributes.WATER_DAMAGE.get());
        final float lightning = (float) mob.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get());
        final float magic = (float) mob.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get());
        final float dark = (float) mob.getAttributeValue(IDFAttributes.DARK_DAMAGE.get());
        final float pen = (float) mob.getAttributeValue(IDFAttributes.PENETRATING.get());
        final float lifesteal = (float) mob.getAttributeValue(IDFAttributes.LIFESTEAL.get());
        AuxiliaryData data = mob.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElse(new AuxiliaryData());
        return new IDFEntityDamageSource("mob", mob, fire, water, lightning, magic, dark, pen, lifesteal, data.getDamageClass());
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
        float lifesteal = 0;
        String damageClass = "strike";
        if (indirectSource != null) {
            fire = (float) indirectSource.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get());
            water = (float) indirectSource.getAttributeValue(IDFAttributes.WATER_DAMAGE.get());
            lightning = (float) indirectSource.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get());
            magic = (float) indirectSource.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get());
            dark = (float) indirectSource.getAttributeValue(IDFAttributes.DARK_DAMAGE.get());
            pen = (float) indirectSource.getAttributeValue(IDFAttributes.PENETRATING.get());
            lifesteal = (float) indirectSource.getAttributeValue(IDFAttributes.LIFESTEAL.get());
            AuxiliaryData data = indirectSource.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElse(new AuxiliaryData());
            damageClass = data.getDamageClass();
        }
        return new IDFIndirectEntityDamageSource("mob", source, indirectSource, fire, water, lightning, magic, dark, pen, lifesteal, damageClass);
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource arrow(AbstractArrow arrow, @Nullable Entity indirectSource) {
        float fire = 0;
        float water = 0;
        float lightning = 0;
        float magic = 0;
        float dark = 0;
        float pen = 0;
        float lifesteal = 0;
        String damageClass = "pierce";
        if (indirectSource instanceof LivingEntity livingSource) {
            ProjectileHelper helper = livingSource.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(ProjectileHelper::new);
            fire = helper.getFire();
            water = helper.getWater();
            lightning = helper.getLightning();
            magic = helper.getMagic();
            dark = helper.getDark();
            pen = helper.getPen();
            lifesteal = helper.getLifesteal();
            damageClass = helper.getDamageClass();
        }
        return (new IDFIndirectEntityDamageSource("arrow", arrow, indirectSource, fire, water, lightning, magic, dark, pen, lifesteal, damageClass)).setProjectile();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource trident(Entity source, @Nullable Entity indirectSource) {
        float fire = 0;
        float water = 0;
        float lightning = 0;
        float magic = 0;
        float dark = 0;
        float pen = 0;
        float lifesteal = 0;
        String damageClass = "pierce";
        if (indirectSource instanceof LivingEntity livingSource) {
            ProjectileHelper helper = livingSource.getCapability(TridentHelperProvider.PROJECTILE_HELPER).orElseGet(ProjectileHelper::new);
            fire = helper.getFire();
            water = helper.getWater();
            lightning = helper.getLightning();
            magic = helper.getMagic();
            dark = helper.getDark();
            pen = helper.getPen();
            lifesteal = helper.getLifesteal();
            damageClass = helper.getDamageClass();
        }
        return (new IDFIndirectEntityDamageSource("trident", source, indirectSource, fire, water, lightning, magic, dark, pen, lifesteal, damageClass)).setProjectile();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource fireworks(FireworkRocketEntity firework, @Nullable Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("fireworks", firework, indirectSource,
                0.5f, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setExplosion();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource fireball(Fireball fireball, @Nullable Entity indirectSource) {
        return indirectSource == null ?
                (new IDFIndirectEntityDamageSource("onFire", fireball, fireball, 1, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setIsFire().setProjectile()
                :
                (new IDFIndirectEntityDamageSource("fireball", fireball, indirectSource, 1, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setIsFire().setProjectile();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource witherSkull(WitherSkull witherSkull, Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("witherSkull", witherSkull, indirectSource, 0, 0, 0, 0, 1, 0, 0, "strike")).setIsConversion().setProjectile();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource thrown(Entity source, @Nullable Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("thrown", source, indirectSource, 0, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setProjectile();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource indirectMagic(Entity source, @Nullable Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("indirectMagic", source, indirectSource, 0, 0, 0, 1, 0, 0, 0, "genric")).setIsConversion();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource thorns(Entity source) {
        return (new IDFEntityDamageSource("thorns", source, 0, 0, 0, 1, 0, 0, 0, "pierce")).setIsConversion();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource explosion(@Nullable LivingEntity livingEntity) {
        return livingEntity != null ?
                (new IDFEntityDamageSource("explosion.player", livingEntity, 0.5f, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setScalesWithDifficulty().setExplosion()
                :
                (new IDFDamageSource("explosion", 0.5f, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setScalesWithDifficulty().setExplosion();
    }

}

package net.cwjn.idf.mixin.damage;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.cwjn.idf.capability.data.IDFEntityData;
import net.cwjn.idf.capability.data.ProjectileHelper;
import net.cwjn.idf.capability.provider.ArrowHelperProvider;
import net.cwjn.idf.capability.provider.IDFEntityDataProvider;
import net.cwjn.idf.capability.provider.TridentHelperProvider;
import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
     * @reason
     * need this so mobs use their extra attributes
     */
    @Overwrite
    public static DamageSource sting(LivingEntity bee) {
        final float fire = (float) bee.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get());
        final float water = (float) bee.getAttributeValue(IDFAttributes.WATER_DAMAGE.get());
        final float lightning = (float) bee.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get());
        final float magic = (float) bee.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get());
        final float dark = (float) bee.getAttributeValue(IDFAttributes.DARK_DAMAGE.get());
        final float holy = (float) bee.getAttributeValue(IDFElement.HOLY.damage);
        final float pen = (float) bee.getAttributeValue(IDFAttributes.PENETRATING.get());
        final float lifesteal = (float) bee.getAttributeValue(IDFAttributes.LIFESTEAL.get());
        final float weight = (float) bee.getAttributeValue(IDFAttributes.FORCE.get());
        final float knockback = (float) bee.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        IDFEntityData data = bee.getCapability(IDFEntityDataProvider.ENTITY_DATA).orElse(new IDFEntityData());
        return new IDFEntityDamageSource("sting", bee, fire, water, lightning, magic, dark, holy, pen, lifesteal, knockback, weight, data.getDamageClass());
    }

    /**
     * @author cwJn
     * @reason
     * see above
     */
    @Overwrite
    public static DamageSource mobAttack(LivingEntity mob) {
        final float fire = (float) mob.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get());
        final float water = (float) mob.getAttributeValue(IDFAttributes.WATER_DAMAGE.get());
        final float lightning = (float) mob.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get());
        final float magic = (float) mob.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get());
        final float dark = (float) mob.getAttributeValue(IDFAttributes.DARK_DAMAGE.get());
        final float holy = (float) mob.getAttributeValue(IDFElement.HOLY.damage);
        final float pen = (float) mob.getAttributeValue(IDFAttributes.PENETRATING.get());
        final float lifesteal = (float) mob.getAttributeValue(IDFAttributes.LIFESTEAL.get());
        final float weight = (float) mob.getAttributeValue(IDFAttributes.FORCE.get());
        final float knockback = (float)mob.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        IDFEntityData data = mob.getCapability(IDFEntityDataProvider.ENTITY_DATA).orElse(new IDFEntityData());
        return new IDFEntityDamageSource("mob", mob, fire, water, lightning, magic, dark, holy, pen, lifesteal, knockback, weight, data.getDamageClass());
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource indirectMobAttack(Entity source, @Nullable LivingEntity indirectSource) {
        float fire = 0;
        float water = 0;
        float lightning = 0;
        float magic = 0;
        float dark = 0;
        float holy = 0;
        float pen = 0;
        float lifesteal = 0;
        float knockback = 0;
        float weight = -1;
        String damageClass = "strike";
        if (indirectSource != null) {
            fire = (float) indirectSource.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get());
            water = (float) indirectSource.getAttributeValue(IDFAttributes.WATER_DAMAGE.get());
            lightning = (float) indirectSource.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get());
            magic = (float) indirectSource.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get());
            dark = (float) indirectSource.getAttributeValue(IDFAttributes.DARK_DAMAGE.get());
            holy = (float) indirectSource.getAttributeValue(IDFElement.HOLY.damage);
            pen = (float) indirectSource.getAttributeValue(IDFAttributes.PENETRATING.get());
            lifesteal = (float) indirectSource.getAttributeValue(IDFAttributes.LIFESTEAL.get());
            knockback = (float) indirectSource.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            weight = (float) indirectSource.getAttributeValue(IDFAttributes.FORCE.get());
            IDFEntityData data = indirectSource.getCapability(IDFEntityDataProvider.ENTITY_DATA).orElse(new IDFEntityData());
            damageClass = data.getDamageClass();
        }
        return new IDFIndirectEntityDamageSource("mob", source, indirectSource, fire, water, lightning, magic, dark, holy, pen, lifesteal, knockback, weight, damageClass);
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource arrow(AbstractArrow arrow, @Nullable Entity indirectSource) {
        float fire = 0;
        float water = 0;
        float lightning = 0;
        float magic = 0;
        float dark = 0;
        float holy = 0;
        float pen = 0;
        float lifesteal = 0;
        float knockback = 0;
        float weight = -1;
        String damageClass = "pierce";
        if (indirectSource instanceof LivingEntity livingSource) {
            ProjectileHelper helper = livingSource.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(ProjectileHelper::new);
            if (helper.getCrit()) {
                fire = (float) (helper.getFire()*helper.getCritDmg()*0.01);
                water = (float) (helper.getWater()*helper.getCritDmg()*0.01);
                lightning = (float) (helper.getLightning()*helper.getCritDmg()*0.01);
                magic = (float) (helper.getMagic()*helper.getCritDmg()*0.01);
                dark = (float) (helper.getDark()*helper.getCritDmg()*0.01);
                holy = (float) (helper.getHoly()*helper.getCritDmg()*0.01);
                pen = helper.getPen();
                lifesteal = helper.getLifesteal();
                knockback = helper.getKnockback();
                weight = helper.getWeight();
                damageClass = helper.getDamageClass();
            } else {
                fire = helper.getFire();
                water = helper.getWater();
                lightning = helper.getLightning();
                magic = helper.getMagic();
                dark = helper.getDark();
                holy = helper.getHoly();
                pen = helper.getPen();
                lifesteal = helper.getLifesteal();
                knockback = helper.getKnockback();
                weight = helper.getWeight();
                damageClass = helper.getDamageClass();
            }
        }
        return (new IDFIndirectEntityDamageSource("arrow", arrow, indirectSource, fire, water, lightning, magic, dark, holy, pen, lifesteal, knockback, weight, damageClass)).setProjectile();
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource trident(Entity source, @Nullable Entity indirectSource) {
        float fire = 0;
        float water = 0;
        float lightning = 0;
        float magic = 0;
        float dark = 0;
        float holy = 0;
        float pen = 0;
        float lifesteal = 0;
        float knockback = 0;
        float weight = -1;
        String damageClass = "pierce";
        if (indirectSource instanceof LivingEntity livingSource) {
            ProjectileHelper helper = livingSource.getCapability(TridentHelperProvider.PROJECTILE_HELPER).orElseGet(ProjectileHelper::new);
            if (helper.getCrit()) {
                fire = (float) (helper.getFire()*helper.getCritDmg()*0.01);
                water = (float) (helper.getWater()*helper.getCritDmg()*0.01);
                lightning = (float) (helper.getLightning()*helper.getCritDmg()*0.01);
                magic = (float) (helper.getMagic()*helper.getCritDmg()*0.01);
                dark = (float) (helper.getDark()*helper.getCritDmg()*0.01);
                holy = (float) (helper.getHoly()*helper.getCritDmg()*0.01);
                pen = helper.getPen();
                lifesteal = helper.getLifesteal();
                knockback = helper.getKnockback();
                weight = helper.getWeight();
                damageClass = helper.getDamageClass();
            } else {
                fire = helper.getFire();
                water = helper.getWater();
                lightning = helper.getLightning();
                magic = helper.getMagic();
                dark = helper.getDark();
                holy = helper.getHoly();
                pen = helper.getPen();
                lifesteal = helper.getLifesteal();
                knockback = helper.getKnockback();
                weight = helper.getWeight();
                damageClass = helper.getDamageClass();
            }
        }
        return (new IDFIndirectEntityDamageSource("trident", source, indirectSource, fire, water, lightning, magic, dark, holy, pen, lifesteal, knockback, weight, damageClass)).setProjectile();
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource fireworks(FireworkRocketEntity firework, @Nullable Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("fireworks", firework, indirectSource,
                0.5f, 0, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setExplosion();
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource fireball(Fireball fireball, @Nullable Entity indirectSource) {
        if (indirectSource == null) {
            return new IDFIndirectEntityDamageSource("onFire", fireball, fireball, 1, 0, 0, 0, 0, 0, 0, 0, "strike").setIsConversion().setIsFire().setProjectile();
        } else if (indirectSource instanceof LivingEntity mob) {
            final float fire = (float) mob.getAttributeValue(IDFAttributes.FIRE_DAMAGE.get());
            final float water = (float) mob.getAttributeValue(IDFAttributes.WATER_DAMAGE.get());
            final float lightning = (float) mob.getAttributeValue(IDFAttributes.LIGHTNING_DAMAGE.get());
            final float magic = (float) mob.getAttributeValue(IDFAttributes.MAGIC_DAMAGE.get());
            final float dark = (float) mob.getAttributeValue(IDFAttributes.DARK_DAMAGE.get());
            final float holy = (float) mob.getAttributeValue(IDFElement.HOLY.damage);
            final float pen = (float) mob.getAttributeValue(IDFAttributes.PENETRATING.get());
            final float lifesteal = (float) mob.getAttributeValue(IDFAttributes.LIFESTEAL.get());
            final float weight = (float) mob.getAttributeValue(IDFAttributes.FORCE.get());
            final float knockback = (float) mob.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            IDFEntityData data = mob.getCapability(IDFEntityDataProvider.ENTITY_DATA).orElse(new IDFEntityData());
            return new IDFIndirectEntityDamageSource("fireball", fireball, indirectSource, fire, water, lightning, magic, dark, holy, pen, lifesteal, knockback, weight, data.getDamageClass()).setIsFire().setProjectile();
        }
        else {
            return new IDFIndirectEntityDamageSource("fireball", fireball, fireball, 1, 0, 0, 0, 0, 0, 0, 0, "strike").setIsConversion().setIsFire().setProjectile();
        }
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource witherSkull(WitherSkull witherSkull, Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("witherSkull", witherSkull, indirectSource, 0, 0, 0, 0, 1, 0, 0, 0, "strike")).setIsConversion().setProjectile();
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource thrown(Entity source, @Nullable Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("thrown", source, indirectSource, 0, 0, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setProjectile();
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource indirectMagic(Entity source, @Nullable Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("indirectMagic", source, indirectSource, 0, 0, 0, 1, 0, 0, 0, 0, "none")).setIsConversion();
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource thorns(Entity source) {
        return (new IDFEntityDamageSource("thorns", source, 0, 0, 0, 1, 0, 0, 0, 0, "pierce")).setIsConversion();
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource sonicBoom(Entity source) {
        return (new IDFEntityDamageSource("sonic_boom", source, 0, 0, 0, 0.0f, 3.0f, 0, 0, 0,"none")).setIsConversion();
    }

    /**
     * @author cwJn
     * @reason see above
     */
    @Overwrite
    public static DamageSource explosion(@Nullable LivingEntity livingEntity) {
        return livingEntity != null ?
                (new IDFEntityDamageSource("explosion.player", livingEntity, 0.5f, 0, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setScalesWithDifficulty().setExplosion()
                :
                (new IDFDamageSource("explosion", 0.5f, 0, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setScalesWithDifficulty().setExplosion();
    }

}

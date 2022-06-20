package net.cwjn.idf.mixin;

import net.cwjn.idf.attribute.AttributeRegistry;
import net.cwjn.idf.capability.AuxiliaryProvider;
import net.cwjn.idf.capability.ArrowHelperProvider;
import net.cwjn.idf.capability.ProjectileHelper;
import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
        float fire = (float) bee.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get());
        float water = (float) bee.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get());
        float lightning = (float) bee.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get());
        float magic = (float) bee.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get());
        float dark = (float) bee.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get());
        float pen = (float) bee.getAttributeValue(AttributeRegistry.PENETRATING.get());
        String damageClass = bee.getCapability(AuxiliaryProvider.AUXILIARY_DATA).isPresent() ? bee.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElse(null).getDamageClass() : "strike";
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
        String damageClass = mob.getCapability(AuxiliaryProvider.AUXILIARY_DATA).isPresent() ? mob.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElse(null).getDamageClass() : "strike";
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
            damageClass = indirectSource.getCapability(AuxiliaryProvider.AUXILIARY_DATA).isPresent() ? source.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElse(null).getDamageClass() : "strike";
        }
        return new IDFIndirectEntityDamageSource("mob", source, indirectSource, fire, water, lightning, magic, dark, pen, damageClass);
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
        String damageClass = "strike";
        if (indirectSource instanceof LivingEntity livingSource) {
            ProjectileHelper helper = livingSource.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(null);
            fire = helper.getFire();
            water = helper.getWater();
            lightning = helper.getLightning();
            magic = helper.getMagic();
            dark = helper.getDark();
            pen = helper.getPen();
            damageClass = helper.getDamageClass();
        }
        return (new IDFIndirectEntityDamageSource("arrow", arrow, indirectSource, fire, water, lightning, magic, dark, pen, damageClass)).setProjectile();
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
        String damageClass = "strike";
        if (indirectSource instanceof LivingEntity livingSource) {
            ProjectileHelper helper = livingSource.getCapability(ArrowHelperProvider.PROJECTILE_HELPER).orElseGet(null);
            fire = helper.getFire();
            water = helper.getWater();
            lightning = helper.getLightning();
            magic = helper.getMagic();
            dark = helper.getDark();
            pen = helper.getPen();
            damageClass = helper.getDamageClass();
        }
        return (new IDFIndirectEntityDamageSource("trident", source, indirectSource, fire, water, lightning, magic, dark, pen, damageClass)).setProjectile();
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
        String damageClass = player.getCapability(AuxiliaryProvider.AUXILIARY_DATA).orElseThrow(() -> new RuntimeException("player has no damage class!")).getDamageClass();
        return new IDFEntityDamageSource("player", player, fire, water, lightning, magic, dark, pen, damageClass);
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource fireworks(FireworkRocketEntity firework, @Nullable Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("fireworks", firework, indirectSource,
                0.5f, 0, 0, 0, 0, 0, "strike")).setIsConversion().setExplosion();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource fireball(Fireball fireball, @Nullable Entity indirectSource) {
        return indirectSource == null ?
                (new IDFIndirectEntityDamageSource("onFire", fireball, fireball, 1, 0, 0, 0, 0, 0, "strike")).setIsConversion().setIsFire().setProjectile()
                :
                (new IDFIndirectEntityDamageSource("fireball", fireball, indirectSource, 1, 0, 0, 0, 0, 0, "strike")).setIsConversion().setIsFire().setProjectile();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource witherSkull(WitherSkull witherSkull, Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("witherSkull", witherSkull, indirectSource, 0, 0, 0, 0, 1, 0, "strike")).setIsConversion().setProjectile();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource thrown(Entity source, @Nullable Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("thrown", source, indirectSource, 0, 0, 0, 0, 0, 0, "strike")).setIsConversion().setProjectile();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource indirectMagic(Entity source, @Nullable Entity indirectSource) {
        return (new IDFIndirectEntityDamageSource("indirectMagic", source, indirectSource, 0, 0, 0, 1, 0, 0, "genric")).setIsConversion();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource thorns(Entity source) {
        return (new IDFEntityDamageSource("thorns", source, 0, 0, 0, 1, 0, 0, "pierce")).setIsConversion();
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public static DamageSource explosion(@Nullable LivingEntity livingEntity) {
        return livingEntity != null ?
                (new IDFEntityDamageSource("explosion.player", livingEntity, 0.5f, 0, 0, 0, 0, 0, "strike")).setIsConversion().setScalesWithDifficulty().setExplosion()
                :
                (new IDFDamageSource("explosion", 0.5f, 0, 0, 0, 0, 0, "strike")).setIsConversion().setScalesWithDifficulty().setExplosion();
    }

}

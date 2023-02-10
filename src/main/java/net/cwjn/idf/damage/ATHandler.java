package net.cwjn.idf.damage;

import net.cwjn.idf.damage.helpers.*;
import net.minecraft.world.damagesource.DamageSource;

public class ATHandler {
    public static void alterStaticSources() {
        //physical sources
        DamageSource.STARVE = new PhysicalDamageSource("starve", 0.0f, "none").setTrue();
        DamageSource.OUT_OF_WORLD = new PhysicalDamageSource("outOfWorld", 0.0f, "none").setTrue();
        DamageSource.CACTUS = new PhysicalDamageSource("cactus", 0.0f, "pierce");
        DamageSource.SWEET_BERRY_BUSH = new PhysicalDamageSource("sweetBerryBush", 0.0f, "pierce");
        DamageSource.STALAGMITE = new PhysicalDamageSource("stalagmite", 0.0f, "pierce").setIsFall();
        DamageSource.FALLING_STALACTITE = new PhysicalDamageSource("fallingStalactite", 0.0f, "pierce");
        DamageSource.FALL = new PhysicalDamageSource("fall", 0.0f, "strike").setIsFall();
        DamageSource.FLY_INTO_WALL = new PhysicalDamageSource("flyIntoWall", 0.0f, "strike");
        DamageSource.GENERIC = new PhysicalDamageSource("generic", 0.0f, "none").setTrue();
        DamageSource.ANVIL = new PhysicalDamageSource("anvil", 0.0f, "strike");
        DamageSource.FALLING_BLOCK = new PhysicalDamageSource("fallingBlock", 0.0f, "strike");
        DamageSource.IN_WALL = new PhysicalDamageSource("inWall", 0.0f, "none");
        DamageSource.CRAMMING = new PhysicalDamageSource("cramming", 0.0f, "none");
        //fire sources
        DamageSource.IN_FIRE = new FireDamageSource("inFire", "none").setIsFire();
        DamageSource.ON_FIRE = new FireDamageSource("onFire", "none").setIsFire();
        DamageSource.LAVA = new FireDamageSource("lava", "none").setIsFire();
        DamageSource.HOT_FLOOR = new FireDamageSource("hotFloor", "none").setIsFire();
        DamageSource.DRY_OUT = new FireDamageSource("dryout", "none");
        //water sources
        DamageSource.DROWN = new WaterDamageSource("drown", "none");
        DamageSource.FREEZE = new WaterDamageSource("freeze", "none");
        //lightning sources
        DamageSource.LIGHTNING_BOLT = new LightningDamageSource("lightningBolt", "pierce");
        //magic sources
        DamageSource.MAGIC = new MagicDamageSource("magic", "none");
        //dark sources
        DamageSource.WITHER = new DarkDamageSource("wither", "none");
        //mixed sources
        DamageSource.DRAGON_BREATH = new IDFDamageSource("dragonBreath", 0.5f,  0, 0, 0.5f, 0, 0, 0, 0, "none").setIsConversion();
    }
}

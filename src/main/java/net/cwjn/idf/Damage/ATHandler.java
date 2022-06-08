package net.cwjn.idf.Damage;

import net.cwjn.idf.Damage.helpers.FireDamageSource;
import net.cwjn.idf.Damage.helpers.LightningDamageSource;
import net.cwjn.idf.Damage.helpers.PhysicalDamageSource;
import net.cwjn.idf.Damage.helpers.WaterDamageSource;
import net.minecraft.world.damagesource.DamageSource;

public class ATHandler {
    public static void alterStaticSources() {
        //physical sources
        DamageSource.STARVE = new PhysicalDamageSource("starve", 0.0f, "genric").setTrue();
        DamageSource.OUT_OF_WORLD = new PhysicalDamageSource("outOfWorld", 0.0f, "genric").setTrue();
        DamageSource.CACTUS = new PhysicalDamageSource("cactus", 0.0f, "pierce");
        DamageSource.SWEET_BERRY_BUSH = new PhysicalDamageSource("sweetBerryBush", 0.0f, "pierce");
        DamageSource.STALAGMITE = new PhysicalDamageSource("stalagmite", 0.0f, "pierce").setIsFall();
        DamageSource.FALLING_STALACTITE = new PhysicalDamageSource("fallingStalactite", 0.0f, "pierce");
        DamageSource.FALL = new PhysicalDamageSource("fall", 0.0f, "strike").setIsFall();
        DamageSource.FLY_INTO_WALL = new PhysicalDamageSource("flyIntoWall", 0.0f, "strike");
        DamageSource.GENERIC = new PhysicalDamageSource("generic", 0.0f, "strike");
        DamageSource.ANVIL = new PhysicalDamageSource("anvil", 0.0f, "strike");
        DamageSource.FALLING_BLOCK = new PhysicalDamageSource("fallingBlock", 0.0f, "strike");
        DamageSource.IN_WALL = new PhysicalDamageSource("inWall", 0.0f, "_crush");
        DamageSource.CRAMMING = new PhysicalDamageSource("cramming", 0.0f, "_crush");
        //fire sources
        DamageSource.IN_FIRE = new FireDamageSource("inFire", 0.0f, "genric").setIsFire();
        DamageSource.ON_FIRE = new FireDamageSource("onFire", 0.0f, "genric").setIsFire();
        DamageSource.LAVA = new FireDamageSource("lava", 0.0f, "genric").setIsFire();
        DamageSource.HOT_FLOOR = new FireDamageSource("hotFloor", 0.0f, "genric").setIsFire();
        DamageSource.DRY_OUT = new FireDamageSource("dryout", 0.0f, "genric");
        //water sources
        DamageSource.DROWN = new WaterDamageSource("drown", 0.0f, "genric");
        DamageSource.FREEZE = new WaterDamageSource("freeze", 0.0f, "genric");
        //lightning sources
        DamageSource.LIGHTNING_BOLT = new LightningDamageSource("lightningBolt", 0.0f, "pierce");
        //magic sources
        DamageSource.
    }
}

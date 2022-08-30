package net.cwjn.idf.mixin.equipment;

import net.cwjn.idf.util.WeaponInterface;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.*;

@Mixin(SwordItem.class)
public class MixinSwordItem implements WeaponInterface {

    @Unique @Final @Mutable
    private String damageClass;

    @Override
    public String getDamageClass() {
        return damageClass;
    }

    @Override
    public void setDamageClass(String s) {
        damageClass = s;
    }

}

package net.cwjn.idf.mixin.rpg;

import net.cwjn.idf.rpg.RpgItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class MixinItemStack implements RpgItem {

    @Unique @Final @Mutable
    private Tier tier;

    @Unique
    private int consReq = 0, strReq = 0, dexReq = 0, aglReq = 0, intReq = 0, wisReq = 0, fthReq = 0;


    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier t) {
        tier = t;
    }


    public int getConsReq() {
        return consReq;
    }


    public void setConsReq(int consReq) {
        this.consReq = consReq;
    }


    public int getStrReq() {
        return strReq;
    }


    public void setStrReq(int strReq) {
        this.strReq = strReq;
    }


    public int getDexReq() {
        return dexReq;
    }


    public void setDexReq(int dexReq) {
        this.dexReq = dexReq;
    }


    public int getAglReq() {
        return aglReq;
    }


    public void setAglReq(int aglReq) {
        this.aglReq = aglReq;
    }


    public int getIntReq() {
        return intReq;
    }


    public void setIntReq(int intReq) {
        this.intReq = intReq;
    }


    public int getWisReq() {
        return wisReq;
    }


    public void setWisReq(int wisReq) {
        this.wisReq = wisReq;
    }


    public int getFthReq() {
        return fthReq;
    }


    public void setFthReq(int fthReq) {
        this.fthReq = fthReq;
    }

}

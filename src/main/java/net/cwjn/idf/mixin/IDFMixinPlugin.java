package net.cwjn.idf.mixin;

import net.cwjn.idf.mixin.tetra.MixinItemModularHandheld;
import net.minecraftforge.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class IDFMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals("net.cwjn.idf.mixin.tetra$MixinItemModularHandheld") ||
            mixinClassName.equals("net.cwjn.idf.mixin.tetra$MixinModularBowItem") ||
            mixinClassName.equals("net.cwjn.idf.mixin.tetra$MixinModularCrossbowItem") ||
            mixinClassName.equals("net.cwjn.idf.mixin.tetra$MixinSweepingEffect")) {
            return ModList.get().isLoaded("tetra");
        } else {
            return true;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

}

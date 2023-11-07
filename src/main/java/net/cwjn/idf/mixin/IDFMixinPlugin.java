package net.cwjn.idf.mixin;

import net.minecraftforge.fml.loading.FMLLoader;
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
        if (mixinClassName.equals("net.cwjn.idf.mixin.tetra.MixinItemModularHandheld") ||
            mixinClassName.equals("net.cwjn.idf.mixin.tetra.MixinSweepingEffect") ||
            mixinClassName.equals("net.cwjn.idf.mixin.tetra.MixinItemEffectHandler") ||
            mixinClassName.equals("net.cwjn.idf.mixin.tetra.MixinWorkbenchTile")) {
            return FMLLoader.getLoadingModList().getModFileById("tetra")!=null;
        }
        else if (mixinClassName.equals("net.cwjn.idf.mixin.irons_spells.MixinDamageSources")) {
            return FMLLoader.getLoadingModList().getModFileById("irons_spellbooks")!=null;
        }
        else if (mixinClassName.equals("net.cwjn.idf.mixin.bettercombat.MixinPlayerAttackHelper")) {
            return FMLLoader.getLoadingModList().getModFileById("bettercombat")!=null;
        }
        else if (mixinClassName.equals("net.cwjn.idf.mixin.artifactCompat.MixinLivingEntity")) {
            return FMLLoader.getLoadingModList().getModFileById("artifacts")!=null;
        }
        else {
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

package net.cwjn.idf.mixin.apothCompat;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.apotheosis.core.attributeslib.impl.AttributeEvents;

@Mixin(AttributeEvents.class)
public class MixinApothAttributeEvents {

    @Inject(method = "apothCriticalStrike", at = @At("HEAD"), cancellable = true, remap = false)
    public void removeApothCrit(LivingHurtEvent e, CallbackInfo ci) {
        ci.cancel();
    }

}

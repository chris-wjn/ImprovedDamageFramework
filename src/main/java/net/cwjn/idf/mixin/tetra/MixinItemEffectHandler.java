package net.cwjn.idf.mixin.tetra;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.effect.ItemEffectHandler;
import se.mickelus.tetra.items.modular.IModularItem;

import java.util.Optional;

import static se.mickelus.tetra.effect.EffectHelper.getEffectLevel;

@Mixin(ItemEffectHandler.class)
public class MixinItemEffectHandler {

    /**
     * @author cwJn
     * @reason need to invalidate the crit effect on entity hit. Cannot use redirect for some reason, I suspect it is
     * because of the use of optionals. Crit chance and damage will be added as an attribute modifier to still work
     * on basic attacks.
     */
    @Overwrite(remap = false)
    public void onCriticalHit(CriticalHitEvent event) {
        Optional.ofNullable(event.getEntity()).map(LivingEntity::getMainHandItem).filter((itemStack) -> !itemStack.isEmpty()).filter((itemStack) -> itemStack.getItem() instanceof IModularItem).ifPresent((itemStack) -> {
            int backstabLevel = getEffectLevel(itemStack, ItemEffect.backstab);
            if (backstabLevel > 0 && event.getTarget() instanceof LivingEntity) {
                LivingEntity attacker = event.getEntity();
                LivingEntity target = (LivingEntity)event.getTarget();
                if (180.0F - Math.abs(Math.abs(attacker.yHeadRot - target.yHeadRot) % 360.0F - 180.0F) < 60.0F) {
                    event.setDamageModifier(Math.max(1.25F + 0.25F * (float)backstabLevel, event.getDamageModifier()));
                    event.setResult(Event.Result.ALLOW);
                }
            }
        });
    }

}

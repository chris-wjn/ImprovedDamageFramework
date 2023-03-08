package net.cwjn.idf.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LivingLifestealEvent extends LivingEvent {

    private float healAmount;
    private final LivingEntity target;

    public LivingLifestealEvent(LivingEntity entity, LivingEntity target, float healAmount) {
        super(entity);
        this.target = target;
        this.healAmount = healAmount;
    }

    public float getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(float healAmount) {
        this.healAmount = healAmount;
    }

    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

}

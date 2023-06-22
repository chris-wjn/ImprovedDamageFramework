package net.cwjn.idf.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class OnFoodExhaustionEvent extends LivingEvent {

    private float exhaustionAmount;

    public OnFoodExhaustionEvent(LivingEntity entity, float exh) {
        super(entity);
        exhaustionAmount = exh;
    }

    public void setExhaustionAmount(float exhaustionAmount) {
        this.exhaustionAmount = exhaustionAmount;
    }

    public float getExhaustionAmount() {
        return exhaustionAmount;
    }

}

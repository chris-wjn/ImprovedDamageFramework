package net.cwjn.idf.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class LivingEntityAttributeChange extends Event {

    private final LivingEntity entity;

    public LivingEntityAttributeChange(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

}

package net.cwjn.idf.gui;

import net.cwjn.idf.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BestiaryScreen extends Screen {

    private static final List<EntityType<? extends LivingEntity>> unlockedEntities = new ArrayList<>();

    public BestiaryScreen() {
        super(Util.translationComponent("idf.bestiary_screen"));
    }

    public static void addEntity(EntityType<? extends LivingEntity> entityType) {
        if (!unlockedEntities.contains(entityType)) unlockedEntities.add(entityType);
    }

}

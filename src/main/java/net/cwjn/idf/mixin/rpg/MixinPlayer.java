package net.cwjn.idf.mixin.rpg;

import net.cwjn.idf.rpg.RpgPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class MixinPlayer implements RpgPlayer {

    @Unique
    private int level;

}

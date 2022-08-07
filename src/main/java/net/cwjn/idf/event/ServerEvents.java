package net.cwjn.idf.event;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.Util;
import net.cwjn.idf.command.ChangeDebugStatusCommand;
import net.cwjn.idf.command.UpdateJsonFilesCommand;
import net.cwjn.idf.config.CommonConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class ServerEvents {

    public static boolean debugMode = false;

    @SubscribeEvent
    public static void removeInvulnerability(LivingHurtEvent event) {
        LivingEntity attacked = event.getEntity();
        if (!attacked.level.isClientSide()) {
            DamageSource source = event.getSource();
            String msgID = source.getMsgId();
            if (CommonConfig.WHITELISTED_DAMAGE_SOURCES_NO_INVULN.get().contains(msgID)) {
                if (event.getSource().getEntity() == null) {
                    attacked.invulnerableTime = 0;
                } else {
                    if (CommonConfig.BLACKLISTED_ENTITIES.get().contains(Util.getEntityRegistryName(event.getSource().getEntity().getType()).toString())) {
                        ImprovedDamageFramework.LOGGER.debug("MOB IS BLACKLISTED");
                    }
                    else attacked.invulnerableTime = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getHealth() > entity.getMaxHealth()) entity.setHealth(entity.getMaxHealth());
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (!player.level.isClientSide()) {
            if (player.getAttackStrengthScale(0.5f) < 0.3) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new UpdateJsonFilesCommand(event.getDispatcher());
        new ChangeDebugStatusCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

}

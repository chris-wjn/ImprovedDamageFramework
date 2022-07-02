package net.cwjn.idf.event.hook;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.command.ChangeDebugStatusCommand;
import net.cwjn.idf.command.UpdateJsonFilesCommand;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.event.post.PostMitigationDamageEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class ServerEvents {

    public static Set<String> acceptableSources = new HashSet<>(CommonConfig.WHITELISTED_DAMAGE_SOURCES_NO_INVULN.get());
    public static Set<String> blacklistedMobs = new HashSet<>(CommonConfig.BLACKLISTED_ENTITIES.get());
    public static boolean debugMode = false;

    /*@SubscribeEvent
    public static void removeInvulnerability(LivingHurtEvent event) {
        LivingEntity attacked = event.getEntityLiving();
        if (!attacked.level.isClientSide()) {
            DamageSource source = event.getSource();
            String msgID = source.getMsgId();
            if (acceptableSources.contains(msgID)) {
                if (event.getSource().getEntity() == null) {
                    attacked.invulnerableTime = 10;
                    return;
                } else {
                    String attackerName = event.getSource().getEntity().getType().getRegistryName().toString();
                    if (blacklistedMobs.contains(attackerName)) {
                        if (ServerEvents.debugMode) ImprovedDamageFramework.LOGGER.debug("MOB IS BLACKLISTED");
                        return;
                    }
                    else attacked.invulnerableTime = 10;
                }
            }
        }
    }*/

    @SubscribeEvent
    public static void removeInvulnOnPlayerAttack(LivingHurtEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (!target.level.isClientSide()) {
            if (event.getSource().msgId.equals("player")) {
                target.invulnerableTime = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
        }
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getPlayer();
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

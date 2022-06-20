package net.cwjn.idf.event;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.command.ChangeDebugStatusCommand;
import net.cwjn.idf.command.UpdateJsonFilesCommand;
import net.cwjn.idf.config.CommonConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
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

    @SubscribeEvent
    public static void removeInvulnerability(LivingHurtEvent event) {
        LivingEntity attacked = event.getEntityLiving();
        if (!attacked.level.isClientSide()) {
            DamageSource source = event.getSource();
            String msgID = source.getMsgId();
            if (acceptableSources.contains(msgID)) {
                if (event.getSource().getEntity() == null) {
                    attacked.invulnerableTime = 0;
                    return;
                } else {
                    String attackerName = event.getSource().getEntity().getType().getRegistryName().toString();
                    if (blacklistedMobs.contains(attackerName)) {
                        if (ServerEvents.debugMode) ImprovedDamageFramework.getLog().debug("MOB IS BLACKLISTED");
                        return;
                    }
                    else attacked.invulnerableTime = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new UpdateJsonFilesCommand(event.getDispatcher());
        new ChangeDebugStatusCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void nerfKnockback(LivingKnockBackEvent event) {
        event.setStrength(event.getStrength() * 0.5f);
    }

}

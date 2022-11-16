package net.cwjn.idf.event;

import net.cwjn.idf.api.event.OnItemStackCreatedEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.json.data.WeaponData;
import net.cwjn.idf.util.ItemInterface;
import net.cwjn.idf.util.Util;
import net.cwjn.idf.command.ChangeDebugStatusCommand;
import net.cwjn.idf.command.UpdateJsonFilesCommand;
import net.cwjn.idf.compat.TooltipsCompat;
import net.cwjn.idf.config.json.JSONHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber
public class ServerEvents {

    public static boolean debugMode = false;

    @SubscribeEvent
    public static void updateTags(OnItemStackCreatedEvent event) {
        ItemStack item = event.getItemStack();
        Item baseItem = item.getItem();
        if (((ItemInterface) baseItem).getDefaultTags() != null) {
            item.getOrCreateTag().merge(((ItemInterface) baseItem).getDefaultTags());
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

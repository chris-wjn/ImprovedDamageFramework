package net.cwjn.idf.event;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.util.Util;
import net.cwjn.idf.command.ChangeDebugStatusCommand;
import net.cwjn.idf.command.UpdateJsonFilesCommand;
import net.cwjn.idf.compat.TooltipsCompat;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.DamageData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber
public class ServerEvents {

    public static boolean debugMode = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void ItemModifierEvent(ItemAttributeModifierEvent event) {
        ItemStack item = event.getItemStack(); //get the item from the event
        if (item.hasTag() && item.getTag().contains("idf.equipment")) return;
        Item baseItem = item.getItem();
        //clauses are sorted in order of likeliness
        if (baseItem instanceof SwordItem || baseItem instanceof ArmorItem || baseItem instanceof DiggerItem
                || JSONHandler.damageMap.containsKey(Util.getItemRegistryName(baseItem))
                || JSONHandler.resistanceMap.containsKey(Util.getItemRegistryName(baseItem))) {
            item.getOrCreateTag().putBoolean("idf.equipment", true);
            if (JSONHandler.damageMap.containsKey(Util.getItemRegistryName(baseItem))) {
                DamageData data = JSONHandler.getDamageData(Util.getItemRegistryName(baseItem));
                item.getTag().putString("idf.damage_class", data.getDamageClass());
            }
            if (TooltipsCompat.enabled && event.getSlotType() == EquipmentSlot.MAINHAND) {
                double fire = event.getModifiers().get(IDFAttributes.FIRE_DAMAGE.get()).stream().mapToDouble(AttributeModifier::getAmount).sum();
                double water = event.getModifiers().get(IDFAttributes.WATER_DAMAGE.get()).stream().mapToDouble(AttributeModifier::getAmount).sum();
                double lightning = event.getModifiers().get(IDFAttributes.LIGHTNING_DAMAGE.get()).stream().mapToDouble(AttributeModifier::getAmount).sum();
                double magic = event.getModifiers().get(IDFAttributes.MAGIC_DAMAGE.get()).stream().mapToDouble(AttributeModifier::getAmount).sum();
                double dark = event.getModifiers().get(IDFAttributes.DARK_DAMAGE.get()).stream().mapToDouble(AttributeModifier::getAmount).sum();
                double phys = 2 + event.getModifiers().get(Attributes.ATTACK_DAMAGE).stream().mapToDouble(AttributeModifier::getAmount).sum();
                item.getTag().putInt("idf.tooltip_border", Util.getItemBorderType(item, item.getTag().getString("idf.damage_class"), fire, water, lightning, magic, dark, phys));
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

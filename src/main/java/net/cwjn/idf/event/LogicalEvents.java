package net.cwjn.idf.event;

import net.cwjn.idf.api.event.OnItemStackCreatedEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.command.ChangeDebugStatusCommand;
import net.cwjn.idf.command.UpdateJsonFilesCommand;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.util.ItemInterface;
import net.cwjn.idf.util.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber
public class LogicalEvents {

    public static boolean debugMode = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void updateTags(OnItemStackCreatedEvent event) {
        ItemStack item = event.getItemStack();
        Item baseItem = item.getItem();
        CompoundTag defaultTag = ((ItemInterface) baseItem).getDefaultTags();
        if (defaultTag != null) {
            item.getOrCreateTag().merge(defaultTag);
            if (CommonConfig.LEGENDARY_TOOLTIPS_COMPAT_MODE.get() && !item.getTag().contains("idf.tooltip_border")) {
                item.getTag().putInt("idf.tooltip_border", Util.getItemBorderType(item.getTag().getString("idf.damage_class"), item.getAttributeModifiers(EquipmentSlot.MAINHAND)));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getHealth() > entity.getMaxHealth()) entity.setHealth(entity.getMaxHealth());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (!player.level.isClientSide()) {
            if (player.getAttackStrengthScale(0.5f) < 0.3) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void evasion(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        if (target.getLevel() instanceof ServerLevel level) {
            if (target.getAttributeValue(IDFAttributes.EVASION.get())/100 >= Math.random()) {
                for (int i = 0; i < 360; i++) {
                    if (i % 20 == 0) {
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 1.5, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 1.25, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 1, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 0.75, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 0.5, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                    }
                }
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

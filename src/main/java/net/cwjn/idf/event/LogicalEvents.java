package net.cwjn.idf.event;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.api.event.LivingLifestealEvent;
import net.cwjn.idf.api.event.OnFoodExhaustionEvent;
import net.cwjn.idf.api.event.PostMitigationDamageEvent;
import net.cwjn.idf.api.event.ReplaceItemAttributeModifierEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.command.ChangeDebugStatusCommand;
import net.cwjn.idf.command.InfoPageCommand;
import net.cwjn.idf.command.UnlockBestiaryCommand;
import net.cwjn.idf.command.UpdateItemsFromConfigCommand;
import net.cwjn.idf.config.json.records.ArmourData;
import net.cwjn.idf.config.json.records.ItemData;
import net.cwjn.idf.config.json.records.PresetData;
import net.cwjn.idf.config.json.records.WeaponData;
import net.cwjn.idf.data.BestiaryData;
import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.network.packets.DisplayDamageIndicatorPacket;
import net.cwjn.idf.network.packets.DisplayMissPacket;
import net.cwjn.idf.network.packets.OpenInfoScreenPacket;
import net.cwjn.idf.network.packets.SyncSkyDarkenPacket;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.util.ItemInterface;
import net.cwjn.idf.util.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static net.cwjn.idf.config.CommonConfig.*;
import static net.cwjn.idf.data.CommonData.*;
import static net.cwjn.idf.util.Util.UUIDS_BASE_STAT_ADDITION;
import static net.cwjn.idf.util.Util.UUIDS_BASE_STAT_MULTIPLY_BASE;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID)
public class LogicalEvents {

    public static boolean debugMode = false;
    private static final Random random = new Random();
    @OnlyIn(Dist.CLIENT) private static ItemStack cache;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void instantiateDefaultTags(ItemAttributeModifierEvent event) {
        ItemStack item = event.getItemStack();
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT) {
            if (item == cache) return;
            else cache = item;
        }
        if (item.getTag() != null && item.getTag().contains(COMPAT_ITEM)) {
            ResourceLocation loc = Util.getItemRegistryName(item.getItem());
            int equipmentSlot = LivingEntity.getEquipmentSlotForItem(item).getFilterFlag();
            if (event.getSlotType() == LivingEntity.getEquipmentSlotForItem(item)) {
                if (LOGICAL_ARMOUR_MAP_FLAT.containsKey(loc)) {
                    ArmourData data0 = LOGICAL_ARMOUR_MAP_FLAT.get(loc);
                    ItemData data2 = LOGICAL_ARMOUR_MAP_MULT.get(loc);
                    for (String s : data0.presets()) {
                        PresetData preset = LOGICAL_PRESET_MAP.get(s);
                        if (preset == null) {
                            ImprovedDamageFramework.LOGGER.warn("Preset " + s + " does not exist!");
                            continue;
                        }
                        for (PresetData.AttributeAndModifier combo : preset) {
                            event.addModifier(combo.getAttribute(), new AttributeModifier("preset_modifier", combo.getAmount(), AttributeModifier.Operation.valueOf(combo.getOperation())));
                        }
                    }
                    data0.forEach(pair -> {
                        if (pair.getB() != 0) {
                            event.addModifier(pair.getA(), new AttributeModifier(UUIDS_BASE_STAT_ADDITION[equipmentSlot], "json_flat", pair.getB(), ADDITION));
                        }
                    });
                    if (data2 != null) data2.forEach(pair -> {
                        if (pair.getB() != 0) {
                            event.addModifier(pair.getA(), new AttributeModifier(UUIDS_BASE_STAT_MULTIPLY_BASE[equipmentSlot], "json_mult", pair.getB(), MULTIPLY_BASE));
                        }
                    });
                } else if (LOGICAL_WEAPON_MAP_FLAT.containsKey(loc)) {
                    WeaponData data01 = LOGICAL_WEAPON_MAP_FLAT.get(loc);
                    ItemData data21 = LOGICAL_WEAPON_MAP_MULT.get(loc);
                    for (String s : data01.presets()) {
                        PresetData preset = LOGICAL_PRESET_MAP.get(s);
                        if (preset == null) {
                            ImprovedDamageFramework.LOGGER.warn("Preset " + s + " does not exist!");
                            continue;
                        }
                        for (PresetData.AttributeAndModifier combo : preset) {
                            event.addModifier(combo.getAttribute(), new AttributeModifier("preset_modifier", combo.getAmount(), AttributeModifier.Operation.valueOf(combo.getOperation())));
                        }
                    }
                    data01.forEach(pair -> {
                        if (pair.getB() != 0) {
                            event.addModifier(pair.getA(), new AttributeModifier(UUIDS_BASE_STAT_ADDITION[equipmentSlot], "json_flat", pair.getB(), ADDITION));
                        }
                    });
                    if (data21 != null) data21.forEach(pair -> {
                        if (pair.getB() != 0) {
                            event.addModifier(pair.getA(), new AttributeModifier(UUIDS_BASE_STAT_MULTIPLY_BASE[equipmentSlot], "json_mult", pair.getB(), MULTIPLY_BASE));
                        }
                    });
                }
            }
        }
        if (item.getTag() != null && item.getTag().getBoolean(DEFAULT_TAG_APPLIED)) return;
        CompoundTag defaultTag = ((ItemInterface) item.getItem()).getDefaultTags();
        if (defaultTag != null) {
            defaultTag.putBoolean(DEFAULT_TAG_APPLIED, true);
            item.getOrCreateTag().merge(defaultTag);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void instantiateDefaultTags(ReplaceItemAttributeModifierEvent event) {
        ItemStack item = event.item;
        if (item.getTag() != null && item.getTag().getBoolean(DEFAULT_TAG_APPLIED)) return;
        CompoundTag defaultTag = ((ItemInterface) item.getItem()).getDefaultTags();
        if (defaultTag != null) {
            defaultTag.putBoolean(DEFAULT_TAG_APPLIED, true);
            item.getOrCreateTag().merge(defaultTag);
        }
    }

    //prevents entity NAN error
    @SubscribeEvent
    public static void assertMaxHP(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (Float.isNaN(entity.getHealth())) entity.setHealth(entity.getMaxHealth());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void cancelWeakAttacks(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (!player.level.isClientSide()) {
            if (player.getAttackStrengthScale(0.5f) < MIN_ATTACK_STRENGTH_THRESHOLD.get()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void checkEvasion(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        if (target.getLevel() instanceof ServerLevel) {
            if (target.getAttributeValue(IDFAttributes.EVASION.get())/100 >= Math.random()) {
                if (UNDODGABLE_SOURCES.get().contains(event.getSource().msgId)) return;
                PacketHandler.serverToNearPoint(new DisplayMissPacket(target.getX(), target.getY(), target.getZ(), 0, target.getUUID()), target.getX(), target.getY(), target.getZ(), 15, target.getCommandSenderWorld().dimension());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void addDamageIndicators(PostMitigationDamageEvent event) {
        LivingEntity target = event.getTarget();
        Level world = target.getLevel();
        UUID id = net.minecraft.Util.NIL_UUID;
        if (target instanceof Player) id = target.getUUID();
        if (!world.isClientSide()) {
            double x = target.getX();
            double y = target.getEyeY();
            double z = target.getZ();
            List<Float> locs = new ArrayList<>(List.of(
                    random.nextFloat(-1.0f, -0.6666667f),
                    random.nextFloat(-0.6666667f, -0.333333f),
                    random.nextFloat(-0.333333f, 0),
                    random.nextFloat(0, 0.333333f),
                    random.nextFloat(0.333333f, 0.6666667f),
                    random.nextFloat(0.6666667f, 1.0f),
                    random.nextFloat(1.0f, 1.333333f)));
            if (event.getFire() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getFire(), locs.get(loc), Color.FIRE_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getWater() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getWater(), locs.get(loc), Color.WATER_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getLightning() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getLightning(), locs.get(loc),  Color.LIGHTNING_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getMagic() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getMagic(), locs.get(loc),  Color.MAGIC_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getDark() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getDark(), locs.get(loc),  Color.DARK_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getPhysical() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getPhysical(), locs.get(loc), Color.PHYSICAL_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
            }
            if (event.getHoly() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getHoly(), locs.get(loc), Color.HOLY_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void addLifestealIndicator(LivingLifestealEvent event) {
        if (event.getEntity() instanceof Player player) {
            LivingEntity target = event.getTarget();
            if (target.getLevel() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.CRIMSON_SPORE, target.getX(), target.getEyeY(), target.getZ(), 50, player.getX() - target.getX(), player.getY() - target.getY(), player.getZ() - target.getZ(), 1);
            }
        }
    }

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new ChangeDebugStatusCommand(event.getDispatcher());
        new InfoPageCommand(event.getDispatcher());
        new UnlockBestiaryCommand(event.getDispatcher());
        new UpdateItemsFromConfigCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.haveTime()) {
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                PacketHandler.serverToPlayer(new SyncSkyDarkenPacket(player.getLevel().getSkyDarken()), player);
            }
        }
    }

    @SubscribeEvent
    public static void addWeightToFoodExhaustion(OnFoodExhaustionEvent event) {
        LivingEntity entity = event.getEntity();
        double weight = entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        event.setExhaustionAmount((float) (event.getExhaustionAmount() * (weight*WEIGHT_FOOD_EXHAUSTION_MULTIPLIER.get() + 1)));
    }

    @SubscribeEvent
    public static void onPlayerFirstJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().getPersistentData().getBoolean("idf.first_join")) {
            PacketHandler.serverToPlayer(new OpenInfoScreenPacket(), (ServerPlayer) event.getEntity());
            event.getEntity().getPersistentData().putBoolean("idf.first_join", true);
        }
    }

    @SubscribeEvent
    public static void onWorldBoot(ServerStartedEvent event) {
        BestiaryData.loadBestiaryData(event.getServer());
    }

    @SubscribeEvent
    public static void onWorldShutdown(ServerStoppingEvent event) {
        BestiaryData.saveBestiaryData(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerKillMob(LivingDeathEvent event) {
        if (event.getEntity().getKillCredit() instanceof Player player) {
            CommonData.BESTIARY_MAP.put(player.getUUID(), Util.getEntityRegistryName(event.getEntity().getType()));
        }
    }

}

package net.cwjn.idf.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.data.ClientData;
import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.gui.StatScreen;
import net.cwjn.idf.gui.buttons.TabButton;
import net.cwjn.idf.hud.MobHealthbar;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;

import static net.cwjn.idf.damage.DamageHandler.DEFAULT_ATTACK_SPEED;
import static net.cwjn.idf.damage.DamageHandler.DEFAULT_KNOCKBACK;
import static net.cwjn.idf.data.CommonData.EQUIPMENT_TAG;
import static net.cwjn.idf.gui.buttons.TabButton.TabType.INVENTORY;
import static net.cwjn.idf.gui.buttons.TabButton.TabType.STATS;
import static net.cwjn.idf.util.Util.writeTooltipDouble;
import static net.cwjn.idf.util.Util.writeTooltipInteger;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsForgeBus {

    public static float MOB_HEALTH_BAR_DISTANCE_FACTOR = 35;
    private static final Predicate<Attribute> isPercentageAttribute = a -> (
            a.equals(IDFAttributes.EVASION.get()) ||
            a.equals(IDFAttributes.LIFESTEAL.get()) ||
            a.equals(IDFAttributes.CRIT_CHANCE.get()) ||
            a.equals(IDFAttributes.PENETRATING.get())
            );


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack item = event.getItemStack();
        //we only need to affect equipment tagged items
        if (item.hasTag() && item.getTag().contains(EQUIPMENT_TAG)) {
            //first get items attribute modifiers for the item's normal slot
            //we don't display attribute modifiers in slots the item doesn't usually take
            //modifiers in because it's too hard to fit in.
            List<Component> list = event.getToolTip();
            EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(item);
            Multimap<Attribute, AttributeModifier> map = HashMultimap.create(item.getAttributeModifiers(slot));
            MutableComponent line1 = Component.empty().withStyle(Style.EMPTY);
            MutableComponent line2 = Component.empty().withStyle(Style.EMPTY);
            MutableComponent mainArea = Component.empty().withStyle(Style.EMPTY);
            List<Component> other = new ArrayList<>();
            boolean isWeapon = item.getTag().contains("idf.damage_class");
            boolean isRanged = item.getTag().getBoolean("idf.ranged_weapon");

            if (isWeapon) {
                //static lines for weapons. Durability, force, atkspd, crit, pen, knockback
                if (item.isDamageableItem()) {
                    double percentage = (double) (item.getMaxDamage() - item.getDamageValue()) / (double) item.getMaxDamage();
                    Color colour = new Color((int) (128 + (128 * 0.5 * (1.0 - percentage))), (int) (255 * percentage), 0);
                    line1.append(Util.writeStaticTooltipComponent(percentage*100, "durability", colour, true, true));
                } else {
                    line1.append(Util.writeStaticInfinityComponent(Color.DARKSEAGREEN, true));
                }
                double force = convertAndRemoveAttribute(map, IDFAttributes.FORCE.get());
                line1.append(Util.writeStaticTooltipComponent(force, "force", null, false, false));
                if (!isRanged) {
                    double atkSpd = (convertAndRemoveAttribute(map, Attributes.ATTACK_SPEED));
                    line1.append(Util.writeStaticTooltipComponent(atkSpd, "attack_speed", null, false, true));
                } else {
                    double accuracy = (getAndRemoveAttribute(map, IDFAttributes.ACCURACY.get()));
                    line1.append(Util.writeStaticTooltipComponent(accuracy, "accuracy", null, false, true));
                }
                double crit = convertAndRemoveAttribute(map, IDFAttributes.CRIT_CHANCE.get());
                double pen = convertAndRemoveAttribute(map, IDFAttributes.PENETRATING.get());
                BigDecimal numerator = BigDecimal.valueOf(convertAndRemoveAttribute(map, Attributes.ATTACK_KNOCKBACK)), denominator = new BigDecimal(DEFAULT_KNOCKBACK);
                double knockback = numerator.divide(denominator, RoundingMode.CEILING).doubleValue();
                line2.append(Util.writeStaticTooltipComponent(crit, "critical_chance", null, true, true));
                line2.append(Util.writeStaticTooltipComponent(pen, "armour_penetration", null, true, false));
                line2.append(Util.writeStaticTooltipComponent(knockback*100, "knockback", null, true, true));
            }
            else {
                //If the item is tagged as equipment but doesn't have a damage class it is wearable equipment.
                if (item.isDamageableItem()) {
                    double percentage = (double) (item.getMaxDamage() - item.getDamageValue()) / (double) item.getMaxDamage();
                    Color colour = new Color((int) (128 + (128 * 0.5 * (1.0 - percentage))), (int) (255 * percentage), 0);
                    line1.append(Util.writeStaticTooltipComponent(percentage*100, "durability", colour, true, true));
                } else {
                    line1.append(Util.writeStaticInfinityComponent(Color.DARKSEAGREEN, true));
                }
                double wgt = getAndRemoveAttribute(map, Attributes.ARMOR_TOUGHNESS);
                double kbr = getAndRemoveAttribute(map, Attributes.KNOCKBACK_RESISTANCE);
                line1.append(Util.writeStaticTooltipComponent(wgt, "weight", null, false, false));
                line1.append(Util.writeStaticTooltipComponent(kbr * 100, "knockback_resistance", null, true, true));
                double str = getAndRemoveAttribute(map, IDFAttributes.STRIKE_MULT.get())*100;
                double prc = getAndRemoveAttribute(map, IDFAttributes.PIERCE_MULT.get())*100;
                double sls = getAndRemoveAttribute(map, IDFAttributes.SLASH_MULT.get())*100;
                line2.append(Util.writeStaticTooltipComponent(str, "strike", pickColour(str), true, true));
                line2.append(Util.writeStaticTooltipComponent(prc, "pierce", pickColour(prc), true, false));
                line2.append(Util.writeStaticTooltipComponent(sls, "slash", pickColour(sls), true, true));
            }

            List<Component> firstComponents = new ArrayList<>();
            String[] keys = Util.sort(map, isWeapon);
            for (String key : keys) {
                Attribute a = CommonData.ATTRIBUTES.get(key);
                Collection<AttributeModifier> mods = map.get(a);
                final double base = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                double flat = base + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(base)).sum();
                double mult = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).reduce(1, (x, y) -> x * y);
                if (mult != 1)
                    firstComponents.add((Util.writeIcon(key)).append(writeTooltipDouble(flat)).append(Util.writeScalingTooltip(mult * flat)));
                else firstComponents.add((Util.writeIcon(key)).append(writeTooltipDouble(flat)));
                map.removeAll(a);
            }
            if (firstComponents.size() >= 1) mainArea.append(firstComponents.get(0));
            for (int i = 1; i < firstComponents.size(); i++) {
                mainArea.append(", ").append(firstComponents.get(i));
            }

            for (Map.Entry<Attribute, AttributeModifier> entry : map.entries()) {
                MutableComponent comp = Util.text(" ");
                if (entry.getValue().getOperation() == ADDITION) {
                    if (isPercentageAttribute.test(entry.getKey())) {
                        comp.append(Util.translation("idf.right_arrow.symbol"));
                        comp.append(Util.writeIcon(entry.getKey().getDescriptionId()));
                        comp.append(" ");
                        comp.append(writeTooltipInteger((int) (entry.getValue().getAmount()), entry.getValue().getAmount() >= 0));
                        comp.append("%");
                    } else {
                        comp.append(Util.translation("idf.right_arrow.symbol"));
                        comp.append(Util.writeIcon(entry.getKey().getDescriptionId()));
                        comp.append(" ");
                        comp.append(writeTooltipInteger((int) (entry.getValue().getAmount()), entry.getValue().getAmount() >= 0));
                    }
                } else {
                    comp.append(Util.translation("idf.right_arrow.symbol"));
                    comp.append(Util.writeIcon(entry.getKey().getDescriptionId()));
                    comp.append(" ");
                    comp.append(writeTooltipInteger((int) entry.getValue().getAmount()+1, entry.getValue().getAmount() >= 0));
                    comp.append("x");
                }
                other.add(comp);
            }

            //here we add all the components we've created.
            list.add(line1);
            list.add(line2);
            list.add(mainArea);
            list.addAll(other);
        }
    }

    private static Color pickColour(double n) {
        if (n == 0) return null;
        return n > 0? Color.GREEN : Color.DARKRED;
    }

    private static double getAndRemoveAttribute(Multimap<Attribute, AttributeModifier> map, Attribute a) {
        Collection<AttributeModifier> mods = map.get(a);
        final double base = (a == Attributes.ATTACK_SPEED ? 4 : 0) + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
        double flat = base + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(base)).sum();
        map.removeAll(a);
        return flat;
    }

    private static double convertAndRemoveAttribute(Multimap<Attribute, AttributeModifier> map, Attribute a) {
        Collection<AttributeModifier> mods = map.get(a);
        final double flat = (a == Attributes.ATTACK_SPEED ? 4 : 0) + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
        double f1 = flat + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(flat)).sum();
        double f2 = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
        map.removeAll(a);
        if (f1 >= 0) {
            return f1 * f2;
        } else {
            return f1;
        }
    }

    @SubscribeEvent
    public static void onInitGui(ScreenEvent.Init event) {
        Screen screen = event.getScreen();
        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen || screen instanceof StatScreen) {
            int x = (screen.width - (screen instanceof CreativeModeInventoryScreen ? 195 : 176)) / 2 - 28;
            int y = (screen.height - (screen instanceof CreativeModeInventoryScreen ? 136 : 166)) / 2;
            event.addListener(new TabButton(x, y + 7, INVENTORY, !(screen instanceof StatScreen)));
            event.addListener(new TabButton(x, y + 36, STATS, (screen instanceof StatScreen)));
        }
    }

    @SubscribeEvent
    public static void openStatsScreen(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        onInput(minecraft, event.getKey(), event.getAction());
    }

    private static void onInput(Minecraft minecraft, int key, int action) {
        if (Keybinds.openStats.isDown() && minecraft.screen == null) {
            minecraft.setScreen(new StatScreen());
        }
    }

    @SubscribeEvent
    public static void prepareHealthbar(RenderNameTagEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            if (entity != Minecraft.getInstance().player) {
                int unpackedLight = Math.max(LightTexture.sky(event.getPackedLight()) - ClientData.skyDarken, LightTexture.block(event.getPackedLight()));
                float distance = entity.distanceTo(Minecraft.getInstance().player) - unpackedLight + 15;
                float alpha = 1 - distanceFunction(distance/MOB_HEALTH_BAR_DISTANCE_FACTOR);
                MobHealthbar.prepare(entity, alpha);
            }
        }
    }

    private static float distanceFunction(float distance) {
        if (distance < 0.75) {
            return 0.5f*distance;
        } else {
            return 2.5f*distance - 1f;
        }
    }

    @SubscribeEvent
    public static void renderHealthbar(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            Camera c = Minecraft.getInstance().gameRenderer.getMainCamera();
            MobHealthbar.renderBars(event.getPartialTick(), event.getPoseStack(), c);
        }
    }

}

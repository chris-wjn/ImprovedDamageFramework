package net.cwjn.idf.event;

import com.google.common.collect.Multimap;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.gui.EquipmentInspectScreen;
import net.cwjn.idf.gui.StatScreen;
import net.cwjn.idf.gui.buttons.TabButton;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static net.cwjn.idf.ImprovedDamageFramework.*;
import static net.cwjn.idf.gui.buttons.TabButton.TabType.INVENTORY;
import static net.cwjn.idf.gui.buttons.TabButton.TabType.STATS;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsForgeBus {

    private static final DecimalFormat hundredths = new DecimalFormat("#.##");
    private static final DecimalFormat tenths = new DecimalFormat("#.#");
    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);
    public static final int ICON_PIXEL_SPACER = 2;
    private static final Predicate<AttributeModifier> isAddition = o -> o.getOperation() == ADDITION;
    private static final Predicate<Attribute> isPercentageAttribute = a -> {
        String name = a.getDescriptionId();
        return (
                name.contains("lifesteal") ||
                name.contains("pen") ||
                name.contains("crit") ||
                name.contains("evasion") ||
                name.contains("knockback_resistance")
                );
    };

    public static void addInspectText(ItemTooltipEvent event) {
        ItemStack hoveredItem = event.getItemStack();
        if (hoveredItem.hasTag() && hoveredItem.getTag().contains("idf.equipment") && Minecraft.getInstance().player != null) {
            event.getToolTip().add(Component.translatable("idf.press_to_inspect"));
            if (Keybinds.inspectItem.isDown() && !(Minecraft.getInstance().screen instanceof EquipmentInspectScreen)) {
                Minecraft.getInstance().pushGuiLayer(new EquipmentInspectScreen(hoveredItem));
                Keybinds.inspectItem.setDown(false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        //get variables from event
        List<Component> list = event.getToolTip();
        ItemStack item = event.getItemStack();
        EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(item);
        LivingEntity owner = event.getEntity();

        //we only need to affect equipment tagged items
        if (item.hasTag() && item.getTag().contains("idf.equipment")) {
            //first get items attribute modifiers for the item's normal slot
            //we don't display attribute modifiers in slots the item doesn't usually take
            //modifiers in because it's too hard to fit in.
            Multimap<Attribute, AttributeModifier> map = item.getAttributeModifiers(slot);
            MutableComponent line1 = Component.empty();
            MutableComponent line2 = Component.empty();
            MutableComponent mainArea = Component.empty();
            MutableComponent otherArea = Component.empty();
            boolean isWeapon = item.getTag().contains("idf.damage_class");
            boolean isRanged = item.getTag().getBoolean("idf.ranged_weapon");

            if (isWeapon) {
                //the name component of the item should be at index 0
                list.remove(0);
                MutableComponent nameComponent = Component.empty().append(item.getHoverName());
                nameComponent.append(Util.writeIcon(item.getTag().getString("idf.damage_class")));
                list.add(0, nameComponent);

                //static lines for weapons. Durability, force, atkspd, crit, pen, knockback
                if (item.isDamageableItem()) {
                    double percentage = (double) (item.getMaxDamage() - item.getDamageValue()) / (double) item.getMaxDamage();
                    Color colour = new Color((int) (128 + (128 * 0.5 * (1.0 - percentage))), (int) (255 * percentage), 0);
                    line1.append(Util.writeStaticTooltipComponent((int) percentage, "durability", colour, true, true));
                } else {
                    line1.append(Util.writeStaticInfinityComponent(Color.DARKSEAGREEN, true));
                }
                double force = getAndRemoveAttribute(map, IDFAttributes.FORCE.get());
                line1.append(Util.writeStaticTooltipComponent(force, "force", null, false, false));
                if (!isRanged) {
                    double atkSpd = getAndRemoveAttribute(map, Attributes.ATTACK_SPEED);
                    line1.append(Util.writeStaticTooltipComponent(atkSpd, "attack_speed", null, false, true));
                }
                double crit = getAndRemoveAttribute(map, IDFAttributes.CRIT_CHANCE.get());
                double pen = getAndRemoveAttribute(map, IDFAttributes.PENETRATING.get());
                double knockback = getAndRemoveAttribute(map, Attributes.ATTACK_KNOCKBACK);
                line2.append(Util.writeStaticTooltipComponent(crit, "critical_chance", null, true, true));
                line2.append(Util.writeStaticTooltipComponent(pen, "armour_penetration", null, true, false));
                line2.append(Util.writeStaticTooltipComponent(knockback * 100, "knockback", null, true, true));

                //dynamic damage lines for weapons
                List<Component> damageComponents = new ArrayList<>();
                for (Attribute a : map.keySet()) {
                    String name = a.getDescriptionId();
                    if (name.contains("damage")) {
                        Collection<AttributeModifier> mods = map.get(a);
                        final double base = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                        double flat = base + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(base)).sum();
                        double mult = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                        damageComponents.add((Util.writeIcon(name)).append(Util.writeTooltipInteger((int)flat)).append("+").append(Util.writeTooltipDouble(mult)));
                        map.removeAll(a);
                    }
                }
                if (damageComponents.size() >= 1) mainArea.append(damageComponents.get(0));
                for (int i = 1; i < damageComponents.size(); i++) {
                    mainArea.append(", ").append(damageComponents.get(i));
                }

                //slap on the rest of the tooltips now
                for (AttributeModifier am : map.values()) {
                    if (am.getOperation() == ADDITION) {

                    }
                }

            } else {
                //If the item is tagged as equipment but doesn't have a damage class it is wearable equipment.
                if (item.isDamageableItem()) {
                    double percentage = (double) (item.getMaxDamage() - item.getDamageValue()) / (double) item.getMaxDamage();
                    Color colour = new Color((int) (128 + (128 * 0.5 * (1.0 - percentage))), (int) (255 * percentage), 0);
                    line1.append(Util.writeStaticTooltipComponent((int) percentage, "durability", colour, true, true));
                } else {
                    line1.append(Util.writeStaticInfinityComponent(Color.DARKSEAGREEN, true));
                }
                double def = getAndRemoveAttribute(map, Attributes.ARMOR_TOUGHNESS);
                double kbr = getAndRemoveAttribute(map, Attributes.KNOCKBACK_RESISTANCE);
                line1.append(Util.writeStaticTooltipComponent(def, "defense", null, false, false));
                line1.append(Util.writeStaticTooltipComponent(kbr * 100, "knockback_resistance", null, true, true));
                double str = getAndRemoveAttribute(map, IDFAttributes.STRIKE_MULT.get());
                double prc = getAndRemoveAttribute(map, IDFAttributes.PIERCE_MULT.get());
                double sls = getAndRemoveAttribute(map, IDFAttributes.SLASH_MULT.get());
                line2.append(Util.writeStaticTooltipComponent(str * 100, "strike", null, true, true));
                line2.append(Util.writeStaticTooltipComponent(prc * 100, "pierce", null, true, false));
                line2.append(Util.writeStaticTooltipComponent(sls * 100, "slash", null, true, true));

                //dynamic damage lines for weapons
                for (Attribute a : map.keySet()) {
                    String name = a.getDescriptionId();
                    if (name.contains("resistance") && !name.contains("knock") || name.contains("armor")) {
                        Collection<AttributeModifier> mods = map.get(a);
                        final double base = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                        double flat = base + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(base)).sum();
                        double mult = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                        mainArea.append(Util.writeIcon(name)).append(Util.writeTooltipInteger((int)flat)).append("+").append(Util.writeTooltipDouble(mult)).append(", ");
                    }
                }

            }

            for (Attribute a : attributeMap.keySet()) {
                //we need the name here to create tooltips abstractedly
                String name = a.getDescriptionId();

                //if the item is a melee weapon, there's no need to take the attack speed attribute since we've already displayed it
                //same thing for non-weapons and defense.
                if (isMeleeWeapon) {
                    if (name.contains("attack_speed")) continue;
                } else {
                    if (name.contains("armor_toughness")) continue;
                }

                //here we handle the attribute. First create a new component to display it in.
                MutableComponent component = Util.textComponent("");
                //use the name of the attribute to find the translation key for its icon
                component.append(Util.writeIcon(name));
                //grab all the modifiers attached to this attribute
                Collection<AttributeModifier> mods = attributeMap.get(a);
                //first we check if this item is a weapon, if it is display
                if (isWeapon && name.contains("damage")) {
                    final double flat = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                    double f1 = flat + mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_BASE)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount * Math.abs(flat)).sum();
                    double f2 = mods.stream().filter((modifier) -> modifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                    double finalValue = f1 * f2;
                    component.append(Util.writeTooltipNumber((int) finalValue));
                } else {
                    double flat = mods.stream().filter((modifier) -> modifier.getOperation().equals(ADDITION)).mapToDouble(AttributeModifier::getAmount).sum();
                    component.append(Util.textComponent(Util.threeDigit((int) flat)));
                    double totalMult = mods.stream().filter((modifier) -> modifier.getOperation().equals(MULTIPLY_TOTAL)).mapToDouble(AttributeModifier::getAmount).map((amount) -> amount + 1.0).reduce(1.0, (x, y) -> x * y);
                    if (totalMult != 1) component.append(Util.textComponent(" + " + Util.threeDigit((int) (totalMult * 100)) + "%"));
                }
                if (name.contains("damage")) {
                    damage.add(Util.textComponent(" ").append(component));
                } else if (name.contains("resistance") && !name.contains("knock") || name.contains("armor")) {
                    resistance.add(Util.textComponent(" ").append(component));
                } else {
                    other.add(component);
                }
            }

            //here we add all the components we've created.
            list.add(line1);
            list.add(line2);
            while (!other.isEmpty()) {
                MutableComponent component = Util.textComponent("");
                for (int i = 1; i <= 3; ++i) {
                    component.append(other.remove(0));
                    if (!other.isEmpty() && i < 3) component.append(Util.withColor(Util.textComponent(" | "), Color.LIGHTGOLDENRODYELLOW));
                    if (other.isEmpty()) break;
                }
                list.add(component);
            }
            if (damage.size() > 1) list.addAll(damage);
            if (resistance.size() > 1) list.addAll(resistance);
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

    public static boolean checkShiftDown() {
        return Screen.hasShiftDown();
    }

    private static double getAndRemoveAttribute(Multimap<Attribute, AttributeModifier> map, Attribute a) {
        double val = 0;
        for (AttributeModifier m : map.get(a)) {
            if (m.getOperation() == ADDITION) {
                val += m.getAmount();
                map.remove(a, m);
            }
        }
        return val;
    }

}

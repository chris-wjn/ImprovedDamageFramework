package net.cwjn.idf.event;

import com.google.common.collect.Multimap;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.gui.EquipmentInspectScreen;
import net.cwjn.idf.gui.StatScreen;
import net.cwjn.idf.gui.buttons.TabButton;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.util.Util;
import net.minecraft.ChatFormatting;
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
import java.util.List;
import java.util.Map;

import static net.cwjn.idf.ImprovedDamageFramework.FONT_ICONS;
import static net.cwjn.idf.gui.buttons.TabButton.TabType.INVENTORY;
import static net.cwjn.idf.gui.buttons.TabButton.TabType.STATS;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsForgeBus {

    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static final DecimalFormat onePlace = new DecimalFormat("#.#");
    private static final Style symbolStyle = Style.EMPTY.withFont(FONT_ICONS);

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
        //durability, damage class, and attack speed (if melee weapon)
        if (item.hasTag() && item.getTag().contains("idf.equipment")) {
            MutableComponent component = Util.textComponent("");
            component.append(Util.translationComponent("idf.icon.durability").withStyle(symbolStyle));
            if (item.isDamageableItem()) {
                double percentage = (double)(item.getMaxDamage()-item.getDamageValue())/(double)item.getMaxDamage();
                component.append(Util.withColor(
                        Util.textComponent((int)(percentage*100) + "%"),
                        new Color((int) (128+(128*0.5*(1.0-percentage))), (int) (255*percentage), 0)));
            } else {
                component.append(Util.withColor(Util.translationComponent("idf.infinity.symbol"), Color.DARKSEAGREEN));
            }
            component.append(Util.withColor(Util.textComponent(" | "), Color.LIGHTGOLDENRODYELLOW));
            if (item.getTag().contains("idf.damage_class")) {
                component.append(Util.translationComponent("idf.icon.damage_class").withStyle(symbolStyle));
                component.append(Util.translationComponent("idf.damage_class.tooltip." + item.getTag().getString("idf.damage_class")));
                if (!item.getTag().getBoolean("idf.ranged_weapon")) {
                    component.append(Util.withColor(Util.textComponent(" | "), Color.LIGHTGOLDENRODYELLOW));
                    component.append(Util.translationComponent("idf.icon.attack_speed").withStyle(symbolStyle));
                    double atkSpd = item.getAttributeModifiers(slot).get(Attributes.ATTACK_SPEED).stream().
                            filter(m -> m.getOperation() == ADDITION).
                            mapToDouble(AttributeModifier::getAmount).
                            sum();
                    component.append(Util.textComponent(onePlace.format(4 + atkSpd)));
                }
            }
            list.add(component);
        }
        Multimap<Attribute, AttributeModifier> multimap = item.getAttributeModifiers(slot);
        for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
            
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

}

package net.cwjn.idf.event;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.gui.EquipmentInspectScreen;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.gui.StatsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsForgeBus {

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

    @SubscribeEvent
    public static void openStatsScreen(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        onInput(minecraft, event.getKey(), event.getAction());
    }

    private static void onInput(Minecraft minecraft, int key, int action) {
        if (Keybinds.openStats.isDown() && minecraft.screen == null) {
            minecraft.setScreen(new StatsScreen());
        }
    }

    public static boolean checkShiftDown() {
        return Screen.hasShiftDown();
    }

}

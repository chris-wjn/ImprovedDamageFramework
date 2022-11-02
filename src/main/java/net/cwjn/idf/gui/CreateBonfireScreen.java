package net.cwjn.idf.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.block.entity.BonfireBlockEntity;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.network.packets.bonfire.ActivateBonfireMessage;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public class CreateBonfireScreen extends Screen {

    private EditBox nameEditor;
    private Button confirm;
    private final BonfireBlockEntity be;

    public CreateBonfireScreen(BonfireBlockEntity be) {
        super(Util.translationComponent("idf.create_bonfire_screen"));
        this.be = be;
    }

    @Override
    protected void init() {
        super.init();
        nameEditor = addRenderableWidget(new EditBox(font, width/2 - 50, height/2 - 7, 100, 15, Component.empty()));
        confirm = addRenderableWidget(new Button(width/2 - 40, height/2 + 30, 80, 20, Util.translationComponent("button.confirm"), press -> confirmAction()));
        nameEditor.setMaxLength(20);
        updateButtons();
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTick);
        nameEditor.render(matrix, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean charTyped(char c, int key) {
        boolean returnNameEditor = true;
        if (nameEditor.isFocused()) {
            returnNameEditor = nameEditor.charTyped(c, key);
            updateButtons();
        }
        return returnNameEditor;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        nameEditor.mouseClicked(mouseX, mouseY, button);
        updateButtons();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected void confirmAction() {
        if (!nameEditor.getValue().isEmpty()) {
            Minecraft.getInstance().level.playSound(Minecraft.getInstance().player, be.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1, 1);
            PacketHandler.playerToServer(new ActivateBonfireMessage(nameEditor.getValue(), be.getBlockPos().getX(), be.getBlockPos().getY(), be.getBlockPos().getZ()));
            minecraft.setScreen(null);
        }
        updateButtons();
    }

    public void updateButtons() {
        confirm.active = !nameEditor.getValue().isEmpty();
    }

    @Override
    public void tick() {
        if (nameEditor != null) {
            nameEditor.tick();
        }
        super.tick();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}

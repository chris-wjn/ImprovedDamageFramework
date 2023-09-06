package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.data.ClientData;
import net.cwjn.idf.gui.buttons.ArrowButton;
import net.cwjn.idf.gui.buttons.InvisibleButton;
import net.cwjn.idf.gui.buttons.OpenBestiaryEntryButton;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.network.packets.RequestBestiaryEntriesPacket;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class BestiaryScreen extends Screen {

    private static final ResourceLocation[] BOOK_OPEN = Util.openAnim();
    private static final ResourceLocation[] BOOK_FLIP_LEFT = Util.flipLeftAnim();
    private static final ResourceLocation[] BOOK_FLIP_RIGHT = Util.flipRightAnim();
    private static final ResourceLocation[] BOOK_CONTENT_APPEAR = Util.contentAppearAnim();
    private static final ResourceLocation GUI_BOOK = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/bestiary.png");

    private int w;
    private int h;
    private int left;
    private int top;
    private final int BOOK_WIDTH = 661;
    private final int BOOK_HEIGHT = 417;
    private final int BOOK_TURNING_HEIGHT = 457;
    private final int BOOK_OPENING_HEIGHT = 507;
    private InvisibleButton openBook;
    private ArrowButton turnLeft, turnRight;
    private AnimationState animState = AnimationState.CLOSED;
    private DisplayState dispState = DisplayState.CONTENTS;
    private int currentPage = 0;
    public List<ContentPage> pages = new ArrayList<>();
    private int timer = 0;
    private int OPENING_ANIM_FRAME = 1, TURNING_LEFT_ANIM_FRAME = 1, TURNING_RIGHT_ANIM_FRAME = 1, CONTENT_APPEAR_ANIM_FRAME = 1;
    private double savedScale;
    private boolean playAppearingAnim = false;

    public BestiaryScreen(double scale) {
        super(Component.translatable("idf.bestiary_screen"));
        savedScale = scale;
    }

    @Override
    protected void init() {
        super.init();
        PacketHandler.playerToServer(new RequestBestiaryEntriesPacket(minecraft.player.getUUID()));
        pages.add(new ContentPage(new ArrayList<>()));
        minecraft.getWindow().setGuiScale(2);
        w = minecraft.getWindow().getGuiScaledWidth();
        h = minecraft.getWindow().getGuiScaledHeight();
        left = (w - BOOK_WIDTH)/2;
        top = (h - BOOK_HEIGHT)/2;
        openBook = addWidget(new InvisibleButton(left, top, BOOK_WIDTH, BOOK_HEIGHT, Component.empty(), (f) -> beginOpenAnim()));
        turnLeft = addRenderableWidget(new ArrowButton(left-14, (int) (top+(BOOK_HEIGHT*0.5)), Component.empty(), (f) -> turnLeft(), false));
        turnRight = addRenderableWidget(new ArrowButton(left+(BOOK_WIDTH), (int) (top+(BOOK_HEIGHT*0.5)), Component.empty(), (f) -> turnRight(), true));
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float pTick) {
        matrix.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        boolean turningResolution = false;
        boolean openingResolution = false;
        if (animState == AnimationState.OPEN) {
            dispState = DisplayState.CONTENTS;
            RenderSystem.setShaderTexture(0, GUI_BOOK);
        }
        else if (animState == AnimationState.TURNING_LEFT) {
            dispState = DisplayState.NONE;
            if (timer % 10 == 0) {
                TURNING_LEFT_ANIM_FRAME++;
            }
            timer++;
            RenderSystem.setShaderTexture(0, BOOK_FLIP_LEFT[TURNING_LEFT_ANIM_FRAME-1]);
            if (TURNING_LEFT_ANIM_FRAME >= 8) {
                animState = AnimationState.OPEN;
                playAppearingAnim = true;
                timer = 0;
                TURNING_LEFT_ANIM_FRAME = 1;
            }
            turningResolution = true;
        }
        else if (animState == AnimationState.TURNING_RIGHT) {
            dispState = DisplayState.NONE;
            if (timer % 10 == 0) {
                TURNING_RIGHT_ANIM_FRAME++;
            }
            timer++;
            RenderSystem.setShaderTexture(0, BOOK_FLIP_RIGHT[TURNING_RIGHT_ANIM_FRAME-1]);
            if (TURNING_RIGHT_ANIM_FRAME >= 8) {
                animState = AnimationState.OPEN;
                playAppearingAnim = true;
                timer = 0;
                TURNING_RIGHT_ANIM_FRAME = 1;
            }
            turningResolution = true;
        }
        else if (animState == AnimationState.OPENING) {
            dispState = DisplayState.NONE;
            if (timer % 8 == 0) {
                OPENING_ANIM_FRAME++;
            }
            timer++;
            RenderSystem.setShaderTexture(0, BOOK_OPEN[OPENING_ANIM_FRAME-1]);
            if (OPENING_ANIM_FRAME >= 5) {
                animState = AnimationState.OPEN;
                OPENING_ANIM_FRAME = 1;
                timer = 0;
            }
            openingResolution = true;
        }
        else {
            RenderSystem.setShaderTexture(0, BOOK_OPEN[0]);
            dispState = DisplayState.NONE;
            openingResolution = true;
        }
        toggleArrows();
        int BOOK_TEXTURE_OFFSET_X = 118;
        if (turningResolution) {
            int BOOK_TEXTURE_OFFSET_Y_TURNING = 140;
            blit(matrix, left, top-41, BOOK_TEXTURE_OFFSET_X, BOOK_TEXTURE_OFFSET_Y_TURNING, BOOK_WIDTH, BOOK_TURNING_HEIGHT, 896, 720);
        }
        else if (openingResolution) {
            int BOOK_TEXTURE_OFFSET_Y_OPENING = 85;
            blit(matrix, left, top-96, BOOK_TEXTURE_OFFSET_X, BOOK_TEXTURE_OFFSET_Y_OPENING, BOOK_WIDTH, BOOK_OPENING_HEIGHT, 896, 720);
        }
        else {
            int BOOK_TEXTURE_OFFSET_Y = 181;
            blit(matrix, left, top, BOOK_TEXTURE_OFFSET_X, BOOK_TEXTURE_OFFSET_Y, BOOK_WIDTH, BOOK_HEIGHT, 896, 720);
        }
        if (dispState == DisplayState.CONTENTS) {
            pages.get(currentPage).buttons.forEach(b -> {
                b.active = true;
                b.visible = true;
            });
        }
        matrix.popPose();
        super.render(matrix, mouseX, mouseY, pTick);
    }

    private void beginOpenAnim() {
        int FP_LEFT_COLUMN = 38, SP_LEFT_COLUMN = 368, TOP_ROW = 8, ROW_SPACER = 33, COLUMN_SPACER = 136, MAX_Y = 338;
        int xOffset = FP_LEFT_COLUMN, yOffset = TOP_ROW, pageIteration = 0;
        boolean secondColumn = false, secondPage = false;
        for (ResourceLocation rl : ClientData.BESTIARY_ENTRIES) {
            OpenBestiaryEntryButton b = new OpenBestiaryEntryButton(left+xOffset, top+(yOffset+=ROW_SPACER), rl, (f) -> {
                minecraft.pushGuiLayer(new BestiaryEntryScreen(Util.getEntityTypeFromRegistryName(rl)));
            });
            b.active = false;
            b.visible = false;
            pages.get(pageIteration).buttons.add(addRenderableWidget(b));
            if (yOffset >= MAX_Y) {
                if (secondColumn) {
                    if (secondPage) {
                        pages.add(new ContentPage(new ArrayList<>()));
                        pageIteration++;
                        xOffset = FP_LEFT_COLUMN;
                        yOffset = TOP_ROW;
                        secondPage = false;
                    }
                    else {
                        xOffset = SP_LEFT_COLUMN;
                        yOffset = TOP_ROW;
                        secondPage = true;
                    }
                    secondColumn = false;
                }
                else {
                    xOffset += COLUMN_SPACER;
                    yOffset = TOP_ROW;
                    secondColumn = true;
                }
            }
        }
        animState = AnimationState.OPENING;
        openBook.active = false;
    }

    private void turnLeft() {
        pages.get(currentPage).buttons.forEach(b -> {
            b.active = false;
            b.visible = false;
        });
        animState = AnimationState.TURNING_LEFT;
        currentPage--;
    }

    private void turnRight() {
        pages.get(currentPage).buttons.forEach(b -> {
            b.active = false;
            b.visible = false;
        });
        animState = AnimationState.TURNING_RIGHT;
        currentPage++;
    }

    private void toggleArrows() {
        turnLeft.visible = currentPage >= 1;
        turnLeft.active = currentPage >= 1;
        turnRight.visible = currentPage+1 < pages.size();
        turnRight.active = currentPage+1 < pages.size();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        if (pMinecraft.screen == this) {
            pMinecraft.setScreen(null);
            pMinecraft.pauseGame(false);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        minecraft.getWindow().setGuiScale(savedScale);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == Keybinds.openBestiary.getKey().getValue() || pKeyCode == KeyEvent.VK_E) {
            this.minecraft.setScreen(null);
            minecraft.getWindow().setGuiScale(savedScale);
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private static class ContentPage {
        List<OpenBestiaryEntryButton> buttons = new ArrayList<>();
        public ContentPage(List<OpenBestiaryEntryButton> buttons) {
            this.buttons = buttons;
        }
    }

    private enum AnimationState {
        OPENING,
        OPEN,
        APPEARING,
        TURNING_RIGHT,
        TURNING_LEFT,
        CLOSED
    }

    private enum DisplayState {
        CONTENTS,
        ENTITY_PAGE,
        NONE
    }

}

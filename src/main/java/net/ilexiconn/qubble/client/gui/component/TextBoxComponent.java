package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class TextBoxComponent extends Gui implements IComponent<GuiScreen> {
    private String text;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private boolean selected;

    public TextBoxComponent(String defaultText, int posX, int posY, int width, int height) {
        this.text = defaultText;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(GuiScreen gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
        int primaryColor = QubbleGUI.getPrimaryColor();
        int secondaryColor = QubbleGUI.getSecondaryColor();
        GlStateManager.disableTexture2D();
        this.drawGradientRect(this.posX + 1, this.posY + 1, this.posX + this.width - 1, this.posY + this.height - 1, primaryColor, selected ? secondaryColor : primaryColor);
        QubbleGUI.drawOutline(this.posX, this.posY, this.width, this.height, Qubble.CONFIG.getAccentColor(), 1);
        GlStateManager.enableTexture2D();
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        int x = this.posX + (this.width / 2);
        int y = this.posY + (this.height / 2) - (fontRenderer.FONT_HEIGHT / 2);
        int centerWidth = fontRenderer.getStringWidth(this.text) / 2;
        fontRenderer.drawString(this.text, x - (centerWidth) + 0.625F, y - 0.625F, QubbleGUI.getTextColor(), false);
        if (this.selected && System.currentTimeMillis() % 1000 > 500) {
            QubbleGUI.drawRectangle(x + centerWidth + 1, y - 0.625, 1, fontRenderer.FONT_HEIGHT, QubbleGUI.getTextColor());
        }
    }

    @Override
    public void renderAfter(GuiScreen gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
    }

    @Override
    public boolean mouseClicked(GuiScreen gui, float mouseX, float mouseY, int button) {
        this.selected = this.isMouseSelecting(mouseX, mouseY);
        return this.selected;
    }

    @Override
    public boolean mouseDragged(GuiScreen gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        return false;
    }

    @Override
    public boolean mouseReleased(GuiScreen gui, float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(GuiScreen gui, char character, int key) {
        if (this.selected) {
            if (key == Keyboard.KEY_BACK) {
                if (this.text.length() > 0) {
                    this.text = this.text.substring(0, this.text.length() - 1);
                }
            } else if (character != 167 && character >= 32 && character != 127) {
                String newText = this.text + character;
                FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
                if (fontRenderer.getStringWidth(newText) < this.width) {
                    this.text = newText;
                }
            }
        }
        return this.selected;
    }

    private boolean isMouseSelecting(float mouseX, float mouseY) {
        return mouseX >= this.posX && mouseX < this.posX + this.width && mouseY >= this.posY && mouseY < this.posY + this.height;
    }

    public String getText() {
        return this.text;
    }
}

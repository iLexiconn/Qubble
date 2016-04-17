package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class TextBoxComponent extends Gui implements IGUIComponent {
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
    public void render(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks) {
        int primaryColor = QubbleGUI.getPrimaryColor();
        int secondaryColor = QubbleGUI.getSecondaryColor();
        GlStateManager.disableTexture2D();
        this.drawGradientRect(this.posX + 1, this.posY + 1, this.posX + this.width - 1, this.posY + this.height - 1, primaryColor, selected ? secondaryColor : primaryColor);
        gui.drawOutline(this.posX, this.posY, this.width, this.height, Qubble.CONFIG.getAccentColor(), 1);
        GlStateManager.enableTexture2D();
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        int x = this.posX + (this.width / 2);
        int y = this.posY + (this.height / 2) - (fontRenderer.FONT_HEIGHT / 2);
        int centerWidth = fontRenderer.getStringWidth(this.text) / 2;
        fontRenderer.drawString(this.text, x - (centerWidth) + 0.625F, y - 0.625F, 0xFFFFFF, false);
        if (this.selected && System.currentTimeMillis() % 1000 > 500) {
            gui.drawRectangle(x + centerWidth + 1, y - 0.625, 1, fontRenderer.FONT_HEIGHT, 0xFFFFFFFF);
        }
    }

    @Override
    public void renderAfter(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks) {
    }

    @Override
    public void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button) {
        this.selected = this.isMouseSelecting(mouseX, mouseY);
    }

    @Override
    public void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick) {
    }

    @Override
    public void mouseReleased(QubbleGUI gui, int mouseX, int mouseY, int button) {
    }

    @Override
    public void keyPressed(QubbleGUI gui, char character, int key) {
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
    }

    private boolean isMouseSelecting(int mouseX, int mouseY) {
        return mouseX >= this.posX && mouseX < this.posX + this.width && mouseY >= this.posY && mouseY < this.posY + this.height;
    }

    public String getText() {
        return this.text;
    }
}

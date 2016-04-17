package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;

public class TextComponent implements IComponent<QubbleGUI> {
    private String text;
    private int posX;
    private int posY;
    private int color;

    public TextComponent(String text, int posX, int posY, int color) {
        this.text = text;
        this.posX = posX;
        this.posY = posY;
        this.color = color;
    }

    @Override
    public void render(QubbleGUI gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        fontRenderer.drawString(this.text, this.posX - (fontRenderer.getStringWidth(this.text) / 2), this.posY - (fontRenderer.FONT_HEIGHT / 2), this.color);
    }

    @Override
    public void renderAfter(QubbleGUI gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
    }

    @Override
    public boolean mouseClicked(QubbleGUI gui, float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(QubbleGUI gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        return false;
    }

    @Override
    public boolean mouseReleased(QubbleGUI gui, float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(QubbleGUI gui, char character, int key) {
        return false;
    }
}

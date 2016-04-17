package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;

public class TextComponent implements IGUIComponent {
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
    public void render(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks) {
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        fontRenderer.drawString(this.text, this.posX - (fontRenderer.getStringWidth(this.text) / 2), this.posY - (fontRenderer.FONT_HEIGHT / 2), this.color);
    }

    @Override
    public void renderAfter(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks) {
    }

    @Override
    public void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button) {
    }

    @Override
    public void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick) {
    }

    @Override
    public void mouseReleased(QubbleGUI gui, int mouseX, int mouseY, int button) {
    }

    @Override
    public void keyPressed(QubbleGUI gui, char character, int key) {
    }
}

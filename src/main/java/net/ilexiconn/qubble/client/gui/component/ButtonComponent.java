package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class ButtonComponent extends Gui implements IGuiComponent {
    private int x;
    private int y;

    private int width;
    private int height;

    private String text;

    private IActionHandler actionHandler;

    public ButtonComponent(int x, int y, int width, int height, String text, IActionHandler action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.actionHandler = action;
    }

    @Override
    public void render(QubbleGUI gui, int mouseX, int mouseY, float partialTicks) {
        boolean selected = this.isSelected(mouseX, mouseY);
        GlStateManager.disableTexture2D();
        this.drawGradientRect(this.x + 1, this.y + 1, this.width - 1, this.height - 1, selected ? QubbleGUI.PRIMARY_COLOR : QubbleGUI.PRIMARY_COLOR, selected ? QubbleGUI.SECONDARY_COLOR : QubbleGUI.PRIMARY_COLOR);
        this.drawOutline(this.x, this.y, this.width, this.height, Qubble.CONFIG.accentColor, 1);
        GlStateManager.enableTexture2D();
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        fontRenderer.drawString(this.text, this.x + (this.width / 2) - (fontRenderer.getStringWidth(this.text) / 2), this.y + (this.height / 2) - (fontRenderer.FONT_HEIGHT / 2), 0xFFFFFF);
    }

    @Override
    public void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button) {
        if (this.isSelected(mouseX, mouseY)) {
            this.actionHandler.onAction();
        }
    }

    @Override
    public void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick) {

    }

    protected boolean isSelected(int mouseX, int mouseY) {
        return mouseX > this.x && mouseY > this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}

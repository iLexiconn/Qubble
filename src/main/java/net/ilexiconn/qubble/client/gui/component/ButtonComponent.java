package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.GuiQubbleEditor;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ButtonComponent implements IGuiComponent {
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
    public void render(GuiQubbleEditor gui, int mouseX, int mouseY, float partialTicks) {
        boolean selected = this.isSelected(mouseX, mouseY);
        GlStateManager.disableTexture2D();
        IGuiComponent.drawRectangle(this.x, this.y, this.width, this.height, selected ? GuiQubbleEditor.SECONDARY_COLOR : GuiQubbleEditor.PRIMARY_COLOR);
        IGuiComponent.drawOutline(this.x, this.y, this.width, this.height, selected ? GuiQubbleEditor.TERTIARY_COLOR : GuiQubbleEditor.SECONDARY_COLOR, 1);
        GlStateManager.enableTexture2D();
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        fontRenderer.drawString(this.text, this.x + (this.width / 2) - (fontRenderer.getStringWidth(this.text) / 2), this.y + (this.height / 2) - (fontRenderer.FONT_HEIGHT / 2), 0xFFFFFF);
    }

    @Override
    public void mouseClicked(GuiQubbleEditor gui, int mouseX, int mouseY, int button) {
        if (this.isSelected(mouseX, mouseY)) {
            this.actionHandler.action();
        }
    }

    @Override
    public void mouseDragged(GuiQubbleEditor gui, int mouseX, int mouseY, int button, long timeSinceClick) {
    }

    protected boolean isSelected(int mouseX, int mouseY) {
        return mouseX > this.x && mouseY > this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}

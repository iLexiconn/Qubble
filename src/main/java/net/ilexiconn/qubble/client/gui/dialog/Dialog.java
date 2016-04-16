package net.ilexiconn.qubble.client.gui.dialog;

import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.component.ButtonComponent;
import net.ilexiconn.qubble.client.gui.component.IGUIComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

public class Dialog {
    private String name;

    private int posX;
    private int posY;

    private int prevPosX;
    private int prevPosY;

    private int width;
    private int height;

    private int dragOffsetX;
    private int dragOffsetY;
    private boolean isDragging;

    private List<IGUIComponent> components = new ArrayList<>();

    public Dialog(String name, int posX, int posY, int width, int height) {
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.width = width;
        this.height = height;
        ButtonComponent closeWindowComponent = new ButtonComponent("x", this.width - 12, 0, 12, 12, "Close this dialog", (gui, component) -> gui.closeDialog(Dialog.this));
        closeWindowComponent.setColorScheme(0xFFFF2020, 0xFF7F0000, 0xFFFF2020, 0xFFFFFFFF);
        this.addComponent(closeWindowComponent);
    }

    public void addComponent(IGUIComponent component) {
        this.components.add(component);
    }

    public void render(QubbleGUI gui, int mouseX, int mouseY, float partialTicks) {
        double drawX = ClientUtils.interpolate(this.prevPosX, this.posX, partialTicks);
        double drawY = ClientUtils.interpolate(this.prevPosY, this.posY, partialTicks);
        int accentColor = Qubble.CONFIG.getAccentColor();
        gui.drawRectangle(drawX, drawY, this.width, this.height, QubbleGUI.getSecondaryColor());
        gui.drawOutline(drawX, drawY, this.width, this.height, accentColor, 1);
        gui.drawOutline(drawX, drawY, this.width, 12, accentColor, 1);
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        fontRenderer.drawString(this.name, (float) drawX + 2.0F, (float) drawY + 2.0F, QubbleGUI.getTextColor(), false);
        mouseX -= this.posX;
        mouseY -= this.posY;
        GlStateManager.pushMatrix();
        GlStateManager.translate(drawX, drawY, 0.0);
        for (IGUIComponent component : this.components) {
            component.render(gui, mouseX, mouseY, drawX, drawY, partialTicks);
        }
        GlStateManager.popMatrix();
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
    }

    public void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button) {
        if (button == 0 && mouseX > this.posX && mouseX < this.posX + this.width && mouseY > this.posY && mouseY < this.posY + 12) {
            this.dragOffsetX = mouseX - this.posX;
            this.dragOffsetY = mouseY - this.posY;
            this.isDragging = true;
        }
        mouseX -= this.posX;
        mouseY -= this.posY;
        for (IGUIComponent component : this.components) {
            component.mouseClicked(gui, mouseX, mouseY, button);
        }
    }

    public void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick) {
        if (this.isDragging) {
            this.posX = Math.min(Math.max(mouseX - this.dragOffsetX, 0), gui.width - this.width);
            this.posY = Math.min(Math.max(mouseY - this.dragOffsetY, 0), gui.height - this.height);
        }
        mouseX -= this.posX;
        mouseY -= this.posY;
        for (IGUIComponent component : this.components) {
            component.mouseDragged(gui, mouseX, mouseY, button, timeSinceClick);
        }
    }

    public void mouseReleased(QubbleGUI gui, int mouseX, int mouseY, int button) {
        this.isDragging = false;
        mouseX -= this.posX;
        mouseY -= this.posY;
        for (IGUIComponent component : this.components) {
            component.mouseReleased(gui, mouseX, mouseY, button);
        }
    }
}

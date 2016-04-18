package net.ilexiconn.qubble.client.gui.dialog;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.component.ButtonComponent;
import net.ilexiconn.qubble.client.gui.component.IComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class Dialog<T extends GuiScreen> {
    public float posX;
    public float posY;
    public int width;
    public int height;
    private T gui;
    private String name;
    private float prevPosX;
    private float prevPosY;
    private float dragOffsetX;
    private float dragOffsetY;
    private boolean isDragging;

    private List<IComponent<T>> components = new ArrayList<>();

    public Dialog(T gui, String name, int posX, int posY, int width, int height) {
        this.gui = gui;
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.width = width;
        this.height = height;
        ButtonComponent closeWindowComponent = new ButtonComponent("x", this.width - 12, 0, 12, 12, "Close this dialog", (g, c) -> DialogHandler.INSTANCE.closeDialog(gui, Dialog.this));
        closeWindowComponent.setColorScheme(0xFFFF2020, 0xFF7F0000, 0xFFFF2020, 0xFFFFFFFF);
        this.addComponent(closeWindowComponent);
    }

    public void addComponent(IComponent<?> component) {
        this.components.add((IComponent<T>) component);
    }

    public void update() {
        for (IComponent<T> component : this.components) {
            component.update(this.gui);
        }
    }

    public void render(float mouseX, float mouseY, float partialTicks) {
        double drawX = QubbleGUI.interpolate(this.prevPosX, this.posX, partialTicks);
        double drawY = QubbleGUI.interpolate(this.prevPosY, this.posY, partialTicks);
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        int scaleFactor = scaledResolution.getScaleFactor();
        int accentColor = Qubble.CONFIG.getAccentColor();
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (drawX * scaleFactor), (int) ((gui.height - (drawY + this.height)) * scaleFactor) + 1, this.width * scaleFactor, this.height * scaleFactor);
        QubbleGUI.drawRectangle(drawX, drawY, this.width, this.height, QubbleGUI.getSecondaryColor());
        QubbleGUI.drawOutline(drawX, drawY, this.width, this.height - 1, accentColor, 1);
        QubbleGUI.drawOutline(drawX, drawY, this.width, 12, accentColor, 1);
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        fontRenderer.drawString(this.name, (float) drawX + 2.0F, (float) drawY + 2.0F, QubbleGUI.getTextColor(), false);
        mouseX -= this.posX;
        mouseY -= this.posY;
        GlStateManager.translate(drawX, drawY, 0.0);
        for (IComponent<T> component : this.components) {
            component.render(gui, mouseX, mouseY, drawX, drawY, partialTicks);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
    }

    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (button != 0 || mouseX < this.posX || mouseX > this.posX + this.width || mouseY < this.posY || mouseY > this.posY + this.height) {
            return false;
        }
        if (mouseY < this.posY + 12) {
            this.dragOffsetX = mouseX - this.posX;
            this.dragOffsetY = mouseY - this.posY;
            this.isDragging = true;
        }
        mouseX -= this.posX;
        mouseY -= this.posY;
        for (IComponent<T> component : this.components) {
            if (component.mouseClicked(gui, mouseX, mouseY, button)) {
                return true;
            }
        }
        return true;
    }

    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.isDragging) {
            this.posX = Math.min(Math.max(mouseX - this.dragOffsetX, 0), gui.width - this.width);
            this.posY = Math.min(Math.max(mouseY - this.dragOffsetY, 0), gui.height - this.height);
            return true;
        }
        mouseX -= this.posX;
        mouseY -= this.posY;
        for (IComponent<T> component : this.components) {
            if (component.mouseDragged(gui, mouseX, mouseY, button, timeSinceClick)) {
                return true;
            }
        }
        return false;
    }

    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.isDragging = false;
        mouseX -= this.posX;
        mouseY -= this.posY;
        for (IComponent<T> component : this.components) {
            if (component.mouseReleased(gui, mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public boolean keyPressed(char character, int keyCode) {
        for (IComponent<T> component : this.components) {
            if (component.keyPressed(gui, character, keyCode)) {
                return true;
            }
        }
        return false;
    }
}

package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.color.ColorScheme;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WindowElement extends Element<QubbleGUI> {
    private String name;
    private float dragOffsetX;
    private float dragOffsetY;
    private boolean isDragging;

    public WindowElement(QubbleGUI gui, String name, int width, int height) {
        this(gui, name, width, height, gui.width / 2 - width / 2, gui.height / 2 - height / 2);
    }

    public WindowElement(QubbleGUI gui, String name, int width, int height, int posX, int posY) {
        super(gui, posX, posY, width, height);
        this.name = name;
        this.addElement(new ButtonElement(gui, "x", this.getWidth() - 14, 0, 14, 14, (v) -> {
            ElementHandler.INSTANCE.removeElement(this.getGUI(), this);
            return true;
        }).withColorScheme(ColorScheme.CLOSE));
    }

    public void addElement(Element<QubbleGUI> element) {
        element.withParent(this);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        this.startScissor();
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), LLibrary.CONFIG.getPrimaryColor());
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), 14, LLibrary.CONFIG.getAccentColor());
        FontRenderer fontRenderer = this.getGUI().mc.fontRendererObj;
        fontRenderer.drawString(this.name, this.getPosX() + 2.0F, this.getPosY() + 3.0F, LLibrary.CONFIG.getTextColor(), false);
        GlStateManager.popMatrix();
        this.endScissor();
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (button != 0 || !this.isSelected(mouseX, mouseY)) {
            return false;
        }
        if (mouseY < this.getPosY() + 14) {
            this.dragOffsetX = mouseX - this.getPosX();
            this.dragOffsetY = mouseY - this.getPosY();
            this.isDragging = true;
            ElementHandler.INSTANCE.removeElement(this.getGUI(), this);
            ElementHandler.INSTANCE.addElement(this.getGUI(), this);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.isDragging) {
            this.setPosX(Math.min(Math.max(mouseX - this.dragOffsetX, 0), this.getGUI().width - this.getWidth()));
            this.setPosY(Math.min(Math.max(mouseY - this.dragOffsetY, 0), this.getGUI().height - this.getHeight()));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.isDragging = false;
        return false;
    }
}

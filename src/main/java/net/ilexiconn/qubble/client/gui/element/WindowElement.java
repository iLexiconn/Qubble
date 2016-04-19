package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class WindowElement extends Element<QubbleGUI> {
    private String name;
    private float dragOffsetX;
    private float dragOffsetY;
    private boolean isDragging;

    private List<Element<QubbleGUI>> elementList = new ArrayList<>();

    public WindowElement(QubbleGUI gui, String name, int width, int height) {
        this(gui, name, width, height, gui.width / 2 - width / 2, gui.height / 2 - height / 2);
    }

    public WindowElement(QubbleGUI gui, String name, int width, int height, int posX, int posY) {
        super(gui, posX, posY, width, height);
        this.name = name;
        this.addElement(new ButtonElement(gui, "x", this.getWidth() - 14, 0, 14, 14, (g, c) -> ElementHandler.INSTANCE.removeElement(g, this)).withReverseColors(true).withColorScheme(-1, 0xFFE04747));
    }

    public void addElement(Element<QubbleGUI> element) {
        this.elementList.add(element.withParent(this));
    }

    @Override
    public void update() {
        this.elementList.forEach(Element::update);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), Qubble.CONFIG.getPrimaryColor());
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), 14, Qubble.CONFIG.getAccentColor());
        FontRenderer fontRenderer = this.getGUI().mc.fontRendererObj;
        fontRenderer.drawString(this.name, this.getPosX() + 2.0F, this.getPosY() + 3.0F, Qubble.CONFIG.getTextColor(), false);
        mouseX -= this.getPosX();
        mouseY -= this.getPosY();
        GlStateManager.translate(this.getPosX(), this.getPosY(), 0.0);
        for (Element<QubbleGUI> element : this.elementList) {
            element.render(mouseX, mouseY, partialTicks);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (button != 0 || mouseX < this.getPosX() || mouseX > this.getPosX() + this.getWidth() || mouseY < this.getPosY() || mouseY > this.getPosY() + this.getHeight()) {
            return false;
        }
        if (mouseY < this.getPosY() + 14) {
            this.dragOffsetX = mouseX - this.getPosX();
            this.dragOffsetY = mouseY - this.getPosY();
            this.isDragging = true;
        }
        mouseX -= this.getPosX();
        mouseY -= this.getPosY();
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.isDragging) {
            this.setPosX(Math.min(Math.max(mouseX - this.dragOffsetX, 0), this.getGUI().width - this.getWidth()));
            this.setPosY(Math.min(Math.max(mouseY - this.dragOffsetY, 0), this.getGUI().height - this.getHeight()));
            return true;
        }
        mouseX -= this.getPosX();
        mouseY -= this.getPosY();
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.mouseDragged(mouseX, mouseY, button, timeSinceClick)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.isDragging = false;
        mouseX -= this.getPosX();
        mouseY -= this.getPosY();
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(char character, int keyCode) {
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.keyPressed(character, keyCode)) {
                return true;
            }
        }
        return false;
    }
}

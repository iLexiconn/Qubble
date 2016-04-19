package net.ilexiconn.qubble.client.gui.element;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Element<T extends GuiScreen> extends Gui {
    private T gui;
    private float posX;
    private float posY;
    private float width;
    private float height;
    private WindowElement parent;

    public Element(T gui, float posX, float posY, float width, float height) {
        this.gui = gui;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void init() {

    }

    public void update() {

    }

    public void render(float mouseX, float mouseY, float partialTicks) {

    }

    public void renderAfter(float mouseX, float mouseY, float partialTicks) {

    }

    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        return false;
    }

    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        return false;
    }

    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        return false;
    }

    public boolean keyPressed(char character, int key) {
        return false;
    }

    public T getGUI() {
        return gui;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Element<T> withParent(WindowElement parent) {
        this.parent = parent;
        return this;
    }

    public WindowElement getParent() {
        return parent;
    }
}

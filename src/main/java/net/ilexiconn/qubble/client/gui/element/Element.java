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
    private int width;
    private int height;
    private WindowElement parent;
    private boolean visible = true;

    public Element(T gui, float posX, float posY, int width, int height) {
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
        return this.isSelected(mouseX, mouseY);
    }

    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        return this.isSelected(mouseX, mouseY);
    }

    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Element<T> withParent(WindowElement parent) {
        this.parent = parent;
        return this;
    }

    public WindowElement getParent() {
        return parent;
    }

    public void setParent(WindowElement parent) {
        this.parent = parent;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected boolean isSelected(float mouseX, float mouseY) {
        return ElementHandler.INSTANCE.isOnTop(this.getGUI(), this, mouseX, mouseY) && mouseX >= this.getActualPosX() && mouseY >= this.getActualPosY() && mouseX < this.getActualPosX() + this.getWidth() && mouseY < this.getActualPosY() + this.getHeight();
    }

    public float getActualPosX() {
        return this.posX + (this.getParent() != null ? this.getParent().getPosX() : 0);
    }

    public float getActualPosY() {
        return this.posY + (this.getParent() != null ? this.getParent().getPosY() : 0);
    }
}

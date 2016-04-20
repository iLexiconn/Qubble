package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.client.ClientProxy;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getPosX() {
        return this.posX + (this.getParent() != null ? this.getParent().getPosX() : 0);
    }

    public float getPosY() {
        return this.posY + (this.getParent() != null ? this.getParent().getPosY() : 0);
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
        return ElementHandler.INSTANCE.isOnTop(this.getGUI(), this, mouseX, mouseY) && mouseX >= this.getPosX() && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + this.getHeight();
    }

    protected void startScissor() {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        float scaleFactor = new ScaledResolution(ClientProxy.MINECRAFT).getScaleFactor();
        GL11.glScissor((int) (this.posX * scaleFactor), (int) ((this.gui.height - (this.posY + this.height)) * scaleFactor), (int) (this.width * scaleFactor), (int) (this.height * scaleFactor));
    }

    protected void endScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}

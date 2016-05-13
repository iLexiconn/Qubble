package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.server.util.IGetter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ScrollBarElement<T extends GuiScreen> extends Element<T> {
    private int maxScroll;
    private float scrollPerEntry;
    private IGetter<Integer> entryCount;
    private float entryHeight;
    private IGetter<Float> offsetX;
    private IGetter<Float> offsetY;
    private IGetter<Float> displayHeight;

    private int scroll;
    private int scrollYOffset;
    private boolean scrolling;

    public ScrollBarElement(T gui, Element<T> parent, IGetter<Float> posX, IGetter<Float> posY, IGetter<Float> displayHeight, int entryHeight, IGetter<Integer> entryCount) {
        super(gui, posX.get(), posY.get(), 4, 0);
        this.withParent(parent);
        this.offsetX = posX;
        this.offsetY = posY;
        this.entryHeight = entryHeight;
        this.entryCount = entryCount;
        this.displayHeight = displayHeight;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        if (this.maxScroll > 0) {
            this.drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight() - 3, this.scrolling ? Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getSecondaryColor());
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (this.maxScroll > 0 && button == 0) {
            if (this.isSelected(mouseX, mouseY)) {
                this.scrolling = true;
                this.scrollYOffset = (int) (mouseY - this.getPosY());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.scrolling) {
            this.scroll = (int) Math.max(0, Math.min(this.maxScroll / this.scrollPerEntry, mouseY - this.scrollYOffset - (this.offsetY.get() + this.getParent().getPosY())));
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.scrolling = false;
        return false;
    }

    @Override
    public void update() {
        this.setPosX(this.offsetX.get());
        this.setPosY(this.offsetY.get() + this.scroll);
        float parentHeight = this.getParent().getHeight();
        int maxDisplayEntries = (int) (parentHeight / entryHeight);
        int entryCount = this.entryCount.get();
        this.maxScroll = Math.max(0, entryCount - maxDisplayEntries);
        this.scrollPerEntry = (float) entryCount / parentHeight;
        this.setHeight((int) (parentHeight / ((float) entryCount / (float) maxDisplayEntries)));

        if (this.scroll > this.maxScroll / this.scrollPerEntry) {
            this.scroll = (int) (this.maxScroll / this.scrollPerEntry);
        }
    }

    public float getScrollOffset() {
        return this.scroll * (this.entryCount.get() / this.displayHeight.get()) * this.entryHeight;
    }

    private void drawRectangle(double x, double y, double width, double height, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        float a = (float) (color >> 24 & 0xFF) / 255.0F;
        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(x, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x, y, 0.0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public int getMaxScroll() {
        return maxScroll;
    }
}

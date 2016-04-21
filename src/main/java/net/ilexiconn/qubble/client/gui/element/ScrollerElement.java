package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;

public class ScrollerElement extends Element<QubbleGUI> {
    private int maxDisplayEntries;
    private int maxScroll;
    private float scrollPerEntry;
    private int entryCount;
    private float entryHeight;

    private int scroll;
    private int scrollYOffset;
    private boolean scrolling;

    public ScrollerElement(QubbleGUI gui, int width) {
        super(gui, 0.0F, 0.0F, width, 0);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        if (this.maxScroll > 0) {
            this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight() - 3, this.scrolling ? Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getSecondaryColor());
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
            this.scroll = (int) Math.max(0, Math.min(this.maxScroll / this.scrollPerEntry, ((mouseY - this.scrollYOffset) - (this.getPosY() - this.scroll))));
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.scrolling = false;
        return false;
    }

    public void updateState(Element<QubbleGUI> element, float offsetX, float offsetY, int entryHeight, int entryCount) {
        this.setPosX(element.getPosX() + offsetX);
        this.setPosY(element.getPosY() + offsetY + this.scroll);
        this.maxDisplayEntries = element.getHeight() / entryHeight;
        this.maxScroll = Math.max(0, entryCount - this.maxDisplayEntries);
        this.scrollPerEntry = (float) entryCount / element.getHeight();
        this.entryCount = entryCount;
        this.entryHeight = entryHeight;
        this.setHeight((int) (element.getHeight() / ((float) entryCount / (float) this.maxDisplayEntries)));
    }

    public int getScroll() {
        return this.scroll;
    }

    public float getScrollYOffset() {
        return this.scroll * this.scrollPerEntry * this.entryHeight;
    }
}

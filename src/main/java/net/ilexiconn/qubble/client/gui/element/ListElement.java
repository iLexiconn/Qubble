package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class ListElement extends Element<QubbleGUI> {
    private List<String> entries;
    private Function<String, Boolean> function;

    private int scroll;
    private int maxDisplayEntries;
    private int maxScroll;
    private int scrollYOffset;
    private boolean scrolling;
    private float scrollPerEntry;

    public ListElement(QubbleGUI gui, float posX, float posY, int width, int height, List<String> entries, Function<String, Boolean> function) {
        super(gui, posX, posY, width, height);
        this.entries = entries;
        this.function = function;
        this.maxDisplayEntries = this.getHeight() / 12;
        this.maxScroll = Math.max(0, this.entries.size() - this.maxDisplayEntries);
        this.scrollPerEntry = (float) (this.entries.size()) / (this.getHeight() - 2);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), Qubble.CONFIG.getSecondaryColor());
        FontRenderer fontRenderer = this.getGUI().mc.fontRendererObj;
        int y = (int) (-this.scroll * this.scrollPerEntry * 12);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = this.getGUI().getResolution().getScaleFactor();
        GL11.glScissor((int) (this.getPosX() * scaleFactor), (int) ((this.getGUI().height - (this.getPosY() + this.getHeight()) + 2) * scaleFactor), this.getWidth() * scaleFactor, (this.getHeight() - 4) * scaleFactor);
        for (String entry : this.entries) {
            float entryX = this.getPosX() + 2;
            float entryY = this.getPosY() + y + 2;
            float entryWidth = this.getWidth() - 4;
            int entryHeight = 11;
            boolean selected = this.isSelected(this.getPosX() + 2, this.getPosY() + y + 1, entryWidth, entryHeight + 1, mouseX, mouseY) && !this.scrolling;
            this.getGUI().drawRectangle(entryX, entryY, entryWidth, entryHeight + 1, selected ? Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getSecondaryColor());
            fontRenderer.drawString(entry, entryX + 2, entryY + 2, Qubble.CONFIG.getTextColor(), false);
            y += 13;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (this.maxScroll > 0) {
            float scrollX = this.getPosX() + this.getWidth() - 8;
            float scrollY = this.getPosY() + this.scroll + 2;
            int height = (int) ((this.getHeight() - 2) / ((float) this.entries.size() / (float) this.maxDisplayEntries)) - 2;
            this.getGUI().drawRectangle(scrollX, scrollY, 6, height, this.scrolling ? Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getPrimaryColor());
        } else {
            this.getGUI().drawRectangle(this.getPosX() + this.getWidth() - 8, this.getPosY() + 2, 6, this.getHeight() - 4, Qubble.CONFIG.getPrimaryColor());
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (this.maxScroll > 0) {
            float scrollX = this.getPosX() + this.getWidth() - 8;
            float scrollY = this.getPosY() + (this.scroll);
            int height = (int) ((this.getHeight() - 2) / ((float) this.entries.size() / (float) this.maxDisplayEntries));
            if (mouseX >= scrollX && mouseX < scrollX + 6 && mouseY >= scrollY + 1 && mouseY < scrollY + height) {
                this.scrolling = true;
                this.scrollYOffset = (int) (mouseY - scrollY);
                return true;
            }
        }
        int y = (int) (-this.scroll * this.scrollPerEntry * 12);
        for (String entry : this.entries) {
            if (y + 13 < this.getHeight() && y >= 0) {
                float entryX = this.getPosX() + 2;
                float entryY = this.getPosY() + y + 1;
                float entryWidth = this.getWidth() - 12;
                int entryHeight = 12;
                if (this.isSelected(entryX, entryY, entryWidth, entryHeight, mouseX, mouseY)) {
                    if (this.function.apply(entry)) {
                        this.getGUI().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
                    }
                    return true;
                }
            }
            y += 13;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.scrolling) {
            this.scroll = (int) Math.max(0, Math.min(this.maxScroll / this.scrollPerEntry, mouseY - this.getPosY() - 2 - this.scrollYOffset));
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.scrolling = false;
        return false;
    }

    private boolean isSelected(float entryX, float entryY, float entryWidth, float entryHeight, float mouseX, float mouseY) {
        return ElementHandler.INSTANCE.isOnTop(this.getGUI(), this, mouseX, mouseY) && mouseX > entryX && mouseX < entryX + entryWidth && mouseY > entryY && mouseY < entryY + entryHeight;
    }
}

package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
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
    private ScrollBarElement<QubbleGUI> scroller;

    public ListElement(QubbleGUI gui, float posX, float posY, int width, int height, List<String> entries, Function<String, Boolean> function) {
        super(gui, posX, posY, width, height);
        this.entries = entries;
        this.function = function;
    }

    @Override
    public void init() {
        this.scroller = new ScrollBarElement<>(this.getGUI(), this, () -> this.getWidth() - 8.0F, () -> 2.0F, () -> this.getHeight() - 2.0F, 13, () -> this.entries.size());
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), LLibrary.CONFIG.getSecondaryColor());
        FontRenderer fontRenderer = this.getGUI().mc.fontRendererObj;
        float y = -this.scroller.getScrollOffset();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = this.getGUI().getResolution().getScaleFactor();
        GL11.glScissor((int) (this.getPosX() * scaleFactor), (int) ((this.getGUI().height - (this.getPosY() + this.getHeight()) + 2) * scaleFactor), this.getWidth() * scaleFactor, (this.getHeight() - 4) * scaleFactor);
        for (String entry : this.entries) {
            float entryX = this.getPosX() + 2;
            float entryY = this.getPosY() + y + 2;
            float entryWidth = this.getWidth() - 4;
            int entryHeight = 11;
            boolean selected = this.isSelected(this.getPosX() + 2, this.getPosY() + y + 1, entryWidth, entryHeight + 1, mouseX, mouseY) && !this.scroller.isScrolling();
            if (selected) {
                this.getGUI().drawRectangle(entryX, entryY, entryWidth, entryHeight + 1, LLibrary.CONFIG.getAccentColor());
            }
            fontRenderer.drawString(entry, entryX + 2, entryY + 2, LLibrary.CONFIG.getTextColor(), false);
            y += 13;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        this.getGUI().drawRectangle(this.getPosX() + this.getWidth() - 9, this.getPosY() + 1, 6, this.getHeight() - 2, LLibrary.CONFIG.getPrimaryColor());
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (this.isSelected(mouseX, mouseY)) {
            float y = -this.scroller.getScrollOffset();
            for (String entry : this.entries) {
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
                y += 13;
            }
        }
        return false;
    }

    private boolean isSelected(float entryX, float entryY, float entryWidth, float entryHeight, float mouseX, float mouseY) {
        return ElementHandler.INSTANCE.isOnTop(this.getGUI(), this, mouseX, mouseY) && mouseX > entryX && mouseX < entryX + entryWidth && mouseY > entryY && mouseY < entryY + entryHeight;
    }
}

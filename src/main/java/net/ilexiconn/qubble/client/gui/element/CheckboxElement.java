package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class CheckboxElement extends Element<QubbleGUI> {
    private boolean selected;
    private Function<Boolean, Boolean> function;

    public CheckboxElement(QubbleGUI gui, float posX, float posY) {
        this(gui, posX, posY, null);
    }

    public CheckboxElement(QubbleGUI gui, float posX, float posY, Function<Boolean, Boolean> function) {
        super(gui, posX, posY, 14, 14);
        this.function = function;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().drawRectangle(this.getPosX() + 1, this.getPosY() + 1, this.getWidth() - 1, this.getHeight() - 1, Qubble.CONFIG.getSecondaryColor());
        if (this.selected) {
            this.getGUI().drawRectangle(this.getPosX() + 3, this.getPosY() + 3, this.getWidth() - 5, this.getHeight() - 5, Qubble.CONFIG.getTextColor());
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0 && super.isSelected(mouseX, mouseY)) {
            if (this.function != null && this.function.apply(!this.selected)) {
                this.selected = !this.selected;
                this.getGUI().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
            }
            return true;
        }
        return false;
    }

    public CheckboxElement withActionHandler(Function<Boolean, Boolean> actionHandler) {
        this.function = actionHandler;
        return this;
    }

    public CheckboxElement withSelection(boolean selected) {
        this.selected = selected;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }
}

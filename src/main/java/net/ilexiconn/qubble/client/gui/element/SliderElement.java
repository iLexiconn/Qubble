package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class SliderElement extends Element<QubbleGUI> {
    private float value;
    private Function<Float, Boolean> function;
    private boolean intValue;
    private DecimalFormat decimalFormat;
    private boolean editable = true;

    public SliderElement(QubbleGUI gui, float posX, float posY, Function<Float, Boolean> function) {
        this(gui, posX, posY, false, function);
    }

    public SliderElement(QubbleGUI gui, float posX, float posY, boolean intValue, Function<Float, Boolean> function) {
        super(gui, posX, posY, 38, 12);
        this.function = function;
        this.intValue = intValue;
        this.decimalFormat = new DecimalFormat("#.#");
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        QubbleGUI gui = this.getGUI();
        float posX = this.getPosX();
        float posY = this.getPosY();
        int width = this.getWidth();
        int height = this.getHeight();
        gui.drawRectangle(posX, posY, width, height, this.editable ? Qubble.CONFIG.getSecondaryColor() : Qubble.CONFIG.getSecondarySubcolor());
        boolean selected = this.isSelected(mouseX, mouseY);
        boolean upperSelected = this.editable && selected && mouseX >= posX + width - 11 && mouseY < posY + 6;
        boolean lowerSelected = this.editable && selected && mouseX >= posX + width - 11 && mouseY > posY + 6;
        gui.drawRectangle(posX + width - 11, posY, 11, 6, this.editable ? upperSelected ? Qubble.CONFIG.getDarkAccentColor() : Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getTertiaryColor());
        gui.drawRectangle(posX + width - 11, posY + 6, 11, 6, this.editable ? lowerSelected ? Qubble.CONFIG.getDarkAccentColor() : Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getTertiaryColor());
        int textColor = Qubble.CONFIG.getTextColor();
        gui.drawRectangle(posX + width - 8, posY + 4, 5, 1, textColor);
        gui.drawRectangle(posX + width - 7, posY + 3, 3, 1, textColor);
        gui.drawRectangle(posX + width - 6, posY + 2, 1, 1, textColor);
        gui.drawRectangle(posX + width - 8, posY + 7, 5, 1, textColor);
        gui.drawRectangle(posX + width - 7, posY + 8, 3, 1, textColor);
        gui.drawRectangle(posX + width - 6, posY + 9, 1, 1, textColor); //who needs good code, amirite?
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        float scaleFactor = gui.getResolution().getScaleFactor();
        GL11.glScissor((int) (posX * scaleFactor), (int) ((gui.height - (posY + height)) * scaleFactor), (int) ((width - 11) * scaleFactor), (int) (height * scaleFactor));
        String text = this.intValue ? String.valueOf((int) this.value) : this.decimalFormat.format(this.value);
        gui.mc.fontRendererObj.drawString(text, posX + 2, posY + 3.0F, textColor, false);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (!this.editable) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        boolean upperSelected = this.isSelected(mouseX, mouseY) && mouseX >= this.getPosX() + this.getWidth() - 11 && mouseY < this.getPosY() + 6;
        boolean lowerSelected = this.isSelected(mouseX, mouseY) && mouseX >= this.getPosX() + this.getWidth() - 11 && mouseY > this.getPosY() + 6;
        if (upperSelected) {
            float newValue = this.intValue || GuiScreen.isShiftKeyDown() ? this.value + 1 : this.value + 0.1F;
            if (this.function.apply(newValue)) {
                this.getGUI().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
                this.value = newValue;
                return true;
            }
        } else if (lowerSelected) {
            float newValue = this.intValue || GuiScreen.isShiftKeyDown() ? this.value - 1 : this.value - 0.1F;
            if (this.function.apply(newValue)) {
                this.getGUI().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
                this.value = newValue;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}

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
    private boolean hasSlider;
    private float sliderWidth;
    private float minValue;
    private float maxValue;
    private boolean editable = true;
    private boolean dragging;

    public SliderElement(QubbleGUI gui, float posX, float posY, Function<Float, Boolean> function) {
        this(gui, posX, posY, false, function);
    }

    public SliderElement(QubbleGUI gui, float posX, float posY, boolean intValue, Function<Float, Boolean> function) {
        this(gui, posX, posY, intValue, 0.0F, -1.0F, -1.0F, function);
    }

    public SliderElement(QubbleGUI gui, float posX, float posY, boolean intValue, float sliderWidth, float minValue, float maxValue, Function<Float, Boolean> function) {
        super(gui, posX, posY, (int) (38 + sliderWidth), 12);
        this.function = function;
        this.intValue = intValue;
        this.decimalFormat = new DecimalFormat("#.#");
        this.hasSlider = sliderWidth > 0.0F;
        this.sliderWidth = sliderWidth;
        this.minValue = minValue;
        this.maxValue = maxValue;
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
        boolean upperSelected = this.editable && selected && mouseX >= posX + width - this.sliderWidth - 11 && mouseY < posY + 6 && mouseX < posX + width - this.sliderWidth;
        boolean lowerSelected = this.editable && selected && mouseX >= posX + width - this.sliderWidth - 11 && mouseY > posY + 6 && mouseX < posX + width - this.sliderWidth;
        gui.drawRectangle(posX + width - 11 - this.sliderWidth, posY, 11, 6, this.editable ? upperSelected ? Qubble.CONFIG.getDarkAccentColor() : Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getTertiaryColor());
        gui.drawRectangle(posX + width - 11 - this.sliderWidth, posY + 6, 11, 6, this.editable ? lowerSelected ? Qubble.CONFIG.getDarkAccentColor() : Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getTertiaryColor());
        int textColor = Qubble.CONFIG.getTextColor();
        gui.drawRectangle(posX + width - 8 - this.sliderWidth, posY + 4, 5, 1, textColor);
        gui.drawRectangle(posX + width - 7 - this.sliderWidth, posY + 3, 3, 1, textColor);
        gui.drawRectangle(posX + width - 6 - this.sliderWidth, posY + 2, 1, 1, textColor);
        gui.drawRectangle(posX + width - 8 - this.sliderWidth, posY + 7, 5, 1, textColor);
        gui.drawRectangle(posX + width - 7 - this.sliderWidth, posY + 8, 3, 1, textColor);
        gui.drawRectangle(posX + width - 6 - this.sliderWidth, posY + 9, 1, 1, textColor); //who needs good code, amirite?
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        float scaleFactor = gui.getResolution().getScaleFactor();
        GL11.glScissor((int) (posX * scaleFactor), (int) ((gui.height - (posY + height)) * scaleFactor), (int) ((width - 11) * scaleFactor), (int) (height * scaleFactor));
        String text = String.valueOf(this.intValue ? (int) this.value : Float.parseFloat(this.decimalFormat.format(this.value)) + 0.0F);
        gui.mc.fontRendererObj.drawString(text, posX + 2, posY + 3.0F, textColor, false);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (this.hasSlider) {
            float offsetX = ((this.sliderWidth - 4) * (this.value - this.minValue) / (this.maxValue - this.minValue));
            boolean indicatorSelected = this.editable && selected && mouseX >= this.getPosX() + 38 + offsetX && mouseX <= this.getPosX() + 38 + offsetX + 4;
            this.getGUI().drawRectangle(this.getPosX() + 38 + offsetX, this.getPosY(), 4, this.getHeight(), this.editable ? indicatorSelected ? Qubble.CONFIG.getDarkAccentColor() : Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getTertiaryColor());
        }
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
        float offsetX = ((this.sliderWidth - 4) * (this.value - this.minValue) / (this.maxValue - this.minValue));
        boolean indicatorSelected = this.editable && this.isSelected(mouseX, mouseY) && mouseX >= this.getPosX() + 38 + offsetX && mouseX <= this.getPosX() + 38 + offsetX + 4;
        boolean upperSelected = this.isSelected(mouseX, mouseY) && mouseX >= this.getPosX() + this.getWidth() - this.sliderWidth - 11 && mouseY < this.getPosY() + 6 && mouseX < this.getPosX() + this.getWidth() - this.sliderWidth;
        boolean lowerSelected = this.isSelected(mouseX, mouseY) && mouseX >= this.getPosX() + this.getWidth() - this.sliderWidth - 11 && mouseY > this.getPosY() + 6 && mouseX < this.getPosX() + this.getWidth() - this.sliderWidth;
        if (upperSelected) {
            float newValue = this.intValue || GuiScreen.isShiftKeyDown() ? this.value + 10 : this.value + 0.1F;
            if (this.maxValue == -1.0F || newValue <= this.maxValue) {
                if (this.function.apply(newValue)) {
                    this.getGUI().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
                    this.value = newValue;
                    return true;
                }
            }
        } else if (lowerSelected) {
            float newValue = this.intValue || GuiScreen.isShiftKeyDown() ? this.value - 10 : this.value - 0.1F;
            if (this.minValue == -1.0F || newValue >= this.minValue) {
                if (this.function.apply(newValue)) {
                    this.getGUI().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
                    this.value = newValue;
                    return true;
                }
            }
        } else if (indicatorSelected) {
            this.dragging = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.dragging) {
            //do stuff, idk
        }
        return this.dragging;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.dragging = false;
        return false;
    }
}

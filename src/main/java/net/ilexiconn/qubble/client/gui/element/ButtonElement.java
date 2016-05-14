package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.color.ColorScheme;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class ButtonElement extends Element<QubbleGUI> {
    private String text;
    private ColorScheme colorScheme = ColorScheme.DEFAULT;
    private Function<ButtonElement, Boolean> function;
    private boolean enabled = true;

    public ButtonElement(QubbleGUI gui, String text, float posX, float posY, int width, int height, Function<ButtonElement, Boolean> function) {
        super(gui, posX, posY, width, height);
        this.text = text;
        this.function = function;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), this.enabled && this.isSelected(mouseX, mouseY) ? this.colorScheme.getSecondaryColor() : this.colorScheme.getPrimaryColor());
        FontRenderer fontRenderer = this.getGUI().mc.fontRendererObj;
        if (this.text.length() == 1) {
            fontRenderer.drawString(this.text, this.getPosX() + (this.getWidth() / 2) - (fontRenderer.getStringWidth(this.text) / 2) + 0.0625F, this.getPosY() + (this.getHeight() / 2) - (fontRenderer.FONT_HEIGHT / 2) + 0.0625F, Qubble.CONFIG.getTextColor(), false);
        } else {
            fontRenderer.drawString(this.text, this.getPosX() + (this.getWidth() / 2) - (fontRenderer.getStringWidth(this.text) / 2), this.getPosY() + (this.getHeight() / 2) - (fontRenderer.FONT_HEIGHT / 2), Qubble.CONFIG.getTextColor(), false);
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (this.enabled && this.isSelected(mouseX, mouseY)) {
            if (this.function.apply(this)) {
                this.getGUI().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
            }
            return true;
        } else {
            return false;
        }
    }

    public ButtonElement withColorScheme(ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
        return this;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            this.withColorScheme(ColorScheme.DISABLED);
        }
    }
}

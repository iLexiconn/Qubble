package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.color.ColorScheme;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ButtonElement extends Element<QubbleGUI> {
    private String text;
    private ColorScheme colorScheme = ColorScheme.DEFAULT;
    private IActionHandler<QubbleGUI, ButtonElement> actionHandler;

    public ButtonElement(QubbleGUI gui, String text, float posX, float posY, int width, int height, IActionHandler<QubbleGUI, ButtonElement> actionHandler) {
        super(gui, posX, posY, width, height);
        this.text = text;
        this.actionHandler = actionHandler;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        if (this.isSelected(mouseX, mouseY)) {
            this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), getHeight(), this.colorScheme.getSecondaryColor());
        } else {
            this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), getHeight(), this.colorScheme.getPrimaryColor());
        }
        FontRenderer fontRenderer = this.getGUI().mc.fontRendererObj;
        if (this.text.length() == 1) {
            fontRenderer.drawString(this.text, this.getPosX() + (this.getWidth() / 2) - (fontRenderer.getStringWidth(this.text) / 2) + 0.0625F, this.getPosY() + (this.getHeight() / 2) - (fontRenderer.FONT_HEIGHT / 2) + 0.0625F, Qubble.CONFIG.getTextColor(), false);
        } else {
            fontRenderer.drawString(this.text, this.getPosX() + (this.getWidth() / 2) - (fontRenderer.getStringWidth(this.text) / 2), this.getPosY() + (this.getHeight() / 2) - (fontRenderer.FONT_HEIGHT / 2), Qubble.CONFIG.getTextColor(), false);
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (this.isSelected(mouseX, mouseY)) {
            this.getGUI().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
            this.actionHandler.onAction(this.getGUI(), this);
            return true;
        } else {
            return false;
        }
    }

    private boolean isSelected(float mouseX, float mouseY) {
        return ElementHandler.INSTANCE.isOnTop(this.getGUI(), this, mouseX, mouseY) && mouseX >= this.getPosX() && mouseY >= this.getPosY() && mouseX <= this.getPosX() + this.getWidth() && mouseY <= this.getPosY() + this.getHeight();
    }

    public ButtonElement withColorScheme(ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
        return this;
    }

    public String getText() {
        return text;
    }
}

package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorElement extends Element<QubbleGUI> {
    private int horizontalRows;
    public String[] colorStrings = {"0xFEBA01", "0xFF8B00", "0xF7620E", "0xCA500F", "0xDA3B01", "0xEF6950", "0xD03438", "0xFF4244", "0xE64856", "0xE81123", "0xEA005F", "0xC40052", "0xE3008B", "0xBE0177", "0xC339B3", "0x9B008A", "0x0177D7", "0x0063B1", "0x928FD6", "0x6B69D6", "0x8764B8", "0x744DA8", "0xB046C2", "0x871797", "0x0099BB", "0x2D7C9A", "0x01B7C4", "0x038288", "0x00B294", "0x018675", "0x00CE70", "0x10883E", "0x7A7474", "0x5E5A57", "0x677689", "0x505C6A", "0x577C74", "0x496860", "0x4A8205", "0x107C0F", "0x767676", "0x4B4A48", "0x6A797E", "0x4C535B", "0x647C64", "0x535D54", "0x837544", "0x7E735F"};
    public int[] colors = {0xFFFEBA01, 0xFFFF8B00, 0xFFF7620E, 0xFFCA500F, 0xFFDA3B01, 0xFFEF6950, 0xFFD03438, 0xFFFF4244, 0xFFE64856, 0xFFE81123, 0xFFEA005F, 0xFFC40052, 0xFFE3008B, 0xFFBE0177, 0xFFC339B3, 0xFF9B008A, 0xFF0177D7, 0xFF0063B1, 0xFF928FD6, 0xFF6B69D6, 0xFF8764B8, 0xFF744DA8, 0xFFB046C2, 0xFF871797, 0xFF0099BB, 0xFF2D7C9A, 0xFF01B7C4, 0xFF038288, 0xFF00B294, 0xFF018675, 0xFF00CE70, 0xFF10883E, 0xFF7A7474, 0xFF5E5A57, 0xFF677689, 0xFF505C6A, 0xFF577C74, 0xFF496860, 0xFF4A8205, 0xFF107C0F, 0xFF767676, 0xFF4B4A48, 0xFF6A797E, 0xFF4C535B, 0xFF647C64, 0xFF535D54, 0xFF837544, 0xFF7E735F};
    private int selectedColor = -1;
    private IActionHandler<QubbleGUI, ColorElement> actionHandler;

    public ColorElement(QubbleGUI gui, float posX, float posY, int width, int height, IActionHandler<QubbleGUI, ColorElement> actionHandler) {
        super(gui, posX, posY, width, height);
        this.horizontalRows = (int) Math.ceil(width / 23);
        this.actionHandler = actionHandler;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().drawRectangle(this.getPosX() + 1, this.getPosY() + 1, this.getWidth() - 1, this.getHeight() - 1, Qubble.CONFIG.getSecondaryColor());
        float offsetX = this.getWidth() / 2 - (this.horizontalRows * 23 + 4) / 2;
        float offsetY = this.getHeight() / 2 - (this.colors.length / this.horizontalRows * 23 + 4) / 2;
        for (int i = 0; i < this.colors.length; i++) {
            int color = this.colors[i];
            int x = i % this.horizontalRows * 23 + 4;
            int y = i / this.horizontalRows * 23 + 4;
            this.getGUI().drawRectangle(this.getPosX() + x + offsetX, this.getPosY() + y + offsetY, 20, 20, color);
            if (color == Qubble.CONFIG.getAccentColor()) {
                if (selectedColor == -1) {
                    selectedColor = i;
                }
                this.getGUI().drawOutline(this.getPosX() + x + offsetX, this.getPosY() + y + offsetY, 20, 20, Qubble.CONFIG.getTextColor(), 2);
            }
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        float offsetX = this.getWidth() / 2 - (this.horizontalRows * 23 + 4) / 2;
        float offsetY = this.getHeight() / 2 - (this.colors.length / this.horizontalRows * 23 + 4) / 2;
        for (int i = 0; i < this.colors.length; i++) {
            float x = this.getPosX() + i % this.horizontalRows * 23 + 4 + offsetX;
            float y = this.getPosY() + i / this.horizontalRows * 23 + 4 + offsetY;
            if (button == 0 && mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 20) {
                this.selectedColor = i;
                this.getGUI().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
                this.actionHandler.onAction(this.getGUI(), this);
                return true;
            }
        }
        return false;
    }

    public String getColor() {
        return this.colorStrings[this.selectedColor];
    }
}

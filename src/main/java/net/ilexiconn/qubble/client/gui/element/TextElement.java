package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextElement extends Element<QubbleGUI> {
    private String text;

    public TextElement(QubbleGUI gui, String text, float posX, float posY) {
        super(gui, posX, posY, 0, 0);
        this.text = text;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().mc.fontRendererObj.drawString(this.text, this.getPosX(), this.getPosY(), Qubble.CONFIG.getTextColor(), false);
    }
}

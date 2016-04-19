package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorElement extends Element<QubbleGUI> {
    private int horizontalRows;

    public ColorElement(QubbleGUI gui, float posX, float posY, int width, int height) {
        super(gui, posX, posY, width, height);
        this.horizontalRows = (int) Math.ceil(width / 20);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().drawRectangle(this.getPosX() + 1, this.getPosY() + 1, this.getWidth() - 1, this.getHeight() - 1, Qubble.CONFIG.getSecondaryColor());

    }
}

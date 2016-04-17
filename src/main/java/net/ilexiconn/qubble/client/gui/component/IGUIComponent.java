package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGUIComponent {
    void render(QubbleGUI gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks);

    void renderAfter(QubbleGUI gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks);

    void mouseClicked(QubbleGUI gui, float mouseX, float mouseY, int button);

    void mouseDragged(QubbleGUI gui, float mouseX, float mouseY, int button, long timeSinceClick);

    void mouseReleased(QubbleGUI gui, float mouseX, float mouseY, int button);

    void keyPressed(QubbleGUI gui, char character, int key);
}

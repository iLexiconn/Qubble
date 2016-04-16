package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGUIComponent {
    void render(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks);

    void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button);

    void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick);

    void mouseReleased(QubbleGUI gui, int mouseX, int mouseY, int button);
}

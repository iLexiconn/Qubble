package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.renderer.GlStateManager;

public class ModelViewComponent implements IGUIComponent {
    @Override
    public void render(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        gui.drawOutline(0, 20, gui.width, gui.height - 20, QubbleGUI.getPrimaryColor(), 1);
        if (gui.getCurrentModel() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.popMatrix();
        }
        GlStateManager.enableTexture2D();
    }

    @Override
    public void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button) {

    }

    @Override
    public void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick) {

    }

    @Override
    public void mouseReleased(QubbleGUI gui, int mouseX, int mouseY, int button) {

    }
}

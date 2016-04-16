package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.model.QubbleCube;
import net.ilexiconn.qubble.server.model.QubbleModel;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class ModelTreeComponent extends Gui implements IGUIComponent {
    private int width = 100;
    private boolean rescaling;
    private int partY;

    @Override
    public void render(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        this.drawGradientRect(0, 21, this.width, gui.height, QubbleGUI.getPrimaryColor(), QubbleGUI.getSecondaryColor());
        gui.drawOutline(0, 21, this.width, gui.height - 21, Qubble.CONFIG.getAccentColor(), 1);
        if (gui.getCurrentModel() != null) {
            this.partY = 0;
            QubbleModel model = gui.getCurrentModel();
            for (QubbleCube cube : model.getCubes()) {
                this.drawCubeEntry(gui, cube, 0);
            }
        }
    }

    @Override
    public void renderAfter(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks) {

    }

    private void drawCubeEntry(QubbleGUI gui, QubbleCube cube, int xOffset) {
        xOffset++;
        for (QubbleCube child : cube.getChildren()) {
            this.drawCubeEntry(gui, child, xOffset);
        }
        this.partY++;
    }

    @Override
    public void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button) {
        if (mouseX > this.width - 4 && mouseX < this.width + 4 && mouseY > 21) {
            this.rescaling = true;
        }
    }

    @Override
    public void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick) {
        if (this.rescaling) {
            this.width = Math.max(50, Math.min(300, mouseX));
        }
    }

    @Override
    public void mouseReleased(QubbleGUI gui, int mouseX, int mouseY, int button) {
        this.rescaling = false;
    }
}

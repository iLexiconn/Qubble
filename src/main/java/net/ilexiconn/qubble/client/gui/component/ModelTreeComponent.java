package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.model.qubble.QubbleCube;
import net.ilexiconn.qubble.server.model.qubble.QubbleModel;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ModelTreeComponent extends Gui implements IGUIComponent {
    private int width = 100;
    private boolean rescaling;
    private int partY;

    private int scroll;
    private int scrollYOffset;
    private boolean scrolling;

    public ModelTreeComponent() {
    }

    @Override
    public void render(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        int height = gui.height - 22;

        gui.drawRectangle(0, 21, this.width, height, QubbleGUI.getSecondaryColor());

        float scrollPerEntry = (float) (this.partY) / (float) (height - 21);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution resolution = new ScaledResolution(ClientProxy.MINECRAFT);
        int scaleFactor = resolution.getScaleFactor();
        GL11.glScissor(0, 0, (this.width - 11) * scaleFactor, height * scaleFactor);
        if (gui.getCurrentModel() != null) {
            this.partY = 0;
            QubbleModel model = gui.getCurrentModel();
            for (QubbleCube cube : model.getCubes()) {
                this.drawCubeEntry(gui, cube, 0, scrollPerEntry);
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        gui.drawOutline(0, 21, this.width, height, Qubble.CONFIG.getAccentColor(), 1);

        float maxDisplayEntries = height / 13;
        float maxScroll = Math.max(0, this.partY - maxDisplayEntries);

        if (maxScroll > 0) {
            int scrollX = this.width - 9;
            int scrollY = this.scroll + 23;
            int scrollerHeight = (int) ((height - 21) / ((float) this.partY / maxDisplayEntries));
            gui.drawRectangle(scrollX, scrollY, 6, scrollerHeight, this.scrolling ? QubbleGUI.getPrimaryColor() : QubbleGUI.getSecondaryColor());
            gui.drawOutline(scrollX, scrollY, 6, scrollerHeight, Qubble.CONFIG.getAccentColor(), 1);
        }
    }

    @Override
    public void renderAfter(QubbleGUI gui, int mouseX, int mouseY, double offsetX, double offsetY, float partialTicks) {

    }

    private void drawCubeEntry(QubbleGUI gui, QubbleCube cube, int xOffset, float scrollPerEntry) {
        int y = (int) (-this.scroll * scrollPerEntry);
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        String name = cube.getName();
        fontRenderer.drawString(name, xOffset + 5, 25 + (this.partY + y) * 12, 0xFFFFFF);
        gui.drawOutline(xOffset + 3, 23 + (this.partY + y) * 12, this.width - xOffset - 14, 11, QubbleGUI.getPrimaryColor(), 1);
        this.partY++;
        for (QubbleCube child : cube.getChildren()) {
            this.drawCubeEntry(gui, child, xOffset + 3, scrollPerEntry);
        }
    }

    @Override
    public void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button) {
        if (mouseX > this.width - 4 && mouseX < this.width + 4 && mouseY > 21) {
            this.rescaling = true;
        }
        int height = gui.height - 21;
        float maxDisplayEntries = height / 13;
        float maxScroll = Math.max(0, this.partY - maxDisplayEntries);
        if (maxScroll > 0) {
            int scrollX = this.width - 9;
            int scrollY = this.scroll + 23;
            int scrollerHeight = (int) ((height - 21) / ((float) this.partY / maxDisplayEntries));
            if (mouseX >= scrollX && mouseX < scrollX + 6 && mouseY >= scrollY && mouseY < scrollY + scrollerHeight) {
                this.scrolling = true;
                this.scrollYOffset = mouseY - scrollY;
            }
        }
    }

    @Override
    public void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick) {
        if (this.rescaling) {
            this.width = Math.max(50, Math.min(300, mouseX));
        }
        if (this.scrolling) {
            int height = gui.height - 21;
            float maxDisplayEntries = height / 13;
            float maxScroll = Math.max(0, this.partY - maxDisplayEntries);
            float scrollPerEntry = (float) (this.partY) / (float) (height - 23);
            this.scroll = (int) Math.max(0, Math.min(maxScroll / scrollPerEntry, mouseY - 23 - this.scrollYOffset));
        }
    }

    @Override
    public void mouseReleased(QubbleGUI gui, int mouseX, int mouseY, int button) {
        this.rescaling = false;
        this.scrolling = false;
    }
}

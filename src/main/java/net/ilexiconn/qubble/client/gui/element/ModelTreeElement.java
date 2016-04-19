package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ModelTreeElement extends Element<QubbleGUI> {
    private boolean resizing;
    private int cubeY;
    private List<QubbleCube> expandedCubes = new ArrayList<>();

    private int scroll;
    private int scrollYOffset;
    private boolean scrolling;

    private int entryCount;

    public ModelTreeElement(QubbleGUI gui) {
        super(gui, 0.0F, 20.0F, 100.0F, gui.height - 20.0F);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        QubbleGUI gui = this.getGUI();
        float posX = this.getPosX();
        float posY = this.getPosY();
        float width = this.getWidth();
        float height = this.getHeight();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        float scaleFactor = gui.getResolution().getScaleFactor();
        GL11.glScissor((int) (posX * scaleFactor), (int) ((gui.height - (posY + height)) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
        gui.drawRectangle(posX, posY, width, 15.0F, Qubble.CONFIG.getPrimaryColor());

        float scrollPerEntry = (float) this.entryCount / (this.getHeight() - 17.0F);
        int i = 0;
        float offset = this.scroll * scrollPerEntry * 10;
        for (float y = 15.0F - offset; y < height + offset; y += 10.0F) {
            gui.drawRectangle(posX, posY + y, width, 10.0F, i % 2 == 0 ? Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getSecondaryColor()) : Qubble.CONFIG.getPrimaryColor());
            i++;
        }

        if (gui.getSelectedModel() != null) {
            this.cubeY = 0;
            QubbleModel model = gui.getSelectedModel();
            for (QubbleCube cube : model.getCubes()) {
                this.drawCubeEntry(cube, 0, scrollPerEntry);
            }
        }

        this.entryCount = this.cubeY;

        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;

        fontRenderer.drawString("Model Tree", posX + 4, posY + 4, Qubble.CONFIG.getTextColor(), false);

        gui.drawRectangle(posX + width - 2, posY, 2, height, Qubble.CONFIG.getAccentColor());

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        float maxDisplayEntries = height / 10.0F;
        float maxScroll = Math.max(0, this.entryCount - maxDisplayEntries);

        if (maxScroll > 0) {
            float scrollX = posX + width - 10;
            float scrollY = posY + this.scroll + 2;
            float scrollerHeight = (this.getHeight() - 2) / (this.entryCount / maxDisplayEntries) - 2;
            this.getGUI().drawRectangle(scrollX, scrollY, 6, scrollerHeight, this.scrolling ? Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getSecondaryColor());
        } else {
            this.getGUI().drawRectangle(this.getPosX() + this.getWidth() - 10, this.getPosY() + 2, 6, this.getHeight() - 4, Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getPrimaryColor()));
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (mouseX > this.getWidth() - 2 && mouseX < this.getWidth() && mouseY > this.getPosY()) {
            this.resizing = true;
            return true;
        }
        QubbleGUI gui = this.getGUI();
        if (button == 0) {
            float scrollPerEntry = (float) this.entryCount / (this.getHeight() - 17.0F);
            if (gui.getSelectedModel() != null) {
                this.cubeY = 0;
                if (mouseX >= this.getPosX() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY >= this.getPosY() && mouseY < this.getPosY() + this.getHeight()) {
                    gui.setSelectedCube(null);
                }
                QubbleModel model = gui.getSelectedModel();
                for (QubbleCube cube : model.getCubes()) {
                    if (this.mouseDetectionCubeEntry(cube, 0, mouseX, mouseY, scrollPerEntry)) {
                        return true;
                    }
                }
            }
            float maxDisplayEntries = this.getHeight() / 10.0F;
            float maxScroll = Math.max(0, this.entryCount - maxDisplayEntries);
            if (maxScroll > 0) {
                float scrollX = this.getPosX() + this.getWidth() - 10;
                float scrollY = this.getPosY() + this.scroll + 2;
                float scrollerHeight = (this.getHeight() - 2) / (this.entryCount / maxDisplayEntries) - 2;
                if (mouseX >= scrollX && mouseX < scrollX + 6 && mouseY >= scrollY + 1 && mouseY < scrollY + scrollerHeight) {
                    this.scrolling = true;
                    this.scrollYOffset = (int) (mouseY - scrollY);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.resizing) {
            this.setWidth(Math.max(50.0F, Math.min(300.0F, mouseX - this.getPosX())));
            return true;
        } else if (this.scrolling) {
            float maxDisplayEntries = (int) (this.getHeight() / 10.0F);
            float maxScroll = Math.max(0, this.entryCount - maxDisplayEntries);
            float scrollPerEntry = (float) this.entryCount / (this.getHeight() - 17.0F);
//            float maxDisplayEntries = this.getHeight() / 10.0F;
//            float maxScroll = Math.max(0, this.entryCount - maxDisplayEntries);
//            float scrollPerEntry = (this.getHeight() - 15.0F) / (float) this.entryCount;
            this.scroll = (int) Math.max(0, Math.min(maxScroll / scrollPerEntry, mouseY - this.getPosY() - 2 - this.scrollYOffset));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.resizing = false;
        this.scrolling = false;
        return false;
    }

    private boolean mouseDetectionCubeEntry(QubbleCube cube, int xOffset, float mouseX, float mouseY, float scrollPerEntry) {
        float entryX = this.getPosX() + xOffset;
        float entryY = (this.getPosY() + (this.cubeY * 10.0F) + 16.0F) - (this.scroll * scrollPerEntry * 10);
        this.cubeY++;
        boolean expanded = this.isExpanded(cube);
        if (expanded) {
            for (QubbleCube child : cube.getChildren()) {
                this.mouseDetectionCubeEntry(child, xOffset + 3, mouseX, mouseY, scrollPerEntry);
            }
        }
        if (cube.getChildren().size() > 0) {
            if (mouseX >= entryX + 2 && mouseX < entryX + 6 && mouseY >= entryY + 2 && mouseY < entryY + 6) {
                this.setExpanded(cube, !this.isExpanded(cube));
                return true;
            }
        }
        if (mouseX >= entryX + 10 && mouseX < entryX - xOffset + this.getWidth() - 10 && mouseY >= entryY && mouseY < entryY + 10) {
            this.getGUI().setSelectedCube(cube);
            return true;
        }
        return false;
    }

    private void drawCubeEntry(QubbleCube cube, int xOffset, float scrollPerEntry) {
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        String name = cube.getName();
        float entryX = this.getPosX() + xOffset;
        float entryY = (this.getPosY() + (this.cubeY * 10.0F) + 16.0F) - (this.scroll * scrollPerEntry * 10);
        fontRenderer.drawString(name, entryX + 10, entryY, this.getGUI().getSelectedCube() == cube ? Qubble.CONFIG.getAccentColor() : Qubble.CONFIG.getTextColor(), false);
        this.cubeY++;
        boolean expanded = this.isExpanded(cube);
        if (expanded) {
            for (QubbleCube child : cube.getChildren()) {
                this.drawCubeEntry(child, xOffset + 3, scrollPerEntry);
            }
        }
        if (cube.getChildren().size() > 0) {
            int outlineColor = Qubble.CONFIG.getDarkerColor(0xFFACACAC);
            this.getGUI().drawRectangle(entryX + 2, entryY + 2, 4, 4, 0xFF464646);
            this.getGUI().drawRectangle(entryX + 3, entryY + 3.5, 2, 0.75, outlineColor);
            if (!expanded) {
                this.getGUI().drawRectangle(entryX + 3.75, entryY + 3, 0.75, 2, outlineColor);
            }
        }
    }

    private boolean isExpanded(QubbleCube cube) {
        return this.expandedCubes.contains(cube);
    }

    private void setExpanded(QubbleCube cube, boolean expanded) {
        boolean carryToChildren = GuiScreen.isShiftKeyDown();
        if (expanded) {
            if (!this.expandedCubes.contains(cube)) {
                this.expandedCubes.add(cube);
            }
        } else {
            this.expandedCubes.remove(cube);
            carryToChildren = true;
        }
        if (carryToChildren) {
            for (QubbleCube child : cube.getChildren()) {
                this.setExpanded(child, expanded);
            }
        }
    }
}

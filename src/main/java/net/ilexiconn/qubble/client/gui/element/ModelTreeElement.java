package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.ScrollbarElement;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.GUIHelper;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.ilexiconn.qubble.client.project.Project;
import net.ilexiconn.qubble.client.project.action.AutoUVCuboidAction;
import net.ilexiconn.qubble.client.project.action.AutoUVModelAction;
import net.ilexiconn.qubble.client.project.action.CreateCuboidAction;
import net.ilexiconn.qubble.client.project.action.RemoveCuboidAction;
import net.ilexiconn.qubble.client.project.action.ReparentCuboidAction;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ModelTreeElement extends Element<QubbleGUI> implements ModelViewAdapter {
    private boolean resizing;
    private int cubeY;
    private int entryCount;
    private List<CuboidWrapper> expandedCuboids = new ArrayList<>();

    private ScrollbarElement<QubbleGUI> scroller;

    private CuboidWrapper parenting;

    public ModelTreeElement(QubbleGUI gui) {
        super(gui, 0.0F, 20.0F, 100, gui.height - 35);
    }

    @Override
    public void init() {
        this.gui.addElement(this.scroller = new ScrollbarElement<>(this, () -> this.getWidth() - 8.0F, () -> 2.0F, () -> (float) this.getHeight(), 12, () -> this.entryCount));
        this.gui.addElement(new ButtonElement<>(this.gui, "+", this.getPosX(), this.getPosY() + this.getHeight(), 16, 16, (button) -> {
            ModelWrapper model = this.gui.getSelectedProject().getModel();
            String name = ModelHandler.INSTANCE.getCopyName(model, "Cuboid");
            this.gui.perform(new CreateCuboidAction(this.gui, name));
            return true;
        }).withColorScheme(ColorSchemes.DEFAULT));
        this.gui.addElement(new ButtonElement<>(this.gui, "-", this.getPosX() + 16, this.getPosY() + this.getHeight(), 16, 16, (button) -> {
            Project project = this.gui.getSelectedProject();
            if (project != null && project.getSelectedCuboid() != null) {
                this.gui.perform(new RemoveCuboidAction(this.gui, project.getSelectedCuboid().getName()));
            }
            return true;
        }).withColorScheme(ColorSchemes.DEFAULT));
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        float posX = this.getPosX();
        float posY = this.getPosY();
        float width = this.getWidth();
        float height = this.getHeight();
        this.startScissor();
        int i = 0;
        float offset = this.scroller.getScrollOffset();
        for (float y = -offset; y < height + offset; y += 12.0F) {
            this.drawRectangle(posX, posY + y, width, 12.0F, i % 2 == 0 ? LLibrary.CONFIG.getSecondarySubcolor() : LLibrary.CONFIG.getPrimarySubcolor());
            i++;
        }
        this.cubeY = 0;
        if (this.gui.getSelectedProject() != null) {
            ModelWrapper<? extends CuboidWrapper> model = this.gui.getSelectedProject().getModel();
            for (CuboidWrapper cube : model.getCuboids()) {
                this.drawCubeEntry(cube, 0);
            }
        }
        this.entryCount = this.cubeY;
        this.drawRectangle(posX + width - 2, posY, 2, height, LLibrary.CONFIG.getAccentColor());
        this.endScissor();
        if (this.parenting != null) {
            FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
            String name = this.parenting.getName();
            float entryX = mouseX - 12;
            float entryY = mouseY - 2;
            this.drawRectangle(entryX + 9, entryY - 1, fontRenderer.getStringWidth(name) + 1, fontRenderer.FONT_HEIGHT + 1, LLibrary.CONFIG.getSecondaryColor());
            fontRenderer.drawString(name, entryX + 10, entryY, LLibrary.CONFIG.getAccentColor(), false);
        }
        this.drawRectangle(posX, posY + height, this.getWidth(), 16, LLibrary.CONFIG.getAccentColor());
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (mouseX > this.getWidth() - 2 && mouseX < this.getWidth() && mouseY > this.getPosY()) {
            this.resizing = true;
            return true;
        }
        if (button == 0) {
            if (this.getSelectedCube(mouseX, mouseY) != null) {
                return true;
            }
        }
        return false;
    }

    private CuboidWrapper getSelectedCube(float mouseX, float mouseY) {
        if (this.gui.getSelectedProject() != null) {
            this.cubeY = 0;
            if (mouseX >= this.getPosX() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY >= this.getPosY() && mouseY < this.getPosY() + this.getHeight()) {
                this.gui.getSelectedProject().setSelectedCuboid(null);
            }
            ModelWrapper<? extends CuboidWrapper> model = this.gui.getSelectedProject().getModel();
            for (CuboidWrapper cube : model.getCuboids()) {
                CuboidWrapper selected = this.mouseDetectionCubeEntry(cube, 0, mouseX, mouseY);
                if (selected != null) {
                    return selected;
                }
            }
        }
        return null;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.resizing) {
            this.setWidth((int) Math.max(50, Math.min(300, mouseX - this.getPosX())));
            return true;
        } else if (button == 0 && this.isSelected(mouseX, mouseY)) {
            if (this.gui.getSelectedProject() != null && this.parenting == null && this.gui.getSelectedProject().getModel().supportsParenting()) {
                if (this.gui.getSelectedProject() != null && this.gui.getSelectedProject().getSelectedCuboid() != null) {
                    this.parenting = this.gui.getSelectedProject().getSelectedCuboid();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        this.resizing = false;
        if (this.parenting != null) {
            Project project = this.gui.getSelectedProject();
            if (project != null && project.getModel() != null) {
                CuboidWrapper newParent = this.getSelectedCube(mouseX, mouseY);
                this.gui.perform(new ReparentCuboidAction(this.gui, this.parenting, newParent, GuiScreen.isShiftKeyDown()));
            }
            this.parenting = null;
        }
        return false;
    }

    @Override
    public boolean isSelected(float mouseX, float mouseY) {
        return super.isSelected(mouseX, mouseY);
    }

    private CuboidWrapper mouseDetectionCubeEntry(CuboidWrapper<?> cube, int xOffset, float mouseX, float mouseY) {
        float entryX = this.getPosX() + xOffset;
        float entryY = this.getPosY() + this.cubeY * 12.0F + 2.0F - this.scroller.getScrollOffset();
        this.cubeY++;
        boolean expanded = this.isExpanded(cube);
        if (expanded) {
            for (CuboidWrapper child : cube.getChildren()) {
                CuboidWrapper selected = this.mouseDetectionCubeEntry(child, xOffset + 6, mouseX, mouseY);
                if (selected != null) {
                    return selected;
                }
            }
        }
        if (cube.getChildren().size() > 0) {
            if (mouseX >= entryX + 2 && mouseX < entryX + 6 && mouseY >= entryY + 2 && mouseY < entryY + 6) {
                this.setExpanded(cube, !this.isExpanded(cube));
            }
        }
        if (mouseX >= entryX + 10 && mouseX < entryX - xOffset + this.getWidth() - 10 && mouseY >= entryY && mouseY < entryY + 10) {
            this.gui.getSelectedProject().setSelectedCuboid(cube);
            return cube;
        }
        return null;
    }

    private void drawCubeEntry(CuboidWrapper<?> cuboid, int xOffset) {
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        String name = cuboid.getName();
        float entryX = this.getPosX() + xOffset;
        float entryY = this.getPosY() + this.cubeY * 12.0F + 2.0F - this.scroller.getScrollOffset();
        if (!cuboid.equals(this.parenting)) {
            fontRenderer.drawString(name, entryX + 10, entryY, this.gui.getSelectedProject().getSelectedCuboid() == cuboid ? LLibrary.CONFIG.getAccentColor() : LLibrary.CONFIG.getTextColor(), false);
        }
        this.cubeY++;
        boolean expanded = this.isExpanded(cuboid);
        int prevCubeY = this.cubeY;
        int size = 0;
        if (expanded) {
            int i = 0;
            for (CuboidWrapper child : cuboid.getChildren()) {
                if (child != null) {
                    if (i == cuboid.getChildren().size() - 1) {
                        size = (this.cubeY + 1) - prevCubeY;
                    }
                    this.drawCubeEntry(child, xOffset + 6);
                }
                i++;
            }
        }
        int outlineColor = 0xFF9E9E9E;
        this.drawRectangle(entryX - 5, entryY + 3.5, 11, 0.75, outlineColor);
        if (cuboid.getChildren().size() > 0) {
            if (expanded) {
                this.drawRectangle(entryX + 1, entryY + 3.5, 0.75, size * 12.0F, outlineColor);
            }
            this.drawRectangle(entryX + 2, entryY + 2, 4, 4, 0xFF464646);
            this.drawRectangle(entryX + 3, entryY + 3.5, 2, 0.75, outlineColor);
            if (!expanded) {
                this.drawRectangle(entryX + 3.75, entryY + 3, 0.75, 2, outlineColor);
            }
        }
    }

    private boolean isExpanded(CuboidWrapper cuboid) {
        return this.expandedCuboids.contains(cuboid);
    }

    private void setExpanded(CuboidWrapper<?> cuboid, boolean expanded) {
        boolean carryToChildren = GuiScreen.isShiftKeyDown();
        if (expanded) {
            if (!this.expandedCuboids.contains(cuboid)) {
                this.expandedCuboids.add(cuboid);
            }
        } else {
            this.expandedCuboids.remove(cuboid);
            carryToChildren = true;
        }
        if (carryToChildren) {
            for (CuboidWrapper child : cuboid.getChildren()) {
                this.setExpanded(child, expanded);
            }
        }
    }

    @Override
    public boolean keyPressed(char character, int key) {
        Project project = this.gui.getSelectedProject();
        if (project != null) {
            CuboidWrapper selectedCuboid = project.getSelectedCuboid();
            if (GuiScreen.isCtrlKeyDown() && key == Keyboard.KEY_F) {
                if (GuiScreen.isShiftKeyDown()) {
                    GUIHelper.INSTANCE.confirmation(this.gui, "Are you sure you'd like to set all cuboid UVs to their defaults?", 150, accepted -> {
                        if (accepted) {
                            this.gui.perform(new AutoUVModelAction(this.gui));
                        }
                    });
                } else if (selectedCuboid != null) {
                    this.gui.perform(new AutoUVCuboidAction(this.gui, selectedCuboid));
                }
            }
        }
        return false;
    }

    public boolean isParenting() {
        return this.parenting != null;
    }

    @Override
    public float getOffsetX() {
        return this.gui.getModelTree().getWidth() - 100;
    }

    @Override
    public float getOffsetY() {
        return 0;
    }

    @Override
    public float getOffsetZ() {
        return 0;
    }

    @Override
    public boolean shouldHighlightHovered() {
        return false;
    }
}

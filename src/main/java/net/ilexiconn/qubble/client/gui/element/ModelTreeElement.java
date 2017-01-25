package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.ScrollbarElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.ilexiconn.qubble.server.model.ModelHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ModelTreeElement<CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> extends Element<QubbleGUI> {
    private Project<CBE, MDL> project;
    private boolean resizing;
    private int cubeY;
    private int entryCount;
    private List<CBE> expandedCubes = new ArrayList<>();

    private ScrollbarElement<QubbleGUI> scroller;

    private CBE parenting;

    public ModelTreeElement(QubbleGUI gui, Project<CBE, MDL> project) {
        super(gui, 0.0F, 20.0F, 100, gui.height - 36);
        this.project = project;
    }

    @Override
    public void init() {
        this.gui.addElement(this.scroller = new ScrollbarElement<>(this, () -> this.getWidth() - 8.0F, () -> 2.0F, () -> (float) this.getHeight(), 12, () -> this.entryCount));
        this.gui.addElement(new ButtonElement<>(this.gui, "+", this.getPosX(), this.getPosY() + this.getHeight(), 16, 16, (button) -> {
            WindowElement<QubbleGUI> createCubeWindow = new WindowElement<>(this.gui, "Create Cube", 100, 42);
            InputElement<QubbleGUI> nameElement = new InputElement<>(this.gui, 2, 16, 96, "Cube Name", (i) -> {
            });
            createCubeWindow.addElement(nameElement);
            createCubeWindow.addElement(new ButtonElement<>(this.gui, "Create", 2, 30, 96, 10, (element) -> this.createCube(createCubeWindow, nameElement)).withColorScheme(ColorSchemes.WINDOW));
            this.gui.addElement(createCubeWindow);
            return true;
        }).withColorScheme(ColorSchemes.DEFAULT));
        this.gui.addElement(new ButtonElement<>(this.gui, "-", this.getPosX() + 16, this.getPosY() + this.getHeight(), 16, 16, (button) -> {
            if (this.project != null && this.project.getModel() != null && this.project.getSelectedCuboid() != null) {
                this.removeSelectedCube();
            }
            return true;
        }).withColorScheme(ColorSchemes.DEFAULT));
    }

    private boolean createCube(WindowElement<QubbleGUI> window, InputElement<QubbleGUI> input) {
        String name = input.getText().trim();
        if (this.project != null && this.project.getModel() != null && name.length() > 0) {
            MDL model = this.project.getModel();
            this.gui.removeElement(window);
            if (!ModelHandler.INSTANCE.hasDuplicateName(model, name)) {
                CBE cuboid = model.createCuboid(name);
                this.project.setSaved(false);
                this.project.setSelectedCube(cuboid);
            } else {
                WindowElement<QubbleGUI> renameWindow = new WindowElement<>(this.gui, "Duplicate Name!", 100, 42);
                InputElement<QubbleGUI> nameElement = new InputElement<>(this.gui, 2, 16, 96, "Cube Name", (i) -> {
                });
                renameWindow.addElement(nameElement);
                renameWindow.addElement(new ButtonElement<>(this.gui, "Create", 2, 30, 96, 10, (element) -> this.createCube(renameWindow, nameElement)).withColorScheme(ColorSchemes.WINDOW));
                this.gui.addElement(renameWindow);
            }
            return true;
        }
        return false;
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
        if (this.project != null) {
            MDL model = this.project.getModel();
            for (CBE cube : model.getCuboids()) {
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

    private CBE getSelectedCube(float mouseX, float mouseY) {
        if (this.project != null) {
            this.cubeY = 0;
            if (mouseX >= this.getPosX() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY >= this.getPosY() && mouseY < this.getPosY() + this.getHeight()) {
                this.project.setSelectedCube(null);
            }
            MDL model = this.project.getModel();
            for (CBE cube : model.getCuboids()) {
                CBE selected = this.mouseDetectionCubeEntry(cube, 0, mouseX, mouseY);
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
            if (this.project != null && this.parenting == null && this.project.getModel().supportsParenting()) {
                if (this.project != null && this.project.getSelectedCuboid() != null) {
                    this.parenting = this.project.getSelectedCuboid();
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
            if (this.project != null && this.project.getModel() != null) {
                MDL model = this.project.getModel();
                if (model.supportsParenting()) {
                    if (model.reparent(this.parenting, this.getSelectedCube(mouseX, mouseY), GuiScreen.isShiftKeyDown())) {
                        this.project.setSaved(false);
                    }
                }
            }
            this.parenting = null;
        }
        return false;
    }

    private CBE mouseDetectionCubeEntry(CBE cube, int xOffset, float mouseX, float mouseY) {
        float entryX = this.getPosX() + xOffset;
        float entryY = this.getPosY() + this.cubeY * 12.0F + 2.0F - this.scroller.getScrollOffset();
        this.cubeY++;
        boolean expanded = this.isExpanded(cube);
        if (expanded) {
            for (CBE child : cube.getChildren()) {
                CBE selected = this.mouseDetectionCubeEntry(child, xOffset + 6, mouseX, mouseY);
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
            this.project.setSelectedCube(cube);
            return cube;
        }
        return null;
    }

    private void drawCubeEntry(CBE cube, int xOffset) {
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        String name = cube.getName();
        float entryX = this.getPosX() + xOffset;
        float entryY = this.getPosY() + this.cubeY * 12.0F + 2.0F - this.scroller.getScrollOffset();
        if (!cube.equals(this.parenting)) {
            fontRenderer.drawString(name, entryX + 10, entryY, this.project.getSelectedCuboid() == cube ? LLibrary.CONFIG.getAccentColor() : LLibrary.CONFIG.getTextColor(), false);
        }
        this.cubeY++;
        boolean expanded = this.isExpanded(cube);
        int prevCubeY = this.cubeY;
        int size = 0;
        if (expanded) {
            int i = 0;
            for (CBE child : cube.getChildren()) {
                if (i == cube.getChildren().size() - 1) {
                    size = (this.cubeY + 1) - prevCubeY;
                }
                this.drawCubeEntry(child, xOffset + 6);
                i++;
            }
        }
        int outlineColor = 0xFF9E9E9E;
        this.drawRectangle(entryX - 5, entryY + 3.5, 11, 0.75, outlineColor);
        if (cube.getChildren().size() > 0) {
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

    private boolean isExpanded(CBE cube) {
        return this.expandedCubes.contains(cube);
    }

    private void setExpanded(CBE cube, boolean expanded) {
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
            for (CBE child : cube.getChildren()) {
                this.setExpanded(child, expanded);
            }
        }
    }

    @Override
    public boolean keyPressed(char character, int key) {
        if (this.project != null && this.project.getSelectedCuboid() != null) {
            if (key == Keyboard.KEY_DELETE || key == Keyboard.KEY_BACK) {
                this.removeSelectedCube();
                return true;
            } else if (GuiScreen.isKeyComboCtrlC(key)) {
                this.project.duplicateCube();
                return true;
            }
        }
        return false;
    }

    private void removeSelectedCube() {
        CBE selectedCube = this.project.getSelectedCuboid();
        MDL model = this.project.getModel();
        for (CBE currentCube : model.getCuboids()) {
            if (this.removeChildCube(currentCube, selectedCube)) {
                break;
            }
        }
        model.deleteCuboid(selectedCube);
        this.project.setSelectedCube(null);
        this.gui.getSidebar().disable();
        this.project.setSaved(false);
    }

    private boolean removeChildCube(CBE parent, CBE cube) {
        boolean isChild = false;
        for (CBE currentCube : parent.getChildren()) {
            if (currentCube.equals(cube)) {
                isChild = true;
                break;
            }
            if (this.removeChildCube(currentCube, cube)) {
                return true;
            }
        }
        if (isChild) {
            parent.removeChild(cube);
            return true;
        }
        return false;
    }

    public boolean isParenting() {
        return this.parenting != null;
    }
}

package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.*;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.server.model.ModelHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelTreeElement extends Element<QubbleGUI> {
    private boolean resizing;
    private int cubeY;
    private int entryCount;
    private List<QubbleCuboid> expandedCubes = new ArrayList<>();

    private ScrollbarElement<QubbleGUI> scroller;

    private QubbleCuboid parenting;

    public ModelTreeElement(QubbleGUI gui) {
        super(gui, 0.0F, 20.0F, 100, gui.height - 36);
    }

    @Override
    public void init() {
        this.gui.addElement(this.scroller = new ScrollbarElement<>(this, () -> this.getWidth() - 8.0F, () -> 2.0F, () -> (float) this.getHeight(), 12, () -> this.entryCount));
        this.gui.addElement(new ButtonElement<>(this.gui, "+", this.getPosX(), this.getPosY() + this.getHeight(), 16, 16, (button) -> {
            WindowElement<QubbleGUI> createCubeWindow = new WindowElement<>(this.gui, "Create Cube", 100, 42);
            InputElement<QubbleGUI> nameElement = new InputElement<>(this.gui, 2, 16, 96, "Cube Name", (i) -> {});
            createCubeWindow.addElement(nameElement);
            createCubeWindow.addElement(new ButtonElement<>(this.gui, "Create", 2, 30, 96, 10, (element) -> this.createCube(createCubeWindow, nameElement)).withColorScheme(ColorSchemes.WINDOW));
            this.gui.addElement(createCubeWindow);
            return true;
        }).withColorScheme(ColorSchemes.DEFAULT));
        this.gui.addElement(new ButtonElement<>(this.gui, "-", this.getPosX() + 16, this.getPosY() + this.getHeight(), 16, 16, (button) -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getModel() != null && selectedProject.getSelectedCube() != null) {
                this.removeCube(selectedProject);
            }
            return true;
        }).withColorScheme(ColorSchemes.DEFAULT));
    }

    private boolean createCube(WindowElement<QubbleGUI> window, InputElement<QubbleGUI> input) {
        Project selectedProject = this.gui.getSelectedProject();
        String name = input.getText().trim();
        if (selectedProject != null && selectedProject.getModel() != null && name.length() > 0) {
            QubbleModel model = selectedProject.getModel();
            this.gui.removeElement(window);
            if (!ModelHandler.INSTANCE.hasDuplicateName(model, name)) {
                QubbleCuboid cube = QubbleCuboid.create(name);
                cube.setDimensions(1, 1, 1);
                cube.setScale(1.0F, 1.0F, 1.0F);
                model.getCuboids().add(cube);
                selectedProject.setSelectedCube(cube);
                this.gui.getModelView().updateModel();
            } else {
                WindowElement<QubbleGUI> renameWindow = new WindowElement<>(this.gui, "Duplicate Name!", 100, 42);
                InputElement<QubbleGUI> nameElement = new InputElement<>(this.gui, 2, 16, 96, "Cube Name", (i) -> {});
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
        if (this.gui.getSelectedProject() != null) {
            QubbleModel model = this.gui.getSelectedProject().getModel();
            for (QubbleCuboid cube : model.getCuboids()) {
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

    private QubbleCuboid getSelectedCube(float mouseX, float mouseY) {
        QubbleGUI gui = this.gui;
        if (gui.getSelectedProject() != null) {
            this.cubeY = 0;
            if (mouseX >= this.getPosX() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY >= this.getPosY() && mouseY < this.getPosY() + this.getHeight()) {
                gui.getSelectedProject().setSelectedCube(null);
            }
            QubbleModel model = gui.getSelectedProject().getModel();
            for (QubbleCuboid cube : model.getCuboids()) {
                QubbleCuboid selected = this.mouseDetectionCubeEntry(cube, 0, mouseX, mouseY);
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
            if (this.parenting == null) {
                Project selectedProject = this.gui.getSelectedProject();
                if (this.gui.getSelectedProject() != null && selectedProject.getSelectedCube() != null) {
                    this.parenting = selectedProject.getSelectedCube();
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
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getModel() != null) {
                QubbleModel model = selectedProject.getModel();
                QubbleCuboid newParent = this.getSelectedCube(mouseX, mouseY);
                QubbleCuboid prevParent = this.getParent(model, this.parenting);
                if (!this.hasChild(this.parenting, newParent)) {
                    if (newParent != this.parenting) {
                        if (GuiScreen.isShiftKeyDown()) {
                            this.maintainParentTransformation(model, this.parenting);
                            if (newParent != null) {
                                this.inheritParentTransformation(model, this.parenting, newParent);
                            }
                        }
                        model.getCuboids().remove(this.parenting);
                        if (newParent != this.parenting && newParent != null && newParent != prevParent) {
                            if (!newParent.getChildren().contains(this.parenting)) {
                                newParent.getChildren().add(this.parenting);
                            }
                        } else if (newParent == null) {
                            model.getCuboids().add(this.parenting);
                        }
                        if (prevParent != null && newParent != prevParent) {
                            prevParent.getChildren().remove(this.parenting);
                        }
                        this.gui.getModelView().updateModel();
                        selectedProject.setSaved(false);
                    }
                }
            }
            this.parenting = null;
        }
        return false;
    }

    private void maintainParentTransformation(QubbleModel model, QubbleCuboid parenting) {
        this.applyTransformation(parenting, this.getParentTransformation(model, parenting, true, false));
    }

    private void inheritParentTransformation(QubbleModel model, QubbleCuboid parenting, QubbleCuboid newParent) {
        Matrix4d matrix = this.getParentTransformationMatrix(model, newParent, true, false);
        matrix.invert();
        matrix.mul(this.getParentTransformationMatrix(model, parenting, false, false));
        float[][] parentTransformation = this.getParentTransformation(matrix);
        this.applyTransformation(parenting, parentTransformation);
    }

    private void applyTransformation(QubbleCuboid parenting, float[][] parentTransformation) {
        parenting.setPosition(parentTransformation[0][0], parentTransformation[0][1], parentTransformation[0][2]);
        parenting.setRotation(parentTransformation[1][0], parentTransformation[1][1], parentTransformation[1][2]);
    }

    private QubbleCuboid mouseDetectionCubeEntry(QubbleCuboid cube, int xOffset, float mouseX, float mouseY) {
        float entryX = this.getPosX() + xOffset;
        float entryY = this.getPosY() + this.cubeY * 12.0F + 2.0F - this.scroller.getScrollOffset();
        this.cubeY++;
        boolean expanded = this.isExpanded(cube);
        if (expanded) {
            for (QubbleCuboid child : cube.getChildren()) {
                QubbleCuboid selected = this.mouseDetectionCubeEntry(child, xOffset + 6, mouseX, mouseY);
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
            this.gui.getSelectedProject().setSelectedCube(cube);
            return cube;
        }
        return null;
    }

    private void drawCubeEntry(QubbleCuboid cube, int xOffset) {
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        String name = cube.getName();
        float entryX = this.getPosX() + xOffset;
        float entryY = this.getPosY() + this.cubeY * 12.0F + 2.0F - this.scroller.getScrollOffset();
        if (!cube.equals(this.parenting)) {
            fontRenderer.drawString(name, entryX + 10, entryY, this.gui.getSelectedProject().getSelectedCube() == cube ? LLibrary.CONFIG.getAccentColor() : LLibrary.CONFIG.getTextColor(), false);
        }
        this.cubeY++;
        boolean expanded = this.isExpanded(cube);
        int prevCubeY = this.cubeY;
        int size = 0;
        if (expanded) {
            int i = 0;
            for (QubbleCuboid child : cube.getChildren()) {
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

    private boolean isExpanded(QubbleCuboid cube) {
        return this.expandedCubes.contains(cube);
    }

    private void setExpanded(QubbleCuboid cube, boolean expanded) {
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
            for (QubbleCuboid child : cube.getChildren()) {
                this.setExpanded(child, expanded);
            }
        }
    }

    @Override
    public boolean keyPressed(char character, int key) {
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null && selectedProject.getSelectedCube() != null) {
            if (key == Keyboard.KEY_DELETE || key == Keyboard.KEY_BACK) {
                this.removeCube(selectedProject);
                return true;
            } else if (GuiScreen.isKeyComboCtrlC(key)) {
                QubbleCuboid cuboid = selectedProject.getSelectedCube();
                QubbleModel model = selectedProject.getModel();
                model.getCuboids().add(ModelHandler.INSTANCE.copy(model, cuboid));
                this.gui.getModelView().updateModel();
                selectedProject.setSaved(false);
                return true;
            }
        }
        return false;
    }

    private void removeCube(Project selectedProject) {
        QubbleCuboid selectedCube = selectedProject.getSelectedCube();
        for (QubbleCuboid currentCube : selectedProject.getModel().getCuboids()) {
            if (this.removeChildCube(currentCube, selectedCube)) {
                break;
            }
        }
        selectedProject.getModel().getCuboids().remove(selectedCube);
        selectedProject.setSelectedCube(null);
        this.gui.getModelView().updateModel();
        this.gui.getSidebar().disable();
        selectedProject.setSaved(false);
    }

    private boolean removeChildCube(QubbleCuboid parent, QubbleCuboid cube) {
        boolean isChild = false;
        for (QubbleCuboid currentCube : parent.getChildren()) {
            if (currentCube.equals(cube)) {
                isChild = true;
                break;
            }
            if (this.removeChildCube(currentCube, cube)) {
                return true;
            }
        }
        if (isChild) {
            parent.getChildren().remove(cube);
            return true;
        }
        return false;
    }

    public QubbleCuboid getParent(QubbleModel model, QubbleCuboid cuboid) {
        for (QubbleCuboid currentCube : model.getCuboids()) {
            QubbleCuboid foundParent = this.getParent(currentCube, cuboid);
            if (foundParent != null) {
                return foundParent;
            }
        }
        return null;
    }

    private QubbleCuboid getParent(QubbleCuboid parent, QubbleCuboid cuboid) {
        if (parent.getChildren().contains(cuboid)) {
            return parent;
        }
        for (QubbleCuboid child : parent.getChildren()) {
            QubbleCuboid foundParent = this.getParent(child, cuboid);
            if (foundParent != null) {
                return foundParent;
            }
        }
        return null;
    }

    private boolean hasChild(QubbleCuboid parent, QubbleCuboid child) {
        if (parent.getChildren().contains(child)) {
            return true;
        }
        for (QubbleCuboid c : parent.getChildren()) {
            boolean hasChild = this.hasChild(c, child);
            if (hasChild) {
                return true;
            }
        }
        return false;
    }

    private List<QubbleCuboid> getParents(QubbleModel model, QubbleCuboid cube, boolean ignoreSelf) {
        QubbleCuboid parent = cube;
        List<QubbleCuboid> parents = new ArrayList<>();
        if (!ignoreSelf) {
            parents.add(cube);
        }
        while ((parent = this.getParent(model, parent)) != null) {
            parents.add(parent);
        }
        Collections.reverse(parents);
        return parents;
    }

    private float[][] getParentTransformation(QubbleModel model, QubbleCuboid cube, boolean includeParents, boolean ignoreSelf) {
        return this.getParentTransformation(this.getParentTransformationMatrix(model, cube, includeParents, ignoreSelf));
    }

    private Matrix4d getParentTransformationMatrix(QubbleModel model, QubbleCuboid cube, boolean includeParents, boolean ignoreSelf) {
        List<QubbleCuboid> parentCubes = new ArrayList<>();
        if (includeParents) {
            parentCubes = this.getParents(model, cube, ignoreSelf);
        } else if (!ignoreSelf) {
            parentCubes.add(cube);
        }
        Matrix4d matrix = new Matrix4d();
        matrix.setIdentity();
        Matrix4d transform = new Matrix4d();
        for (QubbleCuboid child : parentCubes) {
            transform.setIdentity();
            transform.setTranslation(new Vector3d(child.getPositionX(), child.getPositionY(), child.getPositionZ()));
            matrix.mul(transform);
            transform.rotZ(child.getRotationZ() / 180 * Math.PI);
            matrix.mul(transform);
            transform.rotY(child.getRotationY() / 180 * Math.PI);
            matrix.mul(transform);
            transform.rotX(child.getRotationX() / 180 * Math.PI);
            matrix.mul(transform);
        }
        return matrix;
    }

    private float[][] getParentTransformation(Matrix4d matrix) {
        double sinRotationAngleY, cosRotationAngleY, sinRotationAngleX, cosRotationAngleX, sinRotationAngleZ, cosRotationAngleZ;
        sinRotationAngleY = -matrix.m20;
        cosRotationAngleY = Math.sqrt(1 - sinRotationAngleY * sinRotationAngleY);
        if (Math.abs(cosRotationAngleY) > 0.0001) {
            sinRotationAngleX = matrix.m21 / cosRotationAngleY;
            cosRotationAngleX = matrix.m22 / cosRotationAngleY;
            sinRotationAngleZ = matrix.m10 / cosRotationAngleY;
            cosRotationAngleZ = matrix.m00 / cosRotationAngleY;
        } else {
            sinRotationAngleX = -matrix.m12;
            cosRotationAngleX = matrix.m11;
            sinRotationAngleZ = 0;
            cosRotationAngleZ = 1;
        }
        float rotationAngleX = (float) (this.epsilon((float) Math.atan2(sinRotationAngleX, cosRotationAngleX)) / Math.PI * 180);
        float rotationAngleY = (float) (this.epsilon((float) Math.atan2(sinRotationAngleY, cosRotationAngleY)) / Math.PI * 180);
        float rotationAngleZ = (float) (this.epsilon((float) Math.atan2(sinRotationAngleZ, cosRotationAngleZ)) / Math.PI * 180);
        return new float[][]{{ this.epsilon((float) matrix.m03), this.epsilon((float) matrix.m13), this.epsilon((float) matrix.m23)}, {rotationAngleX, rotationAngleY, rotationAngleZ}};
    }

    private float epsilon(float x) {
        return x < 0 ? x > -0.0001F ? 0 : x : x < 0.0001F ? 0 : x;
    }

    public boolean isParenting() {
        return this.parenting != null;
    }
}

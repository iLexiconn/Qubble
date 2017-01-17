package net.ilexiconn.qubble.client.gui.element.sidebar;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.InputElementBase;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SidebarElement extends Element<QubbleGUI> {
    private InputElementBase<QubbleGUI> nameInput;

    private boolean initialized;

    public SidebarElement(QubbleGUI gui) {
        super(gui, gui.width - 122, 20, 122, gui.height - 20);
    }

    @Override
    public void init() {
        this.initFields();
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null && selectedProject.getSelectedCube() != null) {
            this.enable(selectedProject.getModel(), selectedProject.getSelectedCube());
        } else {
            this.disable();
        }
    }

    @Override
    public void update() {
        if (this.initialized) {
            this.gui.getEditMode().getSidebarHandler().update(this.gui, this.gui.getSelectedProject());
        }
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), LLibrary.CONFIG.getPrimaryColor());
        this.drawRectangle(this.getPosX(), this.getPosY(), 2, this.getHeight(), LLibrary.CONFIG.getAccentColor());
    }

    public <T extends Element<QubbleGUI>> T getElement(Class<T> type, int index) {
        int currentIndex = 0;
        for (Element<QubbleGUI> element : this.getChildren()) {
            if (type.isAssignableFrom(element.getClass())) {
                if (currentIndex == index) {
                    return (T) element;
                } else {
                    currentIndex++;
                }
            }
        }
        return null;
    }

    public void enable(QubbleModel model, QubbleCuboid cuboid) {
        this.nameInput.clearText();
        this.nameInput.writeText(cuboid.getName());
        this.nameInput.setEditable(true);
        this.gui.getEditMode().getSidebarHandler().enable(this.gui, model, cuboid);
    }

    public void disable() {
        this.nameInput.clearText();
        this.nameInput.setEditable(false);
        this.gui.getEditMode().getSidebarHandler().disable();
    }

    public void initFields() {
        this.getChildren().clear();
        new LabelElement<>(this.gui, "Selected cube", 4, 10).withParent(this);
        this.nameInput = (InputElementBase<QubbleGUI>) new InputElement<>(this.gui, 4, 19, 116, "", inputElement -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setName(inputElement.getText());
                this.gui.getModelView().updatePart(selectedCube);
                selectedProject.setSaved(false);
            }
        }).withParent(this);
        this.gui.getEditMode().getSidebarHandler().create(this.gui, this);
        this.initialized = true;
    }
}

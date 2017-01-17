package net.ilexiconn.qubble.client.gui.element.sidebar;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.CheckboxElement;
import net.ilexiconn.llibrary.client.gui.element.InputElementBase;
import net.ilexiconn.llibrary.client.gui.element.PropertyInputElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.server.property.IBooleanProperty;
import net.ilexiconn.llibrary.server.property.IFloatProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class SidebarHandler {
    protected QubbleGUI gui;

    private final List<IBooleanProperty> booleanProperties = new ArrayList<>();
    private final List<IFloatProperty> floatProperties = new ArrayList<>();
    private final List<IStringProperty> stringProperties = new ArrayList<>();
    private final List<SliderElement<QubbleGUI, ?>> sliders = new ArrayList<>();
    private final List<CheckboxElement<QubbleGUI>> checkboxes = new ArrayList<>();
    private final List<ButtonElement<QubbleGUI>> buttons = new ArrayList<>();
    private final List<InputElementBase<QubbleGUI>> inputs = new ArrayList<>();

    public void create(QubbleGUI gui, SidebarElement sidebar) {
        this.gui = gui;

        this.createElements(gui, sidebar);
        for (SliderElement<QubbleGUI, ?> element : this.sliders) {
            element.withParent(sidebar);
        }
        for (CheckboxElement<QubbleGUI> element : this.checkboxes) {
            element.withParent(sidebar);
        }
        for (ButtonElement<QubbleGUI> element : this.buttons) {
            element.withParent(sidebar);
        }
        for (InputElementBase<QubbleGUI> element : this.inputs) {
            element.withParent(sidebar);
        }
    }

    public void enable(QubbleGUI gui, QubbleModel model, QubbleCuboid cuboid) {
        this.gui = gui;

        this.initProperties(model, cuboid);
        this.initElements(model, cuboid);
        this.setEnabled(true);
        this.updateSliders();
    }

    public void disable() {
        for (IFloatProperty property : this.floatProperties) {
            property.setFloat(0.0F);
        }
        for (IBooleanProperty property : this.booleanProperties) {
            property.setBoolean(false);
        }
        for (IStringProperty property : this.stringProperties) {
            property.setString("");
        }
        this.setEnabled(false);
        this.updateSliders();
    }

    public void updateSliders() {
        for (SliderElement<QubbleGUI, ?> element : this.sliders) {
            ((PropertyInputElement) element.getValueInput()).readValue();
        }
    }

    public abstract void update(QubbleGUI gui, Project project);

    protected abstract void initProperties(QubbleModel model, QubbleCuboid cuboid);

    protected abstract void createElements(QubbleGUI gui, SidebarElement sidebar);

    protected abstract void initElements(QubbleModel model, QubbleCuboid cuboid);

    protected void addFloat(IFloatProperty... properties) {
        Collections.addAll(this.floatProperties, properties);
    }

    protected void addBoolean(IBooleanProperty... properties) {
        Collections.addAll(this.booleanProperties, properties);
    }

    protected void addString(IStringProperty... properties) {
        Collections.addAll(this.stringProperties, properties);
    }

    protected void add(SliderElement<QubbleGUI, ?>... sliders) {
        Collections.addAll(this.sliders, sliders);
    }

    protected void add(CheckboxElement<QubbleGUI>... checkboxes) {
        Collections.addAll(this.checkboxes, checkboxes);
    }

    protected void add(ButtonElement<QubbleGUI>... buttons) {
        Collections.addAll(this.buttons, buttons);
    }

    protected void add(InputElementBase<QubbleGUI>... buttons) {
        Collections.addAll(this.inputs, buttons);
    }

    protected void edit(Consumer<QubbleCuboid> edit) {
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null && selectedProject.getSelectedCube() != null) {
            QubbleCuboid selectedCube = selectedProject.getSelectedCube();
            edit.accept(selectedCube);
            this.gui.getModelView().updatePart(selectedCube);
            selectedProject.setSaved(false);
        }
    }

    protected void editModel(Consumer<QubbleModel> edit) {
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null) {
            QubbleModel model = selectedProject.getModel();
            edit.accept(model);
            this.gui.getModelView().updateModel();
            selectedProject.setSaved(false);
        }
    }

    private void setEnabled(boolean enabled) {
        for (SliderElement<QubbleGUI, ?> slider : this.sliders) {
            slider.setEditable(enabled);
        }
        for (CheckboxElement<QubbleGUI> checkbox : this.checkboxes) {
            checkbox.setEnabled(enabled);
        }
        for (ButtonElement<QubbleGUI> button : this.buttons) {
            button.setEnabled(enabled);
        }
        for (InputElementBase<QubbleGUI> input : this.inputs) {
            input.clearText();
            input.setEditable(false);
        }
    }
}

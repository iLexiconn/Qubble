package net.ilexiconn.qubble.client.gui.element.sidebar;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.CheckboxElement;
import net.ilexiconn.llibrary.client.gui.element.DropdownButtonElement;
import net.ilexiconn.llibrary.client.gui.element.InputElementBase;
import net.ilexiconn.llibrary.client.gui.element.PropertyInputElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.llibrary.server.property.IBooleanProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.property.ActionFloatProperty;
import net.ilexiconn.qubble.client.gui.property.ActionObjectProperty;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.project.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class SidebarHandler<CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> {
    protected QubbleGUI gui;

    private final List<IBooleanProperty> booleanProperties = new ArrayList<>();
    private final List<ActionFloatProperty> floatProperties = new ArrayList<>();
    private final List<IStringProperty> stringProperties = new ArrayList<>();
    private final List<SliderElement<QubbleGUI, ?>> sliders = new ArrayList<>();
    private final List<CheckboxElement<QubbleGUI>> checkboxes = new ArrayList<>();
    private final List<ButtonElement<QubbleGUI>> buttons = new ArrayList<>();
    private final List<InputElementBase<QubbleGUI>> inputs = new ArrayList<>();
    private final List<DropdownButtonElement<QubbleGUI>> dropdowns = new ArrayList<>();

    public void create(QubbleGUI gui, SidebarElement sidebar) {
        this.gui = gui;

        this.createProperties(gui, sidebar);
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
        for (DropdownButtonElement<QubbleGUI> element : this.dropdowns) {
            element.withParent(sidebar);
        }
    }

    public abstract void createProperties(QubbleGUI gui, SidebarElement sidebar);

    public void enable(QubbleGUI gui, MDL model, CBE cuboid) {
        this.gui = gui;

        this.initProperties(model, cuboid);
        this.initElements(model, cuboid);
        this.setEnabled(true);
        this.updateSliders();
    }

    public void disable() {
        for (ActionFloatProperty property : this.floatProperties) {
            property.init(0.0F);
        }
        for (IBooleanProperty property : this.booleanProperties) {
            if (property instanceof ActionObjectProperty) {
                ((ActionObjectProperty) property).init(false);
            } else {
                property.setBoolean(false);
            }
        }
        for (IStringProperty property : this.stringProperties) {
            if (property instanceof ActionObjectProperty) {
                ((ActionObjectProperty) property).init("");
            } else {
                property.setString("");
            }
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

    protected abstract void initProperties(MDL model, CBE cuboid);

    protected abstract void createElements(QubbleGUI gui, SidebarElement sidebar);

    protected abstract void initElements(MDL model, CBE cuboid);

    protected void addFloat(ActionFloatProperty... properties) {
        Collections.addAll(this.floatProperties, properties);
    }

    protected void addBoolean(IBooleanProperty... properties) {
        Collections.addAll(this.booleanProperties, properties);
    }

    protected void addString(IStringProperty... properties) {
        Collections.addAll(this.stringProperties, properties);
    }

    @SafeVarargs
    protected final void add(SliderElement<QubbleGUI, ?>... sliders) {
        Collections.addAll(this.sliders, sliders);
    }

    @SafeVarargs
    protected final void add(CheckboxElement<QubbleGUI>... checkboxes) {
        Collections.addAll(this.checkboxes, checkboxes);
    }

    @SafeVarargs
    protected final void add(ButtonElement<QubbleGUI>... buttons) {
        Collections.addAll(this.buttons, buttons);
    }

    @SafeVarargs
    protected final void add(InputElementBase<QubbleGUI>... buttons) {
        Collections.addAll(this.inputs, buttons);
    }

    @SafeVarargs
    protected final void add(DropdownButtonElement<QubbleGUI>... buttons) {
        Collections.addAll(this.dropdowns, buttons);
    }

    protected void edit(Consumer<CBE> edit) {
        if (this.gui != null) {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null) {
                MDL model = selectedProject.getModel(this.getModelType());
                CBE selectedCuboid = selectedProject.getSelectedCuboid(this.getModelType());
                if (model != null && selectedCuboid != null) {
                    edit.accept(selectedCuboid);
                    model.rebuildModel();
                }
            }
        }
        this.updateSliders();
    }

    protected void editModel(Consumer<MDL> edit) {
        if (this.gui != null) {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null) {
                MDL model = selectedProject.getModel(this.getModelType());
                if (model != null) {
                    edit.accept(model);
                    model.rebuildModel();
                }
            }
        }
        this.updateSliders();
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
        for (DropdownButtonElement<QubbleGUI> dropdown : this.dropdowns) {
            dropdown.setEnabled(enabled);
        }
        for (InputElementBase<QubbleGUI> input : this.inputs) {
            input.clearText();
            input.setEditable(false);
        }
    }

    public abstract ModelType<CBE, MDL> getModelType();
}

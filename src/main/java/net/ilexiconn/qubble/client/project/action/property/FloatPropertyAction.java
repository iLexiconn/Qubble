package net.ilexiconn.qubble.client.project.action.property;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.property.ActionFloatProperty;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.project.action.EditAction;

public class FloatPropertyAction extends EditAction {
    private final ActionFloatProperty property;
    private final CuboidWrapper cuboid;
    private float value;
    private float previousValue;

    public FloatPropertyAction(QubbleGUI gui, ActionFloatProperty property, float value) {
        super(gui);
        this.cuboid = gui.getSelectedProject().getSelectedCuboid();
        this.property = property;
        this.value = value;
    }

    @Override
    public void perform() throws Exception {
        this.gui.getSelectedProject().setSelectedCuboid(this.cuboid);
        this.previousValue = this.property.getAction();
        this.property.setAction(this.value);
    }

    @Override
    public void undo() throws Exception {
        this.gui.getSelectedProject().setSelectedCuboid(this.cuboid);
        this.property.setAction(this.previousValue);
    }

    public static FloatPropertyAction set(QubbleGUI gui, ActionFloatProperty property, float value) throws Exception {
        if (gui.getSelectedProject() != null) {
            FloatPropertyAction action = new FloatPropertyAction(gui, property, value);
            gui.getSelectedProject().perform(action);
            return action;
        }
        return null;
    }
}

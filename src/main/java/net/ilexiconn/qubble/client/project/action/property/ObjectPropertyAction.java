package net.ilexiconn.qubble.client.project.action.property;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.property.ActionObjectProperty;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.project.action.EditAction;

public class ObjectPropertyAction<T> extends EditAction {
    private final ActionObjectProperty<T> property;
    private final CuboidWrapper cuboid;
    private T value;
    private T previousValue;

    public ObjectPropertyAction(QubbleGUI gui, ActionObjectProperty<T> property, T value) {
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

    public static <T> ObjectPropertyAction<T> set(QubbleGUI gui, ActionObjectProperty<T> property, T value) throws Exception {
        if (gui.getSelectedProject() != null) {
            ObjectPropertyAction<T> action = new ObjectPropertyAction<>(gui, property, value);
            gui.getSelectedProject().perform(action);
            return action;
        }
        return null;
    }
}

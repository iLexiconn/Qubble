package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.ilexiconn.qubble.client.gui.GUIHelper;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.project.action.property.ObjectPropertyAction;

public abstract class ActionObjectProperty<T> implements IStringProperty {
    private QubbleGUI gui;

    public ActionObjectProperty(QubbleGUI gui) {
        this.gui = gui;
    }

    protected final void perform(T value) {
        try {
            ObjectPropertyAction.set(this.gui, this, value);
        } catch (Exception e) {
            GUIHelper.INSTANCE.error(this.gui, 200, "Failed to set property!", e);
            e.printStackTrace();
        }
    }

    public abstract T getAction();

    public abstract void setAction(T value);

    public abstract void init(T value);
}

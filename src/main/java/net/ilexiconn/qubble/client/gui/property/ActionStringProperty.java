package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.qubble.client.gui.QubbleGUI;

public abstract class ActionStringProperty extends ActionObjectProperty<String> {
    public ActionStringProperty(QubbleGUI gui) {
        super(gui);
    }

    @Override
    public String getString() {
        return this.getAction();
    }

    @Override
    public void setString(String string) {
        this.perform(string);
    }

    @Override
    public boolean isValidString(String s) {
        return true;
    }
}

package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IBooleanProperty;
import net.ilexiconn.qubble.client.gui.QubbleGUI;

import java.util.function.Consumer;

public class ActionBooleanProperty extends ActionObjectProperty<Boolean> implements IBooleanProperty {
    private Consumer<Boolean> submit;
    private boolean state;

    public ActionBooleanProperty(QubbleGUI gui, Consumer<Boolean> submit) {
        super(gui);
        this.submit = submit;
    }

    @Override
    public boolean getBoolean() {
        return this.state;
    }

    @Override
    public void setBoolean(boolean state) {
        this.perform(state);
    }

    @Override
    public boolean isValidBoolean(boolean state) {
        return true;
    }

    @Override
    public Boolean getAction() {
        return this.state;
    }

    @Override
    public void setAction(Boolean value) {
        this.state = value;
        this.submit.accept(value);
    }

    @Override
    public void init(Boolean value) {
        this.state = value;
    }

    @Override
    public String getString() {
        return String.valueOf(this.state);
    }

    @Override
    public void setString(String string) {
        this.setAction(Boolean.parseBoolean(string));
    }

    @Override
    public boolean isValidString(String string) {
        return string != null && (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false"));
    }
}

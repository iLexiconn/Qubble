package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.qubble.client.gui.QubbleGUI;

import java.util.function.Consumer;

public class DimensionProperty extends ActionFloatProperty {
    private int value;
    private Consumer<Integer> submit;

    public DimensionProperty(QubbleGUI gui, Consumer<Integer> submit) {
        super(gui);
        this.submit = submit;
    }

    @Override
    public float getMinFloatValue() {
        return 0;
    }

    @Override
    public float getMaxFloatValue() {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getAction() {
        return this.value;
    }

    @Override
    public void setAction(float value) {
        this.value = (int) value;
        this.submit.accept(this.value);
    }

    @Override
    public String getString() {
        return String.valueOf(this.value);
    }

    @Override
    public void setString(String text) {
        this.setFloat(Integer.parseInt(text));
    }

    @Override
    public boolean isValidString(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void init(float value) {
        this.value = (int) value;
    }
}

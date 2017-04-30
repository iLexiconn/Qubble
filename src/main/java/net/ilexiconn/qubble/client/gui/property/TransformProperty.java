package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;

import java.util.function.Consumer;

public class TransformProperty extends ActionFloatProperty {
    private float value;
    private Consumer<Float> submit;

    public TransformProperty(QubbleGUI gui, Consumer<Float> submit) {
        super(gui);
        this.submit = submit;
    }

    @Override
    public float getMinFloatValue() {
        return Integer.MIN_VALUE;
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
        this.value = Float.parseFloat(Qubble.DEFAULT_FORMAT.format(value));
        this.submit.accept(this.value);
    }

    @Override
    public String getString() {
        return Qubble.DEFAULT_FORMAT.format(this.value);
    }

    @Override
    public void setString(String text) {
        this.setFloat(Float.parseFloat(text));
    }

    @Override
    public boolean isValidString(String text) {
        try {
            Float.parseFloat(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void init(float value) {
        this.value = value;
    }
}

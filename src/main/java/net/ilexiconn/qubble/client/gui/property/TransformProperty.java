package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IFloatRangeProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.ilexiconn.qubble.Qubble;

import java.util.function.Consumer;

public class TransformProperty implements IFloatRangeProperty, IStringProperty {
    private float value;
    private Consumer<Float> submit;

    public TransformProperty(Consumer<Float> submit) {
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
    public float getFloat() {
        return this.value;
    }

    @Override
    public void setFloat(float value) {
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

    public void set(float value) {
        this.value = value;
    }
}
package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IFloatRangeProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;

import java.util.function.Consumer;

public class DimensionProperty implements IFloatRangeProperty, IStringProperty {
    private int value;
    private Consumer<Integer> submit;

    public DimensionProperty(Consumer<Integer> submit) {
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
    public float getFloat() {
        return this.value;
    }

    @Override
    public void setFloat(float value) {
        this.value = (int) value;
        this.submit.accept(this.value);
    }

    @Override
    public String getString() {
        return String.valueOf(this.value);
    }

    @Override
    public void setString(String text) {
        this.value = Integer.parseInt(text);
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

    public void set(int value) {
        this.value = value;
    }
}

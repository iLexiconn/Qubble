package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IFloatRangeProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarHandler;

import java.util.function.Consumer;

public class DimensionProperty implements IFloatRangeProperty, IStringProperty {
    private int value;
    private SidebarHandler handler;
    private Consumer<Integer> submit;

    public DimensionProperty(SidebarHandler handler, Consumer<Integer> submit) {
        this.handler = handler;
        this.submit = submit;
    }

    public DimensionProperty(Consumer<Integer> submit) {
        this(null, submit);
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
        if (this.handler != null) {
            this.handler.updateSliders();
        }
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

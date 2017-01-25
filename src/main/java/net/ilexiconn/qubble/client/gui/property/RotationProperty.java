package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IFloatRangeProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarHandler;

import java.util.function.Function;

public class RotationProperty implements IFloatRangeProperty, IStringProperty {
    private float value;
    private SidebarHandler handler;
    private Function<Float, Float> submit;

    public RotationProperty(SidebarHandler handler, Function<Float, Float> submit) {
        this.handler = handler;
        this.submit = submit;
    }

    public RotationProperty(Function<Float, Float> submit) {
        this(null, submit);
    }

    @Override
    public float getMinFloatValue() {
        return -180.0F;
    }

    @Override
    public float getMaxFloatValue() {
        return 180.0F;
    }

    @Override
    public float getFloat() {
        return this.value;
    }

    @Override
    public void setFloat(float value) {
        this.value = this.submit.apply(Float.parseFloat(Qubble.DEFAULT_FORMAT.format(value)));
        this.handler.updateSliders();
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

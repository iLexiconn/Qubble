package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;

import java.util.function.Function;

public class RotationProperty extends ActionFloatProperty {
    private float value;
    private Function<Float, Float> submit;

    public RotationProperty(QubbleGUI gui, Function<Float, Float> submit) {
        super(gui);
        this.submit = submit;
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
    public float getAction() {
        return this.value;
    }

    @Override
    public void setAction(float value) {
        this.value = this.submit.apply(Float.parseFloat(Qubble.DEFAULT_FORMAT.format(value)));
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

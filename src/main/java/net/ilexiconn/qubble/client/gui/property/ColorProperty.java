package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IIntProperty;

import java.util.function.Consumer;

public class ColorProperty implements IIntProperty {
    private int color;
    private Consumer<Integer> submit;

    public ColorProperty(Consumer<Integer> submit) {
        this.submit = submit;
    }

    @Override
    public int getInt() {
        return this.color;
    }

    @Override
    public void setInt(int color) {
        this.color = color;
        this.submit.accept(color);
    }

    @Override
    public boolean isValidInt(int color) {
        return true;
    }
}

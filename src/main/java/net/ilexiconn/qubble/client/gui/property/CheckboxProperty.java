package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IBooleanProperty;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarHandler;

import java.util.function.Consumer;

public class CheckboxProperty implements IBooleanProperty {
    private SidebarHandler handler;
    private Consumer<Boolean> submit;
    private boolean state;

    public CheckboxProperty(SidebarHandler handler, Consumer<Boolean> submit) {
        this.handler = handler;
        this.submit = submit;
    }

    public CheckboxProperty(Consumer<Boolean> submit) {
        this(null, submit);
    }

    @Override
    public boolean getBoolean() {
        return this.state;
    }

    @Override
    public void setBoolean(boolean state) {
        this.state = state;
        this.submit.accept(state);
        if (this.handler != null) {
            this.handler.updateSliders();
        }
    }

    public void set(boolean state) {
        this.state = state;
    }

    @Override
    public boolean isValidBoolean(boolean state) {
        return true;
    }
}

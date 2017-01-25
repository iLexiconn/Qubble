package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IStringSelectionProperty;

import java.util.Set;

public class DropdownProperty implements IStringSelectionProperty {
    private String value;
    private Set<String> values;

    public DropdownProperty(Set<String> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Cannot create DropdownProperty for an empty set of values!");
        }
        this.values = values;
        this.value = this.values.iterator().next();
    }

    @Override
    public Set<String> getValidStringValues() {
        return this.values;
    }

    @Override
    public String getString() {
        return this.value;
    }

    @Override
    public void setString(String value) {
        this.value = value;
    }
}

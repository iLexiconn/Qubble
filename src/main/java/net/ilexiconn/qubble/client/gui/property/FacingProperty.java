package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IStringSelectionProperty;
import net.minecraft.util.EnumFacing;

import java.util.LinkedHashSet;
import java.util.Set;

public class FacingProperty implements IStringSelectionProperty {
    private String value;
    private Set<String> values;

    public FacingProperty() {
        this.values = new LinkedHashSet<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            this.values.add(facing.getName());
        }
        this.value = EnumFacing.NORTH.getName();
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

    public EnumFacing get() {
        return EnumFacing.byName(this.value);
    }

    public void set(EnumFacing facing) {
        this.value = facing.getName();
    }
}

package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IStringSelectionProperty;
import net.minecraft.util.EnumFacing;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class AxisProperty implements IStringSelectionProperty {
    private String value;
    private Set<String> values;
    private Consumer<EnumFacing.Axis> onSubmit;

    public AxisProperty(Consumer<EnumFacing.Axis> onSubmit) {
        this.values = new LinkedHashSet<>();
        for (EnumFacing.Axis facing : EnumFacing.Axis.values()) {
            this.values.add(facing.getName());
        }
        this.value = EnumFacing.Axis.X.getName();
        this.onSubmit = onSubmit;
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
        String last = this.value;
        this.value = value;
        if (last != null && !last.equals(value) && last.length() != 0) {
            this.onSubmit.accept(this.get());
        }
    }

    public EnumFacing.Axis get() {
        return EnumFacing.Axis.byName(this.value);
    }

    public void set(EnumFacing.Axis axis) {
        this.setString(axis.getName());
    }
}

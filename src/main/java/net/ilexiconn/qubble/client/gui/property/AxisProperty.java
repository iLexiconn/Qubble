package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IStringSelectionProperty;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.util.EnumFacing;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class AxisProperty extends ActionStringProperty implements IStringSelectionProperty {
    private String value;
    private Set<String> values;
    private Consumer<EnumFacing.Axis> submit;

    public AxisProperty(QubbleGUI gui, Consumer<EnumFacing.Axis> onSubmit) {
        super(gui);
        this.values = new LinkedHashSet<>();
        for (EnumFacing.Axis facing : EnumFacing.Axis.values()) {
            this.values.add(facing.getName());
        }
        this.value = EnumFacing.Axis.X.getName();
        this.submit = onSubmit;
    }

    @Override
    public Set<String> getValidStringValues() {
        return this.values;
    }

    public EnumFacing.Axis get() {
        return EnumFacing.Axis.byName(this.value);
    }

    public void set(EnumFacing.Axis axis) {
        this.setString(axis.getName());
    }

    @Override
    public String getAction() {
        return this.value;
    }

    @Override
    public void setAction(String value) {
        String last = this.value;
        this.value = value;
        if (last != null && !last.equals(value) && last.length() != 0) {
            this.submit.accept(this.get());
        }
    }

    @Override
    public void init(String value) {
        this.value = value;
    }

    @Override
    public boolean isValidString(String string) {
        return this.values.contains(string);
    }
}

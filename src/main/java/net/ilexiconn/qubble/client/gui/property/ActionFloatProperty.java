package net.ilexiconn.qubble.client.gui.property;

import net.ilexiconn.llibrary.server.property.IFloatRangeProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.ilexiconn.qubble.client.gui.GUIHelper;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.project.action.property.FloatPropertyAction;

public abstract class ActionFloatProperty implements IFloatRangeProperty, IStringProperty {
    private QubbleGUI gui;

    public ActionFloatProperty(QubbleGUI gui) {
        this.gui = gui;
    }

    @Override
    public final float getFloat() {
        return this.getAction();
    }

    @Override
    public final void setFloat(float value) {
        try {
            FloatPropertyAction.set(this.gui, this, value);
        } catch (Exception e) {
            GUIHelper.INSTANCE.error(this.gui, 200, "Failed to set property!", e);
            e.printStackTrace();
        }
    }

    public abstract float getAction();

    public abstract void setAction(float value);

    public abstract void init(float value);
}

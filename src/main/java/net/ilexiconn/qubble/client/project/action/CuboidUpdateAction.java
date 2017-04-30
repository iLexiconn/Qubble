package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

public class CuboidUpdateAction extends EditAction {
    private final String name;
    private final CuboidWrapper updated;
    private CuboidWrapper previous;

    public CuboidUpdateAction(QubbleGUI gui, CuboidWrapper updated) {
        super(gui);
        this.name = updated.getName();
        this.updated = updated;
    }

    @Override
    public void perform() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.name);
        if (cuboid != null) {
            this.previous = cuboid.copyRaw();
            ModelHandler.INSTANCE.apply(cuboid, this.updated);
        } else {
            throw new IllegalStateException("Failed to update cuboid - it no longer exists!");
        }
    }

    @Override
    public void undo() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.name);
        if (cuboid != null) {
            ModelHandler.INSTANCE.apply(cuboid, this.previous);
        } else {
            throw new IllegalStateException("Failed to update cuboid - it no longer exists!");
        }
    }
}

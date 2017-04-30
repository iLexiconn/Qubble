package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

public class ReparentCuboidAction extends EditAction {
    private final String cuboid;
    private final String newParent;
    private final boolean maintainPosition;
    private String previousParent;

    public ReparentCuboidAction(QubbleGUI gui, CuboidWrapper cuboid, CuboidWrapper newParent, boolean maintainPosition) {
        super(gui);
        this.cuboid = cuboid.getName();
        if (newParent != null) {
            this.newParent = newParent.getName();
        } else {
            this.newParent = null;
        }
        this.maintainPosition = maintainPosition;
    }

    @Override
    public void perform() throws Exception {
        if (this.project != null && this.project.getModel() != null) {
            ModelWrapper model = this.project.getModel();
            CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.cuboid);
            CuboidWrapper newParent = ModelHandler.INSTANCE.getCuboid(model, this.newParent);
            if (cuboid != null && newParent != null) {
                if (model.supportsParenting()) {
                    CuboidWrapper parent = ModelHandler.INSTANCE.getParent(model, cuboid);
                    this.previousParent = parent != null ? parent.getName() : null;
                    if (model.reparent(cuboid, newParent, this.maintainPosition)) {
                        model.rebuildModel();
                    }
                }
            } else {
                throw new IllegalStateException("Failed to reparent cuboids - requested cuboids no longer exist");
            }
        } else {
            throw new IllegalStateException("Failed to reparent cuboids - model is not selected");
        }
    }

    @Override
    public void undo() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.cuboid);
        CuboidWrapper newParent = this.previousParent != null ? ModelHandler.INSTANCE.getCuboid(model, this.previousParent) : null;
        if (cuboid != null) {
            if (model.supportsParenting()) {
                if (model.reparent(cuboid, newParent, this.maintainPosition)) {
                    model.rebuildModel();
                }
            }
        } else {
            throw new IllegalStateException("Failed to undo cuboid parenting - requested cuboids no longer exist");
        }
    }
}

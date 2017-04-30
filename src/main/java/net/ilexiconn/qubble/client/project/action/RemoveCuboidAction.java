package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

public class RemoveCuboidAction extends EditAction {
    private final String name;
    private CuboidWrapper removedCuboid;
    private CuboidWrapper removedCuboidParent;

    public RemoveCuboidAction(QubbleGUI gui, String name) {
        super(gui);
        this.name = name;
    }

    @Override
    public void perform() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.name);
        if (cuboid != null) {
            ModelHandler.INSTANCE.removeCuboid(model, cuboid);
            this.removedCuboid = cuboid;
            this.removedCuboidParent = ModelHandler.INSTANCE.getParent(model, cuboid);
        } else {
            throw new IllegalStateException("Failed to remove cuboid - cuboid no longer exists!");
        }
        this.project.setSelectedCuboid(null);
        model.rebuildModel();
    }

    @Override
    public void undo() throws Exception {
        ModelWrapper model = this.project.getModel();
        if (!ModelHandler.INSTANCE.hasDuplicateName(model, this.name)) {
            if (this.removedCuboid != null) {
                if (this.removedCuboidParent != null) {
                    this.removedCuboidParent.addChild(this.removedCuboid);
                } else {
                    model.addCuboid(this.removedCuboid);
                }
            } else {
                throw new IllegalArgumentException("Failed to undo cuboid removal - cuboid was not removed!");
            }
        } else {
            throw new IllegalStateException("Failed to undo cuboid removal - a cuboid with that name already exists!");
        }
        this.project.setSelectedCuboid(null);
        model.rebuildModel();
    }
}

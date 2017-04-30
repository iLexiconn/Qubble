package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

public class CreateCuboidAction extends EditAction {
    private final String name;

    public CreateCuboidAction(QubbleGUI gui, String name) {
        super(gui);
        this.name = name;
    }

    @Override
    public void perform() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = model.createCuboid(this.name);
        this.project.setSelectedCuboid(cuboid);
        this.gui.getSidebar().selectName();
        model.rebuildModel();
    }

    @Override
    public void undo() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.name);
        if (cuboid != null) {
            ModelHandler.INSTANCE.removeCuboid(model, cuboid);
        } else {
            throw new IllegalStateException("Failed to undo cuboid creation - cuboid no longer exists!");
        }
        this.project.setSelectedCuboid(null);
        model.rebuildModel();
    }
}

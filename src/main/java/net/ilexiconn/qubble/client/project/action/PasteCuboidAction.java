package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

public class PasteCuboidAction extends EditAction {
    private final CuboidWrapper cuboid;

    public PasteCuboidAction(QubbleGUI gui, CuboidWrapper cuboid) {
        super(gui);
        this.cuboid = cuboid;
    }

    @Override
    public void perform() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = this.cuboid.copy(model);
        model.addCuboid(cuboid);
        this.project.setSelectedCuboid(cuboid);
    }

    @Override
    public void undo() throws Exception {
        ModelWrapper model = this.project.getModel();
        ModelHandler.INSTANCE.removeCuboid(model, this.cuboid);
    }
}

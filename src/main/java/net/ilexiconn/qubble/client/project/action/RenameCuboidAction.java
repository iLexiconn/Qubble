package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;

public class RenameCuboidAction extends EditAction {
    private final String name;
    private final String rename;

    public RenameCuboidAction(QubbleGUI gui, String name, String rename) {
        super(gui);
        this.name = name;
        this.rename = rename;
    }

    @Override
    public void perform() throws Exception {
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(this.project.getModel(), this.name);
        if (cuboid != null) {
            cuboid.setName(this.rename);
            this.project.setSelectedCuboid(cuboid);
        } else {
            throw new IllegalStateException("Cannot rename cuboid, it doesn't exist");
        }
    }

    @Override
    public void undo() throws Exception {
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(this.project.getModel(), this.rename);
        if (cuboid != null) {
            cuboid.setName(this.name);
            this.project.setSelectedCuboid(cuboid);
            this.gui.getSidebar().enable(this.project.getModel(), this.project.getSelectedCuboid());
        } else {
            throw new IllegalStateException("Cannot rename cuboid, it doesn't exist");
        }
    }
}

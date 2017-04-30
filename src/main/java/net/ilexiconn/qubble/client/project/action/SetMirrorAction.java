package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

public class SetMirrorAction extends EditAction {
    private final String name;
    private boolean mirror;
    private boolean prevMirror;

    public SetMirrorAction(QubbleGUI gui, String name, boolean mirror) {
        super(gui);
        this.name = name;
        this.mirror = mirror;
    }

    @Override
    public void perform() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.name);
        if (cuboid != null) {
            this.prevMirror = cuboid.isTextureMirrored();
            cuboid.setTextureMirrored(this.mirror);
        } else {
            throw new IllegalStateException("Failed to set cuboid mirror - it no longer exists!");
        }
    }

    @Override
    public void undo() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.name);
        if (cuboid != null) {
            cuboid.setTextureMirrored(this.prevMirror);
        } else {
            throw new IllegalStateException("Failed to undo cuboid mirror - it no longer exists!");
        }
    }
}

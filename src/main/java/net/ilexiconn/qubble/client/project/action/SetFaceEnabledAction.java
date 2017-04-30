package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.project.ModelType;
import net.minecraft.util.EnumFacing;

public class SetFaceEnabledAction extends EditAction {
    private final String cuboid;
    private final EnumFacing side;
    private final boolean enabled;
    private boolean previousEnabled;

    public SetFaceEnabledAction(QubbleGUI gui, CuboidWrapper cuboid, EnumFacing side, boolean enabled) {
        super(gui);
        this.cuboid = cuboid.getName();
        this.side = side;
        this.enabled = enabled;
    }

    @Override
    public void perform() throws Exception {
        BlockModelWrapper model = this.project.getModel(ModelType.BLOCK);
        BlockCuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.cuboid);
        if (cuboid != null) {
            QubbleVanillaFace face = cuboid.getCuboid().getFace(this.side);
            this.previousEnabled = face.isEnabled();
            face.setEnabled(this.enabled);
        } else {
            throw new IllegalStateException("Failed to set face enabled - cuboid does not exist");
        }
    }

    @Override
    public void undo() throws Exception {
        BlockModelWrapper model = this.project.getModel(ModelType.BLOCK);
        BlockCuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.cuboid);
        if (cuboid != null) {
            QubbleVanillaFace face = cuboid.getCuboid().getFace(this.side);
            face.setEnabled(this.previousEnabled);
        } else {
            throw new IllegalStateException("Failed to set face enabled - cuboid does not exist");
        }
    }
}

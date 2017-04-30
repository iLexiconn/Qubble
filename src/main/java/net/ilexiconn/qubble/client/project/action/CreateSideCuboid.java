package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumFacing;

public class CreateSideCuboid extends EditAction {
    private final String cuboid;
    private final boolean parented;
    private final EnumFacing side;
    private String created;

    public CreateSideCuboid(QubbleGUI gui, CuboidWrapper cuboid, EnumFacing side, boolean parented) {
        super(gui);
        this.cuboid = cuboid.getName();
        this.parented = parented;
        this.side = side;
    }

    @Override
    public void perform() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.cuboid);
        CuboidWrapper created = model.createSide(cuboid, this.side);
        if (GuiScreen.isShiftKeyDown() && model.supportsParenting()) {
            model.reparent(created, cuboid, true);
        }
        this.created = created.getName();
        this.project.setSelectedCuboid(created);
        this.gui.getSidebar().selectName();
        model.rebuildModel();
    }

    @Override
    public void undo() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.created);
        if (cuboid != null) {
            ModelHandler.INSTANCE.removeCuboid(model, cuboid);
        } else {
            throw new IllegalStateException("Failed to undo cuboid creation - cuboid no longer exists!");
        }
        this.project.setSelectedCuboid(null);
        model.rebuildModel();
    }
}

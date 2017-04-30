package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;
import java.util.Map;

public class AutoUVCuboidAction extends EditAction {
    private final String cuboid;
    private Map<EnumFacing, UV> previousUVs = new HashMap<>();

    public AutoUVCuboidAction(QubbleGUI gui, CuboidWrapper cuboid) {
        super(gui);
        this.cuboid = cuboid.getName();
    }

    @Override
    public void perform() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.cuboid);
        if (cuboid != null) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                this.previousUVs.put(facing, new UV(cuboid.getMinU(facing), cuboid.getMinV(facing), cuboid.getMaxU(facing), cuboid.getMaxV(facing)));
            }
            cuboid.setAutoUV();
        } else {
            throw new IllegalStateException("Failed to set UV on cuboid, it no longer exists!");
        }
        model.rebuildModel();
    }

    @Override
    public void undo() throws Exception {
        ModelWrapper model = this.project.getModel();
        CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.cuboid);
        if (cuboid != null) {
            for (Map.Entry<EnumFacing, UV> entry : this.previousUVs.entrySet()) {
                EnumFacing facing = entry.getKey();
                UV uv = entry.getValue();
                cuboid.setMinU(facing, uv.minU);
                cuboid.setMinV(facing, uv.minV);
                cuboid.setMaxU(facing, uv.maxU);
                cuboid.setMaxV(facing, uv.maxV);
            }
        } else {
            throw new IllegalStateException("Failed to undo auto UV mapping on cuboid, it no longer exists!");
        }
        model.rebuildModel();
    }

    private class UV {
        private float minU;
        private float minV;
        private float maxU;
        private float maxV;

        private UV(float minU, float minV, float maxU, float maxV) {
            this.minU = minU;
            this.minV = minV;
            this.maxU = maxU;
            this.maxV = maxV;
        }
    }
}

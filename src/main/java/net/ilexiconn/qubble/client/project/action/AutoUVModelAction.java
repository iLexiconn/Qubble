package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;
import java.util.Map;

public class AutoUVModelAction extends EditAction {
    private Map<String, Map<EnumFacing, UV>> previousUVs = new HashMap<>();

    public AutoUVModelAction(QubbleGUI gui) {
        super(gui);
    }

    @Override
    public void perform() throws Exception {
        ModelWrapper<?> model = this.project.getModel();
        for (CuboidWrapper cuboid : ModelHandler.INSTANCE.getAllCuboids(model)) {
            Map<EnumFacing, UV> cuboidUVs = new HashMap<>();
            for (EnumFacing facing : EnumFacing.VALUES) {
                cuboidUVs.put(facing, new UV(cuboid.getMinU(facing), cuboid.getMinV(facing), cuboid.getMaxU(facing), cuboid.getMaxV(facing)));
            }
            this.previousUVs.put(cuboid.getName(), cuboidUVs);
            cuboid.setAutoUV();
        }
        model.rebuildModel();
    }

    @Override
    public void undo() throws Exception {
        int failedCount = 0;
        ModelWrapper model = this.project.getModel();
        for (Map.Entry<String, Map<EnumFacing, UV>> entry : this.previousUVs.entrySet()) {
            CuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, entry.getKey());
            if (cuboid != null) {
                Map<EnumFacing, UV> cuboidUV = entry.getValue();
                for (Map.Entry<EnumFacing, UV> uvEntry : cuboidUV.entrySet()) {
                    EnumFacing facing = uvEntry.getKey();
                    UV uv = uvEntry.getValue();
                    cuboid.setMinU(facing, uv.minU);
                    cuboid.setMinV(facing, uv.minV);
                    cuboid.setMaxU(facing, uv.maxU);
                    cuboid.setMaxV(facing, uv.maxV);
                }
            } else {
                failedCount++;
            }
        }
        model.rebuildModel();
        if (failedCount > 0) {
            throw new IllegalStateException("Failed to revert " + failedCount + " cuboids");
        }
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

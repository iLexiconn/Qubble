package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.project.ModelType;
import net.minecraft.util.EnumFacing;

import java.util.EnumMap;
import java.util.Map;

public class SetFaceTextureAction extends EditAction {
    private final String cuboid;
    private final EnumFacing[] sides;
    private final String texture;
    private final Map<EnumFacing, String> lastTexture = new EnumMap<>(EnumFacing.class);

    public SetFaceTextureAction(QubbleGUI gui, String cuboid, String texture, EnumFacing... sides) {
        super(gui);
        this.cuboid = cuboid;
        this.sides = sides;
        this.texture = texture;
    }

    @Override
    public void perform() throws Exception {
        for (EnumFacing side : this.sides) {
            QubbleVanillaFace face = this.getFace(side);
            this.lastTexture.put(side, face.getTexture());
            face.setTexture(this.texture);
        }
    }

    @Override
    public void undo() throws Exception {
        for (EnumFacing side : this.sides) {
            QubbleVanillaFace face = this.getFace(side);
            face.setTexture(this.lastTexture.get(side));
        }
    }

    private QubbleVanillaFace getFace(EnumFacing facing) {
        BlockModelWrapper model = this.project.getModel(ModelType.BLOCK);
        BlockCuboidWrapper cuboid = ModelHandler.INSTANCE.getCuboid(model, this.cuboid);
        return cuboid.getCuboid().getFace(facing);
    }
}

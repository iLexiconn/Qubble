package net.ilexiconn.qubble.client.model;

import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.ilexiconn.qubble.server.model.qubble.QubbleCube;
import net.ilexiconn.qubble.server.model.qubble.QubbleModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class QubbleModelBase extends AdvancedModelBase {
    private QubbleModel model;
    private List<AdvancedModelRenderer> rootCubes = new ArrayList<>();

    public QubbleModelBase(QubbleModel model) {
        this.textureWidth = model.getTextureWidth();
        this.textureHeight = model.getTextureHeight();
        this.model = model;
        for (QubbleCube cube : model.getCubes()) {
            this.parseCube(cube, null);
        }
    }

    private void parseCube(QubbleCube cube, AdvancedModelRenderer parent) {
        AdvancedModelRenderer modelRenderer = this.createCube(cube);
        if (parent != null) {
            parent.addChild(modelRenderer);
        } else {
            this.rootCubes.add(modelRenderer);
        }
        for (QubbleCube child : cube.getChildren()) {
            this.parseCube(child, modelRenderer);
        }
    }

    private AdvancedModelRenderer createCube(QubbleCube cube) {
        AdvancedModelRenderer box = new AdvancedModelRenderer(this, cube.getTextureX(), cube.getTextureY());
        box.setRotationPoint(cube.getPositionX(), cube.getPositionY(), cube.getPositionZ());
        box.addBox(cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ(), cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ(), 0.0F);
        box.rotateAngleX = (float) Math.toRadians(cube.getRotationX());
        box.rotateAngleY = (float) Math.toRadians(cube.getRotationY());
        box.rotateAngleZ = (float) Math.toRadians(cube.getRotationZ());
        return box;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        GlStateManager.pushMatrix();
        for (AdvancedModelRenderer cube : this.rootCubes) {
            cube.render(scale);
        }
        GlStateManager.popMatrix();
    }
}

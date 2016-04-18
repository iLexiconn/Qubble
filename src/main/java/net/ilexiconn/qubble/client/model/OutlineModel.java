package net.ilexiconn.qubble.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class OutlineModel extends ModelBase {
    public ModelRenderer bottomFront;
    public ModelRenderer topFront;
    public ModelRenderer topRight;
    public ModelRenderer topBack;
    public ModelRenderer topLeft;
    public ModelRenderer bottomRight;
    public ModelRenderer bottomBack;
    public ModelRenderer bottomLeft;
    public ModelRenderer frontLeft;
    public ModelRenderer backLeft;
    public ModelRenderer frontRight;
    public ModelRenderer backRight;

    public OutlineModel(int sizeX, int sizeY, int sizeZ) {
        this.bottomFront = new ModelRenderer(this, 0, 0);
        this.bottomFront.setRotationPoint(0.0F, sizeY, 0.0F);
        this.bottomFront.addBox(0.0F, 0.0F, 0.0F, sizeX, 1, 1, 0.0F);
        this.topLeft = new ModelRenderer(this, 0, 0);
        this.topLeft.setRotationPoint(sizeX - 1.0F, 0.0F, 0.0F);
        this.topLeft.addBox(0.0F, 0.0F, 0.0F, 1, 1, sizeZ, 0.0F);
        this.bottomBack = new ModelRenderer(this, 0, 0);
        this.bottomBack.setRotationPoint(0.0F, sizeY, sizeZ - 1.0F);
        this.bottomBack.addBox(0.0F, 0.0F, 0.0F, sizeX, 1, 1, 0.0F);
        this.backRight = new ModelRenderer(this, 0, 0);
        this.backRight.setRotationPoint(0.0F, 0.0F, sizeZ - 1.0F);
        this.backRight.addBox(0.0F, 0.0F, 0.0F, 1, sizeY, 1, 0.0F);
        this.backLeft = new ModelRenderer(this, 0, 0);
        this.backLeft.setRotationPoint(sizeX - 1.0f, 0.0F, sizeZ - 1.0F);
        this.backLeft.addBox(0.0F, 0.0F, 0.0F, 1, sizeY, 1, 0.0F);
        this.topFront = new ModelRenderer(this, 0, 0);
        this.topFront.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.topFront.addBox(0.0F, 0.0F, 0.0F, sizeX, 1, 1, 0.0F);
        this.frontRight = new ModelRenderer(this, 0, 0);
        this.frontRight.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.frontRight.addBox(0.0F, 0.0F, 0.0F, 1, sizeY, 1, 0.0F);
        this.bottomRight = new ModelRenderer(this, 0, 0);
        this.bottomRight.setRotationPoint(0.0F, sizeY, 0.0F);
        this.bottomRight.addBox(0.0F, 0.0F, 0.0F, 1, 1, sizeZ, 0.0F);
        this.topRight = new ModelRenderer(this, 0, 0);
        this.topRight.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.topRight.addBox(0.0F, 0.0F, 0.0F, 1, 1, sizeZ, 0.0F);
        this.topBack = new ModelRenderer(this, 0, 0);
        this.topBack.setRotationPoint(0.0F, 0.0F, sizeZ - 1.0F);
        this.topBack.addBox(0.0F, 0.0F, 0.0F, sizeX, 1, 1, 0.0F);
        this.bottomLeft = new ModelRenderer(this, 0, 0);
        this.bottomLeft.setRotationPoint(sizeX - 1.0F, sizeY, 0.0F);
        this.bottomLeft.addBox(0.0F, 0.0F, 0.0F, 1, 1, sizeZ, 0.0F);
        this.frontLeft = new ModelRenderer(this, 0, 0);
        this.frontLeft.setRotationPoint(sizeX - 1.0F, 0.0F, 0.0F);
        this.frontLeft.addBox(0.0F, 0.0F, 0.0F, 1, sizeY, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.bottomFront.render(f5);
        this.topLeft.render(f5);
        this.bottomBack.render(f5);
        this.backRight.render(f5);
        this.backLeft.render(f5);
        this.topFront.render(f5);
        this.frontRight.render(f5);
        this.bottomRight.render(f5);
        this.topRight.render(f5);
        this.topBack.render(f5);
        this.bottomLeft.render(f5);
        this.frontLeft.render(f5);
    }
}
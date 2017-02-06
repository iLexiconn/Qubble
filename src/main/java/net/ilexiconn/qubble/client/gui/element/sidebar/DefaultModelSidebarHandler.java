package net.ilexiconn.qubble.client.gui.element.sidebar;

import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.property.DimensionProperty;
import net.ilexiconn.qubble.client.gui.property.RotationProperty;
import net.ilexiconn.qubble.client.gui.property.TransformProperty;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import net.minecraft.client.gui.GuiScreen;

public class DefaultModelSidebarHandler extends SidebarHandler<DefaultCuboidWrapper, DefaultModelWrapper> {
    private DimensionProperty propertyDimensionX, propertyDimensionY, propertyDimensionZ;
    private TransformProperty propertyPositionX, propertyPositionY, propertyPositionZ;
    private TransformProperty propertyOffsetX, propertyOffsetY, propertyOffsetZ;
    private TransformProperty propertyScaleX, propertyScaleY, propertyScaleZ;
    private RotationProperty propertyRotationX, propertyRotationY, propertyRotationZ;

    private SliderElement<QubbleGUI, TransformProperty> scaleX, scaleY, scaleZ;

    public DefaultModelSidebarHandler() {
        this.propertyRotationX = new RotationProperty(this, value -> {
            this.edit(cuboid -> cuboid.setRotation(value, cuboid.getRotationY(), cuboid.getRotationZ()));
            return value;
        });
        this.propertyRotationY = new RotationProperty(this, value -> {
            this.edit(cuboid -> cuboid.setRotation(cuboid.getRotationX(), value, cuboid.getRotationZ()));
            return value;
        });
        this.propertyRotationZ = new RotationProperty(this, value -> {
            this.edit(cuboid -> cuboid.setRotation(cuboid.getRotationX(), cuboid.getRotationY(), value));
            return value;
        });
        this.propertyScaleX = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setScale(value, cuboid.getScaleY(), cuboid.getScaleZ())));
        this.propertyScaleY = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setScale(cuboid.getScaleX(), value, cuboid.getScaleZ())));
        this.propertyScaleZ = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setScale(cuboid.getScaleX(), cuboid.getScaleY(), value)));
        this.propertyPositionX = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setPosition(value, cuboid.getPositionY(), cuboid.getPositionZ())));
        this.propertyPositionY = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setPosition(cuboid.getPositionX(), value, cuboid.getPositionZ())));
        this.propertyPositionZ = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setPosition(cuboid.getPositionX(), cuboid.getPositionY(), value)));

        this.propertyOffsetX = new TransformProperty(this, value -> this.edit(cuboid -> {
            if (GuiScreen.isCtrlKeyDown()) {
                cuboid.translateOffset(value, cuboid.getOffsetY(), cuboid.getOffsetZ());
                this.initPosition(cuboid);
            }
            cuboid.setOffset(value, cuboid.getOffsetY(), cuboid.getOffsetZ());
        }));
        this.propertyOffsetY = new TransformProperty(this, value -> this.edit(cuboid -> {
            if (GuiScreen.isCtrlKeyDown()) {
                cuboid.translateOffset(cuboid.getOffsetX(), value, cuboid.getOffsetZ());
                this.initPosition(cuboid);
            }
            cuboid.setOffset(cuboid.getOffsetX(), value, cuboid.getOffsetZ());
        }));
        this.propertyOffsetZ = new TransformProperty(this, value -> this.edit(cuboid -> {
            if (GuiScreen.isCtrlKeyDown()) {
                cuboid.translateOffset(cuboid.getOffsetX(), cuboid.getOffsetY(), value);
                this.initPosition(cuboid);
            }
            cuboid.setOffset(cuboid.getOffsetX(), cuboid.getOffsetY(), value);
        }));

        this.propertyDimensionX = new DimensionProperty(this, value -> this.edit(cuboid -> cuboid.setDimensions(value, cuboid.getDimensionY(), cuboid.getDimensionZ())));
        this.propertyDimensionY = new DimensionProperty(this, value -> this.edit(cuboid -> cuboid.setDimensions(cuboid.getDimensionX(), value, cuboid.getDimensionZ())));
        this.propertyDimensionZ = new DimensionProperty(this, value -> this.edit(cuboid -> cuboid.setDimensions(cuboid.getDimensionX(), cuboid.getDimensionY(), value)));

        this.addFloat(this.propertyDimensionX, this.propertyDimensionY, this.propertyDimensionZ);
        this.addFloat(this.propertyRotationX, this.propertyRotationY, this.propertyRotationZ);
        this.addFloat(this.propertyOffsetX, this.propertyOffsetY, this.propertyOffsetZ);
        this.addFloat(this.propertyScaleX, this.propertyScaleY, this.propertyScaleZ);
        this.addFloat(this.propertyPositionX, this.propertyPositionY, this.propertyPositionZ);
    }

    @Override
    public void update(QubbleGUI gui, Project<DefaultCuboidWrapper, DefaultModelWrapper> project) {
        if (project != null && project.getSelectedCuboid() != null) {
            DefaultCuboidWrapper selectedCuboid = project.getSelectedCuboid();
            boolean hasParent = selectedCuboid.hasParent();
            this.scaleX.setEditable(!hasParent);
            this.scaleY.setEditable(!hasParent);
            this.scaleZ.setEditable(!hasParent);
        }
    }

    @Override
    protected void initProperties(DefaultModelWrapper model, DefaultCuboidWrapper cuboid) {
        this.propertyRotationX.set(cuboid.getRotationX());
        this.propertyRotationY.set(cuboid.getRotationY());
        this.propertyRotationZ.set(cuboid.getRotationZ());
        this.propertyScaleX.set(cuboid.getScaleX());
        this.propertyScaleY.set(cuboid.getScaleY());
        this.propertyScaleZ.set(cuboid.getScaleZ());
        this.propertyOffsetX.set(cuboid.getOffsetX());
        this.propertyOffsetY.set(cuboid.getOffsetY());
        this.propertyOffsetZ.set(cuboid.getOffsetZ());
        this.propertyDimensionX.set((int) cuboid.getDimensionX());
        this.propertyDimensionY.set((int) cuboid.getDimensionY());
        this.propertyDimensionZ.set((int) cuboid.getDimensionZ());
        this.initPosition(cuboid);
    }

    private void initPosition(DefaultCuboidWrapper cuboid) {
        this.propertyPositionX.set(cuboid.getPositionX());
        this.propertyPositionY.set(cuboid.getPositionY());
        this.propertyPositionZ.set(cuboid.getPositionZ());
    }

    @Override
    protected void createElements(QubbleGUI gui, SidebarElement sidebar) {
        new LabelElement<>(this.gui, "Dimensions", 4, 44).withParent(sidebar);
        SliderElement<QubbleGUI, DimensionProperty> dimensionX = new SliderElement<>(this.gui, 4, 53, this.propertyDimensionX, 1);
        SliderElement<QubbleGUI, DimensionProperty> dimensionY = new SliderElement<>(this.gui, 43, 53, this.propertyDimensionY, 1);
        SliderElement<QubbleGUI, DimensionProperty> dimensionZ = new SliderElement<>(this.gui, 82, 53, this.propertyDimensionZ, 1);
        new LabelElement<>(this.gui, "Position", 4, 69).withParent(sidebar);
        SliderElement<QubbleGUI, TransformProperty> positionX = new SliderElement<>(this.gui, 4, 78, this.propertyPositionX, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> positionY = new SliderElement<>(this.gui, 43, 78, this.propertyPositionY, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> positionZ = new SliderElement<>(this.gui, 82, 78, this.propertyPositionZ, 0.1F);
        new LabelElement<>(this.gui, "Offset", 4, 94).withParent(sidebar);
        SliderElement<QubbleGUI, TransformProperty> offsetX = new SliderElement<>(this.gui, 4, 103, this.propertyOffsetX, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> offsetY = new SliderElement<>(this.gui, 43, 103, this.propertyOffsetY, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> offsetZ = new SliderElement<>(this.gui, 82, 103, this.propertyOffsetZ, 0.1F);
        new LabelElement<>(this.gui, "Scale", 4, 119).withParent(sidebar);
        this.scaleX = new SliderElement<>(this.gui, 4, 128, this.propertyScaleX, 0.1F);
        this.scaleY = new SliderElement<>(this.gui, 43, 128, this.propertyScaleY, 0.1F);
        this.scaleZ = new SliderElement<>(this.gui, 82, 128, this.propertyScaleZ, 0.1F);
        new LabelElement<>(this.gui, "Rotation", 4, 144).withParent(sidebar);
        SliderElement<QubbleGUI, RotationProperty> rotationX = new SliderElement<>(this.gui, 4, 153, 78, this.propertyRotationX, 0.1F);
        SliderElement<QubbleGUI, RotationProperty> rotationY = new SliderElement<>(this.gui, 4, 166, 78, this.propertyRotationY, 0.1F);
        SliderElement<QubbleGUI, RotationProperty> rotationZ = new SliderElement<>(this.gui, 4, 179, 78, this.propertyRotationZ, 0.1F);
        this.add(dimensionX, dimensionY, dimensionZ);
        this.add(positionX, positionY, positionZ);
        this.add(offsetX, offsetY, offsetZ);
        this.add(this.scaleX, this.scaleY, this.scaleZ);
        this.add(rotationX, rotationY, rotationZ);
    }

    @Override
    protected void initElements(DefaultModelWrapper model, DefaultCuboidWrapper cuboid) {
    }
}

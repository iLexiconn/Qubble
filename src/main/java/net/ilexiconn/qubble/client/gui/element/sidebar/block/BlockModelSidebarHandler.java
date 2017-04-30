package net.ilexiconn.qubble.client.gui.element.sidebar.block;

import net.ilexiconn.llibrary.client.gui.element.CheckboxElement;
import net.ilexiconn.llibrary.client.gui.element.DropdownButtonElement;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaRotation;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarElement;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarHandler;
import net.ilexiconn.qubble.client.gui.property.ActionBooleanProperty;
import net.ilexiconn.qubble.client.gui.property.AxisProperty;
import net.ilexiconn.qubble.client.gui.property.RotationProperty;
import net.ilexiconn.qubble.client.gui.property.TransformProperty;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.project.Project;
import net.minecraft.util.EnumFacing;

public class BlockModelSidebarHandler extends SidebarHandler<BlockCuboidWrapper, BlockModelWrapper> {
    private AxisProperty propertyRotationAxis;
    private RotationProperty propertyRotation;
    private TransformProperty propertyDimensionX, propertyDimensionY, propertyDimensionZ;
    private TransformProperty propertyPositionX, propertyPositionY, propertyPositionZ;
    private TransformProperty propertyOriginX, propertyOriginY, propertyOriginZ;
    private ActionBooleanProperty propertyShade;

    @Override
    public void createProperties(QubbleGUI gui, SidebarElement sidebar) {
        this.propertyPositionX = new TransformProperty(gui, value -> this.edit(cuboid -> cuboid.setPosition(value, cuboid.getPositionY(), cuboid.getPositionZ())));
        this.propertyPositionY = new TransformProperty(gui, value -> this.edit(cuboid -> cuboid.setPosition(cuboid.getPositionX(), value, cuboid.getPositionZ())));
        this.propertyPositionZ = new TransformProperty(gui, value -> this.edit(cuboid -> cuboid.setPosition(cuboid.getPositionX(), cuboid.getPositionY(), value)));
        this.propertyDimensionX = new TransformProperty(gui, value -> this.edit(cuboid -> cuboid.setDimensions(value, cuboid.getDimensionY(), cuboid.getDimensionZ())));
        this.propertyDimensionY = new TransformProperty(gui, value -> this.edit(cuboid -> cuboid.setDimensions(cuboid.getDimensionX(), value, cuboid.getDimensionZ())));
        this.propertyDimensionZ = new TransformProperty(gui, value -> this.edit(cuboid -> cuboid.setDimensions(cuboid.getDimensionX(), cuboid.getDimensionY(), value)));
        this.propertyOriginX = new TransformProperty(gui, value -> this.edit(cuboid -> cuboid.setOffset(value, cuboid.getOffsetY(), cuboid.getOffsetZ())));
        this.propertyOriginY = new TransformProperty(gui, value -> this.edit(cuboid -> cuboid.setOffset(cuboid.getOffsetX(), value, cuboid.getOffsetZ())));
        this.propertyOriginZ = new TransformProperty(gui, value -> this.edit(cuboid -> cuboid.setOffset(cuboid.getOffsetX(), cuboid.getOffsetY(), value)));
        this.propertyRotation = new RotationProperty(gui, value -> {
            float angle = (int) (value / 22.5) * 22.5F;
            this.edit(cuboid -> {
                QubbleVanillaRotation rotation = cuboid.getCuboid().getRotation();
                if (rotation == null) {
                    EnumFacing.Axis axis = this.propertyRotationAxis.get();
                    rotation = QubbleVanillaRotation.create(axis, 0.0F, 0.0F, 0.0F, angle);
                    cuboid.getCuboid().setRotation(rotation);
                } else {
                    rotation.setAngle(angle);
                }
            });
            return angle;
        });
        this.propertyRotationAxis = new AxisProperty(gui, value -> this.edit(cuboid -> {
            QubbleVanillaRotation rotation = cuboid.getCuboid().getRotation();
            if (rotation == null) {
                rotation = QubbleVanillaRotation.create(value, 0.0F, 0.0F, 0.0F, 0.0F);
                cuboid.getCuboid().setRotation(rotation);
            } else {
                rotation.setAxis(value);
            }
        }));

        this.propertyShade = new ActionBooleanProperty(gui, value -> this.edit(cuboid -> cuboid.setShade(value)));

        this.addString(this.propertyRotationAxis);
        this.addFloat(this.propertyDimensionX, this.propertyDimensionY, this.propertyDimensionZ);
        this.addFloat(this.propertyPositionX, this.propertyPositionY, this.propertyPositionZ);
        this.addFloat(this.propertyOriginX, this.propertyOriginY, this.propertyOriginZ);
        this.addFloat(this.propertyRotation);
        this.addBoolean(this.propertyShade);
    }

    @Override
    public void update(QubbleGUI gui, Project project) {
    }

    @Override
    protected void initProperties(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
        this.propertyPositionX.init(cuboid.getPositionX());
        this.propertyPositionY.init(cuboid.getPositionY());
        this.propertyPositionZ.init(cuboid.getPositionZ());
        this.propertyDimensionX.init(cuboid.getDimensionX());
        this.propertyDimensionY.init(cuboid.getDimensionY());
        this.propertyDimensionZ.init(cuboid.getDimensionZ());
        this.propertyOriginX.init(cuboid.getOffsetX());
        this.propertyOriginY.init(cuboid.getOffsetY());
        this.propertyOriginZ.init(cuboid.getOffsetZ());
        this.propertyShade.init(cuboid.hasShade());
        QubbleVanillaRotation rotation = cuboid.getCuboid().getRotation();
        if (rotation == null) {
            this.propertyRotationAxis.init(EnumFacing.Axis.X.getName());
            this.propertyRotation.init(0.0F);
        } else {
            this.propertyRotationAxis.init(rotation.getAxis().getName());
            this.propertyRotation.init(rotation.getAngle());
        }
    }

    @Override
    protected void createElements(QubbleGUI gui, SidebarElement sidebar) {
        new LabelElement<>(gui, "Dimensions", 4, 44).withParent(sidebar);
        SliderElement<QubbleGUI, TransformProperty> dimensionX = new SliderElement<>(gui, 4, 53, this.propertyDimensionX, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> dimensionY = new SliderElement<>(gui, 43, 53, this.propertyDimensionY, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> dimensionZ = new SliderElement<>(gui, 82, 53, this.propertyDimensionZ, 0.1F);
        new LabelElement<>(gui, "Position", 4, 69).withParent(sidebar);
        SliderElement<QubbleGUI, TransformProperty> positionX = new SliderElement<>(gui, 4, 78, this.propertyPositionX, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> positionY = new SliderElement<>(gui, 43, 78, this.propertyPositionY, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> positionZ = new SliderElement<>(gui, 82, 78, this.propertyPositionZ, 0.1F);
        new LabelElement<>(gui, "Origin", 4, 94).withParent(sidebar);
        SliderElement<QubbleGUI, TransformProperty> originX = new SliderElement<>(gui, 4, 103, this.propertyOriginX, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> originY = new SliderElement<>(gui, 43, 103, this.propertyOriginY, 0.1F);
        SliderElement<QubbleGUI, TransformProperty> originZ = new SliderElement<>(gui, 82, 103, this.propertyOriginZ, 0.1F);
        new LabelElement<>(gui, "Rotation", 4, 119).withParent(sidebar);
        SliderElement<QubbleGUI, RotationProperty> rotation = new SliderElement<>(gui, 4, 130, 78, this.propertyRotation, 22.5F);
        new LabelElement<>(gui, "Axis", 4, 149).withParent(sidebar);
        DropdownButtonElement<QubbleGUI> rotationAxis = (DropdownButtonElement<QubbleGUI>) new DropdownButtonElement<>(gui, 28, 148, 50, 10, this.propertyRotationAxis).withColorScheme(ColorSchemes.WINDOW);
        new LabelElement<>(gui, "Shade", 4, 169).withParent(sidebar);
        CheckboxElement<QubbleGUI> shade = new CheckboxElement<>(gui, 4, 179, this.propertyShade);
        this.add(dimensionX, dimensionY, dimensionZ);
        this.add(positionX, positionY, positionZ);
        this.add(originX, originY, originZ);
        this.add(rotation);
        this.add(rotationAxis);
        this.add(shade);
    }

    @Override
    protected void initElements(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
    }

    @Override
    public ModelType<BlockCuboidWrapper, BlockModelWrapper> getModelType() {
        return ModelType.BLOCK;
    }
}

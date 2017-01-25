package net.ilexiconn.qubble.client.gui.element.sidebar;

import net.ilexiconn.llibrary.client.gui.element.DropdownButtonElement;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.gui.property.FacingProperty;
import net.ilexiconn.qubble.client.gui.property.TransformProperty;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.minecraft.util.EnumFacing;

public class BlockTextureSidebarHandler extends SidebarHandler<BlockCuboidWrapper, BlockModelWrapper> {
    private DropdownButtonElement<QubbleGUI> facing;
    private SliderElement<QubbleGUI, TransformProperty> minU, minV;
    private SliderElement<QubbleGUI, TransformProperty> maxU, maxV;

    private FacingProperty propertyFacing;
    private TransformProperty propertyMinU, propertyMinV;
    private TransformProperty propertyMaxU, propertyMaxV;

    public BlockTextureSidebarHandler() {
        this.propertyFacing = new FacingProperty();
        this.propertyMinU = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setMinU(this.propertyFacing.get(), value)));
        this.propertyMinV = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setMinV(this.propertyFacing.get(), value)));
        this.propertyMaxU = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setMaxU(this.propertyFacing.get(), value)));
        this.propertyMaxV = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setMaxV(this.propertyFacing.get(), value)));
    }

    @Override
    public void update(QubbleGUI gui, Project project) {
    }

    @Override
    protected void initProperties(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
        this.propertyFacing.set(EnumFacing.NORTH);
        this.propertyMinU.set(cuboid.getMinU(this.propertyFacing.get()));
        this.propertyMinV.set(cuboid.getMinV(this.propertyFacing.get()));
        this.propertyMaxU.set(cuboid.getMaxU(this.propertyFacing.get()));
        this.propertyMaxV.set(cuboid.getMaxV(this.propertyFacing.get()));
    }

    @Override
    protected void createElements(QubbleGUI gui, SidebarElement sidebar) {
        new LabelElement<>(this.gui, "Facing", 4, 38).withParent(sidebar);
        this.facing = (DropdownButtonElement<QubbleGUI>) new DropdownButtonElement<>(this.gui, 4, 48, 116, 14, this.propertyFacing).withColorScheme(ColorSchemes.WINDOW);

        new LabelElement<>(this.gui, "Texture min", 4, 66).withParent(sidebar);
        this.minU = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 4, 75, this.propertyMinU, 0.1F).withParent(sidebar);
        this.minV = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 43, 75, this.propertyMinV, 0.1F).withParent(sidebar);
        new LabelElement<>(this.gui, "Texture max", 4, 90).withParent(sidebar);
        this.maxU = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 4, 99, this.propertyMaxU, 0.1F).withParent(sidebar);
        this.maxV = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 43, 99, this.propertyMaxV, 0.1F).withParent(sidebar);

        this.add(this.facing);
        this.add(this.minU, this.minV);
        this.add(this.maxU, this.maxV);
    }

    @Override
    protected void initElements(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
    }
}

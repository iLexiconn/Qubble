package net.ilexiconn.qubble.client.gui.element.sidebar;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.DropdownButtonElement;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.InputElementBase;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.gui.property.FacingProperty;
import net.ilexiconn.qubble.client.gui.property.TransformProperty;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockTextureSidebarHandler extends SidebarHandler<BlockCuboidWrapper, BlockModelWrapper> {
    private DropdownButtonElement<QubbleGUI> facing;
    private SliderElement<QubbleGUI, TransformProperty> minU, minV;
    private SliderElement<QubbleGUI, TransformProperty> maxU, maxV;
    private InputElementBase<QubbleGUI> texture;

    private FacingProperty propertyFacing;
    private TransformProperty propertyMinU, propertyMinV;
    private TransformProperty propertyMaxU, propertyMaxV;

    public BlockTextureSidebarHandler() {
        this.propertyFacing = new FacingProperty(value -> {
            Project<?, ?> selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null) {
                BlockCuboidWrapper cuboid = (BlockCuboidWrapper) selectedProject.getSelectedCuboid();
                if (cuboid != null) {
                    this.propertyMinU.set(cuboid.getMinU(value));
                    this.propertyMinV.set(cuboid.getMinV(value));
                    this.propertyMaxU.set(cuboid.getMaxU(value));
                    this.propertyMaxV.set(cuboid.getMaxV(value));
                    this.updateSliders();
                }
            }
        });
        this.propertyMinU = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setMinU(this.propertyFacing.get(), value)));
        this.propertyMinV = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setMinV(this.propertyFacing.get(), value)));
        this.propertyMaxU = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setMaxU(this.propertyFacing.get(), value)));
        this.propertyMaxV = new TransformProperty(this, value -> this.edit(cuboid -> cuboid.setMaxV(this.propertyFacing.get(), value)));

        this.addFloat(this.propertyMinU, this.propertyMaxU, this.propertyMinV, this.propertyMaxV);
    }

    @Override
    public void update(QubbleGUI gui, Project<BlockCuboidWrapper, BlockModelWrapper> project) {
        this.texture.clearText();
        if (project != null && project.getSelectedCuboid() != null) {
            BlockCuboidWrapper selectedCuboid = project.getSelectedCuboid();
            String texture = selectedCuboid.getCuboid().getFace(this.propertyFacing.get()).getTexture();
            this.texture.writeText(texture != null ? texture : "");
        }
    }

    @Override
    protected void initProperties(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
        this.propertyMinU.set(cuboid.getMinU(this.propertyFacing.get()));
        this.propertyMinV.set(cuboid.getMinV(this.propertyFacing.get()));
        this.propertyMaxU.set(cuboid.getMaxU(this.propertyFacing.get()));
        this.propertyMaxV.set(cuboid.getMaxV(this.propertyFacing.get()));
    }

    @Override
    protected void createElements(QubbleGUI gui, SidebarElement sidebar) {
        new LabelElement<>(this.gui, "Facing", 4, 38).withParent(sidebar);
        this.facing = (DropdownButtonElement<QubbleGUI>) new DropdownButtonElement<>(this.gui, 4, 48, 116, 14, this.propertyFacing).withColorScheme(ColorSchemes.WINDOW);

        new LabelElement<>(this.gui, "Texture", 4, 66).withParent(sidebar);
        new LabelElement<>(this.gui, "Min UV", 4, 92).withParent(sidebar);
        this.minU = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 4, 101, this.propertyMinU, 0.1F).withParent(sidebar);
        this.minV = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 43, 101, this.propertyMinV, 0.1F).withParent(sidebar);
        new LabelElement<>(this.gui, "Max UV", 4, 116).withParent(sidebar);
        this.maxU = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 4, 125, this.propertyMaxU, 0.1F).withParent(sidebar);
        this.maxV = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 43, 125, this.propertyMaxV, 0.1F).withParent(sidebar);

        this.texture = (InputElementBase<QubbleGUI>) new InputElement<>(this.gui, 4, 76, 104, "", (i) -> {
        }).withParent(sidebar);
        new ButtonElement<>(this.gui, "...", 108, 76, 12, 12, (button) -> {
            Project<?, ?> selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCuboid() != null) {
                this.openSelectTextureWindow("Select Texture", (BlockModelWrapper) selectedProject.getModel());
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT).withParent(sidebar);

        this.add(this.texture);

        this.add(this.facing);
        this.add(this.minU, this.minV);
        this.add(this.maxU, this.maxV);
    }

    @Override
    protected void initElements(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
    }

    private void openSelectTextureWindow(String name, BlockModelWrapper wrapper) {
        WindowElement<QubbleGUI> selectTextureWindow = new WindowElement<>(this.gui, name, 100, 100);
        List<String> textures = new ArrayList<>();
        textures.add("none");
        Project<BlockCuboidWrapper, BlockModelWrapper> project = (Project<BlockCuboidWrapper, BlockModelWrapper>) this.gui.getSelectedProject();
        for (Map.Entry<String, ModelTexture> entry : wrapper.getTextures().entrySet()) {
            textures.add(entry.getKey());
        }
        if (project != null) {
            selectTextureWindow.addElement(new ListElement<>(this.gui, 2, 16, 96, 82, textures, (list) -> {
                BlockCuboidWrapper cuboidWrapper = project.getSelectedCuboid();
                if (cuboidWrapper != null) {
                    QubbleVanillaCuboid cuboid = cuboidWrapper.getCuboid();
                    QubbleVanillaFace face = cuboid.getFace(this.propertyFacing.get());
                    if (list.getSelectedIndex() != 0) {
                        face.setTexture(list.getSelectedEntry());
                    } else {
                        face.setTexture(null);
                    }
                    project.setSaved(false);
                    wrapper.rebuildModel();
                }
                this.gui.removeElement(selectTextureWindow);
                return true;
            }));
            this.gui.addElement(selectTextureWindow);
        }
    }
}

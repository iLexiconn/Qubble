package net.ilexiconn.qubble.client.gui.element.sidebar.block;

import net.ilexiconn.llibrary.client.gui.element.DropdownButtonElement;
import net.ilexiconn.llibrary.client.gui.element.InputElementBase;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarElement;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarHandler;
import net.ilexiconn.qubble.client.gui.property.FacingProperty;
import net.ilexiconn.qubble.client.gui.property.TransformProperty;
import net.ilexiconn.qubble.client.model.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.minecraft.util.EnumFacing;

public class BlockTextureSidebarHandler extends SidebarHandler<BlockCuboidWrapper, BlockModelWrapper> {
    private DropdownButtonElement<QubbleGUI> facing;
    private SliderElement<QubbleGUI, TransformProperty> minU, minV;
    private SliderElement<QubbleGUI, TransformProperty> maxU, maxV;
    private InputElementBase<QubbleGUI> texture;
    private InputElementBase<QubbleGUI> particleTexture;

    private FacingProperty propertyFacing;
    private TransformProperty propertyMinU, propertyMinV;
    private TransformProperty propertyMaxU, propertyMaxV;

    public BlockTextureSidebarHandler() {
        this.propertyFacing = new FacingProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null) {
                BlockCuboidWrapper cuboid = selectedProject.getSelectedCuboid(ModelType.BLOCK);
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
    public void update(QubbleGUI gui, Project project) {
        /*this.texture.clearText();
        this.particleTexture.clearText();
        if (project != null) {
            BlockCuboidWrapper selectedCuboid = project.getSelectedCuboid();
            if (selectedCuboid != null) {
                String texture = selectedCuboid.getCuboid().getFace(this.propertyFacing.get()).getTexture();
                this.texture.writeText(texture != null ? texture : "");
            }
            ModelTexture particle = project.getModel().getTexture("particle");
            this.particleTexture.writeText(particle != null ? particle.getName() : "");
        }
        this.texture.setCursorPositionEnd();
        this.particleTexture.setCursorPositionEnd();*/
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
        /*new LabelElement<>(this.gui, "Facing", 4, 38).withParent(sidebar);
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
            Project<BlockCuboidWrapper, BlockModelWrapper> selectedProject = (Project<BlockCuboidWrapper, BlockModelWrapper>) this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCuboid() != null) {
                BlockModelWrapper model = selectedProject.getModel();
                List<String> textures = new ArrayList<>();
                for (Map.Entry<String, ModelTexture> entry : model.getTextures().entrySet()) {
                    textures.add(entry.getKey());
                }
                this.openSelectTextureWindow("Select Texture", textures, list -> {
                    BlockCuboidWrapper cuboidWrapper = selectedProject.getSelectedCuboid();
                    if (cuboidWrapper != null) {
                        QubbleVanillaCuboid cuboid = cuboidWrapper.getCuboid();
                        QubbleVanillaFace face = cuboid.getFace(this.propertyFacing.get());
                        if (list.getSelectedIndex() != 0) {
                            face.setTexture(list.getSelectedEntry());
                        } else {
                            face.setTexture(null);
                        }
                        selectedProject.setSaved(false);
                        model.rebuildModel();
                    }
                });
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT).withParent(sidebar);

        new LabelElement<>(this.gui, "Particle Texture", 4, 144).withParent(sidebar);
        this.particleTexture = (InputElementBase<QubbleGUI>) new InputElement<>(this.gui, 4, 154, 104, "", (i) -> {
        }).withParent(sidebar);
        new ButtonElement<>(this.gui, "...", 108, 154, 12, 12, (button) -> {
            Project<BlockCuboidWrapper, BlockModelWrapper> selectedProject = (Project<BlockCuboidWrapper, BlockModelWrapper>) this.gui.getSelectedProject();
            if (selectedProject != null) {
                BlockModelWrapper model = selectedProject.getModel();
                this.openSelectTextureWindow("Select Particle", QubbleGUI.getFiles(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, ".png"), list -> {
                    if (list.getSelectedIndex() > 0) {
                        String entry = list.getSelectedEntry();
                        ModelTexture texture = new ModelTexture(new File(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, entry + ".png"), entry);
                        model.setTexture("particle", texture);
                    } else {
                        model.setTexture("particle", null);
                    }
                    selectedProject.setSaved(false);
                    model.rebuildModel();
                });
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT).withParent(sidebar);

        this.add(this.texture, this.particleTexture);

        this.add(this.facing);
        this.add(this.minU, this.minV);
        this.add(this.maxU, this.maxV);*/

        int x = 0;
        int y = 0;

        for (EnumFacing facing : EnumFacing.VALUES) {
            float renderX = x * (BlockFaceElement.SIZE + 14) + 12;
            float renderY = y * (BlockFaceElement.SIZE + 16) + 48;

            new BlockFaceElement(this.gui, facing, renderX, renderY).withParent(sidebar);

            if (++x >= 2) {
                x = 0;
                y++;
            }
        }
    }

    @Override
    protected void initElements(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
    }

    @Override
    public ModelType<BlockCuboidWrapper, BlockModelWrapper> getModelType() {
        return ModelType.BLOCK;
    }
}

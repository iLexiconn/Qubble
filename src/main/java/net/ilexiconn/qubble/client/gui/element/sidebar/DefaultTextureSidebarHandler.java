package net.ilexiconn.qubble.client.gui.element.sidebar;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.InputElementBase;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.GUIHelper;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.gui.property.DimensionProperty;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.project.Project;
import net.ilexiconn.qubble.client.project.action.SetMirrorAction;
import net.ilexiconn.qubble.client.project.action.SetTextureAction;

import java.io.File;

public class DefaultTextureSidebarHandler extends SidebarHandler<DefaultCuboidWrapper, DefaultModelWrapper> {
    private SliderElement<QubbleGUI, DimensionProperty> textureX, textureY;
    private SliderElement<QubbleGUI, DimensionProperty> textureWidth, textureHeight;
    private ButtonElement<QubbleGUI> mirror;
    private InputElementBase<QubbleGUI> texture;
    private InputElementBase<QubbleGUI> overlayTexture;

    public DimensionProperty propertyTextureX, propertyTextureY;
    private DimensionProperty propertyTextureWidth, propertyTextureHeight;

    @Override
    public void createProperties(QubbleGUI gui, SidebarElement sidebar) {
        this.propertyTextureX = new DimensionProperty(gui, value -> this.edit(cuboid -> cuboid.setTexture(value, cuboid.getTextureY())));
        this.propertyTextureY = new DimensionProperty(gui, value -> this.edit(cuboid -> cuboid.setTexture(cuboid.getTextureX(), value)));
        this.propertyTextureWidth = new DimensionProperty(gui, value -> this.editModel(model -> model.setTextureWidth(value)));
        this.propertyTextureHeight = new DimensionProperty(gui, value -> this.editModel(model -> model.setTextureHeight(value)));
    }

    @Override
    public void update(QubbleGUI gui, Project project) {
        ModelTexture texture = project != null ? project.getModel().getBaseTexture() : null;
        ModelTexture overlayTexture = project != null ? project.getModel().getOverlayTexture() : null;
        this.texture.clearText();
        this.texture.writeText(texture != null ? texture.getName() : "");
        this.texture.setCursorPositionEnd();
        this.overlayTexture.clearText();
        this.overlayTexture.writeText(overlayTexture != null ? overlayTexture.getName() : "");
        this.overlayTexture.setCursorPositionEnd();
    }

    @Override
    protected void initProperties(DefaultModelWrapper model, DefaultCuboidWrapper cuboid) {
        this.propertyTextureX.init(cuboid.getTextureX());
        this.propertyTextureY.init(cuboid.getTextureY());
        this.propertyTextureWidth.init(model.getTextureWidth());
        this.propertyTextureHeight.init(model.getTextureHeight());
    }

    @Override
    protected void createElements(QubbleGUI gui, SidebarElement sidebar) {
        new LabelElement<>(gui, "Texture offset", 4, 44).withParent(sidebar);
        this.textureX = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(gui, 4, 53, this.propertyTextureX, 1).withParent(sidebar);
        this.textureY = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(gui, 43, 53, this.propertyTextureY, 1).withParent(sidebar);
        this.mirror = (ButtonElement<QubbleGUI>) new ButtonElement<>(gui, "Mirror", 82, 53, 38, 12, (button) -> {
            if (button.getColorScheme() == ColorSchemes.TOGGLE_OFF) {
                button.withColorScheme(ColorSchemes.TOGGLE_ON);
            } else {
                button.withColorScheme(ColorSchemes.TOGGLE_OFF);
            }
            Project selectedProject = gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCuboid() != null) {
                DefaultCuboidWrapper selectedCuboid = selectedProject.getSelectedCuboid(this.getModelType());
                selectedCuboid.setTextureMirrored(button.getColorScheme() != ColorSchemes.TOGGLE_OFF);
                try {
                    selectedProject.perform(new SetMirrorAction(gui, selectedCuboid.getName(), button.getColorScheme() != ColorSchemes.TOGGLE_OFF));
                } catch (Exception e) {
                    GUIHelper.INSTANCE.error(this.gui, 200, "Failed to set mirror!", e);
                    e.printStackTrace();
                }
            }
            return true;
        }).withParent(sidebar);
        new LabelElement<>(gui, "Texture", 4, 69).withParent(sidebar);
        this.texture = (InputElementBase<QubbleGUI>) new InputElement<>(gui, 4, 78, 104, "", (i) -> {
        }).withParent(sidebar);
        new ButtonElement<>(gui, "...", 108, 78, 12, 12, (button) -> {
            if (gui.getSelectedProject() != null) {
                this.openTextureSelection("Select Texture", ModelTexture.BASE);
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT).withParent(sidebar);
        new LabelElement<>(gui, "Texture overlay", 4, 94).withParent(sidebar);
        this.overlayTexture = (InputElementBase<QubbleGUI>) new InputElement<>(gui, 4, 103, 104, "", (i) -> {
        }).withParent(sidebar);
        new ButtonElement<>(gui, "...", 108, 103, 12, 12, (button) -> {
            if (gui.getSelectedProject() != null) {
                this.openTextureSelection("Select Overlay Texture", ModelTexture.OVERLAY);
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT).withParent(sidebar);
        new LabelElement<>(gui, "Texture size", 4, 120).withParent(sidebar);
        this.textureWidth = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(gui, 4, 130, this.propertyTextureWidth, 1).withParent(sidebar);
        this.textureHeight = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(gui, 43, 130, this.propertyTextureHeight, 1).withParent(sidebar);
        this.add(this.textureX, this.textureY);
        this.add(this.textureWidth, this.textureHeight);
        this.add(this.mirror);
        this.add(this.texture, this.overlayTexture);
    }

    @Override
    protected void initElements(DefaultModelWrapper model, DefaultCuboidWrapper cuboid) {
        this.mirror.withColorScheme(cuboid.isTextureMirrored() ? ColorSchemes.TOGGLE_ON : ColorSchemes.TOGGLE_OFF);
    }

    @Override
    public ModelType<DefaultCuboidWrapper, DefaultModelWrapper> getModelType() {
        return ModelType.DEFAULT;
    }

    private void openTextureSelection(String title, String textureName) {
        GUIHelper.INSTANCE.selection(this.gui, title, QubbleGUI.getFiles(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, ".png"), selection -> {
            Project project = this.gui.getSelectedProject();
            if (project != null) {
                ModelTexture texture = null;
                if (selection != null) {
                    texture = new ModelTexture(new File(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, selection + ".png"), selection);
                }
                try {
                    project.perform(new SetTextureAction(this.gui, textureName, texture));
                } catch (Exception e) {
                    GUIHelper.INSTANCE.error(this.gui, 200, "Failed to set texture!", e);
                    e.printStackTrace();
                }
            }
        });
    }
}

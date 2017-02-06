package net.ilexiconn.qubble.client.gui.element.sidebar;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.InputElementBase;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.gui.property.DimensionProperty;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

import java.io.File;
import java.util.List;

public class DefaultTextureSidebarHandler extends SidebarHandler<DefaultCuboidWrapper, DefaultModelWrapper> {
    private SliderElement<QubbleGUI, DimensionProperty> textureX, textureY;
    private SliderElement<QubbleGUI, DimensionProperty> textureWidth, textureHeight;
    private ButtonElement<QubbleGUI> mirror;
    private InputElementBase<QubbleGUI> texture;
    private InputElementBase<QubbleGUI> overlayTexture;

    private DimensionProperty propertyTextureX, propertyTextureY;
    private DimensionProperty propertyTextureWidth, propertyTextureHeight;

    public DefaultTextureSidebarHandler() {
        this.propertyTextureX = new DimensionProperty(this, value -> this.edit(cuboid -> cuboid.setTexture(value, cuboid.getTextureY())));
        this.propertyTextureY = new DimensionProperty(this, value -> this.edit(cuboid -> cuboid.setTexture(cuboid.getTextureX(), value)));
        this.propertyTextureWidth = new DimensionProperty(this, value -> this.editModel(model -> model.setTextureWidth(value)));
        this.propertyTextureHeight = new DimensionProperty(this, value -> this.editModel(model -> model.setTextureHeight(value)));
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
        this.propertyTextureX.set(cuboid.getTextureX());
        this.propertyTextureY.set(cuboid.getTextureY());
        this.propertyTextureWidth.set(model.getTextureWidth());
        this.propertyTextureHeight.set(model.getTextureHeight());
    }

    @Override
    protected void createElements(QubbleGUI gui, SidebarElement sidebar) {
        new LabelElement<>(this.gui, "Texture offset", 4, 44).withParent(sidebar);
        this.textureX = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 4, 53, this.propertyTextureX, 1).withParent(sidebar);
        this.textureY = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 43, 53, this.propertyTextureY, 1).withParent(sidebar);
        this.mirror = (ButtonElement<QubbleGUI>) new ButtonElement<>(this.gui, "Mirror", 82, 53, 38, 12, (button) -> {
            if (button.getColorScheme() == ColorSchemes.TOGGLE_OFF) {
                button.withColorScheme(ColorSchemes.TOGGLE_ON);
            } else {
                button.withColorScheme(ColorSchemes.TOGGLE_OFF);
            }
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCuboid() != null) {
                ModelWrapper model = selectedProject.getModel();
                DefaultCuboidWrapper selectedCube = (DefaultCuboidWrapper) selectedProject.getSelectedCuboid();
                selectedCube.setTextureMirrored(button.getColorScheme() != ColorSchemes.TOGGLE_OFF);
                model.rebuildCuboid(selectedCube);
                selectedProject.setSaved(false);
            }
            return true;
        }).withParent(sidebar);
        new LabelElement<>(this.gui, "Texture", 4, 69).withParent(sidebar);
        this.texture = (InputElementBase<QubbleGUI>) new InputElement<>(this.gui, 4, 78, 104, "", (i) -> {
        }).withParent(sidebar);
        new ButtonElement<>(this.gui, "...", 108, 78, 12, 12, (button) -> {
            if (this.gui.getSelectedProject() != null) {
                this.openSelectTextureWindow("Select Texture", true);
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT).withParent(sidebar);
        new LabelElement<>(this.gui, "Texture overlay", 4, 94).withParent(sidebar);
        this.overlayTexture = (InputElementBase<QubbleGUI>) new InputElement<>(this.gui, 4, 103, 104, "", (i) -> {
        }).withParent(sidebar);
        new ButtonElement<>(this.gui, "...", 108, 103, 12, 12, (button) -> {
            if (this.gui.getSelectedProject() != null) {
                this.openSelectTextureWindow("Select Overlay Texture", false);
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT).withParent(sidebar);
        new LabelElement<>(this.gui, "Texture size", 4, 120).withParent(sidebar);
        this.textureWidth = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 4, 130, this.propertyTextureWidth, 1).withParent(sidebar);
        this.textureHeight = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 43, 130, this.propertyTextureHeight, 1).withParent(sidebar);
        this.add(this.textureX, this.textureY);
        this.add(this.textureWidth, this.textureHeight);
        this.add(this.mirror);
        this.add(this.texture, this.overlayTexture);
    }

    @Override
    protected void initElements(DefaultModelWrapper model, DefaultCuboidWrapper cuboid) {
        this.mirror.withColorScheme(cuboid.isTextureMirrored() ? ColorSchemes.TOGGLE_ON : ColorSchemes.TOGGLE_OFF);
    }

    private void openSelectTextureWindow(String name, boolean base) {
        WindowElement<QubbleGUI> selectTextureWindow = new WindowElement<>(this.gui, name, 100, 100);
        List<String> files = QubbleGUI.getFiles(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, ".png");
        files.add(0, "None");
        selectTextureWindow.addElement(new ListElement<>(this.gui, 2, 16, 96, 82, files, (list) -> {
            Project project = this.gui.getSelectedProject();
            if (project != null) {
                ModelTexture texture = null;
                if (list.getSelectedIndex() != 0) {
                    texture = new ModelTexture(new File(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, list.getSelectedEntry() + ".png"), list.getSelectedEntry());
                }
                ModelWrapper model = project.getModel();
                if (base) {
                    model.setBaseTexture(texture);
                } else {
                    model.setOverlayTexture(texture);
                }
                project.setSaved(false);
                this.gui.removeElement(selectTextureWindow);
            }
            return true;
        }));
        this.gui.addElement(selectTextureWindow);
    }
}

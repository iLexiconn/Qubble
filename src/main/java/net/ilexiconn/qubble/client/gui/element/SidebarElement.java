package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.InputElementBase;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.ilexiconn.llibrary.client.gui.element.PropertyInputElement;
import net.ilexiconn.llibrary.client.gui.element.SliderElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.gui.property.DimensionProperty;
import net.ilexiconn.qubble.client.gui.property.RotationProperty;
import net.ilexiconn.qubble.client.gui.property.TransformProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SidebarElement extends Element<QubbleGUI> {
    private InputElementBase<QubbleGUI> nameInput;
    private SliderElement<QubbleGUI, DimensionProperty> dimensionX, dimensionY, dimensionZ;
    private SliderElement<QubbleGUI, TransformProperty> positionX, positionY, positionZ;
    private SliderElement<QubbleGUI, TransformProperty> offsetX, offsetY, offsetZ;
    private SliderElement<QubbleGUI, TransformProperty> scaleX, scaleY, scaleZ;
    private SliderElement<QubbleGUI, RotationProperty> rotationX, rotationY, rotationZ;
    private SliderElement<QubbleGUI, DimensionProperty> textureX, textureY;
    private SliderElement<QubbleGUI, DimensionProperty> textureWidth, textureHeight;
    private ButtonElement<QubbleGUI> mirror;
    private InputElementBase<QubbleGUI> texture;
    private InputElementBase<QubbleGUI> overlayTexture;

    private DimensionProperty propertyDimensionX, propertyDimensionY, propertyDimensionZ;
    private TransformProperty propertyPositionX, propertyPositionY, propertyPositionZ;
    private TransformProperty propertyOffsetX, propertyOffsetY, propertyOffsetZ;
    private TransformProperty propertyScaleX, propertyScaleY, propertyScaleZ;
    private RotationProperty propertyRotationX, propertyRotationY, propertyRotationZ;
    private DimensionProperty propertyTextureX, propertyTextureY;
    private DimensionProperty propertyTextureWidth, propertyTextureHeight;

    private boolean initialized;

    public SidebarElement(QubbleGUI gui) {
        super(gui, gui.width - 122, 20, 122, gui.height - 20);

        this.createDimensionProperties();
        this.createPositionProperties();
        this.createOffsetProperties();
        this.createScaleProperties();
        this.createRotationProperties();
        this.createTextureProperties();
    }

    private void createTextureProperties() {
        this.propertyTextureX = new DimensionProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setTexture(value, selectedCube.getTextureY());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyTextureY = new DimensionProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setTexture(selectedCube.getTextureX(), value);
                this.gui.getModelView().updatePart(selectedCube);
            }
        });

        this.propertyTextureWidth = new DimensionProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null) {
                QubbleModel model = selectedProject.getModel();
                model.setTextureWidth(value);
                this.gui.getModelView().updateModel();
            }
        });
        this.propertyTextureHeight = new DimensionProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null) {
                QubbleModel model = selectedProject.getModel();
                model.setTextureHeight(value);
                this.gui.getModelView().updateModel();
            }
        });
    }

    private void createRotationProperties() {
        this.propertyRotationX = new RotationProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setRotation(value, selectedCube.getRotationY(), selectedCube.getRotationZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyRotationY = new RotationProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setRotation(selectedCube.getRotationX(), value, selectedCube.getRotationZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyRotationZ = new RotationProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setRotation(selectedCube.getRotationX(), selectedCube.getRotationY(), value);
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
    }

    private void createScaleProperties() {
        this.propertyScaleX = new TransformProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setScale(value, selectedCube.getScaleY(), selectedCube.getScaleZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyScaleY = new TransformProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setScale(selectedCube.getScaleX(), value, selectedCube.getScaleZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyScaleZ = new TransformProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setScale(selectedCube.getScaleX(), selectedCube.getScaleY(), value);
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
    }

    private void createOffsetProperties() {
        this.propertyOffsetX = new TransformProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setOffset(value, selectedCube.getOffsetY(), selectedCube.getOffsetZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyOffsetY = new TransformProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setOffset(selectedCube.getOffsetX(), value, selectedCube.getOffsetZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyOffsetZ = new TransformProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setOffset(selectedCube.getOffsetX(), selectedCube.getOffsetY(), value);
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
    }

    private void createPositionProperties() {
        this.propertyPositionX = new TransformProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setPosition(value, selectedCube.getPositionY(), selectedCube.getPositionZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyPositionY = new TransformProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setPosition(selectedCube.getPositionX(), value, selectedCube.getPositionZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyPositionZ = new TransformProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setPosition(selectedCube.getPositionX(), selectedCube.getPositionY(), value);
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
    }

    private void createDimensionProperties() {
        this.propertyDimensionX = new DimensionProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setDimensions(value, selectedCube.getDimensionY(), selectedCube.getDimensionZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyDimensionY = new DimensionProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setDimensions(selectedCube.getDimensionX(), value, selectedCube.getDimensionZ());
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
        this.propertyDimensionZ = new DimensionProperty(value -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setDimensions(selectedCube.getDimensionX(), selectedCube.getDimensionY(), value);
                this.gui.getModelView().updatePart(selectedCube);
            }
        });
    }

    @Override
    public void init() {
        this.initFields();
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null && selectedProject.getSelectedCube() != null) {
            this.populateFields(selectedProject.getModel(), selectedProject.getSelectedCube());
        } else {
            this.clearFields();
        }
    }

    @Override
    public void update() {
        if (this.initialized) {
            Project selectedProject = this.gui.getSelectedProject();
            switch (this.gui.getMode()) {
                case MODEL: {
                    break;
                }
                case TEXTURE: {
                    ModelTexture texture = selectedProject != null ? selectedProject.getBaseTexture() : null;
                    ModelTexture overlayTexture = selectedProject != null ? selectedProject.getOverlayTexture() : null;
                    this.texture.clearText();
                    this.texture.writeText(texture != null ? texture.getName() : "");
                    this.overlayTexture.clearText();
                    this.overlayTexture.writeText(overlayTexture != null ? overlayTexture.getName() : "");
                    break;
                }
                case ANIMATE: {
                    break;
                }
            }
        }
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), LLibrary.CONFIG.getPrimaryColor());
        this.drawRectangle(this.getPosX(), this.getPosY(), 2, this.getHeight(), LLibrary.CONFIG.getAccentColor());
    }

    public <T extends Element<QubbleGUI>> T getElement(Class<T> type, int index) {
        int currentIndex = 0;
        for (Element<QubbleGUI> element : this.getChildren()) {
            if (type.isAssignableFrom(element.getClass())) {
                if (currentIndex == index) {
                    return (T) element;
                } else {
                    currentIndex++;
                }
            }
        }
        return null;
    }

    public void populateFields(QubbleModel model, QubbleCuboid cube) {
        this.nameInput.clearText();
        this.nameInput.writeText(cube.getName());
        this.nameInput.setEditable(true);
        switch (this.gui.getMode()) {
            case MODEL: {
                this.propertyDimensionX.set(cube.getDimensionX());
                this.propertyDimensionY.set(cube.getDimensionY());
                this.propertyDimensionZ.set(cube.getDimensionZ());
                this.dimensionX.setEditable(true);
                this.dimensionY.setEditable(true);
                this.dimensionZ.setEditable(true);

                this.propertyPositionX.set(cube.getPositionX());
                this.propertyPositionY.set(cube.getPositionY());
                this.propertyPositionZ.set(cube.getPositionZ());
                this.positionX.setEditable(true);
                this.positionY.setEditable(true);
                this.positionZ.setEditable(true);

                this.propertyOffsetX.set(cube.getOffsetX());
                this.propertyOffsetY.set(cube.getOffsetY());
                this.propertyOffsetZ.set(cube.getOffsetZ());
                this.offsetX.setEditable(true);
                this.offsetY.setEditable(true);
                this.offsetZ.setEditable(true);

                this.propertyScaleX.set(cube.getScaleX());
                this.propertyScaleY.set(cube.getScaleY());
                this.propertyScaleZ.set(cube.getScaleZ());
                this.scaleX.setEditable(true);
                this.scaleY.setEditable(true);
                this.scaleZ.setEditable(true);

                this.propertyRotationX.set(cube.getRotationX());
                this.propertyRotationY.set(cube.getRotationY());
                this.propertyRotationZ.set(cube.getRotationZ());
                this.rotationX.setEditable(true);
                this.rotationY.setEditable(true);
                this.rotationZ.setEditable(true);
                break;
            }
            case TEXTURE: {
                this.propertyTextureX.set(cube.getTextureX());
                this.propertyTextureY.set(cube.getTextureY());
                this.textureX.setEditable(true);
                this.textureY.setEditable(true);

                this.mirror.withColorScheme(cube.isTextureMirrored() ? ColorSchemes.TOGGLE_ON : ColorSchemes.TOGGLE_OFF);
                this.mirror.setEnabled(true);
                this.propertyTextureWidth.set(model.getTextureWidth());
                this.textureWidth.setEditable(true);
                this.propertyTextureHeight.set(model.getTextureHeight());
                this.textureHeight.setEditable(true);
                break;
            }
            case ANIMATE: {
                break;
            }
        }

        int i = 0;
        SliderElement slider;
        while ((slider = this.getElement(SliderElement.class, i)) != null) {
            ((PropertyInputElement) slider.getValueInput()).readValue();
            i++;
        }
    }

    public void clearFields() {
        this.nameInput.clearText();
        this.nameInput.setEditable(false);
        switch (this.gui.getMode()) {
            case MODEL: {
                break;
            }
            case TEXTURE: {
                this.texture.clearText();
                this.texture.setEditable(false);
                this.overlayTexture.clearText();
                this.overlayTexture.setEditable(false);
                break;
            }
            case ANIMATE: {
                break;
            }
        }
        this.propertyDimensionX.set(0);
        this.propertyDimensionY.set(0);
        this.propertyDimensionZ.set(0);
        this.propertyPositionX.set(0);
        this.propertyPositionY.set(0);
        this.propertyPositionZ.set(0);
        this.propertyOffsetX.set(0);
        this.propertyOffsetY.set(0);
        this.propertyOffsetZ.set(0);
        this.propertyScaleX.set(0);
        this.propertyScaleY.set(0);
        this.propertyScaleZ.set(0);
        this.propertyRotationX.set(0);
        this.propertyRotationY.set(0);
        this.propertyRotationZ.set(0);
        this.propertyTextureWidth.set(0);
        this.propertyTextureHeight.set(0);
        int i = 0;
        SliderElement slider;
        while ((slider = this.getElement(SliderElement.class, i)) != null) {
            slider.setEditable(false);
            ((PropertyInputElement) slider.getValueInput()).readValue();
            i++;
        }
    }

    public void initFields() {
        this.getChildren().clear();
        new LabelElement<>(this.gui, "Selected cube", 4, 10).withParent(this);
        this.nameInput = (InputElementBase<QubbleGUI>) new InputElement<>(this.gui, 4, 19, 116, "", inputElement -> {
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setName(inputElement.getText());
                this.gui.getModelView().updatePart(selectedCube);
            }
        }).withParent(this);
        switch (this.gui.getMode()) {
            case MODEL: {
                this.addDimensionElements();
                this.addPositionElements();
                this.addOffsetElements();
                this.addScaleElements();
                this.addRotationElements();
                break;
            }
            case TEXTURE: {
                this.addTextureElements();
                this.addTextureFileElements();
                this.addTextureSizeElements();
                break;
            }
            case ANIMATE: {
                break;
            }
        }
        this.initialized = true;
    }

    private void addTextureElements() {
        new LabelElement<>(this.gui, "Texture offset", 4, 44).withParent(this);
        this.textureX = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 4, 53, this.propertyTextureX, 1).withParent(this);
        this.textureY = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 43, 53, this.propertyTextureY, 1).withParent(this);
        this.mirror = (ButtonElement<QubbleGUI>) new ButtonElement<>(this.gui, "Mirror", 82, 53, 38, 12, (button) -> {
            if (button.getColorScheme() == ColorSchemes.TOGGLE_OFF) {
                button.withColorScheme(ColorSchemes.TOGGLE_ON);
            } else {
                button.withColorScheme(ColorSchemes.TOGGLE_OFF);
            }
            Project selectedProject = this.gui.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCube() != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                selectedCube.setTextureMirrored(button.getColorScheme() != ColorSchemes.TOGGLE_OFF);
                this.gui.getModelView().updatePart(selectedCube);
            }
            return true;
        }).withParent(this);
    }

    private void addTextureFileElements() {
        new LabelElement<>(this.gui, "Texture", 4, 69).withParent(this);
        this.texture = (InputElementBase<QubbleGUI>) new InputElement<>(this.gui, 4, 78, 104, "", (i) -> {
        }).withParent(this);
        new ButtonElement<>(this.gui, "...", 108, 78, 12, 12, (button) -> {
            if (this.gui.getSelectedProject() != null) {
                this.openSelectTextureWindow("Select Texture", true);
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT).withParent(this);
        new LabelElement<>(this.gui, "Texture overlay", 4, 94).withParent(this);
        this.overlayTexture = (InputElementBase<QubbleGUI>) new InputElement<>(this.gui, 4, 103, 104, "", (i) -> {
        }).withParent(this);
        new ButtonElement<>(this.gui, "...", 108, 103, 12, 12, (button) -> {
            if (this.gui.getSelectedProject() != null) {
                this.openSelectTextureWindow("Select Overlay Texture", false);
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT).withParent(this);
    }

    private void addTextureSizeElements() {
        new LabelElement<>(this.gui, "Texture size", 4, 120).withParent(this);
        this.textureWidth = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 4, 130, this.propertyTextureWidth, 1).withParent(this);
        this.textureHeight = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 43, 130, this.propertyTextureHeight, 1).withParent(this);
    }

    private void addDimensionElements() {
        new LabelElement<>(this.gui, "Dimensions", 4, 44).withParent(this);
        this.dimensionX = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 4, 53, this.propertyDimensionX, 1).withParent(this);
        this.dimensionY = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 43, 53, this.propertyDimensionY, 1).withParent(this);
        this.dimensionZ = (SliderElement<QubbleGUI, DimensionProperty>) new SliderElement<>(this.gui, 82, 53, this.propertyDimensionZ, 1).withParent(this);
    }

    private void addPositionElements() {
        new LabelElement<>(this.gui, "Position", 4, 69).withParent(this);
        this.positionX = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 4, 78, this.propertyPositionX, 0.1F).withParent(this);
        this.positionY = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 43, 78, this.propertyPositionY, 0.1F).withParent(this);
        this.positionZ = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 82, 78, this.propertyPositionZ, 0.1F).withParent(this);
    }

    private void addOffsetElements() {
        new LabelElement<>(this.gui, "Offset", 4, 94).withParent(this);
        this.offsetX = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 4, 103, this.propertyOffsetX, 0.1F).withParent(this);
        this.offsetY = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 43, 103, this.propertyOffsetY, 0.1F).withParent(this);
        this.offsetZ = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 82, 103, this.propertyOffsetZ, 0.1F).withParent(this);
    }

    private void addScaleElements() {
        new LabelElement<>(this.gui, "Scale", 4, 119).withParent(this);
        this.scaleX = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 4, 128, this.propertyScaleX, 0.1F).withParent(this);
        this.scaleY = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 43, 128, this.propertyScaleY, 0.1F).withParent(this);
        this.scaleZ = (SliderElement<QubbleGUI, TransformProperty>) new SliderElement<>(this.gui, 82, 128, this.propertyScaleZ, 0.1F).withParent(this);
    }

    private void addRotationElements() {
        new LabelElement<>(this.gui, "Rotation", 4, 144).withParent(this);
        this.rotationX = (SliderElement<QubbleGUI, RotationProperty>) new SliderElement<>(this.gui, 4, 153, 78, this.propertyRotationX, 0.1F).withParent(this);
        this.rotationY = (SliderElement<QubbleGUI, RotationProperty>) new SliderElement<>(this.gui, 4, 166, 78, this.propertyRotationY, 0.1F).withParent(this);
        this.rotationZ = (SliderElement<QubbleGUI, RotationProperty>) new SliderElement<>(this.gui, 4, 179, 78, this.propertyRotationZ, 0.1F).withParent(this);
    }

    private void openSelectTextureWindow(String name, boolean base) {
        WindowElement<QubbleGUI> selectTextureWindow = new WindowElement<>(this.gui, name, 100, 100);
        List<String> files = this.getFiles(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, ".png");
        files.add(0, "None");
        selectTextureWindow.addElement(new ListElement<>(this.gui, 2, 16, 96, 82, files, (list) -> {
            Project project = this.gui.getSelectedProject();
            if (project != null) {
                ModelTexture texture = null;
                if (!list.getSelectedEntry().equals("None")) {
                    texture = new ModelTexture(new File(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, list.getSelectedEntry() + ".png"), list.getSelectedEntry());
                }
                if (base) {
                    project.setBaseTexture(texture);
                } else {
                    project.setOverlayTexture(texture);
                }
                this.gui.removeElement(selectTextureWindow);
            }
            return true;
        }));
        this.gui.addElement(selectTextureWindow);
    }

    private List<String> getFiles(File directory, String extension) {
        List<String> list = new ArrayList<>();
        for (File modelFile : directory.listFiles()) {
            if (modelFile.isFile() && modelFile.getName().endsWith(extension)) {
                list.add(modelFile.getName().split(extension)[0]);
            }
        }
        return list;
    }
}

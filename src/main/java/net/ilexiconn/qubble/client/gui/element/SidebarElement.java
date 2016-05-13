package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.color.ColorScheme;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SidebarElement extends Element<QubbleGUI> {
    private InputElement nameInput;
    private SliderElement dimensionX, dimensionY, dimensionZ;
    private SliderElement positionX, positionY, positionZ;
    private SliderElement offsetX, offsetY, offsetZ;
    private SliderElement scaleX, scaleY, scaleZ;
    private SliderElement rotationX, rotationY, rotationZ;
    private SliderElement textureX, textureY;
    private ButtonElement mirror;
    private InputElement texture;
    private InputElement overlayTexture;

    private boolean initialized;

    public SidebarElement(QubbleGUI gui) {
        super(gui, gui.width - 122, 20, 122, gui.height - 20);
    }

    @Override
    public void init() {
        this.initFields();
        Project selectedProject = this.getGUI().getSelectedProject();
        if (selectedProject != null && selectedProject.getSelectedCube() != null) {
            this.populateFields(selectedProject.getSelectedCube());
        } else {
            this.clearFields();
        }
    }

    @Override
    public void update() {
        if (this.initialized) {
            Project selectedProject = this.getGUI().getSelectedProject();
            switch (this.getGUI().getMode()) {
                case MODEL: {
                    break;
                }
                case TEXTURE: {
                    ModelTexture texture = selectedProject != null ? selectedProject.getBaseTexture() : null;
                    ModelTexture overlayTexture = selectedProject != null ? selectedProject.getOverlayTexture() : null;
                    this.texture.clearText();
                    this.texture.writeText(texture != null ? texture.getDisplayName() : "");
                    this.overlayTexture.clearText();
                    this.overlayTexture.writeText(overlayTexture != null ? overlayTexture.getDisplayName() : "");
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
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), Qubble.CONFIG.getPrimaryColor());
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), 2, this.getHeight(), Qubble.CONFIG.getAccentColor());
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

    public void populateFields(QubbleCube cube) {
        this.nameInput.clearText();
        this.nameInput.writeText(cube.getName());
        this.nameInput.setEditable(true);
        switch (this.getGUI().getMode()) {
            case MODEL: {
                this.dimensionX.setValue(cube.getDimensionX());
                this.dimensionX.setEditable(true);
                this.dimensionY.setValue(cube.getDimensionY());
                this.dimensionY.setEditable(true);
                this.dimensionZ.setValue(cube.getDimensionZ());
                this.dimensionZ.setEditable(true);
                this.positionX.setValue(cube.getPositionX());
                this.positionX.setEditable(true);
                this.positionY.setValue(cube.getPositionY());
                this.positionY.setEditable(true);
                this.positionZ.setValue(cube.getPositionZ());
                this.positionZ.setEditable(true);
                this.offsetX.setValue(cube.getOffsetX());
                this.offsetX.setEditable(true);
                this.offsetY.setValue(cube.getOffsetY());
                this.offsetY.setEditable(true);
                this.offsetZ.setValue(cube.getOffsetZ());
                this.offsetZ.setEditable(true);
                this.scaleX.setValue(cube.getScaleX());
                this.scaleX.setEditable(true);
                this.scaleY.setValue(cube.getScaleY());
                this.scaleY.setEditable(true);
                this.scaleZ.setValue(cube.getScaleZ());
                this.scaleZ.setEditable(true);
                this.rotationX.setValue(cube.getRotationX());
                this.rotationX.setEditable(true);
                this.rotationY.setValue(cube.getRotationY());
                this.rotationY.setEditable(true);
                this.rotationZ.setValue(cube.getRotationZ());
                this.rotationZ.setEditable(true);
                break;
            }
            case TEXTURE: {
                this.textureX.setValue(cube.getTextureX());
                this.textureX.setEditable(true);
                this.textureY.setValue(cube.getTextureY());
                this.textureY.setEditable(true);
                this.mirror.withColorScheme(cube.isTextureMirrored() ? ColorScheme.TOGGLE_ON : ColorScheme.TOGGLE_OFF);
                this.mirror.setEnabled(true);
                break;
            }
            case ANIMATE: {
                break;
            }
        }
    }

    public void clearFields() {
        this.nameInput.clearText();
        this.nameInput.setEditable(false);
        switch (this.getGUI().getMode()) {
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
        int i = 0;
        SliderElement slider;
        while ((slider = this.getElement(SliderElement.class, i)) != null) {
            slider.setValue(0.0F);
            slider.setEditable(false);
            i++;
        }
    }

    public void initFields() {
        this.getChildren().clear();
        this.addChild(new TextElement(this.getGUI(), "Selected cube", 4, 10));
        this.addChild(this.nameInput = new InputElement(this.getGUI(), "", 4, 19, 116));
        switch (this.getGUI().getMode()) {
            case MODEL: {
                this.addChild(new TextElement(this.getGUI(), "Dimensions", 4, 44));
                this.addChild(this.dimensionX = new SliderElement(this.getGUI(), 4, 53, true, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setDimensions((int) (float) value, selectedCube.getDimensionY(), selectedCube.getDimensionZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, this::isNumber));
                this.addChild(this.dimensionY = new SliderElement(this.getGUI(), 43, 53, true, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setDimensions(selectedCube.getDimensionX(), (int) (float) value, selectedCube.getDimensionZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, this::isNumber));
                this.addChild(this.dimensionZ = new SliderElement(this.getGUI(), 82, 53, true, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setDimensions(selectedCube.getDimensionX(), selectedCube.getDimensionY(), (int) (float) value);
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, this::isNumber));
                this.addChild(new TextElement(this.getGUI(), "Position", 4, 69));
                this.addChild(this.positionX = new SliderElement(this.getGUI(), 4, 78, false, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setPosition(value, selectedCube.getPositionY(), selectedCube.getPositionZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.positionX.getValueInput().getText().contains("."))));
                this.addChild(this.positionY = new SliderElement(this.getGUI(), 43, 78, false, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setPosition(selectedCube.getPositionX(), value, selectedCube.getPositionZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.positionY.getValueInput().getText().contains("."))));
                this.addChild(this.positionZ = new SliderElement(this.getGUI(), 82, 78, false, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setPosition(selectedCube.getPositionX(), selectedCube.getPositionY(), value);
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.positionZ.getValueInput().getText().contains("."))));
                this.addChild(new TextElement(this.getGUI(), "Offset", 4, 94));
                this.addChild(this.offsetX = new SliderElement(this.getGUI(), 4, 103, false, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setOffset(value, selectedCube.getOffsetY(), selectedCube.getOffsetZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.offsetX.getValueInput().getText().contains("."))));
                this.addChild(this.offsetY = new SliderElement(this.getGUI(), 43, 103, false, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setOffset(selectedCube.getOffsetX(), value, selectedCube.getOffsetZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.offsetY.getValueInput().getText().contains("."))));
                this.addChild(this.offsetZ = new SliderElement(this.getGUI(), 82, 103, false, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setOffset(selectedCube.getOffsetX(), selectedCube.getOffsetY(), value);
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.offsetZ.getValueInput().getText().contains("."))));
                this.addChild(new TextElement(this.getGUI(), "Scale", 4, 119));
                this.addChild(this.scaleX = new SliderElement(this.getGUI(), 4, 128, false, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setScale(value, selectedCube.getScaleY(), selectedCube.getScaleZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.scaleX.getValueInput().getText().contains("."))));
                this.addChild(this.scaleY = new SliderElement(this.getGUI(), 43, 128, false, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setScale(selectedCube.getScaleX(), value, selectedCube.getScaleZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.scaleY.getValueInput().getText().contains("."))));
                this.addChild(this.scaleZ = new SliderElement(this.getGUI(), 82, 128, false, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setScale(selectedCube.getScaleX(), selectedCube.getScaleY(), value);
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.scaleZ.getValueInput().getText().contains("."))));
                this.addChild(new TextElement(this.getGUI(), "Rotation", 4, 144));
                this.addChild(this.rotationX = new SliderElement(this.getGUI(), 4, 153, false, 116 - 38, -180.0F, 180.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setRotation(value, selectedCube.getRotationY(), selectedCube.getRotationZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.rotationX.getValueInput().getText().contains("."))));
                this.addChild(this.rotationY = new SliderElement(this.getGUI(), 4, 166, false, 116 - 38, -180.0F, 180.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setRotation(selectedCube.getRotationX(), value, selectedCube.getRotationZ());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.rotationY.getValueInput().getText().contains("."))));
                this.addChild(this.rotationZ = new SliderElement(this.getGUI(), 4, 179, false, 116 - 38, -180.0F, 180.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setRotation(selectedCube.getRotationX(), selectedCube.getRotationY(), value);
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, key -> this.isNumber(key) || (key == Keyboard.KEY_PERIOD && !this.rotationZ.getValueInput().getText().contains("."))));
                break;
            }
            case TEXTURE: {
                this.addChild(new TextElement(this.getGUI(), "Texture offset", 4, 44));
                this.addChild(this.textureX = new SliderElement(this.getGUI(), 4, 53, true, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setTexture((int) (float) value, selectedCube.getTextureY());
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, this::isNumber));
                this.addChild(this.textureY = new SliderElement(this.getGUI(), 43, 53, true, 0.0F, -1.0F, -1.0F, value -> {
                    QubbleCube selectedCube = this.getGUI().getSelectedProject().getSelectedCube();
                    selectedCube.setTexture(selectedCube.getTextureX(), (int) (float) value);
                    this.getGUI().getModelView().updatePart(selectedCube);
                    return true;
                }, this::isNumber));
                this.addChild(this.mirror = new ButtonElement(this.getGUI(), "Mirror", 82, 53, 38, 12, (button) -> {
                    if (button.getColorScheme() == ColorScheme.TOGGLE_OFF) {
                        button.withColorScheme(ColorScheme.TOGGLE_ON);
                    } else {
                        button.withColorScheme(ColorScheme.TOGGLE_OFF);
                    }
                    return true;
                }));
                this.addChild(new TextElement(this.getGUI(), "Texture", 4, 69));
                this.addChild(this.texture = new InputElement(this.getGUI(), "", 4, 78, 104));
                this.addChild(new ButtonElement(this.getGUI(), "...", 108, 78, 12, 12, (button) -> {
                    if (this.getGUI().getSelectedProject() != null) {
                        this.openSelectTextureWindow("Select Texture", true);
                        return true;
                    }
                    return false;
                }));
                this.addChild(new TextElement(this.getGUI(), "Texture overlay", 4, 94));
                this.addChild(this.overlayTexture = new InputElement(this.getGUI(), "", 4, 103, 104));
                this.addChild(new ButtonElement(this.getGUI(), "...", 108, 103, 12, 12, (button) -> {
                    if (this.getGUI().getSelectedProject() != null) {
                        this.openSelectTextureWindow("Select Overlay Texture", false);
                        return true;
                    }
                    return false;
                }));
                break;
            }
            case ANIMATE: {
                break;
            }
        }
        this.initialized = true;
    }

    private boolean isNumber(int key) {
        return key >= 2 && key <= 11;
    }

    private void addChild(Element<QubbleGUI> element) {
        element.withParent(this);
    }

    private void openSelectTextureWindow(String name, boolean base) {
        WindowElement selectTextureWindow = new WindowElement(this.getGUI(), name, 100, 100);
        List<String> files = this.getFiles(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, ".png");
        files.add(0, "None");
        selectTextureWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, files, (selection) -> {
            try {
                Project project = this.getGUI().getSelectedProject();
                if (project != null) {
                    ModelTexture texture = null;
                    if (!selection.equals("None")) {
                        texture = new ModelTexture(ImageIO.read(new File(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, selection + ".png")), selection);
                    }
                    if (base) {
                        project.setBaseTexture(texture);
                    } else {
                        project.setOverlayTexture(texture);
                    }
                    ElementHandler.INSTANCE.removeElement(this.getGUI(), selectTextureWindow);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), selectTextureWindow);
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

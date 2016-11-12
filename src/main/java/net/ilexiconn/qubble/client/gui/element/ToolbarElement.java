package net.ilexiconn.qubble.client.gui.element;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.CheckboxElement;
import net.ilexiconn.llibrary.client.gui.element.ColorElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.llibrary.client.gui.element.color.ColorMode;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.server.config.ConfigHandler;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelMode;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.gui.property.CheckboxProperty;
import net.ilexiconn.qubble.client.gui.property.ColorProperty;
import net.ilexiconn.qubble.server.model.ModelHandler;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.ModelExporters;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.ModelImporters;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class ToolbarElement extends Element<QubbleGUI> {
    private ButtonElement<QubbleGUI> modelButton;
    private ButtonElement<QubbleGUI> textureButton;
    private ButtonElement<QubbleGUI> animateButton;

    private WindowElement<QubbleGUI> textureWindow;

    private CheckboxElement<QubbleGUI> darkCheckbox;
    private CheckboxElement<QubbleGUI> lightCheckbox;

    public ToolbarElement(QubbleGUI gui) {
        super(gui, 0, 0, gui.width, 20);
    }

    @Override
    public void init() {
        this.gui.addElement(new ButtonElement<>(this.gui, "New", 0, 0, 30, 20, (v) -> {
            this.openNewWindow();
            return true;
        }).withColorScheme(ColorSchemes.DEFAULT));
        this.gui.addElement(new ButtonElement<>(this.gui, "Open", 30, 0, 30, 20, (v) -> {
            this.openModelWindow(null);
            return true;
        }).withColorScheme(ColorSchemes.DEFAULT));
        this.gui.addElement(new ButtonElement<>(this.gui, "Save", 60, 0, 30, 20, (v) -> {
            if (this.gui.getSelectedProject() != null) {
                this.openSaveWindow((saved) -> {}, true);
                return true;
            }
            return false;
        }).withColorScheme(ColorSchemes.DEFAULT));

        this.gui.addElement(this.modelButton = (ButtonElement<QubbleGUI>) new ButtonElement<>(this.gui, "Model", this.gui.width - 212, 0, 40, 20, (v) -> {
            if (this.modelButton.getColorScheme() != ColorSchemes.TAB_ACTIVE) {
                this.setMode(ModelMode.MODEL);
                return true;
            } else {
                return false;
            }
        }).withColorScheme(ColorSchemes.TAB_ACTIVE));
        this.gui.addElement(this.textureButton = (ButtonElement<QubbleGUI>) new ButtonElement<>(this.gui, "Texture", this.gui.width - 172, 0, 50, 20, (v) -> {
            if (this.textureButton.getColorScheme() != ColorSchemes.TAB_ACTIVE) {
                this.setMode(ModelMode.TEXTURE);
                return true;
            } else {
                return false;
            }
        }).withColorScheme(ColorSchemes.DEFAULT));
        /*this.gui.addElement(*/
        this.animateButton = (ButtonElement<QubbleGUI>) new ButtonElement<>(this.gui, "Animate", this.gui.width - 172, 0, 50, 20, (v) -> {
            if (this.animateButton.getColorScheme() != ColorSchemes.TAB_ACTIVE) {
                this.setMode(ModelMode.ANIMATE);
                return true;
            } else {
                return false;
            }
        }).withColorScheme(ColorSchemes.DEFAULT)/*)*/;
        this.setMode(ModelMode.MODEL);

        this.gui.addElement(new ButtonElement<>(this.gui, "o", this.gui.width - 40, 0, 20, 20, (v) -> {
            this.openOptionsWindow();
            return true;
        }).withColorScheme(ColorSchemes.OPTIONS));
        this.gui.addElement(new ButtonElement<>(this.gui, "x", this.gui.width - 20, 0, 20, 20, (v) -> {
            this.gui.close((close) -> {
                if (close) {
                    this.gui.mc.displayGuiScreen(this.gui.getParent());
                }
            });
            return true;
        }).withColorScheme(ButtonElement.CLOSE));
    }

    public void openNewWindow() {
        WindowElement<QubbleGUI> newWindow = new WindowElement<>(this.gui, "New", 100, 44);
        final String[] string = new String[] { "" };
        newWindow.addElement(new InputElement<>(this.gui, 2, 16, 96, "", e -> string[0] = e.getText()));
        newWindow.addElement(new ButtonElement<>(this.gui, "Done", 2, 30, 96, 12, (v) -> {
            if (!string[0].isEmpty()) {
                this.gui.selectModel(QubbleModel.create(string[0], "Unknown", 64, 32));
                this.gui.removeElement(newWindow);
            }
            return true;
        }).withColorScheme(ColorSchemes.WINDOW));
        this.gui.addElement(newWindow);
    }

    public void openModelWindow(IModelImporter modelImporter) {
        WindowElement<QubbleGUI> openWindow = new WindowElement<>(this.gui, (modelImporter == null ? "Open" : "Import " + modelImporter.getName()), 100, modelImporter == null ? 114 : 100);
        openWindow.addElement(new ListElement<>(this.gui, 2, 16, 96, 82, this.getModels(modelImporter), (list) -> {
            this.gui.selectModel(list.getSelectedEntry(), modelImporter);
            this.gui.removeElement(openWindow);
            return true;
        }));
        if (modelImporter == null) {
            openWindow.addElement(new ButtonElement<>(this.gui, "Import", 2, 100, 96, 12, (v) -> {
                this.openImportWindow();
                this.gui.removeElement(openWindow);
                return true;
            }).withColorScheme(ColorSchemes.WINDOW));
        }
        this.gui.addElement(openWindow);
    }

    public void openImportWindow() {
        WindowElement<QubbleGUI> importWindow = new WindowElement<>(this.gui, "Import", 100, 100);
        List<String> importers = Lists.newArrayList(ModelImporters.IMPORTERS).stream().map(IModelImporter::getName).collect(Collectors.toList());
        importers.add(0, "Game Blocks");
        importers.add(0, "Game");
        importWindow.addElement(new ListElement<>(this.gui, 2, 16, 96, 82, importers, (list) -> {
            if (list.getSelectedEntry().equals("Game")) {
                this.openGameImportWindow();
                this.gui.removeElement(importWindow);
                return true;
            } else if (list.getSelectedEntry().equals("Game Blocks")) {
                this.openGameBlockImportWindow();
                this.gui.removeElement(importWindow);
                return true;
            } else {
                IModelImporter importer = ModelHandler.INSTANCE.getImporter(list.getSelectedEntry());
                if (importer != null) {
                    this.openModelWindow(importer);
                    this.gui.removeElement(importWindow);
                    return true;
                } else {
                    return false;
                }
            }
        }));
        this.gui.addElement(importWindow);
    }

    public void openGameImportWindow() {
        WindowElement<QubbleGUI> openWindow = new WindowElement<>(this.gui, "Import Game Model", 150, 200);
        openWindow.addElement(new ListElement<>(this.gui, 2, 16, 146, 182, ClientProxy.getGameModels(), (list) -> {
            QubbleModel model = ClientProxy.GAME_MODELS.get(list.getSelectedEntry());
            this.gui.selectModel(model.copy());
            ResourceLocation texture = ClientProxy.GAME_TEXTURES.get(list.getSelectedEntry());
            if (texture != null) {
                this.gui.getSelectedProject().setBaseTexture(new ModelTexture(texture));
            }
            this.gui.removeElement(openWindow);
            return true;
        }));
        this.gui.addElement(openWindow);
    }

    public void openGameBlockImportWindow() {
        WindowElement<QubbleGUI> openWindow = new WindowElement<>(this.gui, "Import Block Model", 150, 200);
        openWindow.addElement(new ListElement<>(this.gui, 2, 16, 146, 182, ClientProxy.getGameBlockModels(), (list) -> {
            String name = list.getSelectedEntry();
            ResourceLocation location = ClientProxy.GAME_JSON_MODEL_LOCATIONS.get(name);
            QubbleModel model = ClientProxy.loadBlockModel(name, location);
            if (model != null) {
                this.gui.selectModel(model);
            }
            this.gui.removeElement(openWindow);
            return true;
        }));
        this.gui.addElement(openWindow);
    }

    public void openSaveWindow(Consumer<Boolean> callback, boolean closeable) {
        WindowElement<QubbleGUI> saveWindow = new WindowElement<>(this.gui, "Save", 100, 64, closeable);
        saveWindow.addElement(new LabelElement<>(this.gui, "File name", 4, 19));
        InputElement<QubbleGUI> fileName;
        Project project = this.gui.getSelectedProject();
        QubbleModel selectedModel = project.getModel();
        String name = selectedModel.getFileName() == null ? selectedModel.getName() : selectedModel.getFileName();
        saveWindow.addElement(fileName = new InputElement<>(this.gui, 2, 30, 96, name, (i) -> {
        }));
        saveWindow.addElement(new ButtonElement<>(this.gui, "Save", 2, 50, 47, 12, (v) -> {
            try {
                CompressedStreamTools.writeCompressed(selectedModel.copy().serializeNBT(), new FileOutputStream(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, fileName.getText() + ".qbl")));
                project.setSaved(true);
                callback.accept(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.gui.removeElement(saveWindow);
            return true;
        }).withColorScheme(ColorSchemes.WINDOW));
        saveWindow.addElement(new ButtonElement<>(this.gui, "Export", 51, 50, 47, 12, (v) -> {
            this.openExportWindow(fileName.getText(), callback);
            this.gui.removeElement(saveWindow);
            return true;
        }).withColorScheme(ColorSchemes.WINDOW));
        this.gui.addElement(saveWindow);
    }

    public void openExportWindow(String fileName, Consumer<Boolean> callback) {
        WindowElement<QubbleGUI> exportWindow = new WindowElement<>(this.gui, "Export", 100, 100);
        exportWindow.addElement(new ListElement<>(this.gui, 2, 16, 96, 82, Lists.newArrayList(ModelExporters.EXPORTERS).stream().map(IModelExporter::getName).collect(Collectors.toList()), (list) -> {
            IModelExporter exporter = ModelHandler.INSTANCE.getExporter(list.getSelectedEntry());
            if (exporter != null) {
                this.openModelExportWindow(exporter, fileName);
                this.gui.removeElement(exportWindow);
                callback.accept(true);
                return true;
            } else {
                callback.accept(false);
                return false;
            }
        }));
        this.gui.addElement(exportWindow);
    }

    private void openModelExportWindow(IModelExporter modelExporter, String fileName) {
        Project project = this.gui.getSelectedProject();
        QubbleModel copy = project.getModel().copy();
        int argumentY = 18;
        String[] argumentNames = modelExporter.getArgumentNames();
        String[] defaultArguments = modelExporter.getDefaultArguments(copy);
        InputElement[] argumentTextBoxes = new InputElement[argumentNames.length];
        int height = argumentNames.length * 28 + 32;
        WindowElement<QubbleGUI> window = new WindowElement<>(this.gui, "Export " + modelExporter.getName(), 100, height);
        for (int argumentIndex = 0; argumentIndex < argumentNames.length; argumentIndex++) {
            window.addElement(new LabelElement<>(this.gui, argumentNames[argumentIndex], 2, argumentY));
            InputElement<QubbleGUI> input = new InputElement<>(this.gui, 1, argumentY + 9, 97, defaultArguments[argumentIndex], (i) -> {
            });
            window.addElement(input);
            argumentTextBoxes[argumentIndex] = input;
            argumentY += 28;
        }
        window.addElement(new ButtonElement<>(this.gui, "Export", 48, height - 16, 50, 14, (v) -> {
            String[] arguments = new String[argumentNames.length];
            for (int i = 0; i < argumentTextBoxes.length; i++) {
                arguments[i] = argumentTextBoxes[i].getText();
            }
            try {
                modelExporter.save(modelExporter.export(copy, arguments), new File(ClientProxy.QUBBLE_EXPORT_DIRECTORY, fileName + "." + modelExporter.getExtension()));
                project.setSaved(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.gui.removeElement(window);
            return true;
        }).withColorScheme(ColorSchemes.WINDOW));
        this.gui.addElement(window);
    }

    public void openOptionsWindow() {
        WindowElement<QubbleGUI> optionsWindow = new WindowElement<>(this.gui, "Options", 200, 220);

        optionsWindow.addElement(new ColorElement<>(this.gui, 2, 16, 195, 149, new ColorProperty((color) -> {
            if (LLibrary.CONFIG.getAccentColor() != color) {
                LLibrary.CONFIG.setAccentColor(color);
                LLibrary.CONFIG.save();
            }
        })));

        this.darkCheckbox = new CheckboxElement<>(this.gui, 32.5F, 174.5F, new CheckboxProperty(state -> {
            if (state) {
                if (!Objects.equals(LLibrary.CONFIG.getColorMode(), ColorMode.DARK.getName())) {
                    LLibrary.CONFIG.setColorMode(ColorMode.DARK.getName());
                    this.lightCheckbox.withSelection(false);
                }
            } else {
                if (!Objects.equals(LLibrary.CONFIG.getColorMode(), ColorMode.LIGHT.getName())) {
                    LLibrary.CONFIG.setColorMode(ColorMode.LIGHT.getName());
                    this.lightCheckbox.withSelection(true);
                }
            }
            LLibrary.CONFIG.save();
        }));
        this.lightCheckbox = new CheckboxElement<>(this.gui, 122.5F, 174.5F, new CheckboxProperty(state -> {
            if (state) {
                if (!Objects.equals(LLibrary.CONFIG.getColorMode(), ColorMode.LIGHT.getName())) {
                    LLibrary.CONFIG.setColorMode(ColorMode.LIGHT.getName());
                    this.darkCheckbox.withSelection(false);
                }
            } else {
                if (!Objects.equals(LLibrary.CONFIG.getColorMode(), ColorMode.DARK.getName())) {
                    LLibrary.CONFIG.setColorMode(ColorMode.DARK.getName());
                    this.darkCheckbox.withSelection(true);
                }
            }
            LLibrary.CONFIG.save();
        }));

        optionsWindow.addElement(this.darkCheckbox);
        optionsWindow.addElement(this.lightCheckbox);

        this.darkCheckbox.withSelection(Objects.equals(LLibrary.CONFIG.getColorMode(), ColorMode.DARK.getName()));
        this.lightCheckbox.withSelection(Objects.equals(LLibrary.CONFIG.getColorMode(), ColorMode.LIGHT.getName()));

        optionsWindow.addElement(new LabelElement<>(this.gui, "Dark", 50, 178));
        optionsWindow.addElement(new LabelElement<>(this.gui, "Light", 140, 178));
        optionsWindow.addElement(new LabelElement<>(this.gui, "Show Grid", 50, 198));

        final CheckboxElement<QubbleGUI> grid = new CheckboxElement<>(this.gui, 32.5F, 194.5F, new CheckboxProperty(state -> {
            Qubble.CONFIG.showGrid = state;
            ConfigHandler.INSTANCE.saveConfigForID(Qubble.MODID);
        })).withSelection(Qubble.CONFIG.showGrid);
        optionsWindow.addElement(grid);

        this.gui.addElement(optionsWindow);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), LLibrary.CONFIG.getAccentColor());
    }

    private List<String> getModels(IModelImporter modelImporter) {
        String extension = ".qbl";
        if (modelImporter != null) {
            extension = "." + modelImporter.getExtension();
        }
        return this.getFiles(ClientProxy.QUBBLE_MODEL_DIRECTORY, extension);
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

    private void setMode(ModelMode mode) {
        this.gui.setMode(mode);

        if (mode == ModelMode.TEXTURE) {
            if (this.textureWindow == null) {
                this.textureWindow = new WindowElement<>(this.gui, "Texture Map", 200, 214, false);
                new TextureMapElement(this.gui, 0.0F, 14.0F, 200, 200).withParent(this.textureWindow);
                this.gui.addElement(this.textureWindow);
            }
        } else if (this.textureWindow != null) {
            this.gui.removeElement(this.textureWindow);
            this.textureWindow = null;
        }

        this.modelButton.withColorScheme(mode == ModelMode.MODEL ? ColorSchemes.TAB_ACTIVE : ColorSchemes.DEFAULT);
        this.textureButton.withColorScheme(mode == ModelMode.TEXTURE ? ColorSchemes.TAB_ACTIVE : ColorSchemes.DEFAULT);
        this.animateButton.withColorScheme(mode == ModelMode.ANIMATE ? ColorSchemes.TAB_ACTIVE : ColorSchemes.DEFAULT);
    }
}

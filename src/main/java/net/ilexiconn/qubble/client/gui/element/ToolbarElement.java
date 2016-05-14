package net.ilexiconn.qubble.client.gui.element;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelMode;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.color.ColorMode;
import net.ilexiconn.qubble.server.color.ColorScheme;
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
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class ToolbarElement extends Element<QubbleGUI> {
    private ButtonElement modelButton;
    private ButtonElement textureButton;
    private ButtonElement animateButton;

    private WindowElement textureWindow;

    public ToolbarElement(QubbleGUI gui) {
        super(gui, 0, 0, gui.width, 20);
    }

    @Override
    public void init() {
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "New", 0, 0, 30, 20, (v) -> {
            return false; //TODO
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Open", 30, 0, 30, 20, (v) -> {
            this.openModelWindow(null);
            return true;
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Save", 60, 0, 30, 20, (v) -> {
            if (this.getGUI().getSelectedProject() != null) {
                this.openSaveWindow();
                return true;
            }
            return false;
        }));

        ElementHandler.INSTANCE.addElement(this.getGUI(), this.modelButton = new ButtonElement(this.getGUI(), "Model", this.getGUI().width - 262, 0, 40, 20, (v) -> {
            if (this.modelButton.getColorScheme() != ColorScheme.TAB_ACTIVE) {
                this.setMode(ModelMode.MODEL);
                return true;
            } else {
                return false;
            }
        }).withColorScheme(ColorScheme.TAB_ACTIVE));
        ElementHandler.INSTANCE.addElement(this.getGUI(), this.textureButton = new ButtonElement(this.getGUI(), "Texture", this.getGUI().width - 222, 0, 50, 20, (v) -> {
            if (this.textureButton.getColorScheme() != ColorScheme.TAB_ACTIVE) {
                this.setMode(ModelMode.TEXTURE);
                return true;
            } else {
                return false;
            }
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), this.animateButton = new ButtonElement(this.getGUI(), "Animate", this.getGUI().width - 172, 0, 50, 20, (v) -> {
            if (this.animateButton.getColorScheme() != ColorScheme.TAB_ACTIVE) {
                this.setMode(ModelMode.ANIMATE);
                return true;
            } else {
                return false;
            }
        }));
        this.setMode(ModelMode.MODEL);

        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "o", this.getGUI().width - 40, 0, 20, 20, (v) -> {
            this.openOptionsWindow();
            return true;
        }).withColorScheme(ColorScheme.OPTIONS));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "x", this.getGUI().width - 20, 0, 20, 20, (v) -> {
            this.getGUI().mc.displayGuiScreen(this.getGUI().getParent());
            return true;
        }).withColorScheme(ColorScheme.CLOSE));
    }

    public void openModelWindow(IModelImporter modelImporter) {
        WindowElement openWindow = new WindowElement(this.getGUI(), (modelImporter == null ? "Open" : "Import " + modelImporter.getName()), 100, modelImporter == null ? 114 : 100);
        openWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, this.getModels(modelImporter), (selected) -> {
            this.getGUI().selectModel(selected, modelImporter);
            ElementHandler.INSTANCE.removeElement(this.getGUI(), openWindow);
            return true;
        }));
        if (modelImporter == null) {
            openWindow.addElement(new ButtonElement(this.getGUI(), "Import", 2, 100, 96, 12, (v) -> {
                this.openImportWindow();
                ElementHandler.INSTANCE.removeElement(this.getGUI(), openWindow);
                return true;
            }).withColorScheme(ColorScheme.WINDOW));
        }
        ElementHandler.INSTANCE.addElement(this.getGUI(), openWindow);
    }

    public void openImportWindow() {
        WindowElement importWindow = new WindowElement(this.getGUI(), "Import", 100, 100);
        List<String> importers = Lists.newArrayList(ModelImporters.IMPORTERS).stream().map(IModelImporter::getName).collect(Collectors.toList());
        importers.add(0, "Game Blocks");
        importers.add(0, "Game");
        importWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, importers, (selected) -> {
            if (selected.equals("Game")) {
                this.openGameImportWindow();
                ElementHandler.INSTANCE.removeElement(this.getGUI(), importWindow);
                return true;
            } else if (selected.equals("Game Blocks")) {
                this.openGameBlockImportWindow();
                ElementHandler.INSTANCE.removeElement(this.getGUI(), importWindow);
                return true;
            } else {
                IModelImporter importer = ModelHandler.INSTANCE.getImporter(selected);
                if (importer != null) {
                    this.openModelWindow(importer);
                    ElementHandler.INSTANCE.removeElement(this.getGUI(), importWindow);
                    return true;
                } else {
                    return false;
                }
            }
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), importWindow);
    }

    public void openGameImportWindow() {
        WindowElement openWindow = new WindowElement(this.getGUI(), "Import Game Model", 150, 200);
        openWindow.addElement(new ListElement(this.getGUI(), 2, 16, 146, 182, ClientProxy.getGameModels(), (selected) -> {
            QubbleModel model = ClientProxy.GAME_MODELS.get(selected);
            this.getGUI().selectModel(model.copy());
            ResourceLocation texture = ClientProxy.GAME_TEXTURES.get(selected);
            if (texture != null) {
                this.getGUI().getSelectedProject().setBaseTexture(new ModelTexture(texture));
            }
            ElementHandler.INSTANCE.removeElement(this.getGUI(), openWindow);
            return true;
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), openWindow);
    }

    public void openGameBlockImportWindow() {
        WindowElement openWindow = new WindowElement(this.getGUI(), "Import Block Model", 150, 200);
        openWindow.addElement(new ListElement(this.getGUI(), 2, 16, 146, 182, ClientProxy.getGameBlockModels(), (selected) -> {
            QubbleModel model = ClientProxy.GAME_JSON_MODELS.get(selected);
            this.getGUI().selectModel(model);
            ElementHandler.INSTANCE.removeElement(this.getGUI(), openWindow);
            return true;
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), openWindow);
    }

    public void openSaveWindow() {
        WindowElement saveWindow = new WindowElement(this.getGUI(), "Save", 100, 64);
        saveWindow.addElement(new TextElement(this.getGUI(), "File name", 4, 19));
        InputElement fileName;
        QubbleModel selectedModel = this.getGUI().getSelectedProject().getModel();
        saveWindow.addElement(fileName = new InputElement(this.getGUI(), selectedModel.getFileName() == null ? selectedModel.getName() : selectedModel.getFileName(), 2, 30, 96));
        saveWindow.addElement(new ButtonElement(this.getGUI(), "Save", 2, 50, 47, 12, (v) -> {
            try {
                CompressedStreamTools.writeCompressed(selectedModel.copy().serializeNBT(), new FileOutputStream(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, fileName.getText() + ".qbl")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ElementHandler.INSTANCE.removeElement(this.getGUI(), saveWindow);
            return true;
        }).withColorScheme(ColorScheme.WINDOW));
        saveWindow.addElement(new ButtonElement(this.getGUI(), "Export", 51, 50, 47, 12, (v) -> {
            this.openExportWindow(fileName.getText());
            ElementHandler.INSTANCE.removeElement(this.getGUI(), saveWindow);
            return true;
        }).withColorScheme(ColorScheme.WINDOW));
        ElementHandler.INSTANCE.addElement(this.getGUI(), saveWindow);
    }

    public void openExportWindow(String fileName) {
        WindowElement exportWindow = new WindowElement(this.getGUI(), "Export", 100, 100);
        exportWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, Lists.newArrayList(ModelExporters.EXPORTERS).stream().map(IModelExporter::getName).collect(Collectors.toList()), (selected) -> {
            IModelExporter exporter = ModelHandler.INSTANCE.getExporter(selected);
            if (exporter != null) {
                this.openModelExportWindow(exporter, fileName);
                ElementHandler.INSTANCE.removeElement(this.getGUI(), exportWindow);
                return true;
            } else {
                return false;
            }
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), exportWindow);
    }

    private void openModelExportWindow(IModelExporter modelExporter, String fileName) {
        QubbleModel copy = this.getGUI().getSelectedProject().getModel().copy();
        int argumentY = 18;
        String[] argumentNames = modelExporter.getArgumentNames();
        String[] defaultArguments = modelExporter.getDefaultArguments(copy);
        InputElement[] argumentTextBoxes = new InputElement[argumentNames.length];
        int height = argumentNames.length * 28 + 32;
        WindowElement window = new WindowElement(this.getGUI(), "Export " + modelExporter.getName(), 100, height);
        for (int argumentIndex = 0; argumentIndex < argumentNames.length; argumentIndex++) {
            window.addElement(new TextElement(this.getGUI(), argumentNames[argumentIndex], 2, argumentY));
            InputElement input = new InputElement(this.getGUI(), defaultArguments[argumentIndex], 1, argumentY + 9, 97);
            window.addElement(input);
            argumentTextBoxes[argumentIndex] = input;
            argumentY += 28;
        }
        window.addElement(new ButtonElement(this.getGUI(), "Export", 48, height - 16, 50, 14, (v) -> {
            String[] arguments = new String[argumentNames.length];
            for (int i = 0; i < argumentTextBoxes.length; i++) {
                arguments[i] = argumentTextBoxes[i].getText();
            }
            try {
                modelExporter.save(modelExporter.export(copy, arguments), new File(ClientProxy.QUBBLE_EXPORT_DIRECTORY, fileName + "." + modelExporter.getExtension()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ElementHandler.INSTANCE.removeElement(this.getGUI(), window);
            return true;
        }).withColorScheme(ColorScheme.WINDOW));
        ElementHandler.INSTANCE.addElement(this.getGUI(), window);
    }

    public void openOptionsWindow() {
        WindowElement optionsWindow = new WindowElement(this.getGUI(), "Options", 200, 200);
        optionsWindow.addElement(new ColorElement(this.getGUI(), 2, 16, 195, 149, (color) -> {
            if (Qubble.CONFIG.getAccentColor() != color) {
                Qubble.CONFIG.setAccentColor(color);
                try {
                    CompressedStreamTools.write(Qubble.CONFIG.serializeNBT(), Qubble.CONFIG_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return false;
            }
        }));

        final CheckboxElement dark = new CheckboxElement(this.getGUI(), 32.5F, 174.5F).withSelection(Objects.equals(Qubble.CONFIG.getColorMode(), ColorMode.DARK.getName()));
        final CheckboxElement light = new CheckboxElement(this.getGUI(), 122.5F, 174.5F).withSelection(Objects.equals(Qubble.CONFIG.getColorMode(), ColorMode.LIGHT.getName()));
        optionsWindow.addElement(dark.withActionHandler((selected) -> {
            if (!Objects.equals(Qubble.CONFIG.getColorMode(), ColorMode.DARK.getName())) {
                Qubble.CONFIG.setColorMode(ColorMode.DARK.getName());
                light.withSelection(false);
                try {
                    CompressedStreamTools.write(Qubble.CONFIG.serializeNBT(), Qubble.CONFIG_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return false;
            }
        }));
        optionsWindow.addElement(light.withActionHandler((selected) -> {
            if (!Objects.equals(Qubble.CONFIG.getColorMode(), ColorMode.LIGHT.getName())) {
                Qubble.CONFIG.setColorMode(ColorMode.LIGHT.getName());
                dark.withSelection(false);
                try {
                    CompressedStreamTools.write(Qubble.CONFIG.serializeNBT(), Qubble.CONFIG_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return false;
            }
        }));

        optionsWindow.addElement(new TextElement(this.getGUI(), "Dark", 50, 178));
        optionsWindow.addElement(new TextElement(this.getGUI(), "Light", 140, 178));

        ElementHandler.INSTANCE.addElement(this.getGUI(), optionsWindow);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), Qubble.CONFIG.getAccentColor());
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
        this.getGUI().setMode(mode);

        if (mode == ModelMode.TEXTURE) {
            if (this.textureWindow == null) {
                this.textureWindow = new WindowElement(this.getGUI(), "Texture Map", 200, 214);
                new TextureMapElement(this.getGUI(), 0.0F, 14.0F, 200, 200).withParent(this.textureWindow);
                ElementHandler.INSTANCE.addElement(this.getGUI(), this.textureWindow);
            }
        } else if (this.textureWindow != null) {
            ElementHandler.INSTANCE.removeElement(this.getGUI(), this.textureWindow);
            this.textureWindow = null;
        }

        this.modelButton.withColorScheme(mode == ModelMode.MODEL ? ColorScheme.TAB_ACTIVE : ColorScheme.DEFAULT);
        this.textureButton.withColorScheme(mode == ModelMode.TEXTURE ? ColorScheme.TAB_ACTIVE : ColorScheme.DEFAULT);
        this.animateButton.withColorScheme(mode == ModelMode.ANIMATE ? ColorScheme.TAB_ACTIVE : ColorScheme.DEFAULT);
    }
}

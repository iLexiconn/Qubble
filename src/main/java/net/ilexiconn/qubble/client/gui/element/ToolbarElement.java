package net.ilexiconn.qubble.client.gui.element;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.color.ColorMode;
import net.ilexiconn.qubble.server.color.ColorScheme;
import net.ilexiconn.qubble.server.model.ModelHandler;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.ModelExporters;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.ModelImporters;
import net.ilexiconn.qubble.server.util.JSONUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class ToolbarElement extends Element<QubbleGUI> {
    private ButtonElement modelButton;
    private ButtonElement textureButton;
    private ButtonElement animateButton;
    private int currentSelectedButton = 0;

    public ToolbarElement(QubbleGUI gui) {
        super(gui, 0, 0, gui.width, 20);
    }

    @Override
    public void init() {
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Open", 0, 0, 30, 20, (g, e) -> this.openModelWindow(null)));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Save", 30, 0, 30, 20, (g, e) -> System.out.println(e.getText())));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Import", 60, 0, 40, 20, (g, e) -> this.openImportWindow()));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Export", 100, 0, 40, 20, (g, e) -> this.openExportWindow()));

        ElementHandler.INSTANCE.addElement(this.getGUI(), this.modelButton = new ButtonElement(this.getGUI(), "Model", this.getGUI().width - 230, 0, 40, 20, (e, g) -> {
            this.setButtonColors(true, false, false);
            this.getGUI().getModelView().setVisible(true);
        }).withColorScheme(ColorScheme.TAB_ACTIVE));
        ElementHandler.INSTANCE.addElement(this.getGUI(), this.textureButton = new ButtonElement(this.getGUI(), "Texture", this.getGUI().width - 190, 0, 50, 20, (e, g) -> {
            this.setButtonColors(false, true, false);
            this.getGUI().getModelView().setVisible(false);
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), this.animateButton = new ButtonElement(this.getGUI(), "Animate", this.getGUI().width - 140, 0, 50, 20, (e, g) -> {
            this.setButtonColors(false, false, true);
            this.getGUI().getModelView().setVisible(false);
        }));

        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "o", this.getGUI().width - 40, 0, 20, 20, (gui, element) -> this.openOptionsWindow()).withColorScheme(ColorScheme.OPTIONS));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "x", this.getGUI().width - 20, 0, 20, 20, (gui, element) -> this.getGUI().mc.displayGuiScreen(this.getGUI().getParent())).withColorScheme(ColorScheme.CLOSE));
    }

    public void openModelWindow(IModelImporter modelImporter) {
        WindowElement openWindow = new WindowElement(this.getGUI(), (modelImporter == null ? "Open" : "Import " + modelImporter.getName()), 100, 100);
        openWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, this.getModels(modelImporter), (gui, element) -> {
            gui.selectModel(element.getSelected(), modelImporter);
            ElementHandler.INSTANCE.removeElement(this.getGUI(), openWindow);
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), openWindow);
    }

    public void openImportWindow() {
        WindowElement importWindow = new WindowElement(this.getGUI(), "Import", 100, 100);
        importWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, Lists.newArrayList(ModelImporters.IMPORTERS).stream().map(IModelImporter::getName).collect(Collectors.toList()), (gui, element) -> {
            IModelImporter importer = ModelHandler.INSTANCE.getImporter(element.getSelected());
            if (importer != null) {
                this.openModelWindow(importer);
                ElementHandler.INSTANCE.removeElement(this.getGUI(), importWindow);
            }
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), importWindow);
    }

    public void openExportWindow() {
        if (this.getGUI().getSelectedModel() == null) {
            return;
        }
        WindowElement exportWindow = new WindowElement(this.getGUI(), "Export", 100, 100);
        exportWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, Lists.newArrayList(ModelExporters.EXPORTERS).stream().map(IModelExporter::getName).collect(Collectors.toList()), (gui, element) -> {
            IModelExporter exporter = ModelHandler.INSTANCE.getExporter(element.getSelected());
            if (exporter != null) {
                this.openModelExportWindow(exporter);
                ElementHandler.INSTANCE.removeElement(this.getGUI(), exportWindow);
            }
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), exportWindow);
    }

    private void openModelExportWindow(IModelExporter modelExporter) {
        int argumentY = 20;
        String[] argumentNames = modelExporter.getArgumentNames();
        String[] defaultArguments = modelExporter.getDefaultArguments(this.getGUI().getSelectedModel());
        InputElement[] argumentTextBoxes = new InputElement[argumentNames.length];
        boolean compact = argumentNames.length == 0;
        int width = compact ? 200 : 400;
        int height = compact ? 60 : 200;
        WindowElement window = new WindowElement(this.getGUI(), "Export " + this.getGUI().getSelectedModel().getName() + " to " + modelExporter.getName(), width, height);
        for (int argumentIndex = 0; argumentIndex < argumentNames.length; argumentIndex++) {
            window.addElement(new TextElement(this.getGUI(), argumentNames[argumentIndex], 200, argumentY));
            InputElement input = new InputElement(this.getGUI(), defaultArguments[argumentIndex], 40, argumentY + 10, 320);
            window.addElement(input);
            argumentTextBoxes[argumentIndex] = input;
            argumentY += 40;
        }
        window.addElement(new ButtonElement(this.getGUI(), "Export", width / 2 - 50, height - 35, 100, 20, (gui, component) -> {
            String[] arguments = new String[argumentNames.length];
            for (int i = 0; i < argumentTextBoxes.length; i++) {
                arguments[i] = argumentTextBoxes[i].getText();
            }
            try {
                QubbleModel copy = this.getGUI().getSelectedModel().copy();
                modelExporter.save(modelExporter.export(copy, arguments), new File(ClientProxy.QUBBLE_EXPORT_DIRECTORY, copy.getFileName() + "." + modelExporter.getExtension()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ElementHandler.INSTANCE.removeElement(this.getGUI(), window);
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), window);
    }

    public void openOptionsWindow() {
        WindowElement optionsWindow = new WindowElement(this.getGUI(), "Options", 200, 200);
        optionsWindow.addElement(new ColorElement(this.getGUI(), 2, 16, 195, 149, (gui, element) -> {
            Qubble.CONFIG.setAccentColor(element.getColor());
            JSONUtil.saveConfig(Qubble.CONFIG, Qubble.CONFIG_FILE);
        }));

        final CheckboxElement dark = new CheckboxElement(this.getGUI(), 32.5F, 174.5F).withSelection(Qubble.CONFIG.getColorMode() == ColorMode.DARK);
        final CheckboxElement light = new CheckboxElement(this.getGUI(), 122.5F, 174.5F).withSelection(Qubble.CONFIG.getColorMode() == ColorMode.LIGHT);
        optionsWindow.addElement(dark.withActionHandler((selected) -> {
            if (Qubble.CONFIG.getColorMode() != ColorMode.DARK) {
                Qubble.CONFIG.setColorMode(ColorMode.DARK);
                light.withSelection(false);
                JSONUtil.saveConfig(Qubble.CONFIG, Qubble.CONFIG_FILE);
                return true;
            } else {
                return false;
            }
        }));
        optionsWindow.addElement(light.withActionHandler((selected) -> {
            if (Qubble.CONFIG.getColorMode() != ColorMode.LIGHT) {
                Qubble.CONFIG.setColorMode(ColorMode.LIGHT);
                dark.withSelection(false);
                JSONUtil.saveConfig(Qubble.CONFIG, Qubble.CONFIG_FILE);
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

    private void setButtonColors(boolean model, boolean texture, boolean animation) {
        this.modelButton.withColorScheme(model ? ColorScheme.TAB_ACTIVE : ColorScheme.DEFAULT);
        this.textureButton.withColorScheme(texture ? ColorScheme.TAB_ACTIVE  : ColorScheme.DEFAULT);
        this.animateButton.withColorScheme(animation ? ColorScheme.TAB_ACTIVE  : ColorScheme.DEFAULT);
        this.currentSelectedButton = model ? 0 : texture ? 1 : 2;
    }
}

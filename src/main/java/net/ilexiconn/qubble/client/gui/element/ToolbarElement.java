package net.ilexiconn.qubble.client.gui.element;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.server.config.ConfigEntry;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.config.QubbleConfig;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.ModelExporters;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.ModelImporters;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class ToolbarElement extends Element<QubbleGUI> {
    private ButtonElement modelButton;
    private ButtonElement textureButton;
    private ButtonElement animationButton;
    private int currentSelectedButton = 0;

    public ToolbarElement(QubbleGUI gui) {
        super(gui, 0, 0, gui.width, 20);
    }

    @Override
    public void init() {
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Open", 0, 0, 30, 20, (g, e) -> this.openModelWindow()));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Save", 30, 0, 30, 20, (g, e) -> System.out.println(e.getText())));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Import", 60, 0, 40, 20, (g, e) -> this.openImportWindow()));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Export", 100, 0, 40, 20, (g, e) -> this.openExportWindow()));

        ElementHandler.INSTANCE.addElement(this.getGUI(), this.modelButton = new ButtonElement(this.getGUI(), "Model", this.getGUI().width - 230, 0, 40, 20, (e, g) -> {
            this.setButtonColors(true, false, false);
            this.getGUI().getModelView().setVisible(true);
        }).withColorScheme(Qubble.CONFIG.getPrimaryColor(), Qubble.CONFIG.getPrimaryColor()));
        ElementHandler.INSTANCE.addElement(this.getGUI(), this.textureButton = new ButtonElement(this.getGUI(), "Texture", this.getGUI().width - 190, 0, 50, 20, (e, g) -> {
            this.setButtonColors(false, true, false);
            this.getGUI().getModelView().setVisible(false);
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), this.animationButton = new ButtonElement(this.getGUI(), "Animation", this.getGUI().width - 140, 0, 50, 20, (e, g) -> {
            this.setButtonColors(false, false, true);
            this.getGUI().getModelView().setVisible(false);
        }));

        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "o", this.getGUI().width - 40, 0, 20, 20, (gui, element) -> this.openOptionsWindow()).withReverseColors(true).withColorScheme(-1, 0xFF898989));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "x", this.getGUI().width - 20, 0, 20, 20, (gui, element) -> this.getGUI().mc.displayGuiScreen(this.getGUI().getParent())).withReverseColors(true).withColorScheme(-1, 0xFFE04747));
    }

    public void openModelWindow() {
        WindowElement openWindow = new WindowElement(this.getGUI(), "Open", 100, 100);
        openWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, this.getModels(null), (gui, element) -> {
            gui.selectModel(element.getSelected(), null);
            ElementHandler.INSTANCE.removeElement(this.getGUI(), openWindow);
        }));
        ElementHandler.INSTANCE.addElement(this.getGUI(), openWindow);
    }

    public void openImportWindow() {
        WindowElement importWindow = new WindowElement(this.getGUI(), "Import", 100, 100);
        importWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, Lists.newArrayList(ModelImporters.IMPORTERS).stream().map(IModelImporter::getName).collect(Collectors.toList()), (gui, element) -> System.out.println(element.getSelected())));
        ElementHandler.INSTANCE.addElement(this.getGUI(), importWindow);
    }

    public void openExportWindow() {
        WindowElement exportWindow = new WindowElement(this.getGUI(), "Export", 100, 100);
        exportWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, Lists.newArrayList(ModelExporters.EXPORTERS).stream().map(IModelExporter::getName).collect(Collectors.toList()), (gui, element) -> System.out.println(element.getSelected())));
        ElementHandler.INSTANCE.addElement(this.getGUI(), exportWindow);
    }

    public void openOptionsWindow() {
        WindowElement optionsWindow = new WindowElement(this.getGUI(), "Options", 200, 200);
        optionsWindow.addElement(new ColorElement(this.getGUI(), 2, 16, 195, 149, (gui, element) -> {
            try {
                String[] colors = QubbleConfig.class.getField("accentColor").getAnnotation(ConfigEntry.class).validValues();
                Qubble.CONFIG.accentColor = colors[element.getColor()];
                this.setButtonColors(this.currentSelectedButton == 0, this.currentSelectedButton == 1, this.currentSelectedButton == 2);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }));

        final CheckboxElement dark = new CheckboxElement(this.getGUI(), 32.5F, 174.5F).withSelection(Qubble.CONFIG.mode.equals("dark"));
        final CheckboxElement light = new CheckboxElement(this.getGUI(), 122.5F, 174.5F).withSelection(Qubble.CONFIG.mode.equals("light"));
        optionsWindow.addElement(dark.withActionHandler((selected) -> {
            if (!Qubble.CONFIG.mode.equals("dark")) {
                Qubble.CONFIG.mode = "dark";
                light.withSelection(false);
                this.setButtonColors(this.currentSelectedButton == 0, this.currentSelectedButton == 1, this.currentSelectedButton == 2);
                return true;
            } else {
                return false;
            }
        }));
        optionsWindow.addElement(light.withActionHandler((selected) -> {
            if (!Qubble.CONFIG.mode.equals("light")) {
                Qubble.CONFIG.mode = "light";
                dark.withSelection(false);
                this.setButtonColors(this.currentSelectedButton == 0, this.currentSelectedButton == 1, this.currentSelectedButton == 2);
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

    private List<String> getModels(IModelImporter<?> modelImporter) {
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
        int primary = Qubble.CONFIG.getPrimaryColor();
        int accent = Qubble.CONFIG.getAccentColor();
        int accentDark = Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor());
        this.modelButton.withColorScheme(model ? primary : accent, model ? primary : accentDark);
        this.textureButton.withColorScheme(texture ? primary : accent, texture ? primary : accentDark);
        this.animationButton.withColorScheme(animation ? primary : accent, animation ? primary : accentDark);
        this.currentSelectedButton = model ? 0 : texture ? 1 : 2;
    }
}

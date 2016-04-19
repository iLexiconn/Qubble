package net.ilexiconn.qubble.client.gui.element;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.ModelExporters;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.ModelImporters;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class ToolbarElement extends Element<QubbleGUI> {
    private ButtonElement modelButton;
    private ButtonElement textureButton;
    private ButtonElement animationButton;

    public ToolbarElement(QubbleGUI gui) {
        super(gui, 0, 0, gui.width, 20);
    }

    @Override
    public void init() {
        WindowElement openWindow = new WindowElement(this.getGUI(), "Open", 100, 100);
        openWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, this.getModels(null), (gui, element) -> {
            gui.selectModel(element.getSelected(), null);
            ElementHandler.INSTANCE.removeElement(this.getGUI(), openWindow);
        }));

        WindowElement importWindow = new WindowElement(this.getGUI(), "Import", 100, 100);
        importWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, Lists.newArrayList(ModelImporters.IMPORTERS).stream().map(IModelImporter::getName).collect(Collectors.toList()), (gui, element) -> System.out.println(element.getSelected())));

        WindowElement exportWindow = new WindowElement(this.getGUI(), "Export", 100, 100);
        exportWindow.addElement(new ListElement(this.getGUI(), 2, 16, 96, 82, Lists.newArrayList(ModelExporters.EXPORTERS).stream().map(IModelExporter::getName).collect(Collectors.toList()), (gui, element) -> System.out.println(element.getSelected())));

        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Open", 0, 0, 30, 20, (g, e) -> ElementHandler.INSTANCE.addElement(g, openWindow)).withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor())));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Save", 30, 0, 30, 20, (g, e) -> System.out.println(e.getText())).withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor())));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Import", 60, 0, 40, 20, (g, e) -> ElementHandler.INSTANCE.addElement(g, importWindow)).withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor())));
        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "Export", 100, 0, 40, 20, (g, e) -> ElementHandler.INSTANCE.addElement(g, exportWindow)).withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor())));

        ElementHandler.INSTANCE.addElement(this.getGUI(), this.modelButton = new ButtonElement(this.getGUI(), "Model", this.getGUI().width - 230, 0, 40, 20, (e, g) -> {
            this.modelButton.withColorScheme(Qubble.CONFIG.getPrimaryColor(), Qubble.CONFIG.getPrimaryColor());
            this.textureButton.withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor()));
            this.animationButton.withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor()));
        }).withColorScheme(Qubble.CONFIG.getPrimaryColor(), Qubble.CONFIG.getPrimaryColor()));
        ElementHandler.INSTANCE.addElement(this.getGUI(), this.textureButton = new ButtonElement(this.getGUI(), "Texture", this.getGUI().width - 190, 0, 50, 20, (e, g) -> {
            this.modelButton.withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor()));
            this.textureButton.withColorScheme(Qubble.CONFIG.getPrimaryColor(), Qubble.CONFIG.getPrimaryColor());
            this.animationButton.withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor()));
        }).withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor())));
        ElementHandler.INSTANCE.addElement(this.getGUI(), this.animationButton = new ButtonElement(this.getGUI(), "Animation", this.getGUI().width - 140, 0, 50, 20, (e, g) -> {
            this.modelButton.withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor()));
            this.textureButton.withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor()));
            this.animationButton.withColorScheme(Qubble.CONFIG.getPrimaryColor(), Qubble.CONFIG.getPrimaryColor());
        }).withColorScheme(Qubble.CONFIG.getAccentColor(), Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor())));

        ElementHandler.INSTANCE.addElement(this.getGUI(), new ButtonElement(this.getGUI(), "x", this.getGUI().width - 20, 0, 20, 20, (gui, element) -> this.getGUI().mc.displayGuiScreen(this.getGUI().getParent())).withColorScheme(Qubble.CONFIG.getDarkerColor(Qubble.CONFIG.getAccentColor()), 0xFFE04747));
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

    public ButtonElement getModelButton() {
        return modelButton;
    }

    public ButtonElement getTextureButton() {
        return textureButton;
    }

    public ButtonElement getAnimationButton() {
        return animationButton;
    }
}

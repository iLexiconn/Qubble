package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.component.*;
import net.ilexiconn.qubble.client.gui.dialog.Dialog;
import net.ilexiconn.qubble.client.gui.dialog.DialogHandler;
import net.ilexiconn.qubble.server.model.ModelHandler;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class QubbleGUI extends GuiScreen {
    private static final int PRIMARY_COLOR_DARK = 0xFF3D3D3D;
    private static final int SECONDARY_COLOR_DARK = 0xFF212121;
    private static final int TEXT_COLOR_DARK = 0xFFFFFFFF;

    private static final int PRIMARY_COLOR_LIGHT = 0xFFDDDDDD;
    private static final int SECONDARY_COLOR_LIGHT = 0xFFE8E8E8;
    private static final int TEXT_COLOR_LIGHT = 0xFF3D3D3D;

    private GuiMainMenu mainMenu;

    private ModelViewComponent viewComponent;
    private QubbleModel currentModel;

    public QubbleGUI(GuiMainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public static void drawRectangle(double x, double y, double width, double height, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        float a = (float) (color >> 24 & 0xFF) / 255.0F;
        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(x, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x, y, 0.0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawOutline(double x, double y, double width, double height, int color, int outlineSize) {
        drawRectangle(x, y, width - outlineSize, outlineSize, color);
        drawRectangle(x + width - outlineSize, y, outlineSize, height - outlineSize, color);
        drawRectangle(x, y + height - outlineSize, width, outlineSize, color);
        drawRectangle(x, y, outlineSize, height - outlineSize, color);
    }

    public static int getPrimaryColor() {
        return Qubble.CONFIG.darkMode ? PRIMARY_COLOR_DARK : PRIMARY_COLOR_LIGHT;
    }

    public static int getSecondaryColor() {
        return Qubble.CONFIG.darkMode ? SECONDARY_COLOR_DARK : SECONDARY_COLOR_LIGHT;
    }

    public static int getTextColor() {
        return Qubble.CONFIG.darkMode ? TEXT_COLOR_DARK : TEXT_COLOR_LIGHT;
    }

    public static float interpolate(float prev, float current, float partialTicks) {
        return prev + partialTicks * (current - prev);
    }

    @Override
    public void initGui() {
        super.initGui();
        ComponentHandler.INSTANCE.clearGUI(this);
        DialogHandler.INSTANCE.clearGUI(this);
        ComponentHandler.INSTANCE.addComponent(this, new ButtonComponent("x", 0, 0, 20, 20, "Close Qubble and return to the main menu", (gui, component) -> ClientProxy.MINECRAFT.displayGuiScreen(QubbleGUI.this.mainMenu)));
        ComponentHandler.INSTANCE.addComponent(this, new ButtonComponent("o", 21, 0, 20, 20, "Open a model", (gui, component) -> QubbleGUI.this.openModelSelectionDialog(null)));
        ComponentHandler.INSTANCE.addComponent(this, new ButtonComponent("i", 42, 0, 20, 20, "Import a model", (gui, component) -> QubbleGUI.this.openModelImportDialog()));
        ComponentHandler.INSTANCE.addComponent(this, new ButtonComponent("t", 63, 0, 20, 20, "Import a texture", (gui, component) -> {
            if (QubbleGUI.this.currentModel != null) {
                QubbleGUI.this.openTextureSelectDialog();
            }
        }));
        ComponentHandler.INSTANCE.addComponent(this, new ButtonComponent("e", 84, 0, 20, 20, "Export this model", (gui, components) -> {
            if (QubbleGUI.this.currentModel != null) {
                QubbleGUI.this.openModelExportSelectDialog();
            }
        }));
        ComponentHandler.INSTANCE.addComponent(this, viewComponent = new ModelViewComponent());
        ComponentHandler.INSTANCE.addComponent(this, new ModelTreeComponent());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        float preciseMouseX = this.getPreciseMouseX(scaledResolution);
        float preciseMouseY = this.getPreciseMouseY(scaledResolution);
        ComponentHandler.INSTANCE.render(this, preciseMouseX, preciseMouseY, partialTicks);
        if (!DialogHandler.INSTANCE.render(this, preciseMouseX, preciseMouseY, partialTicks)) {
            ComponentHandler.INSTANCE.renderAfter(this, preciseMouseX, preciseMouseY, partialTicks);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        float preciseMouseX = this.getPreciseMouseX(scaledResolution);
        float preciseMouseY = this.getPreciseMouseY(scaledResolution);
        if (!DialogHandler.INSTANCE.mouseDragged(this, preciseMouseX, preciseMouseY, clickedMouseButton, timeSinceLastClick)) {
            ComponentHandler.INSTANCE.mouseDragged(this, preciseMouseX, preciseMouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        float preciseMouseX = this.getPreciseMouseX(scaledResolution);
        float preciseMouseY = this.getPreciseMouseY(scaledResolution);
        if (!DialogHandler.INSTANCE.mouseClicked(this, preciseMouseX, preciseMouseY, button)) {
            ComponentHandler.INSTANCE.mouseClicked(this, preciseMouseX, preciseMouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
        float preciseMouseX = this.getPreciseMouseX(scaledResolution);
        float preciseMouseY = this.getPreciseMouseY(scaledResolution);
        if (!DialogHandler.INSTANCE.mouseReleased(this, preciseMouseX, preciseMouseY, button)) {
            ComponentHandler.INSTANCE.mouseReleased(this, preciseMouseX, preciseMouseY, button);
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) throws IOException {
        super.keyTyped(character, keyCode);
        if (!DialogHandler.INSTANCE.keyPressed(this, character, keyCode)) {
            ComponentHandler.INSTANCE.keyPressed(this, character, keyCode);
        }
    }

    private float getPreciseMouseX(ScaledResolution scaledResolution) {
        return (float) Mouse.getX() / scaledResolution.getScaleFactor();
    }

    private float getPreciseMouseY(ScaledResolution scaledResolution) {
        return this.height - (float) Mouse.getY() * this.height / (float) this.mc.displayHeight - 1.0F;
    }

    private void drawBackground() {
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        int color = getSecondaryColor();
        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;
        GlStateManager.color(r * 0.8F, g * 0.8F, b * 0.8F, 1.0F);
        this.drawTexturedModalRect(0, 0, 0, 0, this.width, this.height);
        GlStateManager.enableTexture2D();
    }

    public QubbleModel getCurrentModel() {
        return this.currentModel;
    }

    private void openModelSelectionDialog(IModelImporter modelImporter) {
        Dialog<QubbleGUI> dialog = new Dialog(this, modelImporter == null ? "Open model" : "Import " + modelImporter.getName() + " model", this.width / 2 - 100, this.height / 2 - 100, 200, 200);
        List<String> models = this.getModels(modelImporter);
        dialog.addComponent(new SelectionListComponent(1, 12, 198, 187, models, (gui, component) -> {
            try {
                QubbleModel model;
                if (modelImporter == null) {
                    model = QubbleModel.deserialize(CompressedStreamTools.readCompressed(new FileInputStream(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, component.getSelected() + ".qbl"))));
                } else {
                    model = modelImporter.getModel(component.getSelected(), modelImporter.read(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, component.getSelected() + "." + modelImporter.getExtension())));
                }
                currentModel = model;
                DialogHandler.INSTANCE.closeDialog(this, dialog);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        DialogHandler.INSTANCE.openDialog(this, dialog);
    }

    private void openModelImportDialog() {
        Dialog<QubbleGUI> dialog = new Dialog(this, "Import model", this.width / 2 - 100, this.height / 2 - 100, 200, 200);
        List<String> types = new ArrayList<>();
        ModelHandler.INSTANCE.getImporters().forEach(modelImporter -> types.add(modelImporter.getName()));
        dialog.addComponent(new SelectionListComponent(1, 12, 198, 187, types, (gui, component) -> {
            IModelImporter<?> importer = ModelHandler.INSTANCE.getImporter(component.getSelected());
            if (importer != null) {
                this.openModelSelectionDialog(importer);
                DialogHandler.INSTANCE.closeDialog(this, dialog);
            }
        }));
        DialogHandler.INSTANCE.openDialog(this, dialog);
    }

    private void openTextureSelectDialog() {
        Dialog<QubbleGUI> dialog = new Dialog(this, "Import texture", this.width / 2 - 100, this.height / 2 - 100, 200, 200);
        dialog.addComponent(new SelectionListComponent(1, 12, 198, 187, this.getFiles(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, ".png"), (gui, component) -> {
            try {
                InputStream stream = new FileInputStream(new File(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, component.getSelected() + ".png"));
                BufferedImage bufferedImage = ImageIO.read(stream);
                DynamicTexture dynamicTexture = new DynamicTexture(bufferedImage);
                ResourceLocation resourceLocation = ClientProxy.MINECRAFT.getTextureManager().getDynamicTextureLocation("texture_" + this.currentModel.getName(), dynamicTexture);
                this.viewComponent.texture = resourceLocation;
                DialogHandler.INSTANCE.closeDialog(this, dialog);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        DialogHandler.INSTANCE.openDialog(this, dialog);
    }

    private void openModelExportSelectDialog() {
        Dialog<QubbleGUI> dialog = new Dialog(this, "Export model", this.width / 2 - 100, this.height / 2 - 100, 200, 200);
        List<String> types = new ArrayList<>();
        ModelHandler.INSTANCE.getExporters().forEach(modelExporter -> types.add(modelExporter.getName()));
        dialog.addComponent(new SelectionListComponent(1, 12, 198, 187, types, ((gui, component) -> {
            IModelExporter<?> exporter = ModelHandler.INSTANCE.getExporter(component.getSelected());
            if (exporter != null) {
                this.openModelExportDialog(exporter);
                DialogHandler.INSTANCE.closeDialog(this, dialog);
            }
        })));
        DialogHandler.INSTANCE.openDialog(this, dialog);
    }

    private <T> void openModelExportDialog(IModelExporter<T> modelExporter) {
        int argumentY = 20;
        String[] argumentNames = modelExporter.getArgumentNames();
        String[] defaultArguments = modelExporter.getDefaultArguments(this.currentModel);
        TextBoxComponent[] argumentTextBoxes = new TextBoxComponent[argumentNames.length];
        boolean compact = argumentNames.length == 0;
        int dialogWidth = compact ? 200 : 400;
        int dialogHeight = compact ? 60 : 200;
        Dialog<QubbleGUI> dialog = new Dialog(this, "Export " + this.currentModel.getName() + " to " + modelExporter.getName(), this.width / 2 - (dialogWidth / 2), this.height / 2 - (dialogHeight / 2), dialogWidth, dialogHeight);
        for (int argumentIndex = 0; argumentIndex < argumentNames.length; argumentIndex++) {
            dialog.addComponent(new TextComponent(argumentNames[argumentIndex], 200, argumentY, getTextColor()));
            TextBoxComponent textBox = new TextBoxComponent(defaultArguments[argumentIndex], 40, argumentY + 10, 320, 20);
            dialog.addComponent(textBox);
            argumentTextBoxes[argumentIndex] = textBox;
            argumentY += 40;
        }
        dialog.addComponent(new ButtonComponent("Export", dialogWidth / 2 - 50, dialogHeight - 35, 100, 20, (gui, component) -> {
            String[] arguments = new String[argumentNames.length];
            for (int i = 0; i < argumentTextBoxes.length; i++) {
                arguments[i] = argumentTextBoxes[i].getText();
            }
            try {
                QubbleModel copy = QubbleGUI.this.currentModel.copy();
                modelExporter.save(modelExporter.export(copy, arguments), new File(ClientProxy.QUBBLE_EXPORT_DIRECTORY, copy.getFileName() + "." + modelExporter.getExtension()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            DialogHandler.INSTANCE.closeDialog(this, dialog);
        }));
        DialogHandler.INSTANCE.openDialog(this, dialog);
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
}

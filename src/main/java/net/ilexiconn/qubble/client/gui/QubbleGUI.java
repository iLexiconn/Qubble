package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.element.*;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class QubbleGUI extends GuiScreen {
    private GuiScreen parent;
    private ScaledResolution resolution;
    private ToolbarElement toolbar;
    private ModelTreeElement modelTree;
    private ModelViewElement modelView;
    private SidebarElement sidebar;
    private ProjectBarElement projectBar;

    private List<QubbleModel> openProjects = new ArrayList<>();
    private int selectedProject;
    private QubbleCube selectedCube;

    private ModelMode mode = ModelMode.MODEL;

    public QubbleGUI(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        ElementHandler.INSTANCE.clearGUI(this);
        ElementHandler.INSTANCE.addElement(this, this.modelView = new ModelViewElement(this));
        ElementHandler.INSTANCE.addElement(this, this.toolbar = new ToolbarElement(this));
        ElementHandler.INSTANCE.addElement(this, this.modelTree = new ModelTreeElement(this));
        ElementHandler.INSTANCE.addElement(this, this.sidebar = new SidebarElement(this));
        ElementHandler.INSTANCE.addElement(this, this.projectBar = new ProjectBarElement(this));
        ElementHandler.INSTANCE.init(this);
    }

    @Override
    public void updateScreen() {
        ElementHandler.INSTANCE.update(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, Qubble.CONFIG.getTertiaryColor());
        this.resolution = new ScaledResolution(this.mc);
        float preciseMouseX = this.getPreciseMouseX();
        float preciseMouseY = this.getPreciseMouseY();
        ElementHandler.INSTANCE.render(this, preciseMouseX, preciseMouseY, partialTicks);
        ElementHandler.INSTANCE.renderAfter(this, preciseMouseX, preciseMouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        float preciseMouseX = this.getPreciseMouseX();
        float preciseMouseY = this.getPreciseMouseY();
        ElementHandler.INSTANCE.mouseClicked(this, preciseMouseX, preciseMouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        float preciseMouseX = this.getPreciseMouseX();
        float preciseMouseY = this.getPreciseMouseY();
        ElementHandler.INSTANCE.mouseDragged(this, preciseMouseX, preciseMouseY, clickedMouseButton, timeSinceLastClick);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        float preciseMouseX = this.getPreciseMouseX();
        float preciseMouseY = this.getPreciseMouseY();
        ElementHandler.INSTANCE.mouseReleased(this, preciseMouseX, preciseMouseY, state);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        ElementHandler.INSTANCE.keyPressed(this, typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        ElementHandler.INSTANCE.clearGUI(this);
    }

    private float getPreciseMouseX() {
        return (float) Mouse.getX() / resolution.getScaleFactor();
    }

    private float getPreciseMouseY() {
        return this.height - (float) Mouse.getY() * this.height / (float) this.mc.displayHeight - 1.0F;
    }

    public GuiScreen getParent() {
        return parent;
    }

    public ScaledResolution getResolution() {
        return resolution;
    }

    public ToolbarElement getToolbar() {
        return toolbar;
    }

    public void drawRectangle(double x, double y, double width, double height, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        float a = (float) (color >> 24 & 0xFF) / 255.0F;
        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(x, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x, y, 0.0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawOutline(double x, double y, double width, double height, int color, double outlineSize) {
        this.drawRectangle(x, y, width - outlineSize, outlineSize, color);
        this.drawRectangle(x + width - outlineSize, y, outlineSize, height - outlineSize, color);
        this.drawRectangle(x, y + height - outlineSize, width, outlineSize, color);
        this.drawRectangle(x, y, outlineSize, height - outlineSize, color);
    }

    public void selectModel(String name, IModelImporter importer) {
        try {
            QubbleModel model;
            if (importer == null) {
                model = QubbleModel.deserialize(CompressedStreamTools.readCompressed(new FileInputStream(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, name + ".qbl"))));
            } else {
                model = importer.getModel(name, importer.read(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, name + "." + importer.getExtension())));
            }
            this.selectedProject = this.openProjects.size();
            this.openProjects.add(model);
            this.setSelectedCube(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectModel(int index) {
        this.selectedProject = Math.max(0, Math.min(this.openProjects.size() - 1, index));
        this.setSelectedCube(null);
    }

    public void closeModel(int index) {
        this.openProjects.remove(index);
        this.selectedProject = Math.max(0, Math.min(this.openProjects.size() - 1, selectedProject));
    }

    public QubbleModel getSelectedModel() {
        return this.openProjects.size() > this.selectedProject ? this.openProjects.get(this.selectedProject) : null;
    }

    public int getSelectedProject() {
        return this.selectedProject;
    }

    public void setSelectedCube(QubbleCube selectedCube) {
        this.selectedCube = selectedCube;
        if (selectedCube != null) {
            this.sidebar.populateFields(selectedCube);
        } else {
            this.sidebar.clearFields();
        }
    }

    public QubbleCube getSelectedCube() {
        return this.selectedCube;
    }

    public ModelTreeElement getModelTree() {
        return this.modelTree;
    }

    public SidebarElement getSidebar() {
        return this.sidebar;
    }

    public ModelViewElement getModelView() {
        return this.modelView;
    }

    public ProjectBarElement getProjectBar() {
        return this.projectBar;
    }

    public List<QubbleModel> getOpenProjects() {
        return this.openProjects;
    }

    public void setMode(ModelMode mode) {
        this.mode = mode;
        switch (mode) {
            case MODEL: {
                this.getSidebar().initModelView();
                break;
            }
            case TEXTURE: {
                this.getSidebar().initTextureView();
                break;
            }
            case ANIMATE: {
                this.getSidebar().initAnimateView();
                break;
            }
        }
        System.out.println(this.getModelView().getCurrentSelected());
        if (this.getModelView().getCurrentSelected() != null) {
            this.getSidebar().populateFields(this.getModelView().getCurrentSelected());
        }
    }

    public ModelMode getMode() {
        return this.mode;
    }
}

package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.gui.ElementGUI;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.element.ModelTreeElement;
import net.ilexiconn.qubble.client.gui.element.ModelViewElement;
import net.ilexiconn.qubble.client.gui.element.ProjectBarElement;
import net.ilexiconn.qubble.client.gui.element.SidebarElement;
import net.ilexiconn.qubble.client.gui.element.ToolbarElement;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class QubbleGUI extends ElementGUI {
    private GuiScreen parent;
    private ScaledResolution resolution;
    private ToolbarElement toolbar;
    private ModelTreeElement modelTree;
    private ModelViewElement modelView;
    private SidebarElement sidebar;
    private ProjectBarElement projectBar;

    private List<Project> openProjects = new ArrayList<>();
    private int selectedProject;

    private int ticks;

    private ModelMode mode = ModelMode.MODEL;

    public QubbleGUI(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initElements() {
        this.clearElements();
        this.addElement(this.modelView = new ModelViewElement(this));
        this.addElement(this.modelTree = new ModelTreeElement(this));
        this.addElement(this.sidebar = new SidebarElement(this));
        this.addElement(this.toolbar = new ToolbarElement(this));
        this.addElement(this.projectBar = new ProjectBarElement(this));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.ticks % 40 == 0) {
            Project selectedProject = this.getSelectedProject();
            if (selectedProject != null) {
                ModelTexture baseTexture = selectedProject.getBaseTexture();
                if (baseTexture != null) {
                    baseTexture.update();
                }
                ModelTexture overlay = selectedProject.getOverlayTexture();
                if (overlay != null) {
                    overlay.update();
                }
            }
        }
        this.ticks++;
    }

    @Override
    public void drawScreen(float v, float v1, float v2) {
        this.resolution = new ScaledResolution(this.mc);
    }

    public GuiScreen getParent() {
        return this.parent;
    }

    public ScaledResolution getResolution() {
        return this.resolution;
    }

    public ToolbarElement getToolbar() {
        return this.toolbar;
    }

    public void selectModel(String name, IModelImporter importer) {
        try {
            QubbleModel model;
            if (importer == null) {
                model = QubbleModel.deserialize(CompressedStreamTools.readCompressed(new FileInputStream(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, name + ".qbl"))));
            } else {
                model = importer.getModel(name, importer.read(new File(ClientProxy.QUBBLE_MODEL_DIRECTORY, name + "." + importer.getExtension())));
            }
            this.selectModel(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectModel(QubbleModel model) {
        this.openProjects.add(new Project(this, model));
        this.selectModel(this.openProjects.size() - 1);
    }

    public void selectModel(int index) {
        this.selectedProject = Math.max(0, Math.min(this.openProjects.size() - 1, index));
        this.modelView.updateModel();
        Project selectedProject = this.getSelectedProject();
        if (selectedProject != null && selectedProject.getSelectedCube() != null) {
            this.sidebar.populateFields(selectedProject.getModel(), selectedProject.getSelectedCube());
        } else {
            this.sidebar.clearFields();
        }
    }

    public void closeModel(int index) {
        Project project = this.openProjects.get(index);
        if (project != null && !project.isSaved()) {
            WindowElement<QubbleGUI> window = new WindowElement<>(this, "Project not saved!", 200, 54);
            new LabelElement<>(this, "You have unsaved changes to this", 3.0F, 16.0F).withParent(window);
            new LabelElement<>(this, "project. Would you like to save now?", 3.0F, 26.0F).withParent(window);
            new ButtonElement<>(this, "Save", 2.0F, 37.0F, 97, 15, (button) -> {
                this.removeElement(window);
                this.toolbar.openSaveWindow((saved) -> this.closeModel(index), false);
                return true;
            }).withParent(window).withColorScheme(ColorSchemes.WINDOW);
            new ButtonElement<>(this, "Discard", 101.0F, 37.0F, 97, 15, (button) -> {
                this.removeElement(window);
                project.setSaved(true);
                this.closeModel(index);
                return true;
            }).withParent(window).withColorScheme(ColorSchemes.WINDOW);
            this.addElement(window);
        } else {
            this.openProjects.remove(index);
            this.selectModel(this.selectedProject);
        }
    }

    public Project getSelectedProject() {
        return this.openProjects.size() > this.selectedProject ? this.openProjects.get(this.selectedProject) : null;
    }

    public int getSelectedProjectIndex() {
        return this.selectedProject;
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

    public List<Project> getOpenProjects() {
        return this.openProjects;
    }

    public ModelMode getMode() {
        return this.mode;
    }

    public void setMode(ModelMode mode) {
        this.mode = mode;
        this.getSidebar().initFields();
        if (this.getSelectedProject() != null && this.getSelectedProject().getSelectedCube() != null) {
            this.getSidebar().populateFields(this.getSelectedProject().getModel(), this.getSelectedProject().getSelectedCube());
        } else {
            this.getSidebar().clearFields();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.close((close) -> {
                if (close) {
                    this.mc.displayGuiScreen(null);
                    if (this.mc.currentScreen == null) {
                        this.mc.setIngameFocus();
                    }
                }
            });
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    public void close(Consumer<Boolean> callback) {
        int unsaved = 0;
        for (Project project : this.openProjects) {
            if (!project.isSaved()) {
                unsaved++;
            }
        }
        if (unsaved > 0) {
            WindowElement<QubbleGUI> window = new WindowElement<>(this, "Warning!", 200, 54);
            new LabelElement<>(this, "You have " + unsaved + " unsaved projects!", 3.0F, 16.0F).withParent(window);
            new LabelElement<>(this, "Save them now before exiting.", 3.0F, 26.0F).withParent(window);
            new ButtonElement<>(this, "Okay", 2.0F, 37.0F, 97, 15, (button) -> {
                this.removeElement(window);
                callback.accept(false);
                return true;
            }).withParent(window).withColorScheme(ColorSchemes.WINDOW);
            new ButtonElement<>(this, "Discard", 101.0F, 37.0F, 97, 15, (button) -> {
                this.removeElement(window);
                callback.accept(true);
                return true;
            }).withParent(window).withColorScheme(ColorSchemes.WINDOW);
            this.addElement(window);
        } else {
            callback.accept(true);
        }
    }
}

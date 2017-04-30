package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.gui.ElementGUI;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.qubble.client.gui.element.ModelTreeElement;
import net.ilexiconn.qubble.client.gui.element.ModelViewElement;
import net.ilexiconn.qubble.client.gui.element.ProjectBarElement;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarElement;
import net.ilexiconn.qubble.client.gui.element.toolbar.ToolbarElement;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.importer.IModelImporter;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.ilexiconn.qubble.client.project.Project;
import net.ilexiconn.qubble.client.project.action.ActionHandler;
import net.ilexiconn.qubble.client.project.action.EditAction;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private EditMode editMode = EditMode.MODEL;

    private boolean initialized;

    private CuboidWrapper<?> clipboard;

    private ActionHandler actionHandler = new ActionHandler(this);

    public QubbleGUI(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initElements() {
        this.initialized = false;
        this.clearElements();
        this.setEditMode(EditMode.MODEL);

        this.addElement(this.modelView = new ModelViewElement(this));
        this.addElement(this.modelTree = new ModelTreeElement(this));
        this.addElement(this.sidebar = new SidebarElement(this));
        this.addElement(this.toolbar = new ToolbarElement(this));
        this.addElement(this.projectBar = new ProjectBarElement(this));

        this.initialized = true;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.ticks % 40 == 0) {
            Project selectedProject = this.getSelectedProject();
            if (selectedProject != null) {
                Map<String, ModelTexture> textures = selectedProject.getModel().getTextures();
                for (Map.Entry<String, ModelTexture> entry : textures.entrySet()) {
                    entry.getValue().update();
                }
            }
        }
        this.ticks++;
    }

    @Override
    public void drawScreen(float mouseX, float mouseY, float partialTicks) {
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

    public void selectModel(String name, IModelImporter importer) throws IOException {
        ModelWrapper model = ModelHandler.INSTANCE.loadModel(name, importer);
        if (model != null) {
            this.selectModel(model);
            this.getSelectedProject().clearHistory();
        }
    }

    public <CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> void selectModel(MDL model) {
        this.openProjects.add(new Project(this, model));
        this.selectModel(this.openProjects.size() - 1);
    }

    public void selectModel(int index) {
        this.selectedProject = Math.max(0, Math.min(this.openProjects.size() - 1, index));
        Project selectedProject = this.getSelectedProject();
        this.sidebar.initFields();
        if (selectedProject != null && selectedProject.getSelectedCuboid() != null) {
            ModelWrapper model = selectedProject.getModel();
            model.rebuildModel();
            this.sidebar.enable(model, selectedProject.getSelectedCuboid());
        } else {
            this.sidebar.disable();
        }
        this.toolbar.updateElements();
    }

    public void closeModel(int index) {
        if (!this.openProjects.isEmpty()) {
            Project project = this.openProjects.get(index);
            if (project != null && project.isModified()) {
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
                    project.clearHistory();
                    this.closeModel(index);
                    return true;
                }).withParent(window).withColorScheme(ColorSchemes.WINDOW);
                this.addElement(window);
            } else {
                this.openProjects.remove(index);
                this.selectModel(this.selectedProject);
            }
        }
    }

    public Project getSelectedProject() {
        if (this.openProjects.size() > this.selectedProject) {
            return this.openProjects.get(this.selectedProject);
        }
        return null;
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

    public EditMode getEditMode() {
        return this.editMode;
    }

    public void setEditMode(EditMode editMode) {
        this.editMode = editMode;
        if (this.sidebar != null) {
            this.sidebar.initFields();
            Project selectedProject = this.getSelectedProject();
            if (selectedProject != null && selectedProject.getSelectedCuboid() != null) {
                this.sidebar.enable(selectedProject.getModel(), selectedProject.getSelectedCuboid());
            } else {
                this.sidebar.disable();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        try {
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
            } else {
                this.actionHandler.onKeyTyped(keyCode, this.getPreciseMouseX(), this.getPreciseMouseY());
            }
            super.keyTyped(typedChar, keyCode);
        } catch (Exception e) {
            GUIHelper.INSTANCE.error(this, 200, "Key press threw an exception!", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (Exception e) {
            GUIHelper.INSTANCE.error(this, 200, "Click threw an exception!", e);
            e.printStackTrace();
        }
    }

    public void close(Consumer<Boolean> callback) {
        int unsaved = 0;
        for (Project project : this.openProjects) {
            if (project.isModified()) {
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

    public void setClipboard(CuboidWrapper<?> clipboard) {
        this.clipboard = clipboard;
    }

    public CuboidWrapper<?> getClipboard() {
        return this.clipboard;
    }

    public void perform(EditAction action) {
        Project selectedProject = this.getSelectedProject();
        if (selectedProject != null) {
            try {
                selectedProject.perform(action);
            } catch (Exception e) {
                GUIHelper.INSTANCE.error(this, 200, "Action threw exception!", e);
                e.printStackTrace();
            }
        }
    }

    public static List<String> getFiles(File directory, String extension) {
        List<String> list = new ArrayList<>();
        for (File modelFile : directory.listFiles()) {
            if (modelFile.isFile() && modelFile.getName().endsWith(extension)) {
                list.add(modelFile.getName().split(extension)[0]);
            }
        }
        return list;
    }
}

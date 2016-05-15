package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.gui.ElementGUI;
import net.ilexiconn.llibrary.client.gui.element.ElementHandler;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.element.*;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        ElementHandler.INSTANCE.addElement(this, this.modelView = new ModelViewElement(this));
        ElementHandler.INSTANCE.addElement(this, this.modelTree = new ModelTreeElement(this));
        ElementHandler.INSTANCE.addElement(this, this.sidebar = new SidebarElement(this));
        ElementHandler.INSTANCE.addElement(this, this.toolbar = new ToolbarElement(this));
        ElementHandler.INSTANCE.addElement(this, this.projectBar = new ProjectBarElement(this));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (ticks % 40 == 0) {
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
        ticks++;
    }

    @Override
    public void drawScreen(float v, float v1, float v2) {
        this.resolution = new ScaledResolution(this.mc);
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
            this.sidebar.populateFields(selectedProject.getSelectedCube());
        } else {
            this.sidebar.clearFields();
        }
    }

    public void closeModel(int index) {
        this.openProjects.remove(index);
        this.selectModel(selectedProject);
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

    public void setMode(ModelMode mode) {
        this.mode = mode;
        this.getSidebar().initFields();
        if (this.getSelectedProject() != null && this.getSelectedProject().getSelectedCube() != null) {
            this.getSidebar().populateFields(this.getSelectedProject().getSelectedCube());
        } else {
            this.getSidebar().clearFields();
        }
    }

    public ModelMode getMode() {
        return this.mode;
    }
}

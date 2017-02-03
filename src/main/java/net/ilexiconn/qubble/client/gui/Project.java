package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.qubble.client.model.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

import java.util.LinkedHashMap;
import java.util.Map;

public class Project<CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> {
    private QubbleGUI gui;
    private ModelType modelType;
    private MDL model;
    private CBE selectedCube;
    private Map<String, ModelTexture> textures = new LinkedHashMap<>();
    private boolean saved;

    public Project(QubbleGUI gui, MDL model) {
        this.gui = gui;
        this.modelType = model.getType();
        this.model = model;
        this.saved = true;
    }

    public ModelType getModelType() {
        return this.modelType;
    }

    public MDL getModel() {
        return this.model;
    }

    public CBE getSelectedCuboid() {
        return this.selectedCube;
    }

    public void setSelectedCube(CBE cube) {
        this.selectedCube = cube;
        if (this.selectedCube != null) {
            this.gui.getSidebar().enable(this.getModel(), this.selectedCube);
        } else {
            this.gui.getSidebar().disable();
        }
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isSaved() {
        return this.saved;
    }
}

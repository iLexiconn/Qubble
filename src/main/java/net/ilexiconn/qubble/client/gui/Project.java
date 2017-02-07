package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.qubble.client.model.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

public class Project {
    private QubbleGUI gui;
    private ModelWrapper model;
    private CuboidWrapper selectedCuboid;
    private boolean saved;

    public Project(QubbleGUI gui, ModelWrapper model) {
        this.gui = gui;
        this.model = model;
        this.saved = true;
    }

    public ModelWrapper<?> getModel() {
        return this.model;
    }

    public CuboidWrapper<?> getSelectedCuboid() {
        return this.selectedCuboid;
    }

    public <CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> MDL getModel(ModelType<CBE, MDL> modelType) {
        if (modelType == this.model.getType()) {
            return (MDL) this.model;
        }
        return null;
    }

    public <CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> CBE getSelectedCuboid(ModelType<CBE, MDL> modelType) {
        if (modelType == this.model.getType()) {
            return (CBE) this.selectedCuboid;
        }
        return null;
    }

    public void setSelectedCuboid(CuboidWrapper cube) {
        this.selectedCuboid = cube;
        if (this.selectedCuboid != null) {
            this.gui.getSidebar().enable(this.getModel(), this.selectedCuboid);
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

    public ModelType<?, ?> getModelType() {
        return this.model.getType();
    }
}

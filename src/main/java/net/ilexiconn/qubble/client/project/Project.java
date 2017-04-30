package net.ilexiconn.qubble.client.project;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.ilexiconn.qubble.client.project.action.ActionHistory;
import net.ilexiconn.qubble.client.project.action.EditAction;

public class Project {
    private QubbleGUI gui;
    private ModelWrapper model;
    private CuboidWrapper selectedCuboid;
    private ActionHistory history = new ActionHistory(128);
    private ActionHistory undoHistory = new ActionHistory(128);
    private int actionIndex = 1;

    public Project(QubbleGUI gui, ModelWrapper model) {
        this.gui = gui;
        this.model = model;
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

    public boolean isModified() {
        return this.actionIndex != 0;
    }

    public void perform(EditAction action) throws Exception {
        this.history.push(action);
        this.undoHistory.clear();
        this.actionIndex++;
        action.perform();
    }

    public void undo() throws Exception {
        EditAction action = this.history.pop();
        if (action != null) {
            action.undo();
            this.undoHistory.push(action);
            this.actionIndex--;
        }
    }

    public void redo() throws Exception {
        EditAction action = this.undoHistory.pop();
        if (action != null) {
            this.history.push(action);
            action.perform();
            this.actionIndex++;
        }
    }

    public ModelType<?, ?> getModelType() {
        return this.model.getType();
    }

    public void clearHistory() {
        this.actionIndex = 0;
        this.undoHistory.clear();
        this.history.clear();
    }
}

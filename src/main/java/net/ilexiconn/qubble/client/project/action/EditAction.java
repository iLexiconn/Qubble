package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.project.Project;

public abstract class EditAction {
    protected QubbleGUI gui;
    protected Project project;

    public EditAction(QubbleGUI gui) {
        this.gui = gui;
        this.project = gui.getSelectedProject();
    }

    public abstract void perform() throws Exception;

    public abstract void undo() throws Exception;
}

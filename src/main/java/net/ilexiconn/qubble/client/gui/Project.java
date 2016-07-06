package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;

public class Project {
    private QubbleGUI gui;
    private QubbleModel model;
    private QubbleCuboid selectedCube;
    private ModelTexture baseTexture;
    private ModelTexture overlayTexture;

    public Project(QubbleGUI gui, QubbleModel model) {
        this.gui = gui;
        this.model = model;
    }

    public QubbleModel getModel() {
        return this.model;
    }

    public ModelTexture getBaseTexture() {
        return this.baseTexture;
    }

    public void setBaseTexture(ModelTexture texture) {
        this.baseTexture = texture;
    }

    public ModelTexture getOverlayTexture() {
        return this.overlayTexture;
    }

    public void setOverlayTexture(ModelTexture texture) {
        this.overlayTexture = texture;
    }

    public QubbleCuboid getSelectedCube() {
        return selectedCube;
    }

    public void setSelectedCube(QubbleCuboid cube) {
        this.selectedCube = cube;
        if (this.selectedCube != null) {
            this.gui.getSidebar().populateFields(this.getModel(), selectedCube);
        } else {
            this.gui.getSidebar().clearFields();
        }
    }
}

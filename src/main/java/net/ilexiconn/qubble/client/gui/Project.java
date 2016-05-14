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

    public void setBaseTexture(ModelTexture texture) {
        this.baseTexture = texture;
    }

    public void setOverlayTexture(ModelTexture texture) {
        this.overlayTexture = texture;
    }

    public ModelTexture getBaseTexture() {
        return this.baseTexture;
    }

    public ModelTexture getOverlayTexture() {
        return this.overlayTexture;
    }

    public QubbleCuboid getSelectedCube() {
        return selectedCube;
    }

    public void setSelectedCube(QubbleCuboid cube) {
        this.selectedCube = cube;
        if (this.selectedCube != null) {
            this.gui.getSidebar().populateFields(selectedCube);
        } else {
            this.gui.getSidebar().clearFields();
        }
    }
}

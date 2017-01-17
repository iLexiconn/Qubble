package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;

public class Project {
    private QubbleGUI gui;
    private QubbleModel model;
    private QubbleCuboid selectedCube;
    private ModelTexture baseTexture;
    private ModelTexture overlayTexture;
    private boolean saved;

    public Project(QubbleGUI gui, QubbleModel model) {
        this.gui = gui;
        this.model = model;
        this.saved = true;
    }

    public QubbleModel getModel() {
        return this.model;
    }

    public ModelTexture getBaseTexture() {
        return this.baseTexture;
    }

    public void setBaseTexture(ModelTexture texture) {
        this.baseTexture = texture;
        this.setSaved(false);
    }

    public ModelTexture getOverlayTexture() {
        return this.overlayTexture;
    }

    public void setOverlayTexture(ModelTexture texture) {
        this.overlayTexture = texture;
        this.setSaved(false);
    }

    public QubbleCuboid getSelectedCube() {
        return this.selectedCube;
    }

    public void setSelectedCube(QubbleCuboid cube) {
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

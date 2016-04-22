package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;

public class Project {
    private QubbleModel model;
    private ModelTexture baseTexture;
    private ModelTexture overlayTexture;

    public Project(QubbleModel model) {
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
}

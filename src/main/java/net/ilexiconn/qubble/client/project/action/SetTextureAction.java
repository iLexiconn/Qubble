package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

public class SetTextureAction extends EditAction {
    private final String name;
    private final ModelTexture texture;
    private ModelTexture lastTexture;

    public SetTextureAction(QubbleGUI gui, String name, ModelTexture texture) {
        super(gui);
        this.name = name;
        this.texture = texture;
    }

    @Override
    public void perform() throws Exception {
        ModelWrapper model = this.project.getModel();
        this.lastTexture = model.getTexture(this.name);
        model.setTexture(this.name, this.texture);
    }

    @Override
    public void undo() throws Exception {
        ModelWrapper model = this.project.getModel();
        model.setTexture(this.name, this.lastTexture);
    }
}

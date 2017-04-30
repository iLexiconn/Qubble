package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.project.ModelType;

public class RenameTextureAction extends EditAction {
    private final String initial;
    private final String rename;

    public RenameTextureAction(QubbleGUI gui, String initial, String rename) {
        super(gui);
        this.initial = initial;
        this.rename = rename;
    }

    @Override
    public void perform() throws Exception {
        BlockModelWrapper model = this.project.getModel(ModelType.BLOCK);
        model.renameTexture(this.initial, this.rename);
    }

    @Override
    public void undo() throws Exception {
        BlockModelWrapper model = this.project.getModel(ModelType.BLOCK);
        model.renameTexture(this.rename, this.initial);
    }
}

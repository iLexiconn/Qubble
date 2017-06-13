package net.ilexiconn.qubble.client.project.action;

import net.ilexiconn.qubble.client.gui.GUIHelper;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.model.ModelHandler;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.ilexiconn.qubble.client.project.Project;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class ActionHandler {
    private final QubbleGUI gui;

    public ActionHandler(QubbleGUI gui) {
        this.gui = gui;
    }

    public void onKeyTyped(int key, float mouseX, float mouseY) {
        Project project = this.gui.getSelectedProject();
        if (project != null) {
            ModelWrapper model = project.getModel();
            CuboidWrapper selectedCuboid = project.getSelectedCuboid();
            if (GuiScreen.isCtrlKeyDown()) {
                if (key == Keyboard.KEY_Z) {
                    try {
                        project.undo();
                    } catch (Exception e) {
                        GUIHelper.INSTANCE.error(this.gui, 200, "Failed to undo action", e);
                        e.printStackTrace();
                    }
                } else if (key == Keyboard.KEY_Y) {
                    try {
                        project.redo();
                    } catch (Exception e) {
                        GUIHelper.INSTANCE.error(this.gui, 200, "Failed to redo action", e);
                        e.printStackTrace();
                    }
                } else if (key == Keyboard.KEY_N) {
                    String name = ModelHandler.INSTANCE.getCopyName(model, "Cuboid");
                    this.gui.perform(new CreateCuboidAction(this.gui, name));
                } else if (GuiScreen.isKeyComboCtrlC(key)) {
                    CuboidWrapper clipboard = selectedCuboid.copyRaw();
                    if (model.supportsParenting()) {
                        float[][] transformation = ModelHandler.INSTANCE.getParentTransformation(model, selectedCuboid, true, false);
                        ModelHandler.INSTANCE.applyTransformation(clipboard, transformation);
                    }
                    this.gui.setClipboard(clipboard);
                } else if (GuiScreen.isKeyComboCtrlV(key) && this.gui.getClipboard() != null) {
                    this.perform(new PasteCuboidAction(this.gui, this.gui.getClipboard()));
                }
            }
            if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_DELETE) {
                if (selectedCuboid != null && (this.gui.getModelView().isSelected(mouseX, mouseY) || this.gui.getModelTree().isSelected(mouseX, mouseY))) {
                    this.perform(new RemoveCuboidAction(this.gui, selectedCuboid.getName()));
                }
            }
        }
    }

    private void perform(EditAction action) {
        try {
            this.gui.getSelectedProject().perform(action);
        } catch (Exception e) {
            GUIHelper.INSTANCE.error(this.gui, 200, "Failed to perform action", e);
            e.printStackTrace();
        }
    }
}

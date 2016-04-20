package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ProjectBarElement extends Element<QubbleGUI> {
    private int scroll;

    public ProjectBarElement(QubbleGUI gui) {
        super(gui, 0.0F, gui.getToolbar().getHeight(), 0, 12);
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {
        ModelTreeElement modelTree = this.getGUI().getModelTree();
        SidebarElement sideBar = this.getGUI().getSidebar();
        this.setPosX(modelTree.getWidth());
        this.setWidth(this.getGUI().width - modelTree.getWidth() - sideBar.getWidth());
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        QubbleGUI gui = this.getGUI();
        float posX = this.getPosX();
        float posY = this.getPosY();
        int width = this.getWidth();
        int height = this.getHeight();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        float scaleFactor = gui.getResolution().getScaleFactor();
        GL11.glScissor((int) (posX * scaleFactor), (int) (((gui.height - (posY + height))) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        gui.drawRectangle(posX, posY, width, height, Qubble.CONFIG.getPrimaryColor());
        float projectX = -this.scroll;
        List<QubbleModel> openProjects = gui.getOpenProjects();
        for (int projectIndex = 0; projectIndex < openProjects.size(); projectIndex++) {
            QubbleModel model = openProjects.get(projectIndex);
            float projectWidth = fontRenderer.getStringWidth(model.getName()) + 15.0F;
            boolean hover = mouseX >= posX + projectX + projectWidth - 12 && mouseX <= posX + projectX + projectWidth && mouseY >= posY && mouseY < posY + height;
            if (projectIndex == gui.getSelectedProject()) {
                gui.drawRectangle(posX + projectX, posY, projectWidth, height, Qubble.CONFIG.getTertiaryColor());
                gui.drawRectangle(posX + projectX + projectWidth - 12, posY, 12, 12, hover ? 0xFFE04747 : Qubble.CONFIG.getTertiaryColor());
            } else {
                gui.drawRectangle(posX + projectX + projectWidth - 12, posY, 12, 12, hover ? 0xFFE04747 : Qubble.CONFIG.getPrimaryColor());
            }
            fontRenderer.drawString("x", posX + projectX + projectWidth - 9, posY + 2, Qubble.CONFIG.getTextColor(), false);
            fontRenderer.drawString(model.getName(), posX + projectX + 2, posY + 2, Qubble.CONFIG.getTextColor(), false);
            projectX += projectWidth;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0) {
            QubbleGUI gui = this.getGUI();
            float posX = this.getPosX();
            int width = this.getWidth();
            FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
            if (this.isSelected(mouseX, mouseY)) {
                float projectX = -this.scroll;
                List<QubbleModel> openProjects = gui.getOpenProjects();
                boolean selected = false;
                for (int projectIndex = 0; projectIndex < openProjects.size(); projectIndex++) {
                    QubbleModel model = openProjects.get(projectIndex);
                    String name = model.getName() + " x";
                    float projectWidth = fontRenderer.getStringWidth(name) + 3.0F;
                    if (mouseX >= posX + projectX && mouseX < posX + projectX + projectWidth) {
                        if (mouseX > posX + projectX + projectWidth - 12) {
                            gui.closeModel(projectIndex);
                        } else {
                            gui.selectModel(projectIndex);
                        }
                        if (projectX + projectWidth >= width) {
                            this.scroll = (int) ((projectX - this.scroll) - (projectWidth / 2));
                        } else if (projectX < 0.0F) {
                            this.scroll = (int) (((projectX + projectWidth) - this.scroll) + (projectWidth / 2));
                        }
                        selected = true;
                    }
                    projectX += projectWidth + 1.0F;
                }
                this.scroll = Math.max(0, this.scroll);
                return selected;
            }
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return this.getGUI().getOpenProjects().size() > 0;
    }
}

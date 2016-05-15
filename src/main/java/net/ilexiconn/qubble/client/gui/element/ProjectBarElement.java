package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.Project;
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
        float posX = this.getPosX();
        float posY = this.getPosY();
        int width = this.getWidth();
        int height = this.getHeight();
        this.startScissor();
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        this.drawRectangle(posX, posY, width, height, LLibrary.CONFIG.getPrimaryColor());
        float projectX = -this.scroll;
        List<Project> openProjects = this.getGUI().getOpenProjects();
        for (int projectIndex = 0; projectIndex < openProjects.size(); projectIndex++) {
            QubbleModel model = openProjects.get(projectIndex).getModel();
            float projectWidth = fontRenderer.getStringWidth(model.getName()) + 15.0F;
            boolean hover = this.isSelected(mouseX, mouseY) && mouseX >= posX + projectX + projectWidth - 12 && mouseX <= posX + projectX + projectWidth;
            if (projectIndex == this.getGUI().getSelectedProjectIndex()) {
                this.drawRectangle(posX + projectX, posY, projectWidth, height, LLibrary.CONFIG.getTertiaryColor());
                this.drawRectangle(posX + projectX + projectWidth - 12, posY, 12, 12, hover ? 0xFFE04747 : LLibrary.CONFIG.getTertiaryColor());
            } else {
                this.drawRectangle(posX + projectX + projectWidth - 12, posY, 12, 12, hover ? 0xFFE04747 : LLibrary.CONFIG.getPrimaryColor());
            }
            fontRenderer.drawString("x", posX + projectX + projectWidth - 9, posY + 2, LLibrary.CONFIG.getTextColor(), false);
            fontRenderer.drawString(model.getName(), posX + projectX + 2, posY + 2, LLibrary.CONFIG.getTextColor(), false);
            projectX += projectWidth;
        }
        this.endScissor();
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
                List<Project> openProjects = gui.getOpenProjects();
                boolean selected = false;
                for (int projectIndex = 0; projectIndex < openProjects.size(); projectIndex++) {
                    QubbleModel model = openProjects.get(projectIndex).getModel();
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

package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.project.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarElement;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.minecraft.client.gui.FontRenderer;

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
        ModelTreeElement modelTree = this.gui.getModelTree();
        SidebarElement sideBar = this.gui.getSidebar();
        this.setPosX(modelTree.getWidth());
        this.setWidth(this.gui.width - modelTree.getWidth() - sideBar.getWidth());
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
        List<Project> openProjects = this.gui.getOpenProjects();
        for (int projectIndex = 0; projectIndex < openProjects.size(); projectIndex++) {
            Project project = openProjects.get(projectIndex);
            ModelWrapper model = project.getModel();
            String name = model.getName();
            if (project.isModified()) {
                name = "* " + name;
            }
            float projectWidth = fontRenderer.getStringWidth(name) + 15.0F;
            boolean hover = this.isSelected(mouseX, mouseY) && mouseX >= posX + projectX + projectWidth - 12 && mouseX <= posX + projectX + projectWidth;
            if (projectIndex == this.gui.getSelectedProjectIndex()) {
                this.drawRectangle(posX + projectX, posY, projectWidth, height, LLibrary.CONFIG.getTertiaryColor());
                this.drawRectangle(posX + projectX + projectWidth - 12, posY, 12, 12, hover ? 0xFFE04747 : LLibrary.CONFIG.getTertiaryColor());
            } else {
                this.drawRectangle(posX + projectX + projectWidth - 12, posY, 12, 12, hover ? 0xFFE04747 : LLibrary.CONFIG.getPrimaryColor());
            }
            fontRenderer.drawString("x", posX + projectX + projectWidth - 9, posY + 2, LLibrary.CONFIG.getTextColor(), false);
            fontRenderer.drawString(name, posX + projectX + 2, posY + 2, LLibrary.CONFIG.getTextColor(), false);
            projectX += projectWidth;
        }
        this.endScissor();
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0) {
            QubbleGUI gui = this.gui;
            float posX = this.getPosX();
            int width = this.getWidth();
            FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
            if (this.isSelected(mouseX, mouseY)) {
                float projectX = -this.scroll;
                List<Project> openProjects = gui.getOpenProjects();
                boolean selected = false;
                for (int projectIndex = 0; projectIndex < openProjects.size(); projectIndex++) {
                    Project project = openProjects.get(projectIndex);
                    ModelWrapper model = project.getModel();
                    String name = model.getName();
                    if (project.isModified()) {
                        name = "* " + name;
                    }
                    float projectWidth = fontRenderer.getStringWidth(name) + 15.0F;
                    if (mouseX >= posX + projectX && mouseX < posX + projectX + projectWidth) {
                        if (mouseX >= posX + projectX + projectWidth - 12) {
                            gui.closeModel(projectIndex);
                        } else {
                            gui.selectModel(projectIndex);
                        }
                        this.gui.playClickSound();
                        if (projectX + projectWidth >= width) {
                            this.scroll = (int) ((projectX - this.scroll) - (projectWidth / 2));
                        } else if (projectX < 0.0F) {
                            this.scroll = (int) (((projectX + projectWidth) - this.scroll) + (projectWidth / 2));
                        }
                        selected = true;
                        break;
                    }
                    projectX += projectWidth;
                }
                this.scroll = Math.max(0, this.scroll);
                return selected;
            }
        }
        return false;
    }
}

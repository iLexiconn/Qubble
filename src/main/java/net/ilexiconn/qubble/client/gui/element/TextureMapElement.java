package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TextureMapElement extends Element<QubbleGUI> {
    private int dragOffsetX;
    private int dragOffsetY;

    public TextureMapElement(QubbleGUI gui, float posX, float posY, int width, int height) {
        super(gui, posX, posY, width, height);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        Project selectedProject = this.getGUI().getSelectedProject();
        if (selectedProject != null) {
            ModelTexture texture = selectedProject.getBaseTexture();
            GlStateManager.translate(this.getPosX(), this.getPosY(), 0.0F);
            QubbleModel model = selectedProject.getModel();
            String dimensions = model.getTextureWidth() + "x" + model.getTextureHeight();
            fontRenderer.drawString(dimensions, this.getWidth() - fontRenderer.getStringWidth(dimensions) - 1, this.getHeight() - fontRenderer.FONT_HEIGHT, LLibrary.CONFIG.getTextColor());
            float scale = this.getScale(model);
            GlStateManager.scale(scale, scale, 1.0F);
            if (texture != null) {
                ResourceLocation textureLocation = texture.getLocation();
                ClientProxy.MINECRAFT.getTextureManager().bindTexture(textureLocation);
                GlStateManager.enableTexture2D();
                this.drawTexturedRectangle(1.0F, 1.0F, model.getTextureWidth(), model.getTextureHeight(), 0xFFFFFFFF);
            }
            boolean alpha = texture != null;
            for (QubbleCuboid cube : model.getCuboids()) {
                this.drawCube(cube, alpha);
            }
            QubbleCuboid selectedCube = selectedProject.getSelectedCube();
            if (selectedCube != null) {
                int textureX = selectedCube.getTextureX();
                int textureY = selectedCube.getTextureY();
                int dimensionX = selectedCube.getDimensionX();
                int dimensionY = selectedCube.getDimensionY();
                int dimensionZ = selectedCube.getDimensionZ();
                int outlineColor = LLibrary.CONFIG.getAccentColor();
                this.fillRect(textureX, textureY + dimensionZ - 0.5F, dimensionZ, 0.5F, outlineColor);
                this.fillRect(textureX + dimensionX + dimensionZ + dimensionX, textureY + dimensionZ - 0.5F, dimensionZ, 0.5F, outlineColor);
                this.fillRect(textureX, textureY + dimensionZ + dimensionY, dimensionZ + dimensionX + dimensionZ + dimensionX, 0.5F, outlineColor);
                this.fillRect(textureX - 0.5F, textureY + dimensionZ - 0.5F, 0.5F, dimensionY + 1, outlineColor);
                this.fillRect(textureX + dimensionX + dimensionZ + dimensionX + dimensionZ, textureY + dimensionZ - 0.5F, 0.5F, dimensionY + 1, outlineColor);
                this.fillRect(textureX + dimensionZ, textureY - 0.5F, dimensionX + dimensionX, 0.5F, outlineColor);
                this.fillRect(textureX + dimensionZ - 0.5F, textureY - 0.5F, 0.5F, dimensionZ + 0.5F, outlineColor);
                this.fillRect(textureX + dimensionZ + dimensionX + dimensionX, textureY - 0.5F, 0.5F, dimensionZ + 0.5F, outlineColor);
            }
        } else {
            String text = "No Project Loaded";
            fontRenderer.drawString(text, this.getPosX() + (this.getWidth() / 2) - (fontRenderer.getStringWidth(text) / 2), this.getPosY() + (this.getHeight() / 2) - (fontRenderer.FONT_HEIGHT / 2), LLibrary.CONFIG.getTextColor(), false);
        }
        GlStateManager.popMatrix();
    }

    private float getScale(QubbleModel model) {
        int displayWidth = this.getWidth();
        int displayHeight = this.getHeight();
        float scale = 0.0F;
        while (displayWidth / (scale + 0.25F) >= model.getTextureWidth() && displayHeight / (scale + 0.25F) >= model.getTextureHeight()) {
            scale += 0.25F;
        }
        return scale;
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (this.isSelected(mouseX, mouseY)) {
            Project selectedProject = this.getGUI().getSelectedProject();
            if (selectedProject != null) {
                QubbleCuboid selectedCube = this.getSelectedCube(mouseX, mouseY, selectedProject);
                selectedProject.setSelectedCube(selectedCube);
                if (selectedCube != null) {
                    float scale = this.getScale(selectedProject.getModel());
                    this.dragOffsetX = (int) (selectedCube.getTextureX() - ((mouseX - this.getPosX()) / scale));
                    this.dragOffsetY = (int) (selectedCube.getTextureY() - ((mouseY - this.getPosY()) / scale));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.isSelected(mouseX, mouseY)) {
            Project selectedProject = this.getGUI().getSelectedProject();
            if (selectedProject != null) {
                QubbleCuboid selectedCube = selectedProject.getSelectedCube();
                if (selectedCube != null) {
                    float scale = this.getScale(selectedProject.getModel());
                    selectedCube.setTexture((int) ((mouseX - this.getPosX()) / scale) + this.dragOffsetX, (int) ((mouseY - this.getPosY()) / scale) + this.dragOffsetY);
                    this.getGUI().getModelView().updatePart(selectedCube);
                    this.getGUI().getSidebar().populateFields(selectedCube);
                }
                return true;
            }
        }
        return false;
    }

    private QubbleCuboid getSelectedCube(float mouseX, float mouseY, Project selectedProject) {
        float scale = this.getScale(selectedProject.getModel());
        QubbleCuboid selected = null;
        for (QubbleCuboid cube : selectedProject.getModel().getCuboids()) {
            QubbleCuboid clicked = this.checkCubeClick(cube, mouseX, mouseY, scale);
            if (clicked != null) {
                selected = clicked;
                break;
            }
        }
        return selected;
    }

    private QubbleCuboid checkCubeClick(QubbleCuboid cube, float mouseX, float mouseY, float scale) {
        int textureX = cube.getTextureX();
        int textureY = cube.getTextureY();
        int dimensionX = cube.getDimensionX();
        int dimensionY = cube.getDimensionY();
        int dimensionZ = cube.getDimensionZ();
        if (this.check(mouseX, mouseY, textureX, textureY + dimensionZ, dimensionX + dimensionZ + dimensionX + dimensionZ, dimensionY, scale) || this.check(mouseX, mouseY, textureX + dimensionZ, textureY, dimensionX + dimensionX, dimensionZ, scale)) {
            return cube;
        }
        for (QubbleCuboid child : cube.getChildren()) {
            QubbleCuboid clicked = this.checkCubeClick(child, mouseX, mouseY, scale);
            if (clicked != null) {
                return clicked;
            }
        }
        return null;
    }

    private void drawCube(QubbleCuboid cube, boolean alpha) {
        int textureX = cube.getTextureX();
        int textureY = cube.getTextureY();
        int dimensionX = cube.getDimensionX();
        int dimensionY = cube.getDimensionY();
        int dimensionZ = cube.getDimensionZ();
        this.fillRect(textureX + dimensionZ, textureY, dimensionX, dimensionZ, alpha ? 0x2200FF00 : 0xFF00FF00);
        this.fillRect(textureX + dimensionX + dimensionZ, textureY, dimensionX, dimensionZ, alpha ? 0x2200AA00 : 0xFF00AA00);
        this.fillRect(textureX, textureY + dimensionZ, dimensionZ, dimensionY, alpha ? 0x22FF0000 : 0xFFFF0000);
        this.fillRect(textureX + dimensionZ, textureY + dimensionZ, dimensionX, dimensionY, alpha ? 0x220000FF : 0xFF0000FF);
        this.fillRect(textureX + dimensionX + dimensionZ, textureY + dimensionZ, dimensionZ, dimensionY, alpha ? 0x22AA0000 : 0xFFAA0000);
        this.fillRect(textureX + dimensionX + dimensionZ + dimensionZ, textureY + dimensionZ, dimensionX, dimensionY, alpha ? 0x220000AA : 0xFF0000AA);
        for (QubbleCuboid child : cube.getChildren()) {
            this.drawCube(child, alpha);
        }
    }

    private boolean check(float mouseX, float mouseY, int x, int y, int width, int height, float scale) {
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;
        x += this.getPosX() + 1;
        y += this.getPosY() + 1;
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void fillRect(float x, float y, float width, float height, int color) {
        this.drawRectangle(x + 1, y + 1, width, height, color);
    }
}

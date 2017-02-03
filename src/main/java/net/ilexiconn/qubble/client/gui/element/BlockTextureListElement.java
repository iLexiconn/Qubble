package net.ilexiconn.qubble.client.gui.element;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.ilexiconn.llibrary.client.gui.element.ScrollbarElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.List;
import java.util.Map;

public class BlockTextureListElement extends Element<QubbleGUI> {
    private ScrollbarElement<QubbleGUI> scrollbar;
    private Project<BlockCuboidWrapper, BlockModelWrapper> project;

    public BlockTextureListElement(QubbleGUI gui, float posX, float posY, int width, int height, Project<BlockCuboidWrapper, BlockModelWrapper> project) {
        super(gui, posX, posY, width, height);
        this.project = project;
    }

    @Override
    public void init() {
        this.scrollbar = new ScrollbarElement<>(this, () -> (float) this.getWidth() - 6.0F, () -> 2.0F, () -> (float) this.getHeight(), 58, () -> (int) Math.ceil((float) this.project.getModel().getTextures().size() / 4));
        new ButtonElement<>(this.gui, "+", 0.0F, this.getHeight(), 14, 14, button -> {
            WindowElement<QubbleGUI> selectTextureWindow = new WindowElement<>(this.gui, "Add Texture", 200, 200);
            List<String> files = QubbleGUI.getFiles(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, ".png");
            selectTextureWindow.addElement(new ListElement<>(this.gui, 2, 16, 196, 182, files, (list) -> {
                if (this.project != null) {
                    String entry = list.getSelectedEntry();
                    ModelTexture texture = new ModelTexture(new File(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, entry + ".png"), entry);
                    BlockModelWrapper model = this.project.getModel();
                    model.setTexture(entry, texture);
                    this.project.setSaved(false);
                    this.gui.removeElement(selectTextureWindow);
                }
                return true;
            }));
            this.gui.addElement(selectTextureWindow);
            return true;
        }).withParent(this).withColorScheme(ColorSchemes.TOGGLE_ON);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        ScaledResolution resolution = this.gui.getResolution();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = resolution.getScaleFactor();
        GL11.glScissor((int) (this.getPosX() * scaleFactor), (int) ((this.gui.getHeight() - (this.getPosY() + this.getHeight() - 2)) * scaleFactor), this.getWidth() * scaleFactor, (this.getHeight() - 4) * scaleFactor);
        int size = 48;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -this.scrollbar.getScrollOffset(), 0.0F);
        GlStateManager.translate(this.getPosX(), this.getPosY(), 0.0F);
        float renderMouseX = mouseX - this.getPosX();
        float renderMouseY = (mouseY - this.getPosY()) + this.scrollbar.getScrollOffset();
        FontRenderer fontRenderer = this.gui.getFontRenderer();
        Map<String, ModelTexture> textures = this.project.getModel().getTextures();
        boolean mouseSelected = mouseY - this.getPosY() > 0 && mouseY - this.getPosY() < this.getHeight();
        int x = 0;
        int y = 0;
        for (Map.Entry<String, ModelTexture> entry : textures.entrySet()) {
            float renderX = x * size + 2;
            float renderY = y * (size + 10) + 2;
            if (mouseSelected && renderMouseX > renderX && renderMouseY > renderY && renderMouseX < renderX + size && renderMouseY < renderY + size) {
                this.drawRectangle(renderX, renderY, size - 2, size - 2, LLibrary.CONFIG.getTertiaryColor());
            } else {
                this.drawRectangle(renderX, renderY, size - 2, size - 2, LLibrary.CONFIG.getSecondaryColor());
            }
            ClientProxy.MINECRAFT.getTextureManager().bindTexture(entry.getValue().getLocation());
            this.drawTexturedRectangle(renderX + 2, renderY + 2, size - 6, size - 6, 0xFFFFFFFF);
            GlStateManager.enableTexture2D();
            fontRenderer.drawStringWithShadow(fontRenderer.trimStringToWidth(entry.getKey(), size), renderX, renderY + size, 0xFFFFFF);
            if (++x >= 4) {
                x = 0;
                y++;
            }
        }
        GlStateManager.popMatrix();
        this.endScissor();
        if (mouseSelected) {
            x = 0;
            y = 0;
            for (Map.Entry<String, ModelTexture> entry : textures.entrySet()) {
                float renderX = x * size + 2;
                float renderY = y * (size + 10) + 2;
                if (renderMouseX > renderX && renderMouseY > renderY && renderMouseX < renderX + size && renderMouseY < renderY + size) {
                    GuiUtils.drawHoveringText(Lists.newArrayList(entry.getKey()), (int) mouseX, (int) mouseY, this.gui.width, this.gui.height, 100, fontRenderer);
                    GlStateManager.disableLighting();
                    break;
                }
                if (++x >= 4) {
                    x = 0;
                    y++;
                }
            }
        }
        this.drawRectangle(this.getPosX(), this.getPosY() + this.getHeight(), this.getWidth(), 14, LLibrary.CONFIG.getAccentColor());
    }

    @Override
    public int getHeight() {
        return super.getHeight() - 14;
    }
}

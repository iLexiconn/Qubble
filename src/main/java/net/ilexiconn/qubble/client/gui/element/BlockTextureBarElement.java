package net.ilexiconn.qubble.client.gui.element;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.ilexiconn.llibrary.client.gui.element.ScrollbarElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.QubbleIcons;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.List;
import java.util.Map;

public class BlockTextureBarElement extends Element<QubbleGUI> implements ModelViewAdapter {
    private static final int TILE_SIZE = 48;

    private ScrollbarElement<QubbleGUI> scrollbar;
    private ButtonElement<QubbleGUI> add;
    private Project<BlockCuboidWrapper, BlockModelWrapper> project;
    private int countX;
    private boolean resizing;

    private Tuple<String, Boolean> clickedTexture;
    private boolean draggedTexture;

    public BlockTextureBarElement(QubbleGUI gui, Project<BlockCuboidWrapper, BlockModelWrapper> project) {
        super(gui, gui.getModelTree().getPosX(), gui.height - 100, (int) (gui.getSidebar().getPosX() - gui.getModelTree().getPosX()), 100);
        this.project = project;
    }

    @Override
    public void init() {
        this.scrollbar = new ScrollbarElement<>(this, () -> (float) this.getWidth() - 6.0F, () -> 2.0F, () -> (float) this.getHeight(), TILE_SIZE + 10, () -> (int) Math.ceil((float) this.project.getModel().getTextures().size() / this.countX));
        this.add = (ButtonElement<QubbleGUI>) new ButtonElement<>(this.gui, "+", 0.0F, this.getHeight(), 16, 16, button -> {
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
    public void update() {
        this.setPosX(this.gui.getModelTree().getPosX() + this.gui.getModelTree().getWidth());
        this.setWidth((int) (this.gui.getSidebar().getPosX() - this.getPosX()));
        this.add.setPosY(this.getHeight());
        this.countX = Math.max(1, this.getWidth() / TILE_SIZE);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), LLibrary.CONFIG.getPrimaryColor());
        this.drawRectangle(this.getPosX(), this.getPosY() - 2, this.getWidth(), 2, LLibrary.CONFIG.getAccentColor());
        ScaledResolution resolution = this.gui.getResolution();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = resolution.getScaleFactor();
        GL11.glScissor((int) (this.getPosX() * scaleFactor), (int) ((this.gui.getHeight() - (this.getPosY() + this.getHeight() - 2)) * scaleFactor), this.getWidth() * scaleFactor, (this.getHeight() - 4) * scaleFactor);
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
            float renderX = x * TILE_SIZE + 2;
            float renderY = y * (TILE_SIZE + 10) + 2;
            boolean selected = mouseSelected && renderMouseX > renderX && renderMouseY > renderY && renderMouseX < renderX + TILE_SIZE && renderMouseY < renderY + TILE_SIZE;
            if (selected) {
                this.drawRectangle(renderX, renderY, TILE_SIZE - 2, TILE_SIZE - 2, LLibrary.CONFIG.getTertiaryColor());
            } else {
                this.drawRectangle(renderX, renderY, TILE_SIZE - 2, TILE_SIZE - 2, LLibrary.CONFIG.getSecondaryColor());
            }
            ClientProxy.MINECRAFT.getTextureManager().bindTexture(entry.getValue().getLocation());
            this.drawTexturedRectangle(renderX + 2, renderY + 2, TILE_SIZE - 6, TILE_SIZE - 6, 0xFFFFFFFF);
            if (selected) {
                int closeSize = 12;
                float closeX = renderX + TILE_SIZE - (closeSize + 4);
                float closeY = renderY + 2;
                if (renderMouseX > closeX && renderMouseY > closeY && renderMouseX < closeX + closeSize && renderMouseY < closeY + closeSize) {
                    this.drawRectangle(closeX, closeY, closeSize, closeSize, 0xFFE01324);
                } else {
                    this.drawRectangle(closeX, closeY, closeSize, closeSize, 0xFFE04747);
                }
                GlStateManager.enableTexture2D();
                fontRenderer.drawString("x", closeX + closeSize / 2 - fontRenderer.getCharWidth('x') / 2, closeY + closeSize / 2 - fontRenderer.FONT_HEIGHT / 2, LLibrary.CONFIG.getTextColor(), false);
            }
            GlStateManager.enableTexture2D();
            fontRenderer.drawString(fontRenderer.trimStringToWidth(entry.getKey(), TILE_SIZE), renderX, renderY + TILE_SIZE, LLibrary.CONFIG.getTextColor(), false);
            if (entry.getKey().equals("particle")) {
                ClientProxy.MINECRAFT.getTextureManager().bindTexture(QubbleIcons.PARTICLE);
                this.drawTexturedRectangle(renderX + 2, renderY + 2, 16, 16, 0xA0000000);
                this.drawTexturedRectangle(renderX + 1, renderY + 1, 16, 16, 0xFFFFFFFF);
            }
            if (++x >= this.countX) {
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
                float renderX = x * TILE_SIZE + 2;
                float renderY = y * (TILE_SIZE + 10) + 2;
                if (renderMouseX > renderX && renderMouseY > renderY && renderMouseX < renderX + TILE_SIZE && renderMouseY < renderY + TILE_SIZE) {
                    GuiUtils.drawHoveringText(Lists.newArrayList(entry.getKey()), (int) mouseX, (int) mouseY, this.gui.width, this.gui.height, 100, fontRenderer);
                    GlStateManager.disableLighting();
                    break;
                }
                if (++x >= this.countX) {
                    x = 0;
                    y++;
                }
            }
        }
        this.drawRectangle(this.getPosX(), this.getPosY() + this.getHeight(), this.getWidth(), 16, LLibrary.CONFIG.getAccentColor());
        if (this.draggedTexture) {
            ModelTexture texture = textures.get(this.clickedTexture.getFirst());
            ClientProxy.MINECRAFT.getTextureManager().bindTexture(texture.getLocation());
            this.drawTexturedRectangle(mouseX - 16, mouseY - 16, 32, 32, 0x60FFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        boolean mouseSelected = mouseY - this.getPosY() > 0 && mouseY - this.getPosY() < this.getHeight();
        if (mouseSelected) {
            float renderMouseX = mouseX - this.getPosX();
            float renderMouseY = (mouseY - this.getPosY()) + this.scrollbar.getScrollOffset();
            Tuple<String, Boolean> clicked = this.getClicked(renderMouseX, renderMouseY);
            if (clicked != null) {
                this.clickedTexture = clicked;
                return true;
            }
        } else if (mouseX >= this.getPosX() && mouseX <= this.getPosX() + this.getWidth() && mouseY + 4 >= this.getPosY() && mouseY <= this.getPosY() + 4) {
            this.resizing = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.resizing) {
            this.setHeight(MathHelper.clamp((int) (this.gui.height - mouseY), 50, 150));
            this.setPosY(this.gui.height - this.getHeight() - 16);
            return true;
        } else if (this.clickedTexture != null) {
            this.draggedTexture = true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        if (this.clickedTexture != null) {
            if (this.draggedTexture) {
                boolean accepted = false;
                for (Element element : this.gui.getPostOrderElements()) {
                    if (element instanceof TextureDragAcceptor) {
                        accepted |= ((TextureDragAcceptor) element).acceptTexture(this.clickedTexture.getFirst(), mouseX, mouseY);
                    }
                }
                if (accepted) {
                    this.gui.playClickSound();
                    this.gui.getSelectedProject().setSaved(false);
                }
            } else {
                Tuple<String, Boolean> texture = this.clickedTexture;
                if (texture.getSecond()) {
                    this.project.getModel().setTexture(texture.getFirst(), null);
                } else {
                    WindowElement<QubbleGUI> window = new WindowElement<>(this.gui, "Rename Texture", 100, 42);
                    InputElement<QubbleGUI> nameElement = new InputElement<>(this.gui, 2, 16, 96, texture.getFirst(), (i) -> {
                    });
                    nameElement.select();
                    window.addElement(nameElement);
                    window.addElement(new ButtonElement<>(this.gui, "Done", 2, 30, 96, 10, (element) -> {
                        this.project.getModel().renameTexture(texture.getFirst(), nameElement.getText());
                        this.project.setSaved(false);
                        this.gui.removeElement(window);
                        return true;
                    }).withColorScheme(ColorSchemes.WINDOW));
                    this.gui.addElement(window);
                }
                this.gui.playClickSound();
            }
        }
        this.draggedTexture = false;
        this.resizing = false;
        this.clickedTexture = null;
        return false;
    }

    private Tuple<String, Boolean> getClicked(float renderMouseX, float renderMouseY) {
        Map<String, ModelTexture> textures = this.project.getModel().getTextures();
        int x = 0;
        int y = 0;
        for (Map.Entry<String, ModelTexture> entry : textures.entrySet()) {
            float renderX = x * TILE_SIZE + 2;
            float renderY = y * (TILE_SIZE + 10) + 2;
            boolean selected = renderMouseX > renderX && renderMouseY > renderY && renderMouseX < renderX + TILE_SIZE && renderMouseY < renderY + TILE_SIZE;
            if (selected) {
                int closeSize = 12;
                float closeX = renderX + TILE_SIZE - (closeSize + 4);
                float closeY = renderY + 2;
                if (renderMouseX > closeX && renderMouseY > closeY && renderMouseX < closeX + closeSize && renderMouseY < closeY + closeSize) {
                    return new Tuple<>(entry.getKey(), true);
                } else {
                    return new Tuple<>(entry.getKey(), false);
                }
            }
            if (++x >= this.countX) {
                x = 0;
                y++;
            }
        }
        return null;
    }

    @Override
    public int getHeight() {
        return super.getHeight() - 16;
    }

    @Override
    public float getOffsetX() {
        return 0;
    }

    @Override
    public float getOffsetY() {
        return super.getHeight();
    }

    @Override
    public float getOffsetZ() {
        return 0;
    }

    @Override
    public boolean shouldHighlightHovered() {
        return this.draggedTexture;
    }
}

package net.ilexiconn.qubble.client.gui.element;

import com.google.common.collect.Lists;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.ilexiconn.llibrary.client.gui.element.ScrollbarElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaTexture;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.GUIHelper;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.project.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.QubbleIcons;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.project.action.RenameTextureAction;
import net.ilexiconn.qubble.client.project.action.SetTextureAction;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;
import java.util.Map;

public class BlockTextureBarElement extends Element<QubbleGUI> implements ModelViewAdapter {
    private static final int TILE_SIZE = 48;

    private ScrollbarElement<QubbleGUI> scrollbar;
    private ButtonElement<QubbleGUI> add;
    private int countX;
    private boolean resizing;

    private Tuple<String, Boolean> clickedTexture;
    private boolean draggedTexture;

    public BlockTextureBarElement(QubbleGUI gui) {
        super(gui, gui.getModelTree().getPosX(), gui.height - 100, (int) (gui.getSidebar().getPosX() - gui.getModelTree().getPosX()), 100);
    }

    @Override
    public void init() {
        this.scrollbar = new ScrollbarElement<>(this, () -> (float) this.getWidth() - 6.0F, () -> 2.0F, () -> (float) this.getHeight(), TILE_SIZE + 10, () -> {
            BlockModelWrapper model = this.gui.getSelectedProject().getModel(ModelType.BLOCK);
            return (int) Math.ceil((float) model.getTextures().size() / this.countX);
        });
        this.add = (ButtonElement<QubbleGUI>) new ButtonElement<>(this.gui, "+", 0.0F, this.getHeight(), 16, 16, button -> {
            WindowElement<QubbleGUI> selectTextureWindow = new WindowElement<>(this.gui, "Add Texture", 200, 200);
            List<String> files = QubbleGUI.getFiles(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, ".png");
            selectTextureWindow.addElement(new ListElement<>(this.gui, 2, 16, 196, 182, files, (list) -> {
                Project selectedProject = this.gui.getSelectedProject();
                if (selectedProject != null) {
                    String entry = list.getSelectedEntry();
                    ModelTexture texture = new ModelTexture(new File(ClientProxy.QUBBLE_TEXTURE_DIRECTORY, entry + ".png"), entry);
                    this.gui.perform(new SetTextureAction(this.gui, entry, texture));
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
        Project selectedProject = this.gui.getSelectedProject();
        if (selectedProject != null) {
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
            BlockModelWrapper model = selectedProject.getModel(ModelType.BLOCK);
            Map<String, ModelTexture> textures = model.getTextures();
            boolean mouseSelected = mouseY - this.getPosY() > 0 && mouseY - this.getPosY() < this.getHeight();
            int x = 0;
            int y = 0;
            for (Map.Entry<String, ModelTexture> entry : textures.entrySet()) {
                float renderX = x * TILE_SIZE + 2;
                float renderY = y * (TILE_SIZE + 10) + 2;
                Rectangle2D.Double selectionRectangle = new Rectangle2D.Double(renderX, renderY, TILE_SIZE, TILE_SIZE);
                Rectangle2D.Double rectangle = new Rectangle2D.Double(renderX, renderY, TILE_SIZE - 2, TILE_SIZE - 2);
                boolean selected = mouseSelected && this.isSelected(selectionRectangle, renderMouseX, renderMouseY);
                if (selected) {
                    this.drawRectangle(rectangle, LLibrary.CONFIG.getTertiaryColor());
                } else {
                    this.drawRectangle(rectangle, LLibrary.CONFIG.getSecondaryColor());
                }
                ClientProxy.MINECRAFT.getTextureManager().bindTexture(entry.getValue().getLocation());
                this.drawTexturedRectangle(renderX + 2, renderY + 2, TILE_SIZE - 6, TILE_SIZE - 6, 0xFFFFFFFF);
                if (selected) {
                    int closeSize = 12;
                    Rectangle2D.Double close = new Rectangle2D.Double(renderX + TILE_SIZE - (closeSize + 4), renderY + 2, closeSize, closeSize);
                    if (this.isSelected(close, renderMouseX, renderMouseY)) {
                        this.drawRectangle(close, 0xFFE01324);
                    } else {
                        this.drawRectangle(close, 0xFFE04747);
                    }
                    GlStateManager.enableTexture2D();
                    fontRenderer.drawString("x", (int) close.getCenterX() - fontRenderer.getCharWidth('x') / 2, (int) close.getCenterY() - 6 + closeSize / 2 - fontRenderer.FONT_HEIGHT / 2, LLibrary.CONFIG.getTextColor(), false);
                }
                GlStateManager.enableTexture2D();
                String textureName = entry.getKey();
                fontRenderer.drawString(fontRenderer.trimStringToWidth(textureName, TILE_SIZE), renderX, renderY + TILE_SIZE, LLibrary.CONFIG.getTextColor(), false);
                QubbleVanillaTexture vanillaTexture = model.getModel().getTextures().get(textureName);
                if (textureName.equals("particle") || vanillaTexture.getBoolean("particle")) {
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
                    Rectangle2D.Double selectionRectangle = new Rectangle2D.Double(renderX, renderY, TILE_SIZE, TILE_SIZE);
                    if (this.isSelected(selectionRectangle, renderMouseX, renderMouseY)) {
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
            Project selectedProject = this.gui.getSelectedProject();
            if (this.draggedTexture) {
                List<TextureDragAcceptor> acceptors = GUIHelper.INSTANCE.elements(this.gui, TextureDragAcceptor.class);
                boolean accepted = false;
                for (TextureDragAcceptor acceptor : acceptors) {
                    accepted |= acceptor.acceptTexture(this.clickedTexture.getFirst(), mouseX, mouseY);
                }
                if (accepted) {
                    this.gui.playClickSound();
                }
            } else {
                Tuple<String, Boolean> texture = this.clickedTexture;
                BlockModelWrapper model = selectedProject.getModel(ModelType.BLOCK);
                if (texture.getSecond()) {
                    model.setTexture(texture.getFirst(), null);
                } else {
                    GUIHelper.INSTANCE.input(this.gui, "Rename Texture", texture.getFirst(), response -> {
                        if (response != null && response.trim().length() > 0) {
                            this.gui.perform(new RenameTextureAction(this.gui, texture.getFirst(), response));
                        }
                        return false;
                    });
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
        BlockModelWrapper model = this.gui.getSelectedProject().getModel(ModelType.BLOCK);
        Map<String, ModelTexture> textures = model.getTextures();
        int x = 0;
        int y = 0;
        for (Map.Entry<String, ModelTexture> entry : textures.entrySet()) {
            float renderX = x * TILE_SIZE + 2;
            float renderY = y * (TILE_SIZE + 10) + 2;
            Rectangle2D.Double selectionRectangle = new Rectangle2D.Double(renderX, renderY, TILE_SIZE, TILE_SIZE);
            if (this.isSelected(selectionRectangle, renderMouseX, renderMouseY)) {
                int closeSize = 12;
                Rectangle2D.Double close = new Rectangle2D.Double(renderX + TILE_SIZE - (closeSize + 4), renderY + 2, closeSize, closeSize);
                if (this.isSelected(close, renderMouseX, renderMouseY)) {
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

package net.ilexiconn.qubble.client.gui.element.sidebar.block;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaCuboid;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaFace;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.GUIHelper;
import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.gui.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.TextureDragAcceptor;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockFaceElement extends Element<QubbleGUI> implements TextureDragAcceptor {
    public static final int SIZE = 44;

    private EnumFacing facing;

    public BlockFaceElement(QubbleGUI gui, EnumFacing facing, float posX, float posY) {
        super(gui, posX, posY, SIZE, SIZE);
        this.facing = facing;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        GlStateManager.disableTexture2D();
        this.drawRectangle(this.getPosX() - 2, this.getPosY() - 2, this.getWidth() + 4, this.getHeight() + 4, LLibrary.CONFIG.getSecondaryColor());
        Project<BlockCuboidWrapper, BlockModelWrapper> project = (Project<BlockCuboidWrapper, BlockModelWrapper>) this.gui.getSelectedProject();
        if (project.getSelectedCuboid() != null) {
            QubbleVanillaCuboid cuboid = project.getSelectedCuboid().getCuboid();
            QubbleVanillaFace face = cuboid.getFace(this.facing);
            if (face != null && face.isEnabled()) {
                ModelTexture texture = project.getModel().getTexture(face.getTexture());
                if (texture == null) {
                    this.drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), 0x60FFFFFF);
                } else {
                    ClientProxy.MINECRAFT.getTextureManager().bindTexture(texture.getLocation());
                    this.drawTexturedRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), 0xFFFFFFFF);
                }
                GlStateManager.disableTexture2D();
                GlStateManager.enableBlend();
                float minU = this.getPosX() + MathHelper.clamp(face.getMinU(), 0.0F, 16.0F) / 16.0F * SIZE;
                float minV = this.getPosY() + MathHelper.clamp(face.getMinV(), 0.0F, 16.0F) / 16.0F * SIZE;
                float maxU = this.getPosX() + MathHelper.clamp(face.getMaxU(), 0.0F, 16.0F) / 16.0F * SIZE;
                float maxV = this.getPosY() + MathHelper.clamp(face.getMaxV(), 0.0F, 16.0F) / 16.0F * SIZE;
                int color = LLibrary.CONFIG.getAccentColor();
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer buffer = tessellator.getBuffer();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                buffer.pos(minU, minV, 0.0).color(1.0F, 1.0F, 1.0F, 0.2F).endVertex();
                buffer.pos(maxU, minV, 0.0).color(1.0F, 1.0F, 1.0F, 0.2F).endVertex();
                buffer.pos(maxU, maxV, 0.0).color(1.0F, 1.0F, 1.0F, 0.2F).endVertex();
                buffer.pos(minU, maxV, 0.0).color(1.0F, 1.0F, 1.0F, 0.2F).endVertex();
                tessellator.draw();
                GlStateManager.glLineWidth(4.0F);
                buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                buffer.pos(minU, minV, 0.0).color(r, g, b, 1.0F).endVertex();
                buffer.pos(maxU, minV, 0.0).color(r, g, b, 1.0F).endVertex();
                buffer.pos(maxU, maxV, 0.0).color(r, g, b, 1.0F).endVertex();
                buffer.pos(minU, maxV, 0.0).color(r, g, b, 1.0F).endVertex();
                tessellator.draw();
                GlStateManager.glLineWidth(2.0F);
                GlStateManager.disableBlend();
                if (this.isSelected(mouseX, mouseY)) {
                    if (mouseX >= this.getPosX() + 1 && mouseY >= this.getPosY() + 1 && mouseX <= this.getPosX() + 11 && mouseY <= this.getPosY() + 11) {
                        this.drawRectangle(this.getPosX() + 1, this.getPosY() + 1, 10, 10, LLibrary.CONFIG.getDarkAccentColor());
                    } else {
                        this.drawRectangle(this.getPosX() + 1, this.getPosY() + 1, 10, 10, LLibrary.CONFIG.getAccentColor());
                    }
                    GlStateManager.enableTexture2D();
                    this.gui.getFontRenderer().drawString("...", this.getPosX() + 3, this.getPosY() + 2, LLibrary.CONFIG.getTextColor(), false);
                }
            }
        }
        GlStateManager.enableTexture2D();
        this.gui.getFontRenderer().drawString(this.facing.getName(), this.getPosX() - 1, this.getPosY() - 11, LLibrary.CONFIG.getTextColor(), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (this.isSelected(mouseX, mouseY)) {
            if (mouseX >= this.getPosX() + 1 && mouseY >= this.getPosY() + 1 && mouseX <= this.getPosX() + 11 && mouseY <= this.getPosY() + 11) {
                Project<?, ?> project = this.gui.getSelectedProject();
                ModelWrapper<?> model = project.getModel();
                List<String> textures = new ArrayList<>();
                for (Map.Entry<String, ModelTexture> entry : model.getTextures().entrySet()) {
                    textures.add(entry.getKey());
                }
                this.gui.playClickSound();
                GUIHelper.INSTANCE.selection(this.gui, "Select Texture", textures, selection -> {
                    BlockCuboidWrapper cuboidWrapper = (BlockCuboidWrapper) project.getSelectedCuboid();
                    if (cuboidWrapper != null) {
                        QubbleVanillaCuboid cuboid = cuboidWrapper.getCuboid();
                        QubbleVanillaFace face = cuboid.getFace(this.facing);
                        face.setTexture(selection);
                        project.setSaved(false);
                        model.rebuildModel();
                    }
                });
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(char character, int key) {
        if (key == Keyboard.KEY_DELETE || key == Keyboard.KEY_BACK) {
            CuboidWrapper<?> selectedCuboid = this.gui.getSelectedProject().getSelectedCuboid();
            if (selectedCuboid instanceof BlockCuboidWrapper && this.isSelected(this.gui.getPreciseMouseX(), this.gui.getPreciseMouseY())) {
                BlockCuboidWrapper blockCuboid = (BlockCuboidWrapper) selectedCuboid;
                blockCuboid.getCuboid().getFace(this.facing).setTexture(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean acceptTexture(String texture, float mouseX, float mouseY) {
        if (this.isSelected(mouseX, mouseY)) {
            Project<?, ?> project = this.gui.getSelectedProject();
            if (project != null && project.getSelectedCuboid() instanceof BlockCuboidWrapper) {
                BlockCuboidWrapper cuboid = (BlockCuboidWrapper) project.getSelectedCuboid();
                QubbleVanillaFace face = cuboid.getCuboid().getFace(this.facing);
                face.setTexture(texture);
                return true;
            }
        }
        return false;
    }
}

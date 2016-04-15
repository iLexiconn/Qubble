package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public interface IComponent {
    void render(QubbleGUI gui, int mouseX, int mouseY, float partialTicks);

    void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button);

    void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick);

    default void drawRectangle(int x, int y, int width, int height, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(x, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x, y, 0.0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    default void drawOutline(int x, int y, int width, int height, int color, int outlineSize) {
        drawRectangle(x, y, width - outlineSize, outlineSize, color);
        drawRectangle(x + width - outlineSize, y, outlineSize, height - outlineSize, color);
        drawRectangle(x, y + height - outlineSize, width, outlineSize, color);
        drawRectangle(x, y, outlineSize, height - outlineSize, color);
    }
}

package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.gui.GuiQubbleEditor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public interface IGuiComponent {
    void render(GuiQubbleEditor gui, int mouseX, int mouseY, float partialTicks);

    void mouseClicked(GuiQubbleEditor gui, int mouseX, int mouseY, int button);

    void mouseDragged(GuiQubbleEditor gui, int mouseX, int mouseY, int button, long timeSinceClick);

    static void drawRectangle(int x, int y, int width, int height, int color) {
        GlStateManager.color(((color & 0xFF0000) >> 16) / 255.0F, ((color & 0xFF00) >> 8) / 255.0F, (color & 0xFF) / 255.0F);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(x, y + height, 0.0).endVertex();
        vertexBuffer.pos(x + width, y + height, 0.0).endVertex();
        vertexBuffer.pos(x + width, y, 0.0).endVertex();
        vertexBuffer.pos(x, y, 0.0).endVertex();
        tessellator.draw();
    }

    static void drawOutline(int x, int y, int width, int height, int color, int outlineSize) {
        drawRectangle(x, y, width - outlineSize, outlineSize, color);
        drawRectangle(x + width - outlineSize, y, outlineSize, height - outlineSize, color);
        drawRectangle(x, y + height - outlineSize, width, outlineSize, color);
        drawRectangle(x, y, outlineSize, height - outlineSize, color);
    }
}

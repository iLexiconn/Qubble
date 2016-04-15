package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGUIComponent {
    void render(QubbleGUI gui, int mouseX, int mouseY, float partialTicks);

    void mouseClicked(QubbleGUI gui, int mouseX, int mouseY, int button);

    void mouseDragged(QubbleGUI gui, int mouseX, int mouseY, int button, long timeSinceClick);
}

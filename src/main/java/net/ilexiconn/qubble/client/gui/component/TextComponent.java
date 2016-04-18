package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.ClientProxy;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextComponent implements IComponent<GuiScreen> {
    private String text;
    private int posX;
    private int posY;
    private int color;

    public TextComponent(String text, int posX, int posY, int color) {
        this.text = text;
        this.posX = posX;
        this.posY = posY;
        this.color = color;
    }

    @Override
    public void render(GuiScreen gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        fontRenderer.drawString(this.text, this.posX, this.posY, this.color);
    }

    @Override
    public void renderAfter(GuiScreen gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
    }

    @Override
    public boolean mouseClicked(GuiScreen gui, float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(GuiScreen gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        return false;
    }

    @Override
    public boolean mouseReleased(GuiScreen gui, float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(GuiScreen gui, char character, int key) {
        return false;
    }
}

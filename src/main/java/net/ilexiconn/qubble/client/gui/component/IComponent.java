package net.ilexiconn.qubble.client.gui.component;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IComponent<T extends GuiScreen> {
    void render(T gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks);

    void renderAfter(T gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks);

    boolean mouseClicked(T gui, float mouseX, float mouseY, int button);

    boolean mouseDragged(T gui, float mouseX, float mouseY, int button, long timeSinceClick);

    boolean mouseReleased(T gui, float mouseX, float mouseY, int button);

    boolean keyPressed(T gui, char character, int key);
}

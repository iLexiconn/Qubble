package net.ilexiconn.qubble.client.gui.element;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@FunctionalInterface
@SideOnly(Side.CLIENT)
public interface IActionHandler<V extends GuiScreen, T extends Element<V>> {
    void onAction(V gui, T element);
}

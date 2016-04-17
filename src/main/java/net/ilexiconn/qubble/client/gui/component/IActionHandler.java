package net.ilexiconn.qubble.client.gui.component;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface IActionHandler<V extends GuiScreen, T extends IComponent<V>> {
    void onAction(V gui, T component);
}

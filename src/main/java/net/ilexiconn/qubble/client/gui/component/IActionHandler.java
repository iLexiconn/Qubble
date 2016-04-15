package net.ilexiconn.qubble.client.gui.component;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface IActionHandler<T extends IGUIComponent> {
    void onAction(T component);
}

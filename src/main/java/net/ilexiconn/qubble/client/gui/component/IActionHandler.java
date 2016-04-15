package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface IActionHandler<T extends IGUIComponent> {
    void onAction(QubbleGUI gui, T component);
}

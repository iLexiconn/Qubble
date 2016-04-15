package net.ilexiconn.qubble.client;

import net.ilexiconn.qubble.server.ServerProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
    public static final int QUBBLE_BUTTON_ID = "QUBBLE_BUTTON_ID".hashCode();
    public static final Minecraft MINECRAFT = Minecraft.getMinecraft();

    @Override
    public void onPreInit() {
        super.onPreInit();

        MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void onPostInit() {
        super.onPostInit();
    }
}

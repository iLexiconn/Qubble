package net.ilexiconn.qubble.server;

import net.minecraftforge.common.MinecraftForge;

public class ServerProxy {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(ServerEventHandler.INSTANCE);
    }

    public void onInit() {

    }

    public void onPostInit() {

    }
}

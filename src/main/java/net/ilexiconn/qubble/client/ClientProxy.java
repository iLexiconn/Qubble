package net.ilexiconn.qubble.client;

import net.ilexiconn.qubble.server.ServerProxy;
import net.ilexiconn.qubble.server.model.QubbleModel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
    public static final int QUBBLE_BUTTON_ID = "QUBBLE_BUTTON_ID".hashCode();
    public static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    public static final File QUBBLE_MODEL_DIRECTORY = new File(".", "qubble/models");

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

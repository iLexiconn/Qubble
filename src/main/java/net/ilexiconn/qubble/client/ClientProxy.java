package net.ilexiconn.qubble.client;

import net.ilexiconn.qubble.server.ServerProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
    public static final int QUBBLE_BUTTON_ID = "QUBBLE_BUTTON_ID".hashCode();
    public static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    public static final File QUBBLE_DIRECTORY = new File(".", "llibrary" + File.separator + "qubble");
    public static final File QUBBLE_MODEL_DIRECTORY = new File(QUBBLE_DIRECTORY, "models");
    public static final File QUBBLE_TEXTURE_DIRECTORY = new File(QUBBLE_DIRECTORY, "textures");
    public static final File QUBBLE_EXPORT_DIRECTORY = new File(QUBBLE_DIRECTORY, "exports");

    @Override
    public void onPreInit() {
        super.onPreInit();
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
        if (!QUBBLE_MODEL_DIRECTORY.exists()) {
            QUBBLE_MODEL_DIRECTORY.mkdirs();
        }
        if (!QUBBLE_TEXTURE_DIRECTORY.exists()) {
            QUBBLE_TEXTURE_DIRECTORY.mkdirs();
        }
        if (!QUBBLE_EXPORT_DIRECTORY.exists()) {
            QUBBLE_EXPORT_DIRECTORY.mkdirs();
        }
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

package net.ilexiconn.qubble;

import net.ilexiconn.qubble.server.ServerProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(modid = Qubble.MODID, name = "Qubble", version = Qubble.VERSION, dependencies = "required-after:llibrary@[" + Qubble.LLIBRARY_VERSION + ",)", clientSideOnly = true)
public class Qubble {
    public static final String MODID = "qubble";
    public static final String VERSION = "1.0.0-develop";
    public static final String LLIBRARY_VERSION = "1.4.0";

    @Mod.Instance(Qubble.MODID)
    public static Qubble INSTANCE;
    @SidedProxy(serverSide = "net.ilexiconn.qubble.server.ServerProxy", clientSide = "net.ilexiconn.qubble.client.ClientProxy")
    public static ServerProxy PROXY;
    public static File CONFIG_FILE;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        Qubble.PROXY.onPreInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        Qubble.PROXY.onInit();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        Qubble.PROXY.onPostInit();
    }
}

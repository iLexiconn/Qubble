package net.ilexiconn.qubble;

import net.ilexiconn.qubble.server.proxy.ServerProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Qubble.MODID, name = "Qubble", version = Qubble.VERSION, dependencies = "required-after:llibrary@[1.2.1,)")
public class Qubble {
    public static final String MODID = "qubble";
    public static final String VERSION = "0.0.0-dev";

    @Mod.Instance(Qubble.MODID)
    public static Qubble INSTANCE;

    @SidedProxy(clientSide = "net.ilexiconn.qubble.client.proxy.ClientProxy", serverSide = "net.ilexiconn.qubble.server.proxy.ServerProxy")
    public static ServerProxy PROXY;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        PROXY.onPreInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        PROXY.onInit();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        PROXY.onPostInit();
    }
}

package net.ilexiconn.qubble;

import net.ilexiconn.qubble.server.ServerProxy;
import net.ilexiconn.qubble.server.config.QubbleConfig;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(modid = Qubble.MODID, name = "Qubble", version = Qubble.VERSION, dependencies = "required-after:llibrary@[" + Qubble.LLIBRARY_VERSION + ",)", clientSideOnly = true)
public class Qubble {
    public static final String MODID = "qubble";
    public static final String VERSION = "1.0.0-dev";
    public static final String LLIBRARY_VERSION = "1.3.0";

    @Mod.Instance(Qubble.MODID)
    public static Qubble INSTANCE;
    @SidedProxy(serverSide = "net.ilexiconn.qubble.server.ServerProxy", clientSide = "net.ilexiconn.qubble.client.ClientProxy")
    public static ServerProxy PROXY;
    public static QubbleConfig CONFIG;
    public static File CONFIG_FILE;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        Qubble.CONFIG_FILE = new File(".", "llibrary" + File.separator + "qubble" + File.separator + "config.json");
        try {
            Qubble.CONFIG = new QubbleConfig();
            Qubble.CONFIG.deserializeNBT(CompressedStreamTools.read(Qubble.CONFIG_FILE));
        } catch (Exception e) {
            //Meh
        }
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

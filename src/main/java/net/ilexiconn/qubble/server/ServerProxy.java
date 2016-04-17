package net.ilexiconn.qubble.server;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.qubble.server.model.exporter.*;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.TabulaImporter;
import net.ilexiconn.qubble.server.model.importer.TechneImporter;
import net.ilexiconn.qubble.server.model.obj.OBJModel;
import net.ilexiconn.qubble.server.model.qubble.QubbleModel;
import net.ilexiconn.qubble.server.model.techne.TechneModel;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ServerProxy {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(ServerEventHandler.INSTANCE);

        IModelImporter<TabulaModelContainer> tabulaImporter = new TabulaImporter();
        IModelImporter<TechneModel> techneImporter = new TechneImporter();

        IModelExporter<List<String>> javaExporter = new JavaExporter();
        IModelExporter<List<String>> scalaExporter = new ScalaExporter();
        IModelExporter<List<String>> jsExporter = new JavaScriptExporter();
        IModelExporter<OBJModel> objExporter = new OBJExporter();
        IModelExporter<List<String>> kotlinExporter = new KotlinExporter();

        try {
            QubbleModel model = tabulaImporter.getModel("TabulaModel", tabulaImporter.read(new File(".", "TabulaModel.tbl")));
            javaExporter.save(javaExporter.export(model.copy(), "net.ilexiconn.test", "TabulaModel"), new File(".", "TabulaModel.java"));
            scalaExporter.save(scalaExporter.export(model.copy(), "net.ilexiconn.test", "TabulaModel"), new File(".", "TabulaModel.scala"));
            jsExporter.save(jsExporter.export(model.copy()), new File(".", "TabulaModel.js"));
            objExporter.save(objExporter.export(model.copy()), new File(".", "TabulaModel.obj"));
            kotlinExporter.save(kotlinExporter.export(model.copy(), "net.ilexiconn.test", "TabulaModel"), new File(".", "TabulaModel.kt"));
            CompressedStreamTools.writeCompressed(model.copy().serializeNBT(), new FileOutputStream(new File("TabulaModel.qbl")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            QubbleModel model = techneImporter.getModel("TechneModel", techneImporter.read(new File(".", "TechneModel.tcn")));
            javaExporter.save(javaExporter.export(model.copy(), "net.ilexiconn.test", "TechneModel"), new File(".", "TechneModel.java"));
            scalaExporter.save(scalaExporter.export(model.copy(), "net.ilexiconn.test", "TechneModel"), new File(".", "TechneModel.scala"));
            jsExporter.save(jsExporter.export(model.copy()), new File(".", "TechneModel.js"));
            objExporter.save(objExporter.export(model.copy()), new File(".", "TechneModel.obj"));
            kotlinExporter.save(kotlinExporter.export(model.copy(), "net.ilexiconn.test", "TechneModel"), new File(".", "TechneModel.kt"));
            CompressedStreamTools.writeCompressed(model.copy().serializeNBT(), new FileOutputStream(new File("TechneModel.qbl")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onInit() {

    }

    public void onPostInit() {

    }
}

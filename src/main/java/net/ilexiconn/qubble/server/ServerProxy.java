package net.ilexiconn.qubble.server;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.qubble.server.model.QubbleModel;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.JavaExporter;
import net.ilexiconn.qubble.server.model.exporter.JavaScriptExporter;
import net.ilexiconn.qubble.server.model.exporter.ScalaExporter;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.JavaScriptImporter;
import net.ilexiconn.qubble.server.model.importer.TabulaImporter;
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
        IModelExporter<List<String>> javaExporter = new JavaExporter();
        IModelExporter<List<String>> scalaExporter = new ScalaExporter();
        IModelExporter<List<String>> jsExporter = new JavaScriptExporter();
        IModelImporter<List<String>> jsImporter = new JavaScriptImporter();

        try {
            QubbleModel model = tabulaImporter.getModel(tabulaImporter.read(new File(".", "test.tbl")));
            scalaExporter.save(scalaExporter.export(model, "net.ilexiconn.test", "TestModel"), new File(".", "test.scala"));
            CompressedStreamTools.writeCompressed(model.serializeNBT(), new FileOutputStream(new File("tabula.qbl")));
            jsExporter.save(jsExporter.export(model), new File(".", "test.js"));
            model = jsImporter.getModel(jsImporter.read(new File(".", "test.js")));
            CompressedStreamTools.writeCompressed(model.serializeNBT(), new FileOutputStream(new File("javascript.qbl")));
            javaExporter.save(javaExporter.export(model, "net.ilexiconn.test", "TestModel"), new File(".", "test.java"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onInit() {

    }

    public void onPostInit() {

    }
}

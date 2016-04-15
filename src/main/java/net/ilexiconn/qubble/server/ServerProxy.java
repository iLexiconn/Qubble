package net.ilexiconn.qubble.server;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.qubble.server.model.QubbleModel;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.JavaExporter;
import net.ilexiconn.qubble.server.model.exporter.JavaScriptExporter;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.TabulaImporter;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ServerProxy {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(ServerEventHandler.INSTANCE);

        IModelImporter<TabulaModelContainer> tabulaImporter = new TabulaImporter();
        IModelExporter<List<String>> javaExporter = new JavaExporter();
        IModelExporter<List<String>> jsExporter = new JavaScriptExporter();

        try {
            QubbleModel model = tabulaImporter.getModel(tabulaImporter.read(new File(".", "test.tbl")));
            javaExporter.save(javaExporter.export(model, "net.ilexiconn.test", "TestModel"), new File(".", "test.java"));
            jsExporter.save(jsExporter.export(model), new File(".", "test.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onInit() {

    }

    public void onPostInit() {

    }
}

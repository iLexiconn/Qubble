package net.ilexiconn.qubble.server;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.qubble.server.model.QubbleModel;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.JavaExporter;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.TabulaImporter;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ServerProxy {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(ServerEventHandler.INSTANCE);

        IModelImporter<TabulaModelContainer> importer = new TabulaImporter();
        IModelExporter<List<String>> exporter = new JavaExporter();

        try {
            QubbleModel model = importer.getModel(importer.read(new File(".", "test.tbl")));
            exporter.save(exporter.export(model, "net.ilexiconn.test", "TestModel"), new File(".", "test.java"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onInit() {

    }

    public void onPostInit() {

    }
}

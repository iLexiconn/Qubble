package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.qubble.server.model.QubbleModel;

import java.io.File;
import java.io.IOException;

public class TabulaImporter implements IModelImporter<TabulaModelContainer> {
    @Override
    public QubbleModel getModel(TabulaModelContainer model) {
        return null; //TODO
    }

    @Override
    public TabulaModelContainer read(File file) throws IOException {
        return TabulaModelHandler.INSTANCE.loadTabulaModel(file.getAbsolutePath());
    }
}

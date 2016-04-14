package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.qubble.server.model.QubbleModel;

import java.io.File;
import java.io.IOException;

public interface IModelImporter<T> {
    QubbleModel getModel(T model);

    T read(File file) throws IOException;
}

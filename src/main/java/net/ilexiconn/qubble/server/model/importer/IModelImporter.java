package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.qubble.server.model.qubble.QubbleModel;

import java.io.File;
import java.io.IOException;

public interface IModelImporter<T> {
    String getName();

    String getExtension();

    QubbleModel getModel(T model);

    T read(File file) throws IOException;
}

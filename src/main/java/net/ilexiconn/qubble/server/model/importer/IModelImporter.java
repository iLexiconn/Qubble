package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.qubble.server.model.qubble.QubbleModel;

import java.io.File;
import java.io.IOException;

public interface IModelImporter<T> {
    String getName();

    String getExtension();

    QubbleModel getModel(String fileName, T model);

    T read(File file) throws IOException;
}

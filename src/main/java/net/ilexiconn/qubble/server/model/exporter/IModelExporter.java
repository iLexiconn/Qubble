package net.ilexiconn.qubble.server.model.exporter;

import net.ilexiconn.qubble.server.model.QubbleModel;

import java.io.File;
import java.io.IOException;

public interface IModelExporter<T> {
    String getExtension();

    T export(QubbleModel model, String... arguments);

    void save(T model, File file, Object... arguments) throws IOException;
}

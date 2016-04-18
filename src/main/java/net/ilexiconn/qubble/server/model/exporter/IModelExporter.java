package net.ilexiconn.qubble.server.model.exporter;

import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;

import java.io.File;
import java.io.IOException;

public interface IModelExporter<T> {
    String getName();

    String getExtension();

    T export(QubbleModel model, String... arguments);

    void save(T model, File file) throws IOException;

    String[] getArgumentNames();
    String[] getDefaultArguments(QubbleModel currentModel);
}

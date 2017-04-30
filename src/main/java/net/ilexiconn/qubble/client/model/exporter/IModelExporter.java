package net.ilexiconn.qubble.client.model.exporter;

import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

import java.io.File;
import java.io.IOException;

public interface IModelExporter<T, CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> {
    String getName();

    String getExtension();

    T export(MDL model, String... arguments);

    void save(T model, File file) throws IOException;

    String[] getArgumentNames();

    String[] getDefaultArguments(MDL currentModel);

    String getFileName(String[] arguments, String fileName);

    boolean supports(ModelType modelType);
}

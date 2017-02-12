package net.ilexiconn.qubble.client.model.importer;

import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;

import java.io.File;
import java.io.IOException;

public interface IModelImporter<T, CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> {
    String getName();

    String getExtension();

    MDL getModel(String fileName, T model);

    T read(File file) throws IOException;
}

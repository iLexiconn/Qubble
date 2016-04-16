package net.ilexiconn.qubble.server.model;

import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.ModelExporters;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.ModelImporters;

import java.util.ArrayList;
import java.util.List;

public enum ModelHandler {
    INSTANCE;

    private List<IModelImporter<?>> modelImporterList = new ArrayList<>();
    private List<IModelExporter<?>> modelExporterList = new ArrayList<>();

    public void registerModelImporter(IModelImporter<?> modelImporter) {
        this.modelImporterList.add(modelImporter);
    }

    public void registerModelExporter(IModelExporter<?> modelExporter) {
        this.modelExporterList.add(modelExporter);
    }

    public List<IModelImporter<?>> getImporters() {
        List<IModelImporter<?>> list = new ArrayList<>();
        for (ModelImporters importer : ModelImporters.VALUES) {
            list.add(importer.getModelImporter());
        }
        list.addAll(this.modelImporterList);
        return list;
    }

    public List<IModelExporter<?>> getExporters() {
        List<IModelExporter<?>> list = new ArrayList<>();
        for (ModelExporters exporter : ModelExporters.VALUES) {
            list.add(exporter.getModelExporter());
        }
        list.addAll(this.modelExporterList);
        return list;
    }

    public IModelImporter<?> getImporter(String name) {
        IModelImporter<?> modelImporter = ModelImporters.getBuiltinImporter(name);
        if (modelImporter != null) {
            return modelImporter;
        } else {
            for (IModelImporter<?> importer : this.modelImporterList) {
                if (importer.getName().equals(name)) {
                    return importer;
                }
            }
        }
        return null;
    }

    public IModelExporter<?> getExporter(String name) {
        IModelExporter<?> modelExporter = ModelExporters.getBuiltinExporter(name);
        if (modelExporter != null) {
            return modelExporter;
        } else {
            for (IModelExporter<?> exporter : this.modelExporterList) {
                if (exporter.getName().equals(name)) {
                    return exporter;
                }
            }
        }
        return null;
    }
}

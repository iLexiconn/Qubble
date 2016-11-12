package net.ilexiconn.qubble.server.model;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.server.model.exporter.IModelExporter;
import net.ilexiconn.qubble.server.model.exporter.ModelExporters;
import net.ilexiconn.qubble.server.model.importer.IModelImporter;
import net.ilexiconn.qubble.server.model.importer.ModelImporters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public String getCopyName(QubbleModel model, String name) {
        int index = 2;
        while (this.hasDuplicateName(model, name)) {
            String newName = name + " " + index;
            if (!this.hasDuplicateName(model, newName)) {
                return newName;
            }
            index++;
        }
        return name;
    }

    public boolean hasDuplicateName(QubbleModel model, String name) {
        for (QubbleCuboid cuboid : model.getCuboids()) {
            if (this.hasDuplicateName(cuboid, name)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasDuplicateName(QubbleCuboid cuboid, String name) {
        if (cuboid.getName().trim().equals(name)) {
            return true;
        }
        for (QubbleCuboid child : cuboid.getChildren()) {
            if (this.hasDuplicateName(child, name)) {
                return true;
            }
        }
        return false;
    }

    public QubbleCuboid copy(QubbleModel model, QubbleCuboid cuboid) {
        QubbleCuboid copy = QubbleCuboid.create(this.getCopyName(model, cuboid.getName()));
        copy.getChildren().addAll(cuboid.getChildren().stream().map((child) -> ModelHandler.INSTANCE.copy(model, child)).collect(Collectors.toList()));
        copy.setDimensions(cuboid.getDimensionX(), cuboid.getDimensionY(), cuboid.getDimensionZ());
        copy.setPosition(cuboid.getPositionX(), cuboid.getPositionY(), cuboid.getPositionZ());
        copy.setOffset(cuboid.getOffsetX(), cuboid.getOffsetY(), cuboid.getOffsetZ());
        copy.setRotation(cuboid.getRotationX(), cuboid.getRotationY(), cuboid.getRotationZ());
        copy.setScale(cuboid.getScaleX(), cuboid.getScaleY(), cuboid.getScaleZ());
        copy.setTexture(cuboid.getTextureX(), cuboid.getTextureY());
        copy.setTextureMirrored(cuboid.isTextureMirrored());
        copy.setOpacity(cuboid.getOpacity());
        return copy;
    }
}

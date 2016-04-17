package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeGroupContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.qubble.server.model.qubble.QubbleCube;
import net.ilexiconn.qubble.server.model.qubble.QubbleModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TabulaImporter implements IModelImporter<TabulaModelContainer> {
    @Override
    public String getName() {
        return "Tabula";
    }

    @Override
    public String getExtension() {
        return "tbl";
    }

    @Override
    public QubbleModel getModel(String fileName, TabulaModelContainer model) {
        List<QubbleCube> cubes = new ArrayList<>();
        cubes.addAll(this.addChildren(null, model.getCubes()));
        for (TabulaCubeGroupContainer cubeGroup : model.getCubeGroups()) {
            cubes.addAll(this.addChildrenFromGroup(null, cubeGroup));
        }
        return new QubbleModel(model.getName(), fileName, model.getAuthor(), 1, model.getTextureWidth(), model.getTextureHeight(), cubes);
    }

    @Override
    public TabulaModelContainer read(File file) throws IOException {
        TabulaModelContainer container = null;
        ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.getName().equals("model.json")) {
                container = TabulaModelHandler.INSTANCE.loadTabulaModel(zip);
            }
        }
        return container;
    }

    public List<QubbleCube> addChildren(QubbleCube parent, List<TabulaCubeContainer> cubes) {
        List<QubbleCube> list = new ArrayList<>();
        for (TabulaCubeContainer cube : cubes) {
            QubbleCube qubble = new QubbleCube(cube.getName(), new ArrayList<>(), cube.getDimensions(), doubleToFloat(cube.getPosition()), doubleToFloat(cube.getOffset()), doubleToFloat(cube.getRotation()), doubleToFloat(cube.getScale()), cube.getTextureOffset(), cube.isTextureMirrorEnabled(), (float) cube.getOpacity());
            if (parent == null) {
                list.add(qubble);
            } else {
                parent.getChildren().add(qubble);
            }
            list.addAll(this.addChildren(qubble, cube.getChildren()));
        }
        return list;
    }

    public List<QubbleCube> addChildrenFromGroup(QubbleCube parent, TabulaCubeGroupContainer cubeGroup) {
        List<QubbleCube> list = this.addChildren(parent, cubeGroup.getCubes());
        for (TabulaCubeGroupContainer group : cubeGroup.getCubeGroups()) {
            list.addAll(this.addChildrenFromGroup(parent, group));
        }
        return list;
    }

    public float[] doubleToFloat(double[] doubleArray) {
        float[] floatArray = new float[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) {
            floatArray[i] = (float) doubleArray[i];
        }
        return floatArray;
    }
}

package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeGroupContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;

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
        QubbleModel qubble = QubbleModel.create(model.getName(), model.getAuthor(), model.getTextureWidth(), model.getTextureHeight());
        qubble.getCubes().addAll(cubes);
        qubble.setFileName(fileName);
        return qubble;
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
            QubbleCube qubble = QubbleCube.create(cube.getName());
            qubble.setDimensions(cube.getDimensions()[0], cube.getDimensions()[1], cube.getDimensions()[2]);
            qubble.setPosition((float) cube.getPosition()[0], (float) cube.getPosition()[1], (float) cube.getPosition()[2]);
            qubble.setOffset((float) cube.getOffset()[0], (float) cube.getOffset()[1], (float) cube.getOffset()[2]);
            qubble.setRotation((float) cube.getRotation()[0], (float) cube.getRotation()[1], (float) cube.getRotation()[2]);
            qubble.setScale((float) cube.getScale()[0], (float) cube.getScale()[1], (float) cube.getScale()[2]);
            qubble.setTexture(cube.getTextureOffset()[0], cube.getTextureOffset()[1]);
            qubble.setTextureMirrored(cube.isTextureMirrorEnabled());
            qubble.setOpacity((float) cube.getOpacity());
            if (parent == null) {
                list.add(qubble);
            } else {
                parent.getChildren().add(qubble);
            }
            this.addChildren(qubble, cube.getChildren());
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
}

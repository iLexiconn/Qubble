package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeGroupContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TabulaImporter implements IModelImporter<TabulaModelContainer, DefaultCuboidWrapper, DefaultModelWrapper> {
    @Override
    public String getName() {
        return "Tabula";
    }

    @Override
    public String getExtension() {
        return "tbl";
    }

    @Override
    public DefaultModelWrapper getModel(String fileName, TabulaModelContainer model) {
        List<QubbleCuboid> cubes = new ArrayList<>();
        cubes.addAll(this.addChildren(null, model.getCubes()));
        for (TabulaCubeGroupContainer cubeGroup : model.getCubeGroups()) {
            cubes.addAll(this.addChildrenFromGroup(null, cubeGroup));
        }
        QubbleModel qubble = QubbleModel.create(model.getName(), model.getAuthor(), model.getTextureWidth(), model.getTextureHeight());
        qubble.getCuboids().addAll(cubes);
        qubble.setFileName(fileName);
        return new DefaultModelWrapper(qubble);
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

    public List<QubbleCuboid> addChildren(QubbleCuboid parent, List<TabulaCubeContainer> cubes) {
        List<QubbleCuboid> list = new ArrayList<>();
        for (TabulaCubeContainer cube : cubes) {
            QubbleCuboid qubble = QubbleCuboid.create(cube.getName());
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

    public List<QubbleCuboid> addChildrenFromGroup(QubbleCuboid parent, TabulaCubeGroupContainer cubeGroup) {
        List<QubbleCuboid> list = this.addChildren(parent, cubeGroup.getCubes());
        for (TabulaCubeGroupContainer group : cubeGroup.getCubeGroups()) {
            list.addAll(this.addChildrenFromGroup(parent, group));
        }
        return list;
    }
}

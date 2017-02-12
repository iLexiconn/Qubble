package net.ilexiconn.qubble.client.model.importer;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.techne.TechneCube;
import net.ilexiconn.llibrary.client.model.techne.TechneModel;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;

import java.io.File;
import java.io.IOException;

public class TechneImporter implements IModelImporter<TechneModel, DefaultCuboidWrapper, DefaultModelWrapper> {
    @Override
    public String getName() {
        return "Techne";
    }

    @Override
    public String getExtension() {
        return "tcn";
    }

    @Override
    public DefaultModelWrapper getModel(String fileName, TechneModel model) {
        QubbleModel qubble = QubbleModel.create(model.getFileName(), null, model.getTextureWidth(), model.getTextureHeight());
        for (TechneCube cube : model.getCubes()) {
            QubbleCuboid qubbleCube = QubbleCuboid.create(cube.getName());
            qubbleCube.setDimensions(cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ());
            qubbleCube.setPosition(cube.getPositionX(), cube.getPositionY(), cube.getPositionZ());
            qubbleCube.setOffset(cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ());
            qubbleCube.setRotation(cube.getRotationX(), cube.getRotationY(), cube.getRotationZ());
            qubbleCube.setScale(1.0F, 1.0F, 1.0F);
            qubbleCube.setTextureMirrored(cube.isTextureMirrored());
            qubbleCube.setTexture(cube.getTextureX(), cube.getTextureY());
            qubbleCube.setOpacity(100.0F);
            qubble.getCuboids().add(qubbleCube);
        }
        return new DefaultModelWrapper(qubble);
    }

    @Override
    public TechneModel read(File file) throws IOException {
        return TechneModel.fromFile(file);
    }
}

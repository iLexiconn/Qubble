package net.ilexiconn.qubble.server.model.importer;

import net.ilexiconn.qubble.server.model.QubbleCube;
import net.ilexiconn.qubble.server.model.QubbleModel;
import net.ilexiconn.qubble.server.model.tabula.TechneCube;
import net.ilexiconn.qubble.server.model.tabula.TechneModel;

import java.io.File;
import java.io.IOException;

public class TechneImporter implements IModelImporter<TechneModel> {
    @Override
    public QubbleModel getModel(TechneModel model) {
        QubbleModel qubble = new QubbleModel();
        qubble.setName(model.getFileName());
        qubble.setAuthor("Unknown");
        qubble.setVersion(1);
        qubble.setTexture(model.getTextureWidth(), model.getTextureHeight());
        for (TechneCube cube : model.getCubes()) {
            QubbleCube qubbleCube = new QubbleCube();
            qubbleCube.setName(cube.getName());
            qubbleCube.setDimensions(cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ());
            qubbleCube.setPosition(cube.getPositionX(), cube.getPositionY(), cube.getPositionZ());
            qubbleCube.setOffset(cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ());
            qubbleCube.setRotation(cube.getRotationX(), cube.getRotationY(), cube.getRotationZ());
            qubbleCube.setScale(1.0F, 1.0F, 1.0F);
            qubbleCube.setTextureMirrored(cube.isTextureMirrored());
            qubbleCube.setTexture(cube.getTextureX(), cube.getTextureY());
            qubbleCube.setOpacity(100.0F);
            qubble.getCubes().add(qubbleCube);
        }
        return qubble;
    }

    @Override
    public TechneModel read(File file) throws IOException {
        return new TechneModel(file);
    }
}

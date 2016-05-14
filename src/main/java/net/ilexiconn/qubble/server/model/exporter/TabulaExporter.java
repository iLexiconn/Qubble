package net.ilexiconn.qubble.server.model.exporter;

import com.google.gson.Gson;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TabulaExporter implements IModelExporter<TabulaModelContainer> {
    @Override
    public String getName() {
        return "Tabula";
    }

    @Override
    public String getExtension() {
        return "tbl";
    }

    @Override
    public TabulaModelContainer export(QubbleModel model, String... arguments) {
        List<TabulaCubeContainer> tabulaCubes = new ArrayList<>();
        for (QubbleCuboid cube : model.getCuboids()) {
            TabulaCubeContainer tabulaCube = new TabulaCubeContainer(cube.getName(), RandomStringUtils.randomAscii(20), null, new int[]{cube.getDimensionX(), cube.getDimensionY(), cube.getDimensionZ()}, new double[]{cube.getPositionX(), cube.getPositionY(), cube.getPositionZ()}, new double[]{cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ()}, new double[]{cube.getRotationX(), cube.getRotationY(), cube.getRotationZ()}, new double[]{cube.getScaleX(), cube.getScaleY(), cube.getScaleZ()}, new int[]{cube.getTextureX(), cube.getTextureY()}, cube.isTextureMirrored(), cube.getOpacity(), 0.0, false);
            tabulaCube.getChildren().addAll(this.convertChildren(cube, tabulaCube));
            tabulaCubes.add(tabulaCube);
        }
        return new TabulaModelContainer(model.getName(), model.getAuthor(), model.getTextureWidth(), model.getTextureHeight(), tabulaCubes, 4);
    }

    private List<TabulaCubeContainer> convertChildren(QubbleCuboid parent, TabulaCubeContainer tabulaParent) {
        List<TabulaCubeContainer> children = new ArrayList<>();
        for (QubbleCuboid child : parent.getChildren()) {
            TabulaCubeContainer tabulaChild = new TabulaCubeContainer(child.getName(), RandomStringUtils.randomAscii(20), tabulaParent.getIdentifier(), new int[]{child.getDimensionX(), child.getDimensionY(), child.getDimensionZ()}, new double[]{child.getPositionX(), child.getPositionY(), child.getPositionZ()}, new double[]{child.getOffsetX(), child.getOffsetY(), child.getOffsetZ()}, new double[]{child.getRotationX(), child.getRotationY(), child.getRotationZ()}, new double[]{child.getScaleX(), child.getScaleY(), child.getScaleZ()}, new int[]{child.getTextureX(), child.getTextureY()}, child.isTextureMirrored(), child.getOpacity(), 0.0, false);
            tabulaChild.getChildren().addAll(this.convertChildren(child, tabulaChild));
            children.add(tabulaChild);
        }
        return children;
    }

    @Override
    public void save(TabulaModelContainer model, File file) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
        ZipEntry entry = new ZipEntry("model.json");
        out.putNextEntry(entry);
        out.write(new Gson().toJson(model, TabulaModelContainer.class).getBytes());
        out.closeEntry();
        out.close();
    }

    @Override
    public String[] getArgumentNames() {
        return new String[]{};
    }

    @Override
    public String[] getDefaultArguments(QubbleModel currentModel) {
        return new String[]{};
    }
}

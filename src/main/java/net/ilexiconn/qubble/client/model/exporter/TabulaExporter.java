package net.ilexiconn.qubble.client.model.exporter;

import com.google.gson.Gson;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TabulaExporter implements IModelExporter<TabulaModelContainer, DefaultCuboidWrapper, DefaultModelWrapper> {
    @Override
    public String getName() {
        return "Tabula";
    }

    @Override
    public String getExtension() {
        return "tbl";
    }

    @Override
    public TabulaModelContainer export(DefaultModelWrapper model, String... arguments) {
        List<TabulaCubeContainer> tabulaCubes = new ArrayList<>();
        for (DefaultCuboidWrapper cube : model.getCuboids()) {
            int[] dimensions = { (int) cube.getDimensionX(), (int) cube.getDimensionY(), (int) cube.getDimensionZ() };
            double[] position = { cube.getPositionX(), cube.getPositionY(), cube.getPositionZ() };
            double[] offset = { cube.getOffsetX(), cube.getOffsetY(), cube.getOffsetZ() };
            double[] rotation = { cube.getRotationX(), cube.getRotationY(), cube.getRotationZ() };
            double[] scale = { cube.getScaleX(), cube.getScaleY(), cube.getScaleZ() };
            int[] textureOffset = { cube.getTextureX(), cube.getTextureY() };
            String identifier = cube.getCuboid().getIdentifier();
            if (identifier == null) {
                identifier = this.generateIdentifier(cube.getName(), null);
            }
            TabulaCubeContainer tabulaCube = new TabulaCubeContainer(cube.getName(), identifier, null, dimensions, position, offset, rotation, scale, textureOffset, cube.isTextureMirrored(), 100.0F, 0.0, false);
            tabulaCube.getChildren().addAll(this.convertChildren(cube, tabulaCube));
            tabulaCubes.add(tabulaCube);
        }
        return new TabulaModelContainer(model.getName(), model.getAuthor(), model.getTextureWidth(), model.getTextureHeight(), tabulaCubes, 4);
    }

    private List<TabulaCubeContainer> convertChildren(DefaultCuboidWrapper parent, TabulaCubeContainer tabulaParent) {
        List<TabulaCubeContainer> children = new ArrayList<>();
        for (DefaultCuboidWrapper child : parent.getChildren()) {
            int[] dimensions = { (int) child.getDimensionX(), (int) child.getDimensionY(), (int) child.getDimensionZ() };
            double[] position = { child.getPositionX(), child.getPositionY(), child.getPositionZ() };
            double[] offset = { child.getOffsetX(), child.getOffsetY(), child.getOffsetZ() };
            double[] rotation = { child.getRotationX(), child.getRotationY(), child.getRotationZ() };
            double[] scale = { child.getScaleX(), child.getScaleY(), child.getScaleZ() };
            int[] textureOffset = { child.getTextureX(), child.getTextureY() };
            String identifier = child.getCuboid().getIdentifier();
            if (identifier == null) {
                identifier = this.generateIdentifier(child.getName(), parent.getName());
            }
            TabulaCubeContainer tabulaChild = new TabulaCubeContainer(child.getName(), identifier, tabulaParent.getIdentifier(), dimensions, position, offset, rotation, scale, textureOffset, child.isTextureMirrored(), 100.0F, 0.0, false);
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
        return new String[] {};
    }

    @Override
    public String[] getDefaultArguments(DefaultModelWrapper currentModel) {
        return new String[] {};
    }

    @Override
    public String getFileName(String[] arguments, String fileName) {
        return fileName;
    }

    protected String generateIdentifier(String name, String parentName) {
        return RandomStringUtils.random(20, 32, 127, false, false, null, new Random(parentName == null ? name.hashCode() : name.hashCode() | parentName.hashCode() << 8));
    }

    @Override
    public boolean supports(ModelType modelType) {
        return modelType == ModelType.DEFAULT;
    }
}

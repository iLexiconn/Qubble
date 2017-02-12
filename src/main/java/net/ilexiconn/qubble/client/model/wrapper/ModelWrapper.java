package net.ilexiconn.qubble.client.model.wrapper;

import net.ilexiconn.qubble.client.gui.ModelTexture;
import net.ilexiconn.qubble.client.model.ModelType;
import net.ilexiconn.qubble.client.model.render.QubbleRenderModel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelWrapper<CBE extends CuboidWrapper<CBE>> {
    private Map<String, ModelTexture> textures = new LinkedHashMap<>();

    public abstract boolean reparent(CBE cuboid, CBE parent, boolean inPlace);

    public abstract boolean supportsParenting();

    public abstract void rebuildModel();

    public abstract void addCuboid(CBE cuboid);

    public abstract boolean deleteCuboid(CBE cuboid);

    public abstract void render(boolean selection);

    public abstract List<CBE> getCuboids();

    public abstract String getName();

    public abstract String getFileName();

    public abstract String getAuthor();

    protected abstract ModelWrapper<CBE> copy();

    public abstract NBTTagCompound serializeNBT();

    public abstract CBE createCuboid(String name);

    public abstract ModelType getType();

    public abstract QubbleRenderModel<CBE, ?> getRenderModel();

    public ModelWrapper<CBE> copyModel() {
        ModelWrapper<CBE> modelWrapper = this.copy();
        for (Map.Entry<String, ModelTexture> entry : this.textures.entrySet()) {
            modelWrapper.setTexture(entry.getKey(), entry.getValue().copy());
        }
        return modelWrapper;
    }

    public ModelTexture getTexture(String name) {
        return this.textures.get(name);
    }

    public void setTexture(String name, ModelTexture texture) {
        if (texture == null) {
            this.textures.remove(name);
        } else {
            this.textures.put(name, texture);
        }
        this.rebuildModel();
    }

    public ModelTexture getBaseTexture() {
        return this.getTexture(ModelTexture.BASE);
    }

    public ModelTexture getOverlayTexture() {
        return this.getTexture(ModelTexture.OVERLAY);
    }

    public void setBaseTexture(ModelTexture texture) {
        this.setTexture(ModelTexture.BASE, texture);
    }

    public void setOverlayTexture(ModelTexture texture) {
        this.setTexture(ModelTexture.OVERLAY, texture);
    }

    public Map<String, ModelTexture> getTextures() {
        return this.textures;
    }

    public abstract void importTextures(Map<String, ModelTexture> textures);

    public abstract CBE createSide(CBE selectedCuboid, EnumFacing facing);
}

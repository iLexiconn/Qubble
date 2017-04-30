package net.ilexiconn.qubble.client.project;

import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaModel;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelType<CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> {
    public static final List<String> NAMES = new ArrayList<>();
    public static final Map<Integer, ModelType<?, ?>> TYPES = new HashMap<>();

    public static final ModelType<DefaultCuboidWrapper, DefaultModelWrapper> DEFAULT = new ModelType<DefaultCuboidWrapper, DefaultModelWrapper>(0, true, "Default", DefaultModelWrapper.class, DefaultCuboidWrapper.class) {
        @Override
        public DefaultModelWrapper deserialize(NBTTagCompound compound) {
            return new DefaultModelWrapper(QubbleModel.deserialize(compound));
        }

        @Override
        public DefaultModelWrapper create(String name, String author) {
            return new DefaultModelWrapper(QubbleModel.create(name, author, 64, 32));
        }
    };
    public static final ModelType<BlockCuboidWrapper, BlockModelWrapper> BLOCK = new ModelType<BlockCuboidWrapper, BlockModelWrapper>(1, false, "Vanilla JSON", BlockModelWrapper.class, BlockCuboidWrapper.class) {
        @Override
        public BlockModelWrapper deserialize(NBTTagCompound compound) {
            return new BlockModelWrapper(QubbleVanillaModel.deserialize(compound));
        }

        @Override
        public BlockModelWrapper create(String name, String author) {
            return new BlockModelWrapper(QubbleVanillaModel.create(name, author));
        }
    };

    private int id;
    private String name;
    private Class<MDL> model;
    private Class<CBE> cuboid;
    private boolean inverted;

    private ModelType(int id, boolean inverted, String name, Class<MDL> model, Class<CBE> cuboid) {
        this.id = id;
        this.name = name;
        this.cuboid = cuboid;
        this.model = model;
        this.inverted = inverted;
        NAMES.add(name);
        TYPES.put(id, this);
    }

    public String getName() {
        return this.name;
    }

    public Class<MDL> getModel() {
        return this.model;
    }

    public Class<CBE> getCuboid() {
        return this.cuboid;
    }

    public abstract MDL deserialize(NBTTagCompound compound);

    public abstract MDL create(String name, String author);

    public int id() {
        return this.id;
    }

    public boolean isInverted() {
        return this.inverted;
    }
}

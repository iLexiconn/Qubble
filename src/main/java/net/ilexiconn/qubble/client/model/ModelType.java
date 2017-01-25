package net.ilexiconn.qubble.client.model;

import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.llibrary.client.model.qubble.vanilla.QubbleVanillaModel;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.DefaultModelWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public enum ModelType {
    DEFAULT("Default") {
        @Override
        public ModelWrapper deserialize(NBTTagCompound compound) {
            return new DefaultModelWrapper(QubbleModel.deserialize(compound));
        }

        @Override
        public ModelWrapper create(String name, String author) {
            return new DefaultModelWrapper(QubbleModel.create(name, author, 64, 32));
        }
    },
    JSON_VANILLA("Vanilla JSON") {
        @Override
        public ModelWrapper deserialize(NBTTagCompound compound) {
            return new BlockModelWrapper(QubbleVanillaModel.deserialize(compound));
        }

        @Override
        public ModelWrapper create(String name, String author) {
            return new BlockModelWrapper(QubbleVanillaModel.create(name, author));
        }
    };

    public static final List<String> NAMES = new ArrayList<>();

    private String name;

    ModelType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    static {
        for (ModelType type : values()) {
            NAMES.add(type.getName());
        }
    }

    public abstract ModelWrapper deserialize(NBTTagCompound compound);

    public abstract ModelWrapper create(String name, String author);
}

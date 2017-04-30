package net.ilexiconn.qubble.client.project;

import net.ilexiconn.qubble.client.model.wrapper.CuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.ModelWrapper;
import net.minecraft.util.EnumFacing;

public class Selection<CBE extends CuboidWrapper<CBE>, MDL extends ModelWrapper<CBE>> {
    private final MDL model;
    private final CBE cuboid;
    private final EnumFacing facing;

    public Selection(MDL model, CBE cuboid, EnumFacing facing) {
        this.model = model;
        this.cuboid = cuboid;
        this.facing = facing;
    }

    public MDL getModel() {
        return this.model;
    }

    public CBE getCuboid() {
        return this.cuboid;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }
}

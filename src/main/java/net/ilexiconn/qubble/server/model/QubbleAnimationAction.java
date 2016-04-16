package net.ilexiconn.qubble.server.model;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class QubbleAnimationAction implements INBTSerializable<NBTTagCompound> {
    private String cube;
    private Action action;
    private float valueX;
    private float valueY;
    private float valueZ;

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("cube", this.cube);
        compound.setString("action", this.action.name());
        NBTTagCompound valueTag = new NBTTagCompound();
        valueTag.setFloat("x", this.valueX);
        valueTag.setFloat("y", this.valueY);
        valueTag.setFloat("z", this.valueZ);
        compound.setTag("value", valueTag);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.cube = compound.getString("cube");
        this.action = Action.valueOf(compound.getString("action"));
        NBTTagCompound valueTag = compound.getCompoundTag("value");
        this.valueX = valueTag.getFloat("x");
        this.valueY = valueTag.getFloat("y");
        this.valueZ = valueTag.getFloat("z");
    }

    public enum Action {
        ROTATE,
        MOVE
    }

    public String getCube() {
        return cube;
    }

    public Action getAction() {
        return action;
    }

    public float getValueX() {
        return valueX;
    }

    public float getValueY() {
        return valueY;
    }

    public float getValueZ() {
        return valueZ;
    }

    public void setCube(String cube) {
        this.cube = cube;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setValue(float x, float y, float z) {
        this.valueX = x;
        this.valueY = y;
        this.valueZ = z;
    }
}

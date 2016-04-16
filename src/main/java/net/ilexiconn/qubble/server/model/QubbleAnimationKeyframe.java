package net.ilexiconn.qubble.server.model;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class QubbleAnimationKeyframe implements INBTSerializable<NBTTagCompound> {
    private int duration;
    private Type type;
    private List<QubbleAnimationAction> actions = new ArrayList<>();

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("duration", this.duration);
        compound.setString("type", this.type.name());
        NBTTagList actionsTag = new NBTTagList();
        for (QubbleAnimationAction action : this.actions) {
            actionsTag.appendTag(action.serializeNBT());
        }
        compound.setTag("actions", actionsTag);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.duration = compound.getInteger("duration");
        this.type = Type.valueOf(compound.getString("type"));
        NBTTagList actionsTag = compound.getTagList("actions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < actionsTag.tagCount(); i++) {
            QubbleAnimationAction action = new QubbleAnimationAction();
            action.deserializeNBT(actionsTag.getCompoundTagAt(i));
            this.actions.add(action);
        }
    }

    public enum Type {
        DYNAMIC,
        STATIC,
        RESET
    }

    public int getDuration() {
        return duration;
    }

    public Type getType() {
        return type;
    }

    public List<QubbleAnimationAction> getActions() {
        return actions;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

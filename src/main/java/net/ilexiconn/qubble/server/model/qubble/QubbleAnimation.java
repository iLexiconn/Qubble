package net.ilexiconn.qubble.server.model.qubble;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class QubbleAnimation implements INBTSerializable<NBTTagCompound> {
    private String name;
    private List<QubbleAnimationKeyframe> keyframes = new ArrayList<>();

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("name", this.name);
        NBTTagList keyframesTag = new NBTTagList();
        for (QubbleAnimationKeyframe keyframe : this.keyframes) {
            keyframesTag.appendTag(keyframe.serializeNBT());
        }
        compound.setTag("keyframes", keyframesTag);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.name = compound.getString("name");
        NBTTagList keyframesTag = compound.getTagList("name", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < keyframesTag.tagCount(); i++) {
            QubbleAnimationKeyframe keyframe = new QubbleAnimationKeyframe();
            keyframe.deserializeNBT(keyframesTag.getCompoundTagAt(i));
            this.keyframes.add(keyframe);
        }
    }

    public String getName() {
        return name;
    }

    public List<QubbleAnimationKeyframe> getKeyframes() {
        return keyframes;
    }

    public void setName(String name) {
        this.name = name;
    }
}

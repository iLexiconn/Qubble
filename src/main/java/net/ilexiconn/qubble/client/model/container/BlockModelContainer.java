package net.ilexiconn.qubble.client.model.container;

import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockModelContainer {
    public String parent;
    public boolean ambientocclusion = true;
    public Map<String, Display> display;
    public Map<String, String> textures = new HashMap<>();
    public List<Element> elements = new ArrayList<>();

    public static class Element {
        public String name;
        public String __comment;
        public float[] from = new float[3];
        public float[] to = new float[3];
        public ElementRotation rotation;
        private Boolean shade;
        public Map<String, ElementFace> faces = new HashMap<>();

        public boolean getShade() {
            return this.shade == null ? true : this.shade;
        }

        public void setShade(Boolean shade) {
            this.shade = shade;
        }
    }

    public static class ElementRotation {
        public float[] origin;
        public String axis;
        private Boolean rescale;
        public float angle;

        public boolean getRescale() {
            return this.rescale == null ? false : this.rescale;
        }

        public void setRescale(Boolean rescale) {
            this.rescale = rescale;
        }
    }

    public static class ElementFace {
        public String texture;
        public float[] uv;
        public String cullface;
        public int tintindex;
        private int rotation;

        public int getRotation() {
            return MathHelper.clamp(this.rotation / 90 * 90, 0, 270);
        }

        public void setRotation(int rotation) {
            this.rotation = MathHelper.clamp(rotation / 90 * 90, 0, 270);
        }
    }

    public static class Display {
        public float[] rotation;
        public float[] translation;
        public float[] scale;
    }
}

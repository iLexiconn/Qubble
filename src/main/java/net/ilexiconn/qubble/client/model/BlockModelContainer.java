package net.ilexiconn.qubble.client.model;

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
        private Integer rotation;
        private Integer tintindex;

        public int getRotation() {
            return this.rotation != null ? this.rotation : 0;
        }

        public int getTintIndex() {
            return this.tintindex != null ? this.tintindex : 0;
        }

        public void setRotation(Integer rotation) {
            this.rotation = rotation;
        }

        public void setTintIndex(Integer tintindex) {
            this.tintindex = tintindex;
        }
    }

    public static class Display {
        public float[] rotation;
        public float[] translation;
        public float[] scale;
    }
}

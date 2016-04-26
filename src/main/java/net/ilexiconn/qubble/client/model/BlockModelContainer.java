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
        public boolean shade = true;
        public Map<String, ElementFace> faces = new HashMap<>();
    }

    public static class ElementRotation {
        public float[] origin;
        public String axis;
        public boolean rescale;
        public float angle;
    }

    public static class ElementFace {
        public String texture;
        public float[] uv;
        public String cullface;
        public int rotation;
        public int tintindex;
    }

    public static class Display {
        public float[] rotation;
        public float[] translation;
        public float[] scale;
    }
}

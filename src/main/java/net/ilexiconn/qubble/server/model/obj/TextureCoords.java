package net.ilexiconn.qubble.server.model.obj;

import org.lwjgl.util.vector.Vector2f;

import java.util.Locale;

public class TextureCoords {
    public Vector2f uvCoords;
    public int index;

    public TextureCoords(float u, float v) {
        this(new Vector2f(u, v));
    }

    public TextureCoords(Vector2f uvCoords) {
        this.uvCoords = uvCoords;
    }

    void register(OBJModel model) {
        index = model.nextTexIndex++;
    }

    @Override
    public String toString() {
        return "vt " + String.format(Locale.US, "%.6f", uvCoords.x) + " " + String.format(Locale.US, "%.6f", uvCoords.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TextureCoords) {
            TextureCoords uv = (TextureCoords) obj;
            return uv.uvCoords.x == uvCoords.x && uv.uvCoords.y == uvCoords.y;
        }
        return false;
    }
}

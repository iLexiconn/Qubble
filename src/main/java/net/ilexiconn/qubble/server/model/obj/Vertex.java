package net.ilexiconn.qubble.server.model.obj;

import org.lwjgl.util.vector.Vector3f;

import java.util.Locale;

public class Vertex {
    public Vector3f position;
    public int index;

    public Vertex(float x, float y, float z) {
        this(new Vector3f(x, y, z));
    }

    public Vertex(Vector3f position) {
        this.position = position;
    }

    void register(OBJModel model) {
        index = model.nextVertexIndex++;
    }

    @Override
    public String toString() {
        return "v " + String.format(Locale.US, "%.6f", position.x) + " " + String.format(Locale.US, "%.6f", position.y) + " " + String.format(Locale.US, "%.6f", position.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vertex) {
            Vertex vertex = (Vertex) obj;
            return vertex.position.x == position.x && vertex.position.y == position.y && vertex.position.z == position.z;
        }
        return false;
    }
}

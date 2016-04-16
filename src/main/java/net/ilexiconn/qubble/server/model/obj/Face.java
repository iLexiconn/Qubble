package net.ilexiconn.qubble.server.model.obj;

import java.util.ArrayList;
import java.util.List;

public class Face {
    public Shape parentShape;

    private List<Vertex> vertices = new ArrayList<>();
    private List<TextureCoords> uvCoords = new ArrayList<>();

    public Face(Shape shape) {
        this.parentShape = shape;
    }

    public Face append(Vertex vertex, TextureCoords uvCoords) {
        vertices.add(parentShape.addVertex(vertex));
        this.uvCoords.add(parentShape.addTexCoords(uvCoords));
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("f");
        for (int i = 0; i < vertices.size(); i++) {
            sb.append(" ").append(vertices.get(i).index).append("/").append(uvCoords.get(i).index);
        }
        return sb.toString();
    }
}

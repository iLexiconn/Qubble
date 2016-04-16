package net.ilexiconn.qubble.server.model.obj;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Shape {
    public String name;
    public OBJModel model;
    public List<Face> faces = new ArrayList<>();
    private List<Vertex> vertices = new ArrayList<>();
    private List<TextureCoords> texCoords = new ArrayList<>();

    public Shape(OBJModel parent, String name) {
        this.model = parent;
        this.name = name;
    }

    public Matrix3f rotationMatrix(float angle, float x, float y, float z) {
        angle *= (float) Math.PI / 180f;
        Vector3f axis = new Vector3f(x, y, z);
        axis.normalise();
        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);
        float oc = 1.0f - c;

        Matrix3f mat = new Matrix3f();
        mat.m00 = oc * axis.x * axis.x + c;
        mat.m01 = oc * axis.x * axis.y - axis.z * s;
        mat.m02 = oc * axis.z * axis.x + axis.y * s;
        mat.m10 = oc * axis.x * axis.y + axis.z * s;
        mat.m11 = oc * axis.y * axis.y + c;
        mat.m12 = oc * axis.y * axis.z - axis.x * s;
        mat.m20 = oc * axis.z * axis.x - axis.y * s;
        mat.m21 = oc * axis.y * axis.z + axis.x * s;
        mat.m22 = oc * axis.z * axis.z + c;
        return mat;
    }

    public Vertex addVertex(Vertex newVertex) {
        for (Vertex vertex : vertices) {
            if (vertex.equals(newVertex)) {
                return vertex;
            }
        }
        vertices.add(newVertex);
        newVertex.register(model);
        return newVertex;
    }

    public TextureCoords addTexCoords(TextureCoords newUV) {
        for (TextureCoords uv : texCoords) {
            if (uv.equals(newUV)) {
                return uv;
            }
        }
        texCoords.add(newUV);
        newUV.register(model);
        return newUV;
    }

    public List<String> toStringList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("o " + name);
        list.addAll(vertices.stream().map(Vertex::toString).collect(Collectors.toList()));
        list.addAll(texCoords.stream().map(TextureCoords::toString).collect(Collectors.toList()));
        list.addAll(faces.stream().map(Face::toString).collect(Collectors.toList()));
        return list;
    }

    public void translate(Vector3f translationVector) {
        for (Vertex vertex : vertices) {
            Vector3f.add(vertex.position, translationVector, vertex.position);
        }
    }

    public void scale(Vector3f scaleVector) {
        for (Vertex vertex : vertices) {
            vertex.position.x *= scaleVector.x;
            vertex.position.y *= scaleVector.y;
            vertex.position.z *= scaleVector.z;
        }
    }

    public void rotate(float angle, float x, float y, float z) {
        Matrix3f rotationMatrix = rotationMatrix(angle, x, y, z);
        for (Vertex vertex : vertices) {
            Matrix3f.transform(rotationMatrix, vertex.position, vertex.position);
        }
    }
}

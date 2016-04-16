package net.ilexiconn.qubble.server.model.exporter;

import net.ilexiconn.qubble.server.model.QubbleCube;
import net.ilexiconn.qubble.server.model.QubbleModel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JavaScriptExporter implements IModelExporter<List<String>> {
    @Override
    public String getExtension() {
        return "js";
    }

    @Override
    public List<String> export(QubbleModel model, String... arguments) {
        List<String> list = new ArrayList<>();
        list.add("//" + model.getName() + " by " + model.getAuthor());
        list.add("function addEntityRenderType(renderer) {");
        list.add("    var model = renderer.getModel();");
        list.add("    var body = model.getPart(\"body\").clear();");
        list.add("    var head = model.getPart(\"head\").clear();");
        list.add("    var rightArm = model.getPart(\"rightArm\").clear();");
        list.add("    var leftArm = model.getPart(\"leftArm\").clear();");
        list.add("    var rightLeg = model.getPart(\"rightLeg\").clear();");
        list.add("    var leftLeg = model.getPart(\"leftLeg\").clear();");
        list.add("    body.setTextureSize(" + model.getTextureWidth() + ", " + model.getTextureHeight() + ");");
        list.add("    head.setTextureSize(" + model.getTextureWidth() + ", " + model.getTextureHeight() + ");");
        list.add("    rightArm.setTextureSize(" + model.getTextureWidth() + ", " + model.getTextureHeight() + ");");
        list.add("    leftArm.setTextureSize(" + model.getTextureWidth() + ", " + model.getTextureHeight() + ");");
        list.add("    rightLeg.setTextureSize(" + model.getTextureWidth() + ", " + model.getTextureHeight() + ");");
        list.add("    leftLeg.setTextureSize(" + model.getTextureWidth() + ", " + model.getTextureHeight() + ");");
        list.add("");
        this.addCubeDeclarations(model.getCubes(), list);
        list.add("}");
        return list;
    }

    @Override
    public void save(List<String> model, File file, Object... arguments) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        model.forEach(writer::println);
        writer.close();
    }

    public void addCubeDeclarations(List<QubbleCube> cubes, List<String> list) {
        for (QubbleCube cube : cubes) {
            String name = cube.getName();
            String bodyPart = this.getBodyPart(name);
            list.add("    //" + name);
            list.add("    " + bodyPart + ".setTextureOffset(" + cube.getTextureX() + ", " + cube.getTextureY() + ");");
            list.add("    " + bodyPart + ".addBox(" + (cube.getPositionX() + cube.getOffsetX()) + ", " + (cube.getPositionY() + cube.getOffsetY()) + ", " + (cube.getPositionZ() + cube.getOffsetZ()) + ", " + cube.getDimensionX() + ", " + cube.getDimensionY() + ", " + cube.getDimensionZ() + ");");
            this.addCubeDeclarations(cube.getChildren(), list);
        }
    }

    public String getBodyPart(String name) {
        if (name.toLowerCase(Locale.ENGLISH).contains("arm") || name.toLowerCase(Locale.ENGLISH).contains("hand") || name.toLowerCase(Locale.ENGLISH).contains("claw")) {
            if (name.toLowerCase(Locale.ENGLISH).contains("right")) {
                return "rightArm";
            } else if (name.toLowerCase(Locale.ENGLISH).contains("left")) {
                return "leftArm";
            } else {
                return "body";
            }
        } else if (name.toLowerCase(Locale.ENGLISH).contains("leg") || name.toLowerCase(Locale.ENGLISH).contains("foot") || name.toLowerCase(Locale.ENGLISH).contains("knee") || name.toLowerCase(Locale.ENGLISH).contains("calf")) {
            if (name.toLowerCase(Locale.ENGLISH).contains("right")) {
                return "rightLeg";
            } else if (name.toLowerCase(Locale.ENGLISH).contains("left")) {
                return "leftLeg";
            } else {
                return "body";
            }
        } else if (name.toLowerCase(Locale.ENGLISH).contains("head") || name.toLowerCase(Locale.ENGLISH).contains("neck") || name.toLowerCase(Locale.ENGLISH).contains("throat") || name.toLowerCase(Locale.ENGLISH).contains("jaw") || name.toLowerCase(Locale.ENGLISH).contains("eye")) {
            return "head";
        } else {
            return "body";
        }
    }
}

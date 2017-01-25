package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.gui.element.*;
import net.ilexiconn.qubble.client.gui.element.*;
import net.ilexiconn.qubble.client.gui.element.sidebar.*;
import net.ilexiconn.qubble.client.model.*;

import java.util.function.*;

public enum EditMode {
    MODEL("Model", 40, type -> {
        if (type == ModelType.DEFAULT) {
            return new DefaultModelSidebarHandler();
        } else {
            return new BlockModelSidebarHandler();
        }
    }, gui -> null),
    TEXTURE("Texture", 50, type -> {
        if (type == ModelType.DEFAULT) {
            return new DefaultTextureSidebarHandler();
        } else {
            return new BlockTextureSidebarHandler();
        }
    }, gui -> {
        Project<?, ?> selectedProject = gui.getSelectedProject();
        if (selectedProject == null || selectedProject.getModelType() == ModelType.DEFAULT) {
            WindowElement<QubbleGUI> textureWindow = new WindowElement<>(gui, "Texture Map", 200, 214, false);
            new DefaultTextureMapElement(gui, 0.0F, 14.0F, 200, 200).withParent(textureWindow);
            return new Element[] { textureWindow };
        }
        return null;
    });
//    ANIMATE("Animate", 50, gui -> null);

    private final String name;
    private final int width;
    private final Function<ModelType, SidebarHandler> sidebarHandler;
    private final Function<QubbleGUI, Element<QubbleGUI>[]> createElements;

    EditMode(String name, int width, Function<ModelType, SidebarHandler> sidebarHandler, Function<QubbleGUI, Element<QubbleGUI>[]> createElements) {
        this.name = name;
        this.width = width;
        this.sidebarHandler = sidebarHandler;
        this.createElements = createElements;
    }

    public String getName() {
        return this.name;
    }

    public int getWidth() {
        return this.width;
    }

    public SidebarHandler createHandler(ModelType modelType) {
        return this.sidebarHandler.apply(modelType);
    }

    public Element[] createElements(QubbleGUI create) {
        Element[] elements = this.createElements.apply(create);
        if (elements == null) {
            return new Element[] {};
        }
        return elements;
    }
}

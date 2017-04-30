package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.qubble.client.gui.element.BlockTextureBarElement;
import net.ilexiconn.qubble.client.gui.element.DefaultTextureMapElement;
import net.ilexiconn.qubble.client.gui.element.sidebar.DefaultModelSidebarHandler;
import net.ilexiconn.qubble.client.gui.element.sidebar.DefaultTextureSidebarHandler;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarHandler;
import net.ilexiconn.qubble.client.gui.element.sidebar.block.BlockModelSidebarHandler;
import net.ilexiconn.qubble.client.gui.element.sidebar.block.BlockTextureSidebarHandler;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.project.Project;

import java.util.function.Function;

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
        Project selectedProject = gui.getSelectedProject();
        if (selectedProject != null) {
            ModelType modelType = selectedProject.getModelType();
            if (modelType == ModelType.DEFAULT) {
                WindowElement<QubbleGUI> textureWindow = new WindowElement<>(gui, "Texture Map", 200, 214, false);
                new DefaultTextureMapElement(gui, 0.0F, 14.0F, 200, 200).withParent(textureWindow);
                return new Element[] { textureWindow };
            } else if (modelType == ModelType.BLOCK) {
                BlockTextureBarElement textureBar = new BlockTextureBarElement(gui);
                return new Element[] { textureBar };
            }
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

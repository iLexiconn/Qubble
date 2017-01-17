package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.qubble.client.gui.element.TextureMapElement;
import net.ilexiconn.qubble.client.gui.element.sidebar.ModelSidebarHandler;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarHandler;
import net.ilexiconn.qubble.client.gui.element.sidebar.TextureSidebarHandler;

import java.util.function.Function;

public enum EditMode {
    MODEL("Model", 40, new ModelSidebarHandler(), gui -> null),
    TEXTURE("Texture", 50, new TextureSidebarHandler(), gui -> {
        WindowElement<QubbleGUI> textureWindow = new WindowElement<>(gui, "Texture Map", 200, 214, false);
        new TextureMapElement(gui, 0.0F, 14.0F, 200, 200).withParent(textureWindow);
        return new Element[] { textureWindow };
    });
//    ANIMATE("Animate", 50, gui -> null);

    private final String name;
    private final int width;
    private final SidebarHandler sidebarHandler;
    private final Function<QubbleGUI, Element<QubbleGUI>[]> createElements;

    EditMode(String name, int width, SidebarHandler sidebarHandler, Function<QubbleGUI, Element<QubbleGUI>[]> createElements) {
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

    public SidebarHandler getSidebarHandler() {
        return this.sidebarHandler;
    }

    public Element[] createElements(QubbleGUI create) {
        Element[] elements = this.createElements.apply(create);
        if (elements == null) {
            return new Element[] {};
        }
        return elements;
    }
}

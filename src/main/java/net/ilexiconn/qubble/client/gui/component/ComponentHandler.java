package net.ilexiconn.qubble.client.gui.component;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public enum ComponentHandler {
    INSTANCE;

    private Map<GuiScreen, List<IComponent>> componentMap = new WeakHashMap<>();

    public void clearGUI(GuiScreen gui) {
        if (this.componentMap.containsKey(gui)) {
            this.componentMap.get(gui).clear();
        }
    }

    public <T extends GuiScreen> void addComponent(T gui, IComponent<T> component) {
        if (this.componentMap.containsKey(gui)) {
            this.componentMap.get(gui).add(component);
        } else {
            List<IComponent> componentList = new ArrayList<>();
            componentList.add(component);
            this.componentMap.put(gui, componentList);
        }
    }

    public <T extends GuiScreen> void render(T gui, float mouseX, float mouseY, float partialTicks) {
        if (this.componentMap.containsKey(gui)) {
            List<IComponent<T>> componentList = (List<IComponent<T>>) ((List<?>) this.componentMap.get(gui));
            for (IComponent<T> component : componentList) {
                component.render(gui, mouseX, mouseY, 0.0F, 0.0F, partialTicks);
            }
        }
    }

    public <T extends GuiScreen> void renderAfter(T gui, float mouseX, float mouseY, float partialTicks) {
        if (this.componentMap.containsKey(gui)) {
            List<IComponent<T>> componentList = (List<IComponent<T>>) ((List<?>) this.componentMap.get(gui));
            for (IComponent<T> component : componentList) {
                component.renderAfter(gui, mouseX, mouseY, 0.0F, 0.0F, partialTicks);
            }
        }
    }

    public <T extends GuiScreen> void mouseClicked(T gui, float mouseX, float mouseY, int button) {
        if (this.componentMap.containsKey(gui)) {
            List<IComponent<T>> componentList = (List<IComponent<T>>) ((List<?>) this.componentMap.get(gui));
            for (IComponent<T> component : Lists.reverse(componentList)) {
                if (component.mouseClicked(gui, mouseX, mouseY, button)) {
                    return;
                }
            }
        }
    }

    public <T extends GuiScreen> void mouseDragged(T gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.componentMap.containsKey(gui)) {
            List<IComponent<T>> componentList = (List<IComponent<T>>) ((List<?>) this.componentMap.get(gui));
            for (IComponent<T> component : Lists.reverse(componentList)) {
                if (component.mouseDragged(gui, mouseX, mouseY, button, timeSinceClick)) {
                    return;
                }
            }
        }
    }

    public <T extends GuiScreen> void mouseReleased(T gui, float mouseX, float mouseY, int button) {
        if (this.componentMap.containsKey(gui)) {
            List<IComponent<T>> componentList = (List<IComponent<T>>) ((List<?>) this.componentMap.get(gui));
            for (IComponent<T> component : Lists.reverse(componentList)) {
                if (component.mouseReleased(gui, mouseX, mouseY, button)) {
                    return;
                }
            }
        }
    }

    public <T extends GuiScreen> void keyPressed(T gui, char character, int key) {
        if (this.componentMap.containsKey(gui)) {
            List<IComponent<T>> componentList = (List<IComponent<T>>) ((List<?>) this.componentMap.get(gui));
            for (IComponent<T> component : Lists.reverse(componentList)) {
                if (component.keyPressed(gui, character, key)) {
                    return;
                }
            }
        }
    }
}

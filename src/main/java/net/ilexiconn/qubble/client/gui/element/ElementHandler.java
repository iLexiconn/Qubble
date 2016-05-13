package net.ilexiconn.qubble.client.gui.element;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public enum ElementHandler {
    INSTANCE;

    private Map<GuiScreen, List<Element<?>>> elementMap = new HashMap<>();

    public <T extends GuiScreen> void clearGUI(T gui) {
        if (this.elementMap.containsKey(gui)) {
            this.elementMap.get(gui).clear();
        }
    }

    public <T extends GuiScreen> void addElement(T gui, Element<T> element) {
        if (this.elementMap.containsKey(gui)) {
            this.elementMap.get(gui).add(element);
        } else {
            List<Element<?>> elementList = new ArrayList<>();
            elementList.add(element);
            this.elementMap.put(gui, elementList);
        }
    }

    public <T extends GuiScreen> void removeElement(T gui, Element<T> element) {
        if (this.elementMap.containsKey(gui)) {
            this.elementMap.get(gui).remove(element);
        }
    }

    public <T extends GuiScreen> boolean isOnTop(T gui, Element<T> element, float mouseX, float mouseY) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = Lists.reverse(new ArrayList<>((List<Element<T>>) ((List<?>) this.elementMap.get(gui))));
            this.addChildren(elementList);
            for (Element<T> e : elementList) {
                if (mouseX >= e.getPosX() && mouseY >= e.getPosY() && mouseX < e.getPosX() + e.getWidth() && mouseY < e.getPosY() + e.getHeight()) {
                    return element == e || (element.getParent() != null && element.getParent() == e);
                }
            }
        } else {
            return true;
        }
        return false;
    }

    private <T extends GuiScreen> void addChildren(List<Element<T>> elements) {
        for (Element<T> element : new ArrayList<>(elements)) {
            List<Element<T>> children = new ArrayList<>();
            int index = elements.indexOf(element);
            for (Element<T> child : element.getChildren()) {
                children.add(child);
                List<Element<T>> nextChildren = new ArrayList<>();
                nextChildren.add(child);
                this.addChildren(nextChildren);
                children.addAll(nextChildren);
            }
            for (Element<T> child : children) {
                elements.add(index, child);
            }
        }
    }

    public <T extends GuiScreen> void init(T gui) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = new ArrayList<>((List<Element<T>>) ((List<?>) this.elementMap.get(gui)));
            for (Element<T> element : new ArrayList<>(elementList)) {
                this.initElement(element);
            }
        }
    }

    private <T extends GuiScreen> void initElement(Element<T> element) {
        if (element.isVisible()) {
            element.init();
            for (Element<T> child : element.getChildren()) {
                this.initElement(child);
            }
        }
    }

    public <T extends GuiScreen> void update(T gui) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = new ArrayList<>((List<Element<T>>) ((List<?>) this.elementMap.get(gui)));
            for (Element<T> element : elementList) {
                this.updateElement(element);
            }
        }
    }

    private <T extends GuiScreen> void updateElement(Element<T> element) {
        if (element.isVisible()) {
            element.update();
            for (Element<T> child : element.getChildren()) {
                this.updateElement(child);
            }
        }
    }

    public <T extends GuiScreen> void render(T gui, float mouseX, float mouseY, float partialTicks) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = new ArrayList<>(new ArrayList<>((List<Element<T>>) ((List<?>) this.elementMap.get(gui))));
            for (Element<T> element : elementList) {
                if (!(element instanceof WindowElement)) {
                    this.renderElement(element, mouseX, mouseY, partialTicks);
                }
            }
            for (Element<T> element : elementList) {
                if (element instanceof WindowElement) {
                    this.renderElement(element, mouseX, mouseY, partialTicks);
                }
            }
        }
    }

    private <T extends GuiScreen> void renderElement(Element<T> element, float mouseX, float mouseY, float partialTicks) {
        if (element.isVisible()) {
            element.render(mouseX, mouseY, partialTicks);
            for (Element<T> child : element.getChildren()) {
                this.renderElement(child, mouseX, mouseY, partialTicks);
            }
        }
    }

    public <T extends GuiScreen> void mouseClicked(T gui, float mouseX, float mouseY, int button) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = new ArrayList<>(new ArrayList<>((List<Element<T>>) ((List<?>) this.elementMap.get(gui))));
            for (Element<T> element : Lists.reverse(elementList)) {
                if (this.mouseClickedElement(element, mouseX, mouseY, button)) {
                    return;
                }
            }
        }
    }

    private <T extends GuiScreen> boolean mouseClickedElement(Element<T> element, float mouseX, float mouseY, int button) {
        if (element.isVisible()) {
            for (Element<T> child : element.getChildren()) {
                if (this.mouseClickedElement(child, mouseX, mouseY, button)) {
                    return true;
                }
            }
            return element.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    public <T extends GuiScreen> void mouseDragged(T gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = new ArrayList<>((List<Element<T>>) ((List<?>) this.elementMap.get(gui)));
            for (Element<T> element : Lists.reverse(elementList)) {
                if (this.mouseDraggedElement(element, mouseX, mouseY, button, timeSinceClick)) {
                    return;
                }
            }
        }
    }

    private <T extends GuiScreen> boolean mouseDraggedElement(Element<T> element, float mouseX, float mouseY, int button, long timeSinceClick) {
        if (element.isVisible()) {
            for (Element<T> child : element.getChildren()) {
                if (this.mouseDraggedElement(child, mouseX, mouseY, button, timeSinceClick)) {
                    return true;
                }
            }
            return element.mouseDragged(mouseX, mouseY, button, timeSinceClick);
        }
        return false;
    }

    public <T extends GuiScreen> void mouseReleased(T gui, float mouseX, float mouseY, int button) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = new ArrayList<>((List<Element<T>>) ((List<?>) this.elementMap.get(gui)));
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible()) {
                    if (this.mouseReleasedElement(element, mouseX, mouseY, button)) {
                        return;
                    }
                }
            }
        }
    }

    private <T extends GuiScreen> boolean mouseReleasedElement(Element<T> element, float mouseX, float mouseY, int button) {
        if (element.isVisible()) {
            for (Element<T> child : element.getChildren()) {
                if (this.mouseReleasedElement(child, mouseX, mouseY, button)) {
                    return true;
                }
            }
            return element.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }


    public <T extends GuiScreen> void keyPressed(T gui, char character, int key) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = new ArrayList<>((List<Element<T>>) ((List<?>) this.elementMap.get(gui)));
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible()) {
                    if (this.keyPressedElement(element, character, key)) {
                        return;
                    }
                }
            }
        }
    }

    private <T extends GuiScreen> boolean keyPressedElement(Element<T> element, char character, int key) {
        if (element.isVisible()) {
            for (Element<T> child : element.getChildren()) {
                if (this.keyPressedElement(child, character, key)) {
                    return true;
                }
            }
            return element.keyPressed(character, key);
        }
        return false;
    }

}

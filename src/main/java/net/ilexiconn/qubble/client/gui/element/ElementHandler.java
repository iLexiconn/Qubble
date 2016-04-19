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
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            boolean aboveWindow = true;
            boolean inList = false;
            for (Element<T> e : Lists.reverse(elementList)) {
                if (e == element) {
                    inList = true;
                } else if (e instanceof WindowElement && mouseX >= e.getPosX() && mouseY >= e.getPosY() && mouseX <= e.getPosX() + e.getWidth() && mouseY <= e.getPosY() + e.getHeight()) {
                    aboveWindow = false;
                }
            }
            return !inList || aboveWindow;
        } else {
            return true;
        }
    }

    public <T extends GuiScreen> void init(T gui) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            for (Element<T> element : new ArrayList<>(elementList)) {
                if (element.isVisible()) {
                    element.init();
                }
            }
        }
    }

    public <T extends GuiScreen> void update(T gui) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            for (Element<T> element : elementList) {
                if (element.isVisible()) {
                    element.update();
                }
            }
        }
    }

    public <T extends GuiScreen> void render(T gui, float mouseX, float mouseY, float partialTicks) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            for (Element<T> element : elementList) {
                if (!(element instanceof WindowElement)) {
                    if (element.isVisible()) {
                        element.render(mouseX, mouseY, partialTicks);
                    }
                }
            }
            for (Element<T> element : elementList) {
                if (element instanceof WindowElement) {
                    if (element.isVisible()) {
                        element.render(mouseX, mouseY, partialTicks);
                    }
                }
            }
        }
    }

    public <T extends GuiScreen> void renderAfter(T gui, float mouseX, float mouseY, float partialTicks) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            for (Element<T> element : elementList) {
                if (!(element instanceof WindowElement)) {
                    if (element.isVisible()) {
                        element.renderAfter(mouseX, mouseY, partialTicks);
                    }
                }
            }
            for (Element<T> element : elementList) {
                if (element instanceof WindowElement) {
                    if (element.isVisible()) {
                        element.renderAfter(mouseX, mouseY, partialTicks);
                    }
                }
            }
        }
    }

    public <T extends GuiScreen> void mouseClicked(T gui, float mouseX, float mouseY, int button) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible()) {
                    if (element.mouseClicked(mouseX, mouseY, button)) {
                        return;
                    }
                }
            }
        }
    }

    public <T extends GuiScreen> void mouseDragged(T gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible()) {
                    if (element.mouseDragged(mouseX, mouseY, button, timeSinceClick)) {
                        return;
                    }
                }
            }
        }
    }

    public <T extends GuiScreen> void mouseReleased(T gui, float mouseX, float mouseY, int button) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible()) {
                    if (element.mouseReleased(mouseX, mouseY, button)) {
                        return;
                    }
                }
            }
        }
    }

    public <T extends GuiScreen> void keyPressed(T gui, char character, int key) {
        if (this.elementMap.containsKey(gui)) {
            List<Element<T>> elementList = (List<Element<T>>) ((List<?>) this.elementMap.get(gui));
            for (Element<T> element : Lists.reverse(elementList)) {
                if (element.isVisible()) {
                    if (element.keyPressed(character, key)) {
                        return;
                    }
                }
            }
        }
    }
}

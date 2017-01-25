package net.ilexiconn.qubble.client.gui.element.toolbar;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.qubble.client.gui.EditMode;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Toolbar {
    private final QubbleGUI gui;
    private final Map<EditMode, ButtonElement<QubbleGUI>> modeButtons = new HashMap<>();
    private final List<Element<QubbleGUI>> modeElements = new ArrayList<>();

    public Toolbar(QubbleGUI gui) {
        this.gui = gui;
    }

    public void init(EditMode mode) {
        this.modeButtons.clear();
        int modeX = 122;
        EditMode[] modes = EditMode.values();
        for (int i = modes.length - 1; i >= 0; i--) {
            EditMode editMode = modes[i];
            ButtonElement<QubbleGUI> button = (ButtonElement<QubbleGUI>) new ButtonElement<>(this.gui, editMode.getName(), this.gui.width - modeX - editMode.getWidth(), 0, editMode.getWidth(), 20, (b) -> {
                if (b.getColorScheme() != ColorSchemes.TAB_ACTIVE) {
                    this.setMode(editMode);
                    return true;
                } else {
                    return false;
                }
            }).withColorScheme(mode == editMode ? ColorSchemes.TAB_ACTIVE : ColorSchemes.DEFAULT);
            this.gui.addElement(button);
            this.modeButtons.put(editMode, button);
            modeX += editMode.getWidth();
        }
    }

    private void setMode(EditMode mode) {
        this.gui.setEditMode(mode);
        this.updateElements();

        for (Map.Entry<EditMode, ButtonElement<QubbleGUI>> entry : this.modeButtons.entrySet()) {
            EditMode buttonMode = entry.getKey();
            ButtonElement<QubbleGUI> button = entry.getValue();
            button.withColorScheme(mode == buttonMode ? ColorSchemes.TAB_ACTIVE : ColorSchemes.DEFAULT);
        }
    }

    private void addModeElement(Element<QubbleGUI> element) {
        this.gui.addElement(element);
        this.modeElements.add(element);
    }

    private void removeModeElements() {
        for (Element<QubbleGUI> element : this.modeElements) {
            this.gui.removeElement(element);
        }
        this.modeElements.clear();
    }

    public void updateElements() {
        this.removeModeElements();
        Element[] elements = this.gui.getEditMode().createElements(this.gui);
        for (Element element : elements) {
            this.addModeElement(element);
        }
    }
}

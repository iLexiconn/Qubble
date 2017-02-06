package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.IElementGUI;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public enum GUIHelper {
    INSTANCE;

    public <T extends IElementGUI> WindowElement<T> error(T gui, int width, String title, Throwable throwable) {
        String message = throwable.toString();
        String localizedMessage = throwable.getLocalizedMessage();
        if (localizedMessage != null) {
            message += ": " + localizedMessage;
        }
        return this.info(gui, width, title, message);
    }

    public <T extends IElementGUI> WindowElement<T> info(T gui, int width, String title, String message) {
        FontRenderer fontRenderer = gui.getFontRenderer();
        List<String> lines = new ArrayList<>();
        String[] words = message.split(" ");
        StringBuilder lineBuilder = new StringBuilder();
        for (String word : words) {
            String line = lineBuilder.toString().trim();
            if (fontRenderer.getStringWidth(line) + fontRenderer.getStringWidth(" " + word) >= width) {
                lines.add(line);
                lineBuilder = new StringBuilder();
            }
            lineBuilder.append(" ").append(word);
        }
        if (lineBuilder.length() > 0) {
            lines.add(lineBuilder.toString().trim());
        }
        int lineHeight = fontRenderer.FONT_HEIGHT + 2;
        int height = lines.size() * lineHeight + 29;
        WindowElement<T> window = new WindowElement<>(gui, title, width, height);
        int y = 16;
        for (String line : lines) {
            new LabelElement<>(gui, line, 2, y).withParent(window);
            y += lineHeight;
        }
        new ButtonElement<>(gui, "Okay", 2, height - 14, width - 4, 12, button -> {
            gui.removeElement(window);
            return true;
        }).withParent(window).withColorScheme(ColorSchemes.WINDOW);
        gui.addElement(window);
        return window;
    }

    public <T extends IElementGUI> WindowElement<T> selection(T gui, String title, Collection<String> selection, Consumer<String> callback) {
        WindowElement<T> window = new WindowElement<>(gui, title, 150, 150);
        List<String> textures = new ArrayList<>();
        textures.add("none");
        textures.addAll(selection);
        window.addElement(new ListElement<>(gui, 2, 16, 146, 132, textures, (list) -> {
            callback.accept(list.getSelectedIndex() == 0 ? null : list.getSelectedEntry());
            gui.removeElement(window);
            return true;
        }));
        gui.addElement(window);
        return window;
    }
}

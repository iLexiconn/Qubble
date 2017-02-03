package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.IElementGUI;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.ilexiconn.qubble.client.gui.element.color.ColorSchemes;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public enum GUIHelper {
    INSTANCE;

    public <T extends IElementGUI> WindowElement<T> error(T gui, int width, String title, Throwable throwable) {
        return this.info(gui, width, title, throwable.getLocalizedMessage());
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
}

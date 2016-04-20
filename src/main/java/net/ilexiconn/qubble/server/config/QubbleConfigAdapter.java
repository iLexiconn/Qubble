package net.ilexiconn.qubble.server.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.ilexiconn.qubble.server.color.ColorMode;

import java.io.IOException;
import java.util.Locale;

public class QubbleConfigAdapter extends TypeAdapter<QubbleConfig> {
    @Override
    public void write(JsonWriter out, QubbleConfig value) throws IOException {
        out.beginObject();
        out.name("mode").value(value.getColorMode().getName());
        out.name("accent").value(value.getAccentColor());
        out.endObject();
    }

    @Override
    public QubbleConfig read(JsonReader in) throws IOException {
        QubbleConfig config = new QubbleConfig();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "mode": {
                    try {
                        String mode = in.nextString().toUpperCase(Locale.ENGLISH);
                        config.setColorMode((ColorMode) ColorMode.class.getField(mode).get(null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "accent": {
                    int accent = in.nextInt();
                    config.setAccentColor(accent);
                    break;
                }
            }
        }
        in.endObject();
        return config;
    }
}

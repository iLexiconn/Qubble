package net.ilexiconn.qubble.server.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ilexiconn.qubble.server.config.QubbleConfig;
import net.ilexiconn.qubble.server.config.QubbleConfigAdapter;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class JSONUtil {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(QubbleConfig.class, new QubbleConfigAdapter()).create();

    public static <T> T loadConfig(File f, Class<T> t) {
        if (!f.exists()) {
            try {
                return t.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                return JSONUtil.GSON.fromJson(new FileReader(f), t);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static <T> T saveConfig(T t, File f) {
        String json = JSONUtil.GSON.toJson(t);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            IOUtils.write(json.getBytes(), new FileOutputStream(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }
}


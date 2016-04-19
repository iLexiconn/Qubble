package net.ilexiconn.qubble.server.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class JSONUtil {
    public static final Gson GSON = new Gson();
    public static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

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
        String json = JSONUtil.GSON_PRETTY.toJson(t);
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


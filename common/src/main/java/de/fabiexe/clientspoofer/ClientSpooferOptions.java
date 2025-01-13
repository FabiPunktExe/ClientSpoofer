package de.fabiexe.clientspoofer;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientSpooferOptions {
    public static ClientSpooferOptions INSTANCE;
    public SpoofMode spoofMode = SpoofMode.VANILLA;
    public String customClient = "vanilla";

    public static void load(Path path) {
        if (Files.exists(path)) {
            try {
                INSTANCE = new Gson().fromJson(Files.readString(path), ClientSpooferOptions.class);
            } catch(IOException e){
                e.printStackTrace(System.err);
                INSTANCE = new ClientSpooferOptions();
            }
        } else {
            INSTANCE = new ClientSpooferOptions();
            save(path);
        }
    }

    public static void save(Path path) {
        try {
            Files.writeString(path, new GsonBuilder().setFormattingStyle(FormattingStyle.PRETTY).create().toJson(INSTANCE));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}

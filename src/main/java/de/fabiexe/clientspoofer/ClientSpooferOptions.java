package de.fabiexe.clientspoofer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientSpooferOptions {
    public static SpoofMode SPOOF_MODE = SpoofMode.VANILLA;
    public static String CUSTOM_CLIENT = "vanilla";
    public static boolean HIDE_MODS = true;

    public static void load(Path path) {
        if (!Files.exists(path)) {
            save(path);
            return;
        }

        try {
            JsonObject json = JsonParser.parseString(Files.readString(path)).getAsJsonObject();

            if (json.has("spoof-mode")) {
                String mode = json.get("spoof-mode").getAsString();
                if (mode.equalsIgnoreCase("vanilla")) {
                    SPOOF_MODE = SpoofMode.VANILLA;
                } else if (mode.equalsIgnoreCase("custom")) {
                    SPOOF_MODE = SpoofMode.CUSTOM;
                } else if (mode.equalsIgnoreCase("off")) {
                    SPOOF_MODE = SpoofMode.OFF;
                }
            } else {
                save(path);
            }

            if (json.has("custom-client")) {
                CUSTOM_CLIENT = json.get("custom-client").getAsString();
            } else {
                save(path);
            }

            if (json.has("hide-mods")) {
                HIDE_MODS = json.get("hide-mods").getAsBoolean();
            } else {
                save(path);
            }
        } catch(IOException | JsonParseException e) {
            ClientSpoofer.LOGGER.error("Failed to load ClientSpoofer options", e);
        }
    }

    public static void save(Path path) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("spoof-mode", SPOOF_MODE.name().toLowerCase());
            json.addProperty("custom-client", CUSTOM_CLIENT);
            json.addProperty("hide-mods", HIDE_MODS);
            Files.writeString(path, json.toString());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static boolean hideMods() {
        return SPOOF_MODE == SpoofMode.VANILLA || (SPOOF_MODE != SpoofMode.OFF && HIDE_MODS);
    }
}

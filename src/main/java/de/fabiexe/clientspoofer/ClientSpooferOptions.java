package de.fabiexe.clientspoofer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ClientSpooferOptions {
    public static SpoofMode SPOOF_MODE = SpoofMode.VANILLA;
    public static String CUSTOM_CLIENT = "fabric";
    public static boolean HIDE_MODS = true;
    public static boolean DISABLE_CUSTOM_PAYLOADS = true;
    public static Set<String> ALLOWED_MODS = new HashSet<>();
    public static Set<String> ALLOWED_CUSTOM_PAYLOAD_CHANNELS = new HashSet<>();

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
                } else if (mode.equalsIgnoreCase("fabric")) {
                    SPOOF_MODE = SpoofMode.MODDED;
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

            if (json.has("disable-custom-payloads")) {
                DISABLE_CUSTOM_PAYLOADS = json.get("disable-custom-payloads").getAsBoolean();
            } else {
                save(path);
            }

            if (json.has("allowed-mods")) {
                ALLOWED_MODS.clear();
                for (var element : json.getAsJsonArray("allowed-mods")) {
                    ALLOWED_MODS.add(element.getAsString());
                }
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
            json.addProperty("disable-custom-payloads", DISABLE_CUSTOM_PAYLOADS);
            JsonArray allowedModsArray = new JsonArray();
            ALLOWED_MODS.forEach(allowedModsArray::add);
            json.add("allowed-mods", allowedModsArray);
            JsonArray allowedCustomPayloadChannelsArray = new JsonArray();
            ALLOWED_CUSTOM_PAYLOAD_CHANNELS.forEach(allowedCustomPayloadChannelsArray::add);
            json.add("allowed-custom-payload-channels", allowedCustomPayloadChannelsArray);
            Files.writeString(path, json.toString());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static boolean hideMods() {
        return switch (SPOOF_MODE) {
            case SpoofMode.VANILLA, SpoofMode.MODDED -> true;
            case SpoofMode.CUSTOM -> HIDE_MODS;
            case SpoofMode.OFF -> false;
        };
    }
}

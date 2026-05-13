package de.fabiexe.clientspoofer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class ClientSpooferOptions {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private SpoofMode spoofMode;
    private String customClient;
    private Boolean preventFingerprinting;
    private Boolean hideMods;
    private Set<String> allowedMods;
    private Boolean disableCustomPayloads;
    private Set<String> allowedCustomPayloadChannels;

    public @NotNull SpoofMode getSpoofMode() {
        return spoofMode != null ? spoofMode : SpoofMode.VANILLA;
    }

    public void setSpoofMode(@NotNull SpoofMode spoofMode) {
        this.spoofMode = spoofMode;
    }

    public @NotNull String getCustomClient() {
        return customClient != null ? customClient : "vanilla";
    }

    public void setCustomClient(@NotNull String customClient) {
        this.customClient = customClient;
    }

    public boolean isPreventFingerprinting() {
        return preventFingerprinting != null ? preventFingerprinting : false;
    }

    public void setPreventFingerprinting(boolean preventFingerprinting) {
        this.preventFingerprinting = preventFingerprinting;
    }

    public boolean isHideMods() {
        return hideMods != null ? hideMods : false;
    }

    public void setHideMods(boolean hideMods) {
        this.hideMods = hideMods;
    }

    public @NotNull Set<String> getAllowedMods() {
        return allowedMods != null ? allowedMods : Set.of();
    }

    public void setAllowedMods(@NotNull Set<String> allowedMods) {
        this.allowedMods = allowedMods;
    }

    public boolean isDisableCustomPayloads() {
        return disableCustomPayloads != null ? disableCustomPayloads : true;
    }

    public void setDisableCustomPayloads(boolean disableCustomPayloads) {
        this.disableCustomPayloads = disableCustomPayloads;
    }

    public @NotNull Set<String> getAllowedCustomPayloadChannels() {
        return allowedCustomPayloadChannels != null ? allowedCustomPayloadChannels : Set.of();
    }

    public void setAllowedCustomPayloadChannels(@NotNull Set<String> allowedCustomPayloadChannels) {
        this.allowedCustomPayloadChannels = allowedCustomPayloadChannels;
    }

    public static @NotNull ClientSpooferOptions load(@NotNull Path path) {
        try {
            if (Files.exists(path)) {
                return gson.fromJson(Files.readString(path), ClientSpooferOptions.class);
            }
        } catch (IOException | JsonSyntaxException e) {
            ClientSpoofer.LOGGER.error("Failed to load Client Spoofer options, using defaults", e);
        }
        return new ClientSpooferOptions();
    }

    public static void save(@NotNull Path path, @NotNull ClientSpooferOptions options) {
        try {
            Files.writeString(path, gson.toJson(options));
        } catch (IOException e) {
            ClientSpoofer.LOGGER.error("Failed to save Client Spoofer options", e);
        }
    }
}

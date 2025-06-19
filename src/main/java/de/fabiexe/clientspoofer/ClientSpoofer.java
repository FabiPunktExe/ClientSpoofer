package de.fabiexe.clientspoofer;

import java.nio.file.Path;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class ClientSpoofer implements ClientModInitializer {
    private static Path configFile;

    @Override
    public void onInitializeClient() {
        configFile = Minecraft.getInstance().gameDirectory.toPath().resolve("config/clientspoofer.json");
        ClientSpooferOptions.load(configFile);
    }

    public static @NotNull Path getConfigFile() {
        return configFile;
    }
}

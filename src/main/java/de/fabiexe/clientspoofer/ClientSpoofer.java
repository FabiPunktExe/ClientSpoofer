package de.fabiexe.clientspoofer;

import java.nio.file.Path;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSpoofer implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("clientspoofer");
    public static Path CONFIG_FILE;

    @Override
    public void onInitializeClient() {
        CONFIG_FILE = Minecraft.getInstance().gameDirectory.toPath().resolve("config/clientspoofer.json");
        ClientSpooferOptions.load(CONFIG_FILE);
    }
}

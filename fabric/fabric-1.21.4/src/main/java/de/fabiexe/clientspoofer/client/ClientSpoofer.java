package de.fabiexe.clientspoofer.client;

import de.fabiexe.clientspoofer.ClientSpooferOptions;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public class ClientSpoofer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSpooferOptions.load(Minecraft.getInstance().gameDirectory.toPath().resolve("config/clientspoofer.json"));
    }
}

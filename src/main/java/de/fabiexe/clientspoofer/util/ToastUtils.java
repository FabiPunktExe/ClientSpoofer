package de.fabiexe.clientspoofer.util;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;

public class ToastUtils {
    private static final Set<String> serversAttemptedReadingMods = new HashSet<>();

    public static void showServerAttemptedReadingModsToast() {
        ServerData serverData = Minecraft.getInstance().getCurrentServer();
        if (serverData != null) {
            if (serversAttemptedReadingMods.contains(serverData.ip)) {
                return;
            } else {
                serversAttemptedReadingMods.add(serverData.ip);
            }
        }

        SystemToast.SystemToastId id = new SystemToast.SystemToastId(10000L);
        Component title = Component.literal("Client Spoofer");
        Component message = Component.translatable("clientspoofer.toast.server_attempted_reading_mods");
        Minecraft.getInstance().getToastManager().addToast(SystemToast.multiline(Minecraft.getInstance(), id, title, message));
    }
}

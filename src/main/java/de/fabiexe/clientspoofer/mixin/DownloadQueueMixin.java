package de.fabiexe.clientspoofer.mixin;

import de.fabiexe.clientspoofer.ClientSpooferOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.DownloadQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.file.Path;
import java.util.UUID;

@Mixin(DownloadQueue.class)
public class DownloadQueueMixin {
    @Redirect(method = "lambda$runDownload$0", at = @At(value = "INVOKE", target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;"))
    public Path resolve(Path instance, String other) {
        if (ClientSpooferOptions.preventFingerprinting()) {
            UUID uuid = Minecraft.getInstance().getUser().getProfileId();
            return instance.resolve(uuid.toString()).resolve(other);
        } else {
            return instance.resolve(other);
        }
    }
}

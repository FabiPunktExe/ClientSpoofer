package de.fabiexe.clientspoofer.mixin.fingerprinting;

import de.fabiexe.clientspoofer.ClientSpooferOptions;
import de.fabiexe.clientspoofer.SpoofMode;
import net.minecraft.client.multiplayer.KnownPacksManager;
import net.minecraft.server.packs.repository.KnownPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(KnownPacksManager.class)
public class KnownPacksManagerMixin {
    @Redirect(
            method = "trySelectingPacks",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private <V> V redirectSelectPacks(Map<KnownPack, V> instance, Object object) {
        KnownPack pack = (KnownPack) object;
        if (!pack.namespace().equalsIgnoreCase("fabric") ||
                ClientSpooferOptions.SPOOF_MODE == SpoofMode.OFF) {
            return instance.get(pack);
        }
        if (ClientSpooferOptions.SPOOF_MODE == SpoofMode.MODDED ||
                ClientSpooferOptions.SPOOF_MODE == SpoofMode.CUSTOM) {
            for (String mod : ClientSpooferOptions.ALLOWED_MODS) {
                if (pack.id().toLowerCase().startsWith(mod.toLowerCase())) {
                    return instance.get(pack);
                }
            }
        }
        return null; // Spoof mode is vanilla or mod is not allowed in modded or custom mode
    }
}

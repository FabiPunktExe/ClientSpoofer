package de.fabiexe.clientspoofer.mixin.client;

import de.fabiexe.clientspoofer.ClientSpooferOptions;
import de.fabiexe.clientspoofer.SpoofMode;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBrandRetriever.class)
public class ClientBrandRetrieverMixin {
    @Shadow @Final public static String VANILLA_NAME;

    @Inject(method = "getClientModName", at = @At("HEAD"), remap = false, cancellable = true)
    private static void getClientModName(CallbackInfoReturnable<String> cir) {
        if (ClientSpooferOptions.SPOOF_MODE == SpoofMode.VANILLA) {
            cir.setReturnValue(VANILLA_NAME);
        } else if (ClientSpooferOptions.SPOOF_MODE == SpoofMode.CUSTOM) {
            cir.setReturnValue(ClientSpooferOptions.CUSTOM_CLIENT);
        }
    }
}

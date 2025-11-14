package de.fabiexe.clientspoofer.mixin.client;

import de.fabiexe.clientspoofer.ClientSpooferOptions;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBrandRetriever.class)
public class ClientBrandRetrieverMixin {
    @Inject(method = "getClientModName", at = @At("HEAD"), remap = false, cancellable = true)
    private static void getClientModName(CallbackInfoReturnable<String> cir) {
        switch (ClientSpooferOptions.SPOOF_MODE) {
            case VANILLA -> cir.setReturnValue(ClientBrandRetriever.VANILLA_NAME);
            case MODDED -> cir.setReturnValue("fabric");
            case CUSTOM -> cir.setReturnValue(ClientSpooferOptions.CUSTOM_CLIENT);
        }
    }
}

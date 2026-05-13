package de.fabiexe.clientspoofer.mixin.client;

import de.fabiexe.clientspoofer.ClientSpooferOptions;
import de.fabiexe.clientspoofer.SpoofMode;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        if (packet instanceof ServerboundCustomPayloadPacket(CustomPacketPayload payload)) {
            if (!(payload instanceof DiscardedPayload) && !(payload instanceof BrandPayload)) {
                if (ClientSpooferOptions.SPOOF_MODE == SpoofMode.OFF) {
                    return;
                } else if (ClientSpooferOptions.SPOOF_MODE == SpoofMode.MODDED) {
                    for (String mod : ClientSpooferOptions.ALLOWED_MODS) {
                        if (payload.type().id().toString().toLowerCase().startsWith(mod.toLowerCase())) {
                            return;
                        }
                    }
                } else if (ClientSpooferOptions.SPOOF_MODE == SpoofMode.CUSTOM &&
                        ClientSpooferOptions.DISABLE_CUSTOM_PAYLOADS) {
                    for (String channel : ClientSpooferOptions.ALLOWED_CUSTOM_PAYLOAD_CHANNELS) {
                        if (payload.type().id().toString().toLowerCase().startsWith(channel.toLowerCase())) {
                            return;
                        }
                    }
                }
                ci.cancel();
            }
        }
    }
}

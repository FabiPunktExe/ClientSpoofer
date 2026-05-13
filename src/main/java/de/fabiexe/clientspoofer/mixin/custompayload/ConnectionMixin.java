package de.fabiexe.clientspoofer.mixin.custompayload;

import de.fabiexe.clientspoofer.ClientSpoofer;
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
                switch (ClientSpoofer.getOptions().getSpoofMode()) {
                    case VANILLA -> {}
                    case MODDED -> {
                        for (String mod : ClientSpoofer.getOptions().getAllowedMods()) {
                            if (payload.type().id().toString().toLowerCase().startsWith(mod.toLowerCase())) {
                                return;
                            }
                        }
                    }
                    case CUSTOM -> {
                        if (ClientSpoofer.getOptions().isDisableCustomPayloads()) {
                            for (String channel : ClientSpoofer.getOptions().getAllowedCustomPayloadChannels()) {
                                if (payload.type().id().toString().toLowerCase().startsWith(channel.toLowerCase())) {
                                    return;
                                }
                            }
                        }
                    }
                    case OFF -> {
                        return;
                    }
                }
                ci.cancel();
            }
        }
    }
}

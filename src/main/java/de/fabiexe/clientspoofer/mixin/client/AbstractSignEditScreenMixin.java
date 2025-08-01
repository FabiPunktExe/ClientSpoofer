package de.fabiexe.clientspoofer.mixin.client;

import de.fabiexe.clientspoofer.ClientSpooferOptions;
import de.fabiexe.clientspoofer.util.ComponentUtils;
import de.fabiexe.clientspoofer.util.ToastUtils;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin {
    @Redirect(
            method = "<init>(Lnet/minecraft/world/level/block/entity/SignBlockEntity;ZZLnet/minecraft/network/chat/Component;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;map(Ljava/util/function/Function;)Ljava/util/stream/Stream;"))
    public Stream<String> init(Stream<Component> instance, Function<Component, String> function) {
        return instance.map(message -> {
            if (ClientSpooferOptions.hideMods()) {
                String str = ComponentUtils.getString(message);
                if (!str.equals(message.getString())) {
                    ToastUtils.showServerAttemptedReadingModsToast();
                }
                return str;
            } else {
                return message.getString();
            }
        });
    }
}

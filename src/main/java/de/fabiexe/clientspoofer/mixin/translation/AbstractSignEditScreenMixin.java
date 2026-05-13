package de.fabiexe.clientspoofer.mixin.translation;

import de.fabiexe.clientspoofer.ClientSpoofer;
import de.fabiexe.clientspoofer.util.ComponentUtils;
import de.fabiexe.clientspoofer.util.ToastUtils;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin {
    @Redirect(
            method = "<init>(Lnet/minecraft/world/level/block/entity/SignBlockEntity;ZZLnet/minecraft/network/chat/Component;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;map(Ljava/util/function/Function;)Ljava/util/stream/Stream;"))
    public Stream<String> init(Stream<Component> instance, Function<Component, String> function) {
        return instance.map(message -> {
            if (ClientSpoofer.getOptions().isHideMods()) {
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

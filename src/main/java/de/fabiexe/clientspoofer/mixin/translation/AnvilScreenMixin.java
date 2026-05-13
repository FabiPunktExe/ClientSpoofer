package de.fabiexe.clientspoofer.mixin.translation;

import de.fabiexe.clientspoofer.ClientSpoofer;
import de.fabiexe.clientspoofer.util.ComponentUtils;
import de.fabiexe.clientspoofer.util.ToastUtils;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    @Redirect(
            method = "slotChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;getString()Ljava/lang/String;"))
    public String slotChanged$getString(Component instance) {
        if (ClientSpoofer.getOptions().isHideMods()) {
            String str = ComponentUtils.getString(instance);
            if (!str.equals(instance.getString())) {
                ToastUtils.showServerAttemptedReadingModsToast();
            }
            return str;
        } else {
            return instance.getString();
        }
    }

    @Redirect(
            method = "onNameChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;getString()Ljava/lang/String;"))
    public String onNameChanged$getString(Component instance) {
        if (ClientSpoofer.getOptions().isHideMods()) {
            String str = ComponentUtils.getString(instance);
            if (!str.equals(instance.getString())) {
                ToastUtils.showServerAttemptedReadingModsToast();
            }
            return str;
        } else {
            return instance.getString();
        }
    }
}

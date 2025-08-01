package de.fabiexe.clientspoofer.mixin;

import de.fabiexe.clientspoofer.ClientSpooferOptions;
import de.fabiexe.clientspoofer.util.ComponentUtils;
import de.fabiexe.clientspoofer.util.ToastUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;getString()Ljava/lang/String;"))
    public String getString(Component instance) {
        if (ClientSpooferOptions.hideMods()) {
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

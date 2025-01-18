package de.fabiexe.clientspoofer.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.fabiexe.clientspoofer.gui.ClientSpooferOptionsScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClientSpooferOptionsScreen::new;
    }
}

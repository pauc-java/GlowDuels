package com.glowstudio.glowduels;

import com.glowstudio.glowduels.command.DuelCommand;
import com.glowstudio.glowduels.listener.DuelListener;
import com.glowstudio.glowduels.manager.DuelManager;
import com.glowstudio.glowduels.manager.ProfileManager;
import com.glowstudio.glowduels.manager.SpectateManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GlowDuelsPlugin extends JavaPlugin {

    private static GlowDuelsPlugin instance;
    private DuelManager duelManager;
    private ProfileManager profileManager;
    private SpectateManager spectateManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.profileManager = new ProfileManager();
        this.spectateManager = new SpectateManager();
        this.duelManager = new DuelManager(this);

        getCommand("duels").setExecutor(new DuelCommand(this));
        Bukkit.getPluginManager().registerEvents(new DuelListener(this), this);
    }

    @Override
    public void onDisable() {
        if (duelManager != null) {
            duelManager.shutdown();
        }
        instance = null;
    }

    public static GlowDuelsPlugin getInstance() {
        return instance;
    }

    public DuelManager getDuelManager() {
        return duelManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public SpectateManager getSpectateManager() {
        return spectateManager;
    }
}

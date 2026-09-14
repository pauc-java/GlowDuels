package com.glowstudio.glowduels;

import com.glowstudio.glowduels.command.DuelCommand;
import com.glowstudio.glowduels.configuration.CommandsConfig;
import com.glowstudio.glowduels.listener.DuelListener;
import com.glowstudio.glowduels.manager.BetManager;
import com.glowstudio.glowduels.manager.DuelManager;
import com.glowstudio.glowduels.manager.ProfileManager;
import com.glowstudio.glowduels.manager.SpectateManager;
import com.glowstudio.glowduels.arena.ArenaManager;
import com.glowstudio.glowduels.arena.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GlowDuelsPlugin extends JavaPlugin {

    private static GlowDuelsPlugin instance;
    private DuelManager duelManager;
    private ProfileManager profileManager;
    private SpectateManager spectateManager;
    private ArenaManager arenaManager;
    private MapManager mapManager;
    private BetManager betManager;
    private CommandsConfig commandsConfig;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.commandsConfig = new CommandsConfig(this);
        this.profileManager = new ProfileManager();
        this.spectateManager = new SpectateManager();
        this.mapManager = new MapManager(this);
        this.arenaManager = new ArenaManager(this);
        this.duelManager = new DuelManager(this);
        this.betManager = new BetManager(this);

        DuelCommand duelCommand = new DuelCommand(this);
        if (getCommand("duels") != null) {
            getCommand("duels").setExecutor(duelCommand);
        }
        if (getCommand("stavka") != null) {
            getCommand("stavka").setExecutor(duelCommand);
        }

        Bukkit.getPluginManager().registerEvents(new DuelListener(this), this);
        getLogger().info("GlowDuels v1.1 successfully loaded.");
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

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public BetManager getBetManager() {
        return betManager;
    }

    public CommandsConfig getCommandsConfig() {
        return commandsConfig;
    }
}

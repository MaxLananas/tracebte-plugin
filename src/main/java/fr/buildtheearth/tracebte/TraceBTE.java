package fr.buildtheearth.tracebte;

import fr.buildtheearth.tracebte.command.TutoCommand;
import fr.buildtheearth.tracebte.listener.BlockListener;
import fr.buildtheearth.tracebte.listener.PlayerMoveListener;
import fr.buildtheearth.tracebte.listener.TpllListener;
import fr.buildtheearth.tracebte.tutorial.TutorialManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TraceBTE extends JavaPlugin {

    private TutorialManager tutorialManager;

    @Override
    public void onEnable() {
        tutorialManager = new TutorialManager(this);

        getCommand("tuto").setExecutor(new TutoCommand(tutorialManager));

        getServer().getPluginManager().registerEvents(new BlockListener(tutorialManager, this), this);
        getServer().getPluginManager().registerEvents(new TpllListener(tutorialManager, this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(tutorialManager), this);

        getLogger().info("TraceBTE activé.");
    }

    @Override
    public void onDisable() {
        tutorialManager.cleanup();
    }
}
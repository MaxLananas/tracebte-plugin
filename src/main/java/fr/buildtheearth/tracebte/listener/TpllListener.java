package fr.buildtheearth.tracebte.listener;

import fr.buildtheearth.tracebte.tutorial.TutorialManager;
import fr.buildtheearth.tracebte.tutorial.TutorialSession;
import fr.buildtheearth.tracebte.tutorial.TutorialStep;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TpllListener implements Listener {

    private final TutorialManager manager;
    private final Plugin plugin;
    private final Map<UUID, Boolean> pendingTpll = new HashMap<>();

    public TpllListener(TutorialManager manager, Plugin plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        TutorialSession session = manager.getSession(player);
        if (session == null || session.getStep() != TutorialStep.PLACING_CORNERS) return;

        String raw = event.getMessage().strip().toLowerCase();
        if (!raw.startsWith("/tpll")) return;

        pendingTpll.put(player.getUniqueId(), true);
        plugin.getLogger().info("[TraceBTE] /tpll détecté pour " + player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!pendingTpll.remove(id, true)) return;

        TutorialSession session = manager.getSession(player);
        if (session == null || session.getStep() != TutorialStep.PLACING_CORNERS) return;

        Location destination = event.getTo();
        plugin.getLogger().info("[TraceBTE] Téléportation tpll détectée pour "
            + player.getName() + " → " + destination.getX() + " / " + destination.getZ());

        Bukkit.getScheduler().runTaskLater(plugin, () ->
            manager.onTpll(player, destination), 2L);
    }

    public void cleanup() {
        pendingTpll.clear();
    }
}
package fr.buildtheearth.tracebte.listener;

import fr.buildtheearth.tracebte.tutorial.TutorialManager;
import fr.buildtheearth.tracebte.tutorial.TutorialSession;
import fr.buildtheearth.tracebte.tutorial.TutorialStep;
import fr.buildtheearth.tracebte.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

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

        if (event.getMessage().strip().toLowerCase().startsWith("/tpll")) {
            pendingTpll.put(player.getUniqueId(), true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!pendingTpll.remove(id, true)) return;

        TutorialSession session = manager.getSession(player);
        if (session == null || session.getStep() != TutorialStep.PLACING_CORNERS) return;

        Location destination = event.getTo();
        manager.onTpll(player, destination);

        scheduleBlockScan(player, destination);
    }

    private void scheduleBlockScan(Player player, Location arrival) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 10;

                TutorialSession session = manager.getSession(player);
                if (session == null || session.getStep() != TutorialStep.PLACING_CORNERS) {
                    cancel();
                    return;
                }

                if (scanForRedWool(player, arrival)) {
                    cancel();
                    return;
                }

                if (ticks >= 600) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    private boolean scanForRedWool(Player player, Location arrival) {
        TutorialSession session = manager.getSession(player);
        if (session == null) return true;

        int cx = arrival.getBlockX();
        int cy = arrival.getBlockY();
        int cz = arrival.getBlockZ();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    Block block = arrival.getWorld().getBlockAt(cx + dx, cy + dy, cz + dz);
                    if (block.getType() == Material.RED_WOOL) {
                        Location blockLoc = block.getLocation();
                        boolean placed = manager.onBlockDetected(player, blockLoc);
                        if (placed) return true;
                    }
                }
            }
        }
        return false;
    }

    public void cleanup() {
        pendingTpll.clear();
    }
}

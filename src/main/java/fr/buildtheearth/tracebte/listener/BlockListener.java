package fr.buildtheearth.tracebte.listener;

import fr.buildtheearth.tracebte.TraceBTE;
import fr.buildtheearth.tracebte.tutorial.TutorialManager;
import fr.buildtheearth.tracebte.tutorial.TutorialSession;
import fr.buildtheearth.tracebte.tutorial.TutorialStep;
import fr.buildtheearth.tracebte.util.Msg;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class BlockListener implements Listener {

    private final TutorialManager manager;
    private final TraceBTE plugin;
    private final Set<UUID> stackCheckCooldown = new HashSet<>();

    public BlockListener(TutorialManager manager, TraceBTE plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        TutorialSession session = manager.getSession(player);
        if (session == null) return;

        TutorialStep step = session.getStep();

        if (step == TutorialStep.PLACING_CORNERS) {
            if (event.getBlock().getType() != Material.RED_WOOL) {
                Msg.warn(player, "Pose de la <color:#FF6B6B>laine rouge</color> sur ce coin.");
                return;
            }
            session.getPlacedBlocks().add(event.getBlock().getLocation());
            manager.onBlockDetected(player, event.getBlock().getLocation());
            return;
        }

        if (step == TutorialStep.WORLDEDIT_STACK) {
            scheduleStackCheck(player);
        }
    }

    private void scheduleStackCheck(Player player) {
        UUID id = player.getUniqueId();
        if (stackCheckCooldown.contains(id)) return;
        stackCheckCooldown.add(id);

        new BukkitRunnable() {
            @Override
            public void run() {
                stackCheckCooldown.remove(id);
                TutorialSession session = manager.getSession(player);
                if (session == null || session.getStep() != TutorialStep.WORLDEDIT_STACK) return;

                boolean stacked = fr.buildtheearth.tracebte.util.Geometry.checkStackedUp(
                    player.getWorld(),
                    TutorialManager.WE_SELECTION,
                    3,
                    Material.RED_WOOL
                );

                if (stacked) manager.onStackDetected(player);
            }
        }.runTaskLater(plugin, 20L);
    }
}

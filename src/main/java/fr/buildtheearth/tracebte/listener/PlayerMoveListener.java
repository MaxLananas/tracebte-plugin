package fr.buildtheearth.tracebte.listener;

import fr.buildtheearth.tracebte.tutorial.TutorialManager;
import fr.buildtheearth.tracebte.tutorial.TutorialSession;
import fr.buildtheearth.tracebte.tutorial.TutorialStep;
import fr.buildtheearth.tracebte.util.Msg;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class PlayerMoveListener implements Listener {

    private static final double MAX_DISTANCE = 50.0;

    private final TutorialManager manager;

    public PlayerMoveListener(TutorialManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        manager.removeSession(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        TutorialSession session = manager.getSession(player);
        if (session == null) return;

        TutorialStep step = session.getStep();
        if (step == TutorialStep.INTRO || step == TutorialStep.COMPLETED) return;

        Location destination = event.getTo();
        if (isTooFar(destination)) {
            if (isTpllCause(event.getCause())) {
                return;
            }
            event.setCancelled(true);
            Msg.warn(player, "Tu ne peux pas quitter la zone du tutoriel.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();
        TutorialSession session = manager.getSession(player);
        if (session == null) return;

        TutorialStep step = session.getStep();
        if (step == TutorialStep.INTRO || step == TutorialStep.COMPLETED) return;

        if (isTooFar(event.getTo())) {
            event.setCancelled(true);
            Msg.warn(player, "Tu t'éloignes trop de la zone - reste dans le périmètre du tutoriel.");
        }
    }

    private boolean isTooFar(Location loc) {
        double dx = loc.getX() - TutorialManager.ZONE_CENTER_X;
        double dz = loc.getZ() - TutorialManager.ZONE_CENTER_Z;
        return Math.sqrt(dx * dx + dz * dz) > MAX_DISTANCE;
    }

    private boolean isTpllCause(PlayerTeleportEvent.TeleportCause cause) {
        return cause == PlayerTeleportEvent.TeleportCause.COMMAND
            || cause == PlayerTeleportEvent.TeleportCause.PLUGIN;
    }
}
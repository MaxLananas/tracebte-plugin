package fr.buildtheearth.tracebte.tutorial;

import fr.buildtheearth.tracebte.TraceBTE;
import fr.buildtheearth.tracebte.particle.ParticleDisplay;
import fr.buildtheearth.tracebte.util.Geometry;
import fr.buildtheearth.tracebte.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TutorialManager {

    public static final double[][] CORNERS = {
        {2415338.48, 40.0, -4650899.57},
        {2415342.49, 40.0, -4650908.51},
        {2415355.54, 40.0, -4650902.53},
        {2415351.62, 40.0, -4650893.41}
    };

    public static final double[][] WE_SELECTION = {
        {2415338.60, 40.0, -4650908.49},
        {2415355.51, 40.0, -4650893.70}
    };

    public static final double[] SPAWN_POINT      = {2415351.0, 40.0, -4650911.0};
    public static final double   CORNER_TOLERANCE = 6.0;
    public static final double   TPLL_ZONE_RADIUS = 40.0;
    public static final double   ZONE_CENTER_X    = 2415347.0;
    public static final double   ZONE_CENTER_Z    = -4650901.0;

    private final TraceBTE plugin;
    private final Map<UUID, TutorialSession> sessions      = new HashMap<>();
    private final Map<UUID, BukkitTask>      particleTasks = new HashMap<>();

    public TutorialManager(TraceBTE plugin) {
        this.plugin = plugin;
    }

    public void startTutorial(Player player) {
        TutorialSession session = new TutorialSession(player.getUniqueId());
        sessions.put(player.getUniqueId(), session);

        teleportToStart(player);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Msg.blank(player);
            Msg.immersive(player, "Hm… il manque la maison. Tu vas devoir la construire toi-même.");
            Msg.blank(player);
        }, 40L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> sendMapsInstructions(player), 90L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!hasSession(player)) return;
            session.setStep(TutorialStep.PLACING_CORNERS);
            startCornerParticles(player);
            sendCornerInstructions(player);
        }, 160L);
    }

    public void stopTutorial(Player player) {
        if (!hasSession(player)) {
            Msg.warn(player, "Tu n'es pas en train de faire le tutoriel.");
            return;
        }
        removeSession(player);
        Msg.blank(player);
        Msg.info(player, "Tutoriel interrompu. Tape <color:#FFB347>/tuto</color> pour recommencer.");
        Msg.blank(player);
    }

    private void teleportToStart(Player player) {
        Location loc = new Location(
            player.getWorld(),
            SPAWN_POINT[0], SPAWN_POINT[1], SPAWN_POINT[2],
            -135f, 0f
        );
        player.teleport(loc);
    }

    private void sendMapsInstructions(Player player) {
        if (!hasSession(player)) return;
        Msg.header(player, "Comment trouver les coordonnées");
        Msg.info(player, "Sur BTE, on trace les bâtiments d'après leurs vraies coordonnées GPS.");
        Msg.blank(player);
        Msg.step(player, 1, 3, "Ouvre <color:#74C0FC>Google Maps</color> sur la zone :");
        Msg.gmapsLink(player,
            "Vue satellite — maison cible",
            "https://www.google.com/maps/@44.3619214,-1.0702146,49m/data=!3m1!1e3?entry=ttu&g_ep=EgoyMDI2MDUwNi4wIKXMDSoASAFQAw%3D%3D"
        );
        Msg.blank(player);
        Msg.step(player, 2, 3, "Fais un <color:#FFB347>clic droit</color> sur un coin du bâtiment.");
        Msg.tip(player, "Les coordonnées apparaissent en tête du menu contextuel.");
        Msg.blank(player);
        Msg.step(player, 3, 3, "Reviens ici et tape <color:#FFB347>/tpll <lat> <lon></color>.");
        Msg.tip(player, "Tu vas te téléporter exactement sur ce point dans le monde BTE.");
        Msg.blank(player);
    }

    private void sendCornerInstructions(Player player) {
        Msg.header(player, "Étape 1 · Placer les coins");
        Msg.info(player, "Des particules <color:#FF6B35>oranges</color> marquent les <color:#EEEEEE>4 coins</color> du bâtiment.");
        Msg.blank(player);
        Msg.step(player, 1, 2, "Utilise <color:#FFB347>/tpll</color> pour rejoindre chaque coin.");
        Msg.step(player, 2, 2, "Pose un bloc de <color:#FF6B6B>laine rouge</color> là où tu arrives.");
        Msg.blank(player);
        Msg.tip(player, "Tu dois utiliser /tpll au moins 4 fois — pas de raccourcis.");
        Msg.blank(player);
    }

    public void onTpll(Player player, Location destination) {
        TutorialSession session = sessions.get(player.getUniqueId());
        if (session == null || session.getStep() != TutorialStep.PLACING_CORNERS) return;

        session.incrementTpll();

        double dx = destination.getX() - ZONE_CENTER_X;
        double dz = destination.getZ() - ZONE_CENTER_Z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        if (dist > TPLL_ZONE_RADIUS) {
            Msg.blank(player);
            Msg.warn(player, "Tu ne t'es pas trompé d'endroit ?");
            Msg.tip(player, "Retourne sur Google Maps et vérifie que tu cliques bien sur la bonne maison.");
            Msg.blank(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!hasSession(player)) return;
                teleportToStart(player);
                Msg.info(player, "On repart du début.");
            }, 60L);
        }
    }

    public boolean onBlockDetected(Player player, Location location) {
        TutorialSession session = sessions.get(player.getUniqueId());
        if (session == null || session.getStep() != TutorialStep.PLACING_CORNERS) return false;

        int cornerIndex = Geometry.findNearestCorner(location, CORNERS, CORNER_TOLERANCE);
        if (cornerIndex == -1) return false;

        if (session.getValidatedCorners().contains(cornerIndex)) return true;

        session.getValidatedCorners().add(cornerIndex);

        Msg.blank(player);
        Msg.success(player, "Coin validé !");
        Msg.cornerProgress(player, session.getValidatedCorners().size(), 4);
        Msg.blank(player);

        if (session.allCornersPlaced()) {
            if (session.getTpllCount() < 4) {
                Msg.blank(player);
                Msg.error(player, "Tu n'as pas utilisé /tpll pour chaque coin.");
                Msg.tip(player, "C'est la base du traçage BTE — pas de triche possible sur le vrai serveur.");
                Msg.blank(player);
                resetCorners(player, session);
                return true;
            }
            advanceToLines(player, session);
        }

        return true;
    }

    private void advanceToLines(Player player, TutorialSession session) {
        stopParticles(player);
        session.setStep(TutorialStep.DRAWING_LINES);

        Msg.header(player, "Étape 2 · Relier les points");
        Msg.immersive(player, "Bravo pour ce tracé. Maintenant relie les points avec //line red afin de créer des lignes parfaitement droites.");
        Msg.blank(player);
        Msg.info(player, "Sélectionne deux coins consécutifs avec ta hache WorldEdit.");
        Msg.tip(player, "Clic gauche = premier point  ·  Clic droit = second point");
        Msg.info(player, "Puis tape <color:#FFB347>//line red</color> pour tracer le segment.");
        Msg.info(player, "Répète pour les <color:#EEEEEE>4 côtés</color> du rectangle.");
        Msg.blank(player);
    }

    public void onLinesValidated(Player player) {
        TutorialSession session = sessions.get(player.getUniqueId());
        if (session == null) return;

        session.setStep(TutorialStep.WORLDEDIT_STACK);
        startSelectionParticles(player);

        Msg.header(player, "Étape 3 · Sélection & Stack");
        Msg.info(player, "Les particules <color:#74C0FC>bleues</color> indiquent tes deux coins de sélection WorldEdit.");
        Msg.blank(player);
        Msg.step(player, 1, 3, "Tape <color:#FFB347>//wand</color> pour recevoir la hache de sélection.");
        Msg.step(player, 2, 3, "Clic gauche sur le coin <color:#74C0FC>bleu Nord-Ouest</color>, clic droit sur le <color:#74C0FC>Sud-Est</color>.");
        Msg.step(player, 3, 3, "Exécute <color:#FFB347>//stack 3 up</color> pour copier la structure 3 fois vers le haut.");
        Msg.blank(player);
        Msg.tip(player, "C'est comme ça qu'on monte les murs d'un bâtiment sur BTE.");
        Msg.blank(player);
    }

    public void onStackDetected(Player player) {
        TutorialSession session = sessions.get(player.getUniqueId());
        if (session == null) return;

        stopParticles(player);
        session.setStep(TutorialStep.COMPLETED);

        Msg.blank(player);
        Msg.header(player, "Tutoriel terminé");
        Msg.blank(player);
        Msg.immersive(player, "Tu connais maintenant les bases. Le reste, c'est de la pratique.");
        Msg.blank(player);
        Msg.success(player, "Tu peux rejoindre le serveur principal et commencer à construire.");
        Msg.tip(player, "Rejoins le Discord BTE France pour trouver une zone à construire.");
        Msg.blank(player);

        sessions.remove(player.getUniqueId());
    }

    private void resetCorners(Player player, TutorialSession session) {
        stopParticles(player);
        session.getValidatedCorners().clear();
        session.setStep(TutorialStep.PLACING_CORNERS);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!hasSession(player)) return;
            startCornerParticles(player);
            sendCornerInstructions(player);
        }, 40L);
    }

    private void startCornerParticles(Player player) {
        stopParticles(player);
        BukkitTask task = ParticleDisplay.spawnRectangleLoop(plugin, player, CORNERS);
        particleTasks.put(player.getUniqueId(), task);
    }

    private void startSelectionParticles(Player player) {
        stopParticles(player);
        BukkitTask task = ParticleDisplay.spawnSelectionPoints(plugin, player, WE_SELECTION);
        particleTasks.put(player.getUniqueId(), task);
    }

    private void stopParticles(Player player) {
        BukkitTask existing = particleTasks.remove(player.getUniqueId());
        if (existing != null) existing.cancel();
    }

    public TutorialSession getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }

    public boolean hasSession(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public void removeSession(Player player) {
        stopParticles(player);
        sessions.remove(player.getUniqueId());
    }

    public void cleanup() {
        particleTasks.values().forEach(BukkitTask::cancel);
        particleTasks.clear();
        sessions.clear();
    }
}

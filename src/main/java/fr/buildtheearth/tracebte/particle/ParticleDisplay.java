package fr.buildtheearth.tracebte.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class ParticleDisplay {

    private ParticleDisplay() {}

    public static BukkitTask spawnRectangleLoop(Plugin plugin, Player player, double[][] corners) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) return;
            World world = player.getWorld();

            for (int i = 0; i < corners.length; i++) {
                double[] a = corners[i];
                double[] b = corners[(i + 1) % corners.length];
                spawnLine(world, player, a, b, Particle.DUST,
                    new Particle.DustOptions(org.bukkit.Color.RED, 1.2f));
            }

            for (double[] corner : corners) {
                Location loc = new Location(world, corner[0], corner[1] + 0.5, corner[2]);
                player.spawnParticle(Particle.DUST, loc, 5, 0.1, 0.3, 0.1, 0,
                    new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 50, 50), 1.5f));
            }
        }, 0L, 10L);
    }

    public static BukkitTask spawnSelectionPoints(Plugin plugin, Player player, double[][] points) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) return;
            World world = player.getWorld();

            for (double[] point : points) {
                Location loc = new Location(world, point[0], point[1] + 0.5, point[2]);
                player.spawnParticle(Particle.DUST, loc, 8, 0.2, 0.4, 0.2, 0,
                    new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 200, 255), 1.8f));
            }
        }, 0L, 8L);
    }

    private static void spawnLine(World world, Player player, double[] from, double[] to,
                                   Particle particle, Particle.DustOptions options) {
        double dx = to[0] - from[0];
        double dy = to[1] - from[1];
        double dz = to[2] - from[2];
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int steps = (int) Math.ceil(length * 1.5);

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Location loc = new Location(world,
                from[0] + dx * t,
                from[1] + dy * t + 0.5,
                from[2] + dz * t
            );
            player.spawnParticle(particle, loc, 1, 0, 0, 0, 0, options);
        }
    }
}
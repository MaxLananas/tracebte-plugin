package fr.buildtheearth.tracebte.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class Geometry {

    private Geometry() {}

    public static int findNearestCorner(Location loc, double[][] corners, double tolerance) {
        for (int i = 0; i < corners.length; i++) {
            double dx = loc.getX() - corners[i][0];
            double dz = loc.getZ() - corners[i][2];
            if (Math.sqrt(dx * dx + dz * dz) <= tolerance) return i;
        }
        return -1;
    }

    public static boolean checkLineExists(World world, double[] from, double[] to, Material expected) {
        double dx = to[0] - from[0];
        double dy = to[1] - from[1];
        double dz = to[2] - from[2];
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int steps = (int) Math.ceil(length);
        int matched = 0;

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            int bx = (int) Math.floor(from[0] + dx * t);
            int by = (int) Math.floor(from[1] + dy * t);
            int bz = (int) Math.floor(from[2] + dz * t);
            if (world.getBlockAt(bx, by, bz).getType() == expected) matched++;
        }

        return matched >= steps * 0.6;
    }

    public static boolean checkAllLinesExist(World world, double[][] corners, Material expected) {
        for (int i = 0; i < corners.length; i++) {
            double[] a = corners[i];
            double[] b = corners[(i + 1) % corners.length];
            if (!checkLineExists(world, a, b, expected)) return false;
        }
        return true;
    }

    public static boolean checkStackedUp(World world, double[][] weSelection, int stackCount, Material expected) {
        double minX = Math.min(weSelection[0][0], weSelection[1][0]);
        double maxX = Math.max(weSelection[0][0], weSelection[1][0]);
        double minZ = Math.min(weSelection[0][2], weSelection[1][2]);
        double maxZ = Math.max(weSelection[0][2], weSelection[1][2]);
        int baseY = (int) weSelection[0][1];

        double[][] samplePoints = {
            {minX, minZ},
            {maxX, minZ},
            {minX, maxZ},
            {maxX, maxZ},
            {(minX + maxX) / 2, (minZ + maxZ) / 2}
        };

        for (int s = 1; s <= stackCount; s++) {
            int y = baseY + s;
            int hits = 0;

            for (double[] pt : samplePoints) {
                Block block = world.getBlockAt((int) pt[0], y, (int) pt[1]);
                if (block.getType() != Material.AIR) hits++;
            }

            if (hits < 2) return false;
        }

        return true;
    }
}

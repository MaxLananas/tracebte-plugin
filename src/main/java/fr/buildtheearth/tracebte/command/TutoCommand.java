package fr.buildtheearth.tracebte.command;

import fr.buildtheearth.tracebte.tutorial.TutorialManager;
import fr.buildtheearth.tracebte.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TutoCommand implements CommandExecutor, TabCompleter {

    private final TutorialManager manager;

    public TutoCommand(TutorialManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande est réservée aux joueurs.");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("stop")) {
            manager.stopTutorial(player);
            return true;
        }

        if (manager.hasSession(player)) {
            Msg.warn(player, "Tu es déjà en train de faire le tutoriel.");
            Msg.tip(player, "Tape <color:#FFB347>/tuto stop</color> pour l'interrompre.");
            return true;
        }

        manager.startTutorial(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("stop");
        }
        return List.of();
    }
}

package fr.buildtheearth.tracebte.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public final class Msg {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static final TextColor EMBER   = TextColor.color(0xFF6B35);
    private static final TextColor GOLD    = TextColor.color(0xFFB347);
    private static final TextColor CHALK   = TextColor.color(0xEEEEEE);
    private static final TextColor MUTED   = TextColor.color(0x9A9A9A);
    private static final TextColor MOSS    = TextColor.color(0x6BCB77);
    private static final TextColor SAND    = TextColor.color(0xF4D06F);
    private static final TextColor SKY     = TextColor.color(0x74C0FC);
    private static final TextColor CRIMSON = TextColor.color(0xFF4D6D);

    private static final Component GLYPH = Component.text("⬡ ", EMBER, TextDecoration.BOLD);

    private Msg() {}

    public static void send(Player p, Component c) {
        p.sendMessage(c);
    }

    public static void blank(Player p) {
        p.sendMessage(Component.empty());
    }

    public static void immersive(Player p, String raw) {
        p.sendMessage(
            Component.text("  ❝ ", MUTED)
                .append(Component.text(raw, SAND, TextDecoration.ITALIC))
                .append(Component.text(" ❞", MUTED))
        );
    }

    public static void info(Player p, String miniMsg) {
        p.sendMessage(GLYPH.append(MM.deserialize("<color:#EEEEEE>" + miniMsg)));
    }

    public static void success(Player p, String miniMsg) {
        p.sendMessage(
            Component.text("  ✔ ", MOSS, TextDecoration.BOLD)
                .append(MM.deserialize("<color:#6BCB77>" + miniMsg))
        );
    }

    public static void warn(Player p, String miniMsg) {
        p.sendMessage(
            Component.text("  ⚠ ", SAND, TextDecoration.BOLD)
                .append(MM.deserialize("<color:#F4D06F>" + miniMsg))
        );
    }

    public static void error(Player p, String miniMsg) {
        p.sendMessage(
            Component.text("  ✖ ", CRIMSON, TextDecoration.BOLD)
                .append(MM.deserialize("<color:#FF4D6D>" + miniMsg))
        );
    }

    public static void header(Player p, String title) {
        Component bar = Component.text("  ──────────────────────────────", MUTED);
        p.sendMessage(Component.empty());
        p.sendMessage(bar);
        p.sendMessage(
            Component.text("  ◆ ", EMBER, TextDecoration.BOLD)
                .append(Component.text(title.toUpperCase(), CHALK, TextDecoration.BOLD))
        );
        p.sendMessage(bar);
    }

    public static void step(Player p, int num, int total, String miniMsg) {
        p.sendMessage(
            Component.text("  [", MUTED)
                .append(Component.text(num + "/" + total, EMBER, TextDecoration.BOLD))
                .append(Component.text("]  ", MUTED))
                .append(MM.deserialize("<color:#EEEEEE>" + miniMsg))
        );
    }

    public static void tip(Player p, String miniMsg) {
        p.sendMessage(
            Component.text("  ↳ ", SKY)
                .append(MM.deserialize("<color:#74C0FC><i>" + miniMsg + "</i>"))
        );
    }

    public static void gmapsLink(Player p, String label, String url) {
        p.sendMessage(
            Component.text("  ⊕ ", SKY, TextDecoration.BOLD)
                .append(
                    Component.text(label, SKY, TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(url))
                        .hoverEvent(HoverEvent.showText(
                            Component.text("Cliquer pour ouvrir dans le navigateur", MUTED)
                        ))
                )
        );
    }

    public static void cornerProgress(Player p, int done, int total) {
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < total; i++) {
            bar.append(i < done ? "◆" : "◇");
        }
        p.sendMessage(
            Component.text("  Coins  ", MUTED)
                .append(Component.text(bar.toString(), done == total ? MOSS : EMBER))
                .append(Component.text("  " + done + "/" + total, MUTED))
        );
    }

    public static void lineProgress(Player p, int done, int total) {
      StringBuilder bar = new StringBuilder();
      for (int i = 0; i < total; i++) {
         bar.append(i < done ? "◆" : "◇");
      }
      p.sendMessage(
         Component.text("  Segments  ", MUTED)
             .append(Component.text(bar.toString(), done == total ? MOSS : EMBER))
             .append(Component.text("  " + done + "/" + total, MUTED))
      );
    }
}

package de.lystx.cloudsystem.library.service.console.color;

import lombok.Getter;
import org.fusesource.jansi.Ansi;

@Getter
public class ConsoleColor {


    private static String construct(Ansi.Color color, boolean b) {
        Ansi ansi = Ansi.ansi().a(Ansi.Attribute.RESET).fg(color);
        if (b) {
            ansi.bold();
        } else {
            ansi.boldOff();
        }
        return ansi.toString();
    }


    public static String formatColorString(String input) {

        input = Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + input;
        input = input.replace("§a", construct(Ansi.Color.GREEN, false));
        input = input.replace("§b", construct(Ansi.Color.CYAN, true));
        input = input.replace("§c", construct(Ansi.Color.RED, true));
        input = input.replace("§d", construct(Ansi.Color.MAGENTA, true));
        input = input.replace("§e", construct(Ansi.Color.YELLOW, true));
        input = input.replace("§f", construct(Ansi.Color.WHITE, false));


        input = input.replace("§A", construct(Ansi.Color.GREEN, false));
        input = input.replace("§B", construct(Ansi.Color.CYAN, true));
        input = input.replace("§C", construct(Ansi.Color.RED, true));
        input = input.replace("§D", construct(Ansi.Color.MAGENTA, true));
        input = input.replace("§E", construct(Ansi.Color.YELLOW, true));
        input = input.replace("§F", construct(Ansi.Color.WHITE, false));

        input = input.replace("§0", construct(Ansi.Color.BLACK, false));
        input = input.replace("§1", construct(Ansi.Color.BLUE, false));
        input = input.replace("§2", construct(Ansi.Color.GREEN, true));
        input = input.replace("§3", construct(Ansi.Color.CYAN, false));
        input = input.replace("§4", construct(Ansi.Color.RED, false));
        input = input.replace("§5", construct(Ansi.Color.MAGENTA, false));
        input = input.replace("§6", construct(Ansi.Color.YELLOW, false));
        input = input.replace("§7", construct(Ansi.Color.WHITE, true));
        input = input.replace("§8", construct(Ansi.Color.BLACK, false));
        input = input.replace("§h", construct(Ansi.Color.BLACK, true));
        input = input.replace("§9", construct(Ansi.Color.BLUE, true));
        input = input + Ansi.ansi().reset().toString();
        return input;
    }

    public static String stripColor(String input) {
        return input.replaceAll("\033\\[[;\\d]*m", "");
    }
}

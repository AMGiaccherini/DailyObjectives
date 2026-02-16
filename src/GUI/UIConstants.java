package GUI;

import java.awt.*;
import java.time.format.*;
import java.util.Locale;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class UIConstants {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public static final Color BACKGROUND_COLOR = new Color(34,34,34);
    public static final Color FOREGROUND_COLOR = Color.WHITE;
    public static final Color BUTTON_COLOR = new Color(37, 99, 235);
    public static final Color ERROR_COLOR = new Color(222, 49, 99);
    public static final Color ERROR_HOVER_COLOR = new Color(189, 57, 57);
    public static final Color REDUCE_BUTTON_COLOR = new Color(22, 163, 74);

    public static final Locale SYSTEM_LOCALE = Locale.getDefault();

    public static final Font SMALL_BASE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font MEDIUM_BASE_FONT = new Font("Segoe UI", Font.PLAIN, 20);

    public static final Font SMALL_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font MEDIUM_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 20);

    public static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 14);

    public static final String CROSS_MARK = "\u2716";
    public static final String CHECK_MARK = "\u2714";
}

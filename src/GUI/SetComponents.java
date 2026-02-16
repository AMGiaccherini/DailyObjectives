package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class SetComponents {
    
    static JButton createFlatButton(String text, Font font, Color fg, Color bg, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setPreferredSize(size);
        button.setForeground(fg);
        button.setBackground(bg);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if(!button.isEnabled()) return;
                button.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent e) {
                if(!button.isEnabled()) return;
                button.setBackground(bg);
            }
        });
        return button;
    }

    static JTextField createTextField(Font f, Color bg, Color fg, Color cc, Border b) {
        JTextField text = new JTextField();
        text.setFont(f);
        text.setBackground(bg);
        text.setForeground(fg);
        text.setCaretColor(cc);
        text.setBorder(b);

        return text;
    }

    static JLabel createSwitchLabel(String text) {
        JLabel label = new JLabel(text, SwingUtilities.CENTER);
        label.setOpaque(true);
        label.setFont(UIConstants.SMALL_BOLD_FONT);
        label.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    static JButton createWindowControlButton(String text, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        button.setFont(UIConstants.MEDIUM_BOLD_FONT);
        button.setForeground(UIConstants.FOREGROUND_COLOR);
        button.setBackground(UIConstants.BACKGROUND_COLOR);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.BACKGROUND_COLOR);
            }
        });

        return button;
    }

    static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIConstants.BACKGROUND_COLOR);
        scrollPane.setBackground(UIConstants.BACKGROUND_COLOR);
        scrollPane.setForeground(UIConstants.FOREGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrollPane.getVerticalScrollBar().setBackground(UIConstants.BACKGROUND_COLOR.brighter());
        scrollPane.getHorizontalScrollBar().setBackground(UIConstants.BACKGROUND_COLOR.brighter());
        scrollPane.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getHorizontalScrollBar().setBorder(BorderFactory.createEmptyBorder());

        JPanel corner = new JPanel();
        corner.setBackground(UIConstants.BACKGROUND_COLOR);
        scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, corner);
        scrollPane.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, corner);
        scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, corner);
        scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, corner);

        scrollPane.getVerticalScrollBar().setUI(new ThemedScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ThemedScrollBarUI());
    }

    private static class ThemedScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = UIConstants.BUTTON_COLOR;
            thumbDarkShadowColor = UIConstants.BUTTON_COLOR;
            thumbHighlightColor = UIConstants.BUTTON_COLOR.brighter();
            thumbLightShadowColor = UIConstants.BUTTON_COLOR;
            trackColor = UIConstants.BACKGROUND_COLOR;
            trackHighlightColor = UIConstants.BACKGROUND_COLOR;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            button.setVisible(false);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 8, 8);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width - 2, thumbBounds.height - 2, 8, 8);
            g2.dispose();
        }
    }
}

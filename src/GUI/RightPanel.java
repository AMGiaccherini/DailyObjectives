package GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class RightPanel {
    private static final int STATUS_WIDTH = 36;

    private final DefaultListModel<String> objectivesModel = new DefaultListModel<>();
    private final JList<String> objectivesList = new JList<>(objectivesModel);
    private LocalDate selectedDate;

    private final Border enabledBorder = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UIConstants.BUTTON_COLOR, 1),
        BorderFactory.createEmptyBorder(8,10,8,10)
    );

    private final Border disableBorder = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(UIConstants.BACKGROUND_COLOR, 1),
        BorderFactory.createEmptyBorder(8,10,8,10)
    );

    private JTextField addObjectiveField;
    private JButton addObjectiveButton;

    JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder());

        addObjectiveField = SetComponents.createTextField(
            UIConstants.SMALL_BOLD_FONT, 
            UIConstants.BACKGROUND_COLOR, 
            UIConstants.FOREGROUND_COLOR, 
            UIConstants.FOREGROUND_COLOR,
            enabledBorder);
        addObjectiveButton = SetComponents.createFlatButton(
            "Invia", 
            UIConstants.SMALL_BASE_FONT, 
            UIConstants.FOREGROUND_COLOR, 
            UIConstants.BUTTON_COLOR, 
            new Dimension(120, 36));
        
        addObjectiveField.addActionListener(e -> submitNewObjective());
        addObjectiveButton.addActionListener(e -> submitNewObjective());

        JPanel bottomBar = new JPanel(new BorderLayout(8,0));
        bottomBar.setBackground(UIConstants.BACKGROUND_COLOR);
        bottomBar.setBorder(BorderFactory.createEmptyBorder());
        bottomBar.add(addObjectiveField, BorderLayout.CENTER);
        bottomBar.add(addObjectiveButton, BorderLayout.EAST);

        objectivesList.setCellRenderer(new ObjectiveRenderer());
        objectivesList.setBackground(UIConstants.BACKGROUND_COLOR);
        objectivesList.setForeground(UIConstants.FOREGROUND_COLOR);
        objectivesList.setFont(UIConstants.SMALL_BASE_FONT);
        objectivesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = objectivesList.locationToIndex(e.getPoint());
                if (index < 0 || selectedDate == null) {
                    return;
                }

                Rectangle rowBounds = objectivesList.getCellBounds(index, index);
                if (rowBounds == null ||  !rowBounds.contains(e.getPoint())) return;

                int statusX = rowBounds.x + rowBounds.width - STATUS_WIDTH;
                if (e.getX() < statusX) return;

                if (Window.managingFile.ToggleObjectiveStatus(selectedDate, index)) {
                    updateObjectives(selectedDate);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(objectivesList);
        SetComponents.styleScrollPane(scroll);

        panel.add(bottomBar, BorderLayout.SOUTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    void updateObjectives(LocalDate selectDate) {

        selectedDate = selectDate;
        refreshEditState();

        objectivesModel.clear();
        if (selectDate == null) return;

        for (String objective : Window.managingFile.GetObjectivesByDate(selectDate)) {
            objectivesModel.addElement(objective);
        }
    }

    void bindToDateList(JList<LocalDate> datesList) {
        datesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateObjectives(datesList.getSelectedValue());
            }
        });
    }

    private static boolean isCompleted(String objective) {
        return objective != null && objective.replace("\uFE0F", "").contains(UIConstants.CHECK_MARK);
    }

    private static String cleanObjectiveText(String objective) {
        if (objective == null) {
            return "";
        }
        return objective
            .replace("\uFE0F", "")
            .replace(UIConstants.CHECK_MARK, "")
            .replace(UIConstants.CROSS_MARK, "")
            .replace("\t", " ")
            .trim();
    }

    private static String ellipsis(String text, int maxWidth, java.awt.FontMetrics fm) {
    if (text == null || text.isEmpty()) return "";
    if (maxWidth <= 0) return text;

    if (fm.stringWidth(text) <= maxWidth) {
        return text;
    }

    String dots = "...";
    int dotsWidth = fm.stringWidth(dots);
    int target = maxWidth - dotsWidth;
    if (target <= 0) return dots;

    int i = 0;
    while (i < text.length() && fm.stringWidth(text.substring(0, i + 1)) <= target) {
        i++;
    }

    return text.substring(0, Math.max(0, i)) + dots;
}

    private void submitNewObjective() {
        if (Window.managingFile.GetViewOldArchive()) return;

        if (selectedDate == null) return;

        String text = addObjectiveField.getText().trim();
        if (text.isEmpty()) return;

        if (Window.managingFile.AddObjectiveToDate(selectedDate, text)) {
            addObjectiveField.setText("");
            updateObjectives(selectedDate);
        }
    }

    public void refreshEditState() {
        if (addObjectiveField == null || addObjectiveButton == null) return;

        boolean mainMode = !Window.managingFile.GetViewOldArchive();
        boolean canEdit = mainMode && selectedDate != null;

        addObjectiveField.setEnabled(canEdit);
        addObjectiveButton.setEnabled(canEdit);

        addObjectiveField.setBorder(canEdit ? enabledBorder : disableBorder);
        addObjectiveButton.setBackground(canEdit ? UIConstants.BUTTON_COLOR : UIConstants.BACKGROUND_COLOR.brighter());
    }



    private static class ObjectiveRenderer extends JPanel implements ListCellRenderer<String> {
        private static final Icon CHECK_ICON = new StatusIcon(new Color(22, 163, 74), UIConstants.CHECK_MARK);
        private static final Icon CROSS_ICON = new StatusIcon(UIConstants.ERROR_COLOR, UIConstants.CROSS_MARK);

        private final JLabel objectiveLabel = new JLabel();
        private final JLabel statusLabel = new JLabel("", JLabel.CENTER);

        ObjectiveRenderer() {
            setLayout(new BorderLayout());
            setOpaque(true);

            objectiveLabel.setOpaque(false);
            objectiveLabel.setForeground(UIConstants.FOREGROUND_COLOR);
            objectiveLabel.setFont(UIConstants.SMALL_BASE_FONT);
            objectiveLabel.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));

            statusLabel.setOpaque(false);
            statusLabel.setPreferredSize(new java.awt.Dimension(STATUS_WIDTH, 28));
            statusLabel.setBorder(BorderFactory.createEmptyBorder());

            add(objectiveLabel, BorderLayout.CENTER);
            add(statusLabel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(
            JList<? extends String> list,
            String value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
        ) {
            boolean completed = isCompleted(value);

            objectiveLabel.setText(ellipsis(cleanObjectiveText(value), list.getWidth() - STATUS_WIDTH - 48, objectiveLabel.getFontMetrics(objectiveLabel.getFont())));
            statusLabel.setText("");
            statusLabel.setIcon(completed ? CHECK_ICON : CROSS_ICON);
            setBackground(UIConstants.BACKGROUND_COLOR);
            return this;
        }
    }

    private static class StatusIcon implements Icon {
        private final Color color;
        private final String symbol;
        private final int size = 20;

        StatusIcon(Color color, String symbol) {
            this.color = color;
            this.symbol = symbol;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(x, y, size, size);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Symbol", Font.BOLD, 13));
                Font f = g2.getFont();
                int tx = x + (size - g2.getFontMetrics(f).stringWidth(symbol)) / 2;
                int ty = y + ((size - g2.getFontMetrics(f).getHeight()) / 2) + g2.getFontMetrics(f).getAscent();
                g2.drawString(symbol, tx, ty);
            } finally {
                g2.dispose();
            }
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}

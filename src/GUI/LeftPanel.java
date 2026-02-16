package GUI;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class LeftPanel {
    public final DefaultListModel<LocalDate> datesModel = new DefaultListModel<>();
    public final JList<LocalDate> datesList = new JList<>(datesModel);
    private final DateCellRenderer renderer = new DateCellRenderer();

    private JLabel mainSwitch;
    private JLabel oldSwitch;
    private JButton addDate;

    DialogPage dialogPageInstance;

    public void setDialogPageInstance(DialogPage dialogPageInstance) {this.dialogPageInstance = dialogPageInstance;}
    
    public JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        mainSwitch = SetComponents.createSwitchLabel("Annotations");
        oldSwitch = SetComponents.createSwitchLabel("Old");

        mainSwitch.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {applyArchiveMode(false);}
        });

        oldSwitch.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {applyArchiveMode(true);}
        });

        JPanel topSwitchBar = new JPanel(new GridLayout(1, 2, 8, 0));
        topSwitchBar.setBackground(UIConstants.BACKGROUND_COLOR);
        topSwitchBar.setBorder(BorderFactory.createEmptyBorder());
        topSwitchBar.add(mainSwitch);
        topSwitchBar.add(oldSwitch);

        addDate = SetComponents.createFlatButton(
            "+",
            UIConstants.MEDIUM_BOLD_FONT, 
            UIConstants.FOREGROUND_COLOR, 
            UIConstants.BUTTON_COLOR, 
            new Dimension(0, 40));
        addDate.addActionListener(e -> dialogPageInstance.addDateFromDialog());

        JScrollPane listScroll = new JScrollPane(datesList);
        SetComponents.styleScrollPane(listScroll);

        datesList.setCellRenderer(renderer);
        datesList.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int index = datesList.locationToIndex(e.getPoint());
                boolean hoverOnDelete = false;
                if (!Window.managingFile.GetViewOldArchive() && index >= 0) {
                    Rectangle cellBounds = datesList.getCellBounds(index, index);
                    int deleteWidth = 33;
                    int deleteX = cellBounds.x + cellBounds.width - deleteWidth;
                    hoverOnDelete = e.getX() >= deleteX;
                }
                renderer.setHoveredIndex(index, hoverOnDelete);
                datesList.repaint();
            }
        });

        datesList.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                renderer.setHoveredIndex(-1, false);
                datesList.repaint();
            }

            public void mousePressed(MouseEvent e) {
                if (Window.managingFile.GetViewOldArchive()) {
                    return;
                }

                int index = datesList.locationToIndex(e.getPoint());
                if (index < 0) return;

                Rectangle cellBounds = datesList.getCellBounds(index, index);
                int deleteWidth = 33;
                int deleteX = cellBounds.x + cellBounds.width - deleteWidth;
                boolean clickedOnDelete = e.getX() >= deleteX;

                if(clickedOnDelete && datesList.getSelectedIndex() == index) {
                    LocalDate dateToRemove = datesModel.get(index);
                    
                    Window.managingFile.RemoveDate(dateToRemove);
                    datesModel.remove(index);

                    renderer.setHoveredIndex(-1, false);
                    datesList.clearSelection();
                    datesList.repaint();
                }
            }
        });
        datesList.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel bottomBar = new JPanel(new GridLayout(1, 1));
        bottomBar.setBackground(UIConstants.BACKGROUND_COLOR);
        bottomBar.add(addDate);

        panel.add(topSwitchBar, BorderLayout.NORTH);
        panel.add(listScroll, BorderLayout.CENTER);
        panel.add(bottomBar, BorderLayout.SOUTH);

        applyArchiveMode(false);

        return panel;
    }

    public void LoadDatesAtStart() {
        datesModel.clear();

        Window.managingFile.GetAllDates().stream()
        .sorted(Comparator.reverseOrder())
        .forEach(datesModel::addElement);
    }

    private void applyArchiveMode(boolean oldArchive) {
        Window.managingFile.SetViewOldArchive(oldArchive);
        if(Window.rightPanelRef != null) {
            Window.rightPanelRef.refreshEditState();
        }

        renderer.setReadOnly(oldArchive);

        addDate.setEnabled(!oldArchive);
        addDate.setBackground(!oldArchive ? UIConstants.BUTTON_COLOR : UIConstants.BACKGROUND_COLOR.brighter());

        styleSwitch(mainSwitch, !oldArchive);
        styleSwitch(oldSwitch, oldArchive);

        LoadDatesAtStart();
        datesList.clearSelection();
        datesList.repaint();
    }

    private void styleSwitch(JLabel label, boolean active) {
        label.setForeground(UIConstants.FOREGROUND_COLOR);
        label.setBackground(active ? UIConstants.BUTTON_COLOR : UIConstants.BACKGROUND_COLOR.brighter());
    }
}

class DateCellRenderer extends JPanel implements ListCellRenderer<LocalDate> {
    private final JLabel dateLabel = new JLabel();
    private final JLabel minusLabel = new JLabel("X", SwingConstants.CENTER);

    private boolean readOnly = false;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", UIConstants.SYSTEM_LOCALE);

    private int hoveredIndex = -1;
    private boolean hoverOnDelete = false;

    public DateCellRenderer() {
        setLayout(new BorderLayout());
        setOpaque(true);

        dateLabel.setBackground(UIConstants.BACKGROUND_COLOR);

        minusLabel.setOpaque(true);
        minusLabel.setBackground(UIConstants.ERROR_COLOR);
        minusLabel.setForeground(UIConstants.FOREGROUND_COLOR);
        minusLabel.setFont(UIConstants.SMALL_BOLD_FONT);
        minusLabel.setPreferredSize(new Dimension(33, 30));
        minusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 2, 10));

        add(dateLabel, BorderLayout.CENTER);
        add(minusLabel, BorderLayout.EAST);
    }

    public void setHoveredIndex(int index, boolean onDelete) {
        this.hoveredIndex = index;
        this.hoverOnDelete = onDelete;
    }

    public void setReadOnly(boolean readOnly) {this.readOnly = readOnly;}

    @Override
    public Component getListCellRendererComponent(JList<? extends LocalDate> list,
        LocalDate value, 
        int index,
        boolean isSelected, 
        boolean cellHasFocus) {
        
        dateLabel.setText(value.format(FORMATTER).toUpperCase());
        dateLabel.setForeground(UIConstants.FOREGROUND_COLOR);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        setBackground(UIConstants.BACKGROUND_COLOR);

        if (isSelected) {
            setBackground(UIConstants.BUTTON_COLOR);
            minusLabel.setVisible(!readOnly);
            minusLabel.setBackground(hoverOnDelete && index == hoveredIndex ? UIConstants.ERROR_HOVER_COLOR : UIConstants.ERROR_COLOR);
        } else {
            minusLabel.setVisible(false);
        }

        return this;
    }
}

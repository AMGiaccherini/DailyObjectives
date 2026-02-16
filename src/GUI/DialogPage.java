package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.function.Consumer;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class DialogPage {
    private final LeftPanel leftPanelInstance;
    private DefaultListModel<LocalDate> datesModel;

    public DialogPage(LeftPanel leftPanelInstance) {
        this.leftPanelInstance = leftPanelInstance;
    }

    void addDateFromDialog() {
        datesModel = leftPanelInstance.datesModel;

        while (true) {
            java.awt.Window owner = SwingUtilities.getWindowAncestor(leftPanelInstance.datesList);
            final String[] submittedValue = {null};
            final JDialog[] dialogRef = new JDialog[1];

            JDialog dialog = buildAddDateDialog(owner, raw -> {
                submittedValue[0] = raw;
                if (dialogRef[0] != null) {
                    dialogRef[0].dispose();
                }
            });
            dialogRef[0] = dialog;

            dialog.setVisible(true);

            if (submittedValue[0] == null) {
                return;
            }

            String raw = submittedValue[0].trim();
            if (raw.isEmpty()) {
                System.out.println("Inserisci una data.");
                continue;
            }

            LocalDate date;
            try {
                date = LocalDate.parse(raw, UIConstants.FORMATTER);
            } catch (DateTimeParseException ex) {
                System.out.println("Data non valida.");
                continue;
            }

            if (date.isBefore(LocalDate.now())) {
                System.out.println("Puoi inserire solo la data odierna o future.");
                continue;
            } else if (!Window.managingFile.TryAddDate(date)) {
                System.out.println("Data gia presente, non aggiunta.");
                continue;
            }

            int index = 0;
            for (; index < datesModel.size(); index++) {
                if (date.isAfter(datesModel.get(index))) break;
            }
            datesModel.add(index, date);
            break;
        }
    }

    private JDialog buildAddDateDialog(java.awt.Window owner, Consumer<String> onSubmit) {
        JDialog dialog = new JDialog(owner);
        dialog.setUndecorated(true);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(UIConstants.BACKGROUND_COLOR.brighter(), 1));

        JTextField inputField = new JTextField();
        inputField.setFont(UIConstants.SMALL_BASE_FONT);
        inputField.setBackground(UIConstants.BACKGROUND_COLOR);
        inputField.setForeground(UIConstants.FOREGROUND_COLOR);
        inputField.setCaretColor(UIConstants.FOREGROUND_COLOR);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BUTTON_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        inputField.addActionListener(e -> onSubmit.accept(inputField.getText()));

        JLabel label = new JLabel("Add a Date");
        label.setForeground(UIConstants.FOREGROUND_COLOR);
        label.setFont(UIConstants.MEDIUM_BOLD_FONT);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(UIConstants.BACKGROUND_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(label, BorderLayout.NORTH);
        content.add(inputField, BorderLayout.CENTER);

        dialog.add(createCustomTopBar(dialog), BorderLayout.NORTH);
        dialog.add(content, BorderLayout.CENTER);

        dialog.setSize(360, 150);
        dialog.setLocationRelativeTo(owner);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                inputField.requestFocusInWindow();
            }
        });

        return dialog;
    }

    private JPanel createCustomTopBar(JDialog dialog) {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIConstants.BACKGROUND_COLOR);
        topBar.setPreferredSize(new Dimension(0, 32));

        JPanel filler = new JPanel();
        filler.setOpaque(false);

        JButton closeButton = new JButton("x");
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        closeButton.setFont(UIConstants.MEDIUM_BOLD_FONT);
        closeButton.setForeground(UIConstants.FOREGROUND_COLOR);
        closeButton.setBackground(UIConstants.BACKGROUND_COLOR);
        closeButton.setOpaque(true);
        closeButton.setPreferredSize(new Dimension(44, 32));
        closeButton.addActionListener(e -> dialog.dispose());

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(UIConstants.ERROR_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(UIConstants.BACKGROUND_COLOR);
            }
        });

        topBar.add(filler, BorderLayout.CENTER);
        topBar.add(closeButton, BorderLayout.EAST);
        return topBar;
    }
}
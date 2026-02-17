package GUI;

import FileSorting.ManagingFile;
import java.io.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class Window extends JFrame {
    
    static ManagingFile managingFile;

    public static RightPanel rightPanelRef;

    public Window(File file) {

        Window.managingFile = new ManagingFile(file);

        setTitle("Daily Objectives");

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("app-icon.png"));
        setIconImage(icon.getImage());

        setUndecorated(true);
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);

        setUpWindow();
    }

    private void setUpWindow() {
        RightPanel rightPanelInstance = new RightPanel();
        LeftPanel leftPanelInstance = new LeftPanel();
        DialogPage dialogPageInstance = new DialogPage(leftPanelInstance);

        Window.rightPanelRef = rightPanelInstance;

        leftPanelInstance.setDialogPageInstance(dialogPageInstance);

        JPanel rightPanel = rightPanelInstance.createPanel();
        JPanel leftPanel = leftPanelInstance.createPanel();
        
        rightPanelInstance.bindToDateList(leftPanelInstance.datesList);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.28);
        splitPane.setDividerLocation(280);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);
        splitPane.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));
        splitPane.setBackground(UIConstants.BACKGROUND_COLOR.brighter());
        splitPane.setEnabled(false);
        splitPane.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(UIConstants.BACKGROUND_COLOR.brighter());
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
            }
        });

        add(splitPane, BorderLayout.CENTER);
        add(createCustomTopBar(), BorderLayout.NORTH);

        leftPanelInstance.LoadDatesAtStart();
    }

    private JPanel createCustomTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIConstants.BACKGROUND_COLOR);
        topBar.setPreferredSize(new Dimension(0, 34));

        JPanel dragArea = new JPanel();
        dragArea.setOpaque(false);

        JPanel windowButtons = new JPanel(new GridLayout(1, 2));
        windowButtons.setOpaque(false);
        windowButtons.setPreferredSize(new Dimension(88, 34));

        JButton minimizeButton = SetComponents.createWindowControlButton("-", UIConstants.REDUCE_BUTTON_COLOR);
        minimizeButton.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton closeButton = SetComponents.createWindowControlButton("x", UIConstants.ERROR_COLOR);
        closeButton.addActionListener(e ->
            dispatchEvent(new java.awt.event.WindowEvent(this, java.awt.event.WindowEvent.WINDOW_CLOSING))
        );

        windowButtons.add(minimizeButton);
        windowButtons.add(closeButton);

        Point dragOffset = new Point();
        MouseAdapter dragListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragOffset.setLocation(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point screen = e.getLocationOnScreen();
                setLocation(screen.x - dragOffset.x, screen.y - dragOffset.y);
            }
        };
        dragArea.addMouseListener(dragListener);
        dragArea.addMouseMotionListener(dragListener);

        topBar.add(dragArea, BorderLayout.CENTER);
        topBar.add(windowButtons, BorderLayout.EAST);
        return topBar;
    }
}

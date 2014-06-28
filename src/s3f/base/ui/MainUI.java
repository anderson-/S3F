/**
 * MainUI.java
 *
 * Copyright (C) 2014
 *
 * Anderson de Oliveira Antunes <anderson.utf@gmail.com>
 *
 * This file is part of S3F.
 *
 * S3F is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * S3F is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * S3F. If not, see http://www.gnu.org/licenses/.
 */
package s3f.base.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.FloatingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.BlueHighlightDockingTheme;
import net.infonode.docking.theme.ClassicDockingTheme;
import net.infonode.docking.theme.DefaultDockingTheme;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.GradientDockingTheme;
import net.infonode.docking.theme.LookAndFeelDockingTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.theme.SlimFlatDockingTheme;
import net.infonode.docking.theme.SoftBlueIceDockingTheme;
import net.infonode.docking.util.PropertiesUtil;
import net.infonode.gui.laf.InfoNodeLookAndFeel;
import s3f.base.plugin.Configurable;
import s3f.base.plugin.Data;
import s3f.base.plugin.Extensible;
import s3f.base.plugin.PluginManager;
import s3f.base.project.OLDproject.ProjectTreeTab;
import s3f.base.script.MyJSConsole;
import s3f.base.script.ScriptEnvironment;
import s3f.base.ui.tab.Tab;
import s3f.base.ui.tab.TabProperty;
import s3f.util.RandomColor;
import s3f.util.SplashScreen;

public class MainUI implements Extensible {

    private static MainUI MAIN_UI;

    private JFrame window;
    private JMenuBar menuBar;
    private JPanel toolBarPanel;
    private ComponentListener componentListener;
    private JPanel mainPanel;
    private RootWindow rootWindow;
    private DockingWindowsTheme currentTheme = new net.infonode.docking.theme.DefaultDockingTheme();
    private RootWindowProperties properties = new RootWindowProperties();
    private JToolBar statusBar;
    private JLabel statusLabel;
    private PluginConfigurationWindow pluginConfigurationWindow = null;
    private MyJSConsole terminal = null;
    //actions
    private AbstractAction createAndShowTerminal;
    private AbstractAction createAndShowConfigurationWindow;
    private TabWindow firstTabWindow;
    private TabWindow secondTabWindow;
    private TabWindow thirdTabWindow;
    private JCheckBox helpCheckBox;

    private MainUI() {
        createUI();
        createActions();
        addKeyBindings();
    }

    public static MainUI getInstance() {
        if (MAIN_UI == null) {
            MAIN_UI = new MainUI();
        }
        return MAIN_UI;
    }

    private void createUI() {
        //janela
        window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.getContentPane().setPreferredSize(new Dimension(800, 600));

        //menuBar
        menuBar = new JMenuBar();
//        JMenu jMenu1 = new JMenu();
//        JMenu jMenu2 = new JMenu();
//
//        jMenu1.setText("File");
//        menuBar.add(jMenu1);
//
//        jMenu2.setText("Edit");
//        menuBar.add(jMenu2);

        //menu bar
        window.setJMenuBar(menuBar);

        //toolbar
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        window.getContentPane().add(toolBarPanel, BorderLayout.NORTH);

        //deafult toolbar buttons
        toolBarPanel.add(addTip(createToolbarButton(null, "", "/resources/tango/32x32/actions/document-new.png"), "dica :DDD"));
        toolBarPanel.add(addTip(createToolbarButton(null, "", "/resources/tango/32x32/actions/document-open.png"), "dica :DDD"));
        toolBarPanel.add(addTip(createToolbarButton(null, "", "/resources/tango/32x32/devices/media-floppy.png"), "dica :DDD"));

        //mainPanel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        window.getContentPane().add(mainPanel, BorderLayout.CENTER);

        //docking windows
        rootWindow = new RootWindow(null);
        rootWindow.setBorder(null);
        properties.addSuperObject(currentTheme.getRootWindowProperties());
        rootWindow.getRootWindowProperties().addSuperObject(properties);
        // Add a mouse button listener that closes a window when it's clicked with the middle mouse button.
        rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
        mainPanel.add(rootWindow);

        //ProjectTree & Console
        ProjectTreeTab projectTreeTab = new ProjectTreeTab("Empty");
        View projectTreeView = new View(
                (String) projectTreeTab.getData().getProperty(TabProperty.TITLE),
                (Icon) projectTreeTab.getData().getProperty(TabProperty.ICON),
                (Component) projectTreeTab.getData().getProperty(TabProperty.COMPONENT)
        );

        final JTextArea console = new JTextArea();
        console.setEditable(false);
        JPopupMenu popupMenu = new JPopupMenu();
        console.setComponentPopupMenu(popupMenu);
        JMenuItem menuItem = new JMenuItem("Limpar");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                console.setText("");
            }
        });
        popupMenu.add(menuItem);
        secondTabWindow = new TabWindow();
        //secondTabWindow.getTabWindowProperties().getTabbedPanelProperties().setTabAreaOrientation(Direction.LEFT);
        secondTabWindow.addTab(projectTreeView);
        View consoleView = new View("Console", null, new JScrollPane(console));
        thirdTabWindow = new TabWindow();
        thirdTabWindow.addTab(consoleView);

        SplitWindow leftSplitWindow = new SplitWindow(false, .5f, secondTabWindow, thirdTabWindow);

        firstTabWindow = new TabWindow();
        SplitWindow firstSplitWindow = new SplitWindow(true, .2f, leftSplitWindow, firstTabWindow);
        rootWindow.setWindow(firstSplitWindow);

        lockView(firstTabWindow);
        lockView(secondTabWindow);
        lockView(thirdTabWindow);

        //teste desenhar com outro tema
//        final Random r = new Random();
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                }
//                for (int i = 0; i < 5; i++) {
//
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException ex) {
//                    }
//                    SwingUtilities.invokeLater(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                final LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
//                                InfoNodeLookAndFeelTheme theme
//                                        = new InfoNodeLookAndFeelTheme("My Theme",
//                                                new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)),
//                                                new Color(0, 170, r.nextInt(255)),
//                                                new Color(80, r.nextInt(255), 80),
//                                                Color.WHITE,
//                                                new Color(0, r.nextInt(255), r.nextInt(255)),
//                                                Color.WHITE,
//                                                0.8);
//                                UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme));
//
////                    SwingUtilities.invokeLater(new Runnable() {
////                        @Override
////                        public void run() {
//                                final BufferedImage bi = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
//
//                                SwingUtilities.updateComponentTreeUI(window);
//                                firstTabWindow.setVisible(false);
//                                window.printAll(bi.getGraphics());
//
//                                JPanel p = new JPanel() {
//                                    @Override
//                                    public void paintComponent(Graphics g) {
//                                        g.drawImage(bi, 0, 0, null);
//                                    }
//                                };
//                                firstTabWindow.setVisible(true);
//                                firstTabWindow.addTab(new View("asd", null, p));
//
////                            SwingUtilities.invokeLater(new Runnable() {
////                                @Override
////                                public void run() {
//                                try {
//                                    UIManager.setLookAndFeel(lookAndFeel);
//                                    SwingUtilities.updateComponentTreeUI(window);
//                                } catch (UnsupportedLookAndFeelException ex) {
//
//                                }
////                                }
////                            });
////                        }
////                    });
//                            } catch (UnsupportedLookAndFeelException ex) {
//
//                            }
//                        }
//                    });
//                }
//            }
//        }.start();
        //statusBar
        statusBar = new JToolBar();
        statusLabel = new JLabel();
        statusBar.setFloatable(false);
        statusBar.setRollover(true);
        helpCheckBox = new JCheckBox();
        helpCheckBox.setSelected(true);
        helpCheckBox.setToolTipText("Selecione para dicas");
        helpCheckBox.setFocusable(false);
        helpCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (!helpCheckBox.isSelected()) {
                    statusLabel.setText(PluginManager.getText("s3f.statusbar.welcome"));
                }
            }
        });
        helpCheckBox.setBorder(null);
        statusBar.add(helpCheckBox);
        statusBar.add(Box.createHorizontalStrut(3));

        statusLabel.setText(PluginManager.getText("s3f.statusbar.welcome"));
        statusBar.add(statusLabel);

        window.getContentPane().add(statusBar, BorderLayout.SOUTH);

        //finaliza
        window.pack();
    }

    private void show() {
        //centraliza a janela
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle frame = window.getBounds();
        window.setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
        //torna a janela visivel
        window.setVisible(true);
    }

    private JSeparator separator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(sep.getPreferredSize().width, 32));
        return sep;
    }

    public static JComponent createToolbarButton(AbstractAction action, String tooltip, String iconPath) {
        JButton button = new JButton();
        button.setIcon(new javax.swing.ImageIcon(MainUI.class.getResource(iconPath)));
        button.setToolTipText(tooltip);
        button.setBorderPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusable(false);
        //button.setText("text");
        button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        button.addActionListener(action);
        return button;
    }

    public Component addTip(Component c, final String tip) {
        c.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                printHelp(tip);
            }
        });
        return c;
    }

    public void printHelp(String str) {
        if (helpCheckBox.isSelected()) {
            statusLabel.setText(str);
        }
    }

    public void addView(int order, DockingWindow view) {
        TabWindow tw;
        switch (order) {
            case 1:
                tw = firstTabWindow;
                break;
            case 2:
                tw = secondTabWindow;
                break;
            case 3:
                tw = thirdTabWindow;
                break;
            default:
                tw = firstTabWindow;
        }
        tw.addTab(view);
    }

    public void addView(int order, Configurable obj) {
        View view = new View(
                (String) obj.getData().getProperty(TabProperty.TITLE),
                (Icon) obj.getData().getProperty(TabProperty.ICON),
                (Component) obj.getData().getProperty(TabProperty.COMPONENT)
        );
        addView(order, view);
    }

    private void lockView(DockingWindow dw) {
        dw.getWindowProperties().setMaximizeEnabled(false);
        dw.getWindowProperties().setUndockEnabled(false);
        dw.getWindowProperties().setCloseEnabled(false);
    }

    private void createActions() {
        createAndShowTerminal = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (terminal == null) {
                    terminal = new MyJSConsole(ScriptEnvironment.getVariables(), ScriptEnvironment.getFunctions());
                }

//                {
//                    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//                    Rectangle frame = terminal.getBounds();
//                    terminal.setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
//                    terminal.setVisible(true);
//                }
                {
                    if (!terminal.getRootPane().isShowing()) {
                        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                        Rectangle frame = terminal.getBounds();
                        // Floating windows are created via the root window
                        FloatingWindow fw = rootWindow.createFloatingWindow(
                                new Point((screen.width - frame.width) / 2, (screen.height - frame.height) / 2),
                                null, //new Dimension(300, 200),
                                new View(terminal.getTitle(), null, terminal.getRootPane())
                        );

                        // Show the window
                        fw.getTopLevelAncestor().setVisible(true);
                    }
                }
            }
        };

        createAndShowConfigurationWindow = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pluginConfigurationWindow == null) {
                    pluginConfigurationWindow = new PluginConfigurationWindow();
                }
                pluginConfigurationWindow.show(true);
            }
        };
    }

    ;

    private void addKeyBindings() {
        window.getRootPane().getActionMap().put("myAction", createAndShowTerminal);
        window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control shift A"), "myAction");
    }

    public JMenuItem getS3FPluginConfigurationMenuItem() {
        JMenuItem i = new JMenuItem(PluginManager.getText("pcw.item.name"));
        i.addActionListener(createAndShowConfigurationWindow);
        return i;
    }

    public JMenuItem getS3FShellMenuItem() {
        JMenuItem i = new JMenuItem("Terminal");
        i.addActionListener(createAndShowTerminal);
        return i;
    }

    private JMenu createThemesMenu() {
        JMenu themesMenu = new JMenu("Themes");

        final RootWindowProperties titleBarStyleProperties = PropertiesUtil.createTitleBarStyleRootWindowProperties();

        final JCheckBoxMenuItem titleBarStyleItem = new JCheckBoxMenuItem("Title Bar Style Theme");
        titleBarStyleItem.setSelected(false);
        titleBarStyleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (titleBarStyleItem.isSelected()) {
                    properties.addSuperObject(titleBarStyleProperties);
                } else {
                    properties.removeSuperObject(titleBarStyleProperties);
                }
            }
        });

        themesMenu.add(titleBarStyleItem);
        themesMenu.add(new JSeparator());

        DockingWindowsTheme[] themes = {new DefaultDockingTheme(),
            new LookAndFeelDockingTheme(),
            new BlueHighlightDockingTheme(),
            new SlimFlatDockingTheme(),
            new GradientDockingTheme(),
            new ShapedGradientDockingTheme(),
            new SoftBlueIceDockingTheme(),
            new ClassicDockingTheme()};

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < themes.length; i++) {
            final DockingWindowsTheme theme = themes[i];

            JRadioButtonMenuItem item = new JRadioButtonMenuItem(theme.getName());
            item.setSelected(i == 0);
            group.add(item);

            themesMenu.add(item).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Clear the modified properties values
                    properties.getMap().clear(true);

                    setTheme(theme);
                }
            });
        }

        return themesMenu;
    }

    private void setTheme(DockingWindowsTheme theme) {
        properties.replaceSuperObject(currentTheme.getRootWindowProperties(),
                theme.getRootWindowProperties());
        currentTheme = theme;
    }

    public static void setStatus() {

    }

    public static void buildAndRun() {
        final SplashScreen splashScreen = new SplashScreen("/resources/jifi5.png", true);
        splashScreen.splash();
        try {
//            String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
//            if (!systemLookAndFeelClassName.equals(UIManager.getCrossPlatformLookAndFeelClassName())) {
//                UIManager.setLookAndFeel(systemLookAndFeelClassName);
//            } else {
//                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                    if ("Nimbus".equals(info.getName())) {
//                        UIManager.setLookAndFeel(info.getClassName());
//                        break;
//                    }
//                }
//            }
            UIManager.setLookAndFeel(createLookAndFeel(RandomColor.generate(.3f, .8f)));

//            InfoNodeLookAndFeelTheme theme
//                    = new InfoNodeLookAndFeelTheme("My Theme",
//                            new Color(110, 120, 150),
//                            new Color(0, 170, 0),
//                            new Color(80, 80, 80),
//                            Color.WHITE,
//                            new Color(0, 170, 0),
//                            Color.WHITE,
//                            0.8);
//            UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme));
//            UIManager.setLookAndFeel(new InfoNodeLookAndFeel());
        } catch (Exception ex) {

        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainUI ui = MainUI.getInstance();
                ui.loadModulesFrom(PluginManager.getPluginManager());
                ui.show();
                splashScreen.dispose();
            }
        });
    }

    public static LookAndFeel createLookAndFeel(Color c) {
        InfoNodeLookAndFeel infoNodeLookAndFeel = new InfoNodeLookAndFeel();
        infoNodeLookAndFeel.getTheme().setDesktopColor(c);
        infoNodeLookAndFeel.getTheme().setPrimaryControlColor(c);
        infoNodeLookAndFeel.getTheme().setSelectedMenuBackgroundColor(c);
        infoNodeLookAndFeel.getTheme().setSelectedTextBackgroundColor(c);
        return infoNodeLookAndFeel;
    }

    @Override
    public void loadModulesFrom(PluginManager pm) {

        //pm.PRINT_TEST();
        String platformName = pm.getFactoryData("s3f").getProperty("platform_name");
        String platformVersion = pm.getFactoryData("s3f").getProperty("platform_version");

        String title = PluginManager.getText("s3f.frame.name") + " [ "
                + ((platformName == null) ? PluginManager.getText("s3f.frame.defaultplatformtitle") : platformName)
                + ((platformVersion == null) ? "" : " | " + platformVersion) + " ]";
        window.setTitle(title);

        //view menu
        JMenu viewMenu = new JMenu(PluginManager.getText("s3f.viewmenu.name"));
//        viewMenu.add();
        menuBar.add(viewMenu);

        //window menu
        JMenu windowMenu = new JMenu(PluginManager.getText("s3f.windowmenu.name"));
//        windowMenu.add();
        menuBar.add(windowMenu);

        //load singleton and factories from :
        Data[] factoriesData = pm.getFactoriesData("s3f.guibuilder.*");

        if (factoriesData != null) {
            GUIBuilder builder = new GUIBuilder("") {
                @Override
                public void init() {
                }
            };

            for (Data d : factoriesData) {
                if (d != null && d.getReference() instanceof GUIBuilder) {
                    GUIBuilder gb = (GUIBuilder) d.getReference();
                    gb.init();
                    builder.append(gb);
                }
            }

            for (JMenu o : builder.getMenus()) {
                menuBar.add(o);
            }

            int tmpWidth = 0;

            ArrayList<Component> toolbarComponents = builder.getToolbarComponents(true, 500);

            if (!toolbarComponents.isEmpty()) {
                toolBarPanel.add(separator());
            }

            for (Component c : toolBarPanel.getComponents()) {
                tmpWidth += c.getPreferredSize().width;
            }

            for (Component c : toolbarComponents) {
                toolBarPanel.add(c);
                tmpWidth += c.getPreferredSize().width;
            }

            final JPanel p = new JPanel();
            p.setSize(new Dimension(50, 50));
            toolBarPanel.add(p);

            for (Component o : builder.getToolbarComponents(false, 500)) {
                toolBarPanel.add(o);
                tmpWidth += o.getPreferredSize().width;
            }

            final int width = tmpWidth;

            window.removeComponentListener(componentListener);
            componentListener = new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent e) {
                    p.setPreferredSize(new Dimension(window.getWidth() - width - 2, 50));
                    toolBarPanel.updateUI();
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                }

                @Override
                public void componentShown(ComponentEvent e) {
                }

                @Override
                public void componentHidden(ComponentEvent e) {
                }
            };
            window.addComponentListener(componentListener);

            ArrayList<DockingWindow> tabs = new ArrayList<>();

            for (Tab o : builder.getTabs()) {
                View view = new View(
                        (String) o.getData().getProperty(TabProperty.TITLE),
                        (Icon) o.getData().getProperty(TabProperty.ICON),
                        (Component) o.getData().getProperty(TabProperty.COMPONENT)
                );

                tabs.add(view);
            }

            for (DockingWindow dw : tabs) {
                addView(1, dw);
            }
        }

        //s3f menu
        Object o = PluginManager.getPluginManager().getFactoryProperty("s3f", "hideS3Fmenu");
        boolean hideS3Fmenu = o != null && o instanceof Boolean && ((Boolean) o) == true;
        JMenu S3FMenu = new JMenu((hideS3Fmenu) ? "     " : PluginManager.getText("s3f.menu"));
        S3FMenu.add(getS3FPluginConfigurationMenuItem());
        S3FMenu.add(getS3FShellMenuItem());
        S3FMenu.add(createThemesMenu());
        S3FMenu.addSeparator();
        JMenuItem i;
        i = new JMenuItem(PluginManager.getText("s3f.item.about"));
        S3FMenu.add(i);
        menuBar.add(S3FMenu);

        //testes
//        pm.PRINT_TEST();
//
//        ArrayList<Data> a = new ArrayList<>();
//
//        String b = pm.search("s3f.jifi", pm.getFactoryData("s3f"), a);
//
//        System.out.println("error in : '" + b + "'");
//        for (Data d : a) {
//            System.out.println(d);
//        }
//
//        Class c = pm.getFactoryData("s3f.dwrs.Hello2").getProperty("teste");
//        try {
//            Object o = c.newInstance();
//            System.out.println(o);
//        } catch (Exception ex) {
//            System.out.println(ex.getClass());
//        }
    }

}

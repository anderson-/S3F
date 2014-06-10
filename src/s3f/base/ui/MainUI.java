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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import s3f.base.plugin.Data;
import s3f.base.plugin.Extensible;
import s3f.base.plugin.PluginManager;
import s3f.base.script.MyJSConsole;
import s3f.base.script.ScriptEnvironment;
import s3f.base.ui.tab.Tab;
import s3f.base.ui.tab.tabbedpaneview.TabbedPaneView;
import s3f.util.SplashScreen;
import sun.org.mozilla.javascript.tools.shell.JSConsole;

public class MainUI implements Extensible {

    private JFrame window;
    private JMenuBar menuBar;
    private JPanel toolBarPanel;
    private ComponentListener componentListener;
    private JPanel mainPanel;
    private TabbedPaneView mainView;
    private JToolBar statusBar;
    private JLabel statusLabel;
    private PluginConfigurationWindow pluginConfigurationWindow = null;
    private MyJSConsole terminal = null;
    //actions
    private AbstractAction createAndShowTerminal;
    private AbstractAction createAndShowConfigurationWindow;

    private MainUI() {
        
        createUI();
        createActions();
        addKeyBindings();
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

        window.setJMenuBar(menuBar);

        //toolBar
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        window.getContentPane().add(toolBarPanel, BorderLayout.NORTH);

        //mainPanel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        window.getContentPane().add(mainPanel, BorderLayout.CENTER);

        mainView = new TabbedPaneView();
        mainPanel.add(mainView.getJPanel());

        //####### INICIO TESTES #######//
        /*/
         mainView.split(TabbedPaneView.HORIZONTAL, true);
         Project project = new Project("Projeto X");

         for (int i = 0; i < 10; i++) {
         project.addElement(new TESTE.TMPElement("asdl", UIManager.getIcon("FileView.fileIcon")));
         }

         mainView.add(new ProjectTreeTab(project));
         mainView.add(new s3f.base.ui.tab.MessageTab());
         addTabs(mainView);
         addTabs(mainView.get(TabbedPaneView.SECOND));
         /*/
        //######## FIM TESTES ########//
        //statusBar
        statusBar = new JToolBar();
        statusLabel = new JLabel();
        statusBar.setFloatable(false);
        statusBar.setRollover(true);
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
    
    private void createActions(){
        createAndShowTerminal = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (terminal == null) {
                    terminal = new MyJSConsole(ScriptEnvironment.getVariables(), ScriptEnvironment.getFunctions());
                    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                    Rectangle frame = terminal.getBounds();
                    terminal.setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
                }
                terminal.setVisible(true);
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

    public static void setStatus() {

    }

    public static void buildAndRun() {
        final SplashScreen splashScreen = new SplashScreen("/resources/jifi5.png");
        splashScreen.splash();
        try {
            String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
            if (!systemLookAndFeelClassName.equals(UIManager.getCrossPlatformLookAndFeelClassName())) {
                UIManager.setLookAndFeel(systemLookAndFeelClassName);
            } else {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }
        } catch (Exception ex) {

        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainUI ui = new MainUI();
                ui.loadModulesFrom(PluginManager.getPluginManager());
                ui.show();
                splashScreen.dispose();
            }
        });
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

            for (Component o : builder.getToolbarComponents(true, 500)) {
                toolBarPanel.add(o);
                tmpWidth += o.getPreferredSize().width;
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

            for (Tab o : builder.getTabs()) {
                mainView.add(o);
            }
        }

        Object o = PluginManager.getPluginManager().getFactoryProperty("s3f", "hideS3Fmenu");
        boolean hideS3Fmenu = o != null && o instanceof Boolean && ((Boolean) o) == true;
        JMenu S3FMenu = new JMenu((hideS3Fmenu) ? "     " : PluginManager.getText("s3f.menu"));
        S3FMenu.add(getS3FPluginConfigurationMenuItem());
        S3FMenu.add(getS3FShellMenuItem());
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

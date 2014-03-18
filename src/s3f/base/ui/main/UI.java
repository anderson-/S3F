/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.ui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import s3f.base.project.OLDproject.Project;
import s3f.base.project.OLDproject.ProjectTreeTab;
import static s3f.base.ui.main.Main.addTabs;
import s3f.base.ui.tab.tabbedpaneview.TabbedPaneView;
import s3f.util.SplashScreen;

/**
 *
 * @author antunes2
 */
public class UI {

    private JLabel statusLabel;
    private JMenu jMenu1;
    private JMenu jMenu2;
    private JMenuBar menuBar;
    private JPanel mainPanel;
    private JToolBar mainToolBar;
    private JToolBar statusBar;
    private JFrame window;
    private JPanel toolBarPanel;
    private ArrayList<JToolBar> toolBars = new ArrayList<>();

    private UI() {
        createAndShowUI();
    }

    private void createAndShowUI() {
        //janela
        window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.getContentPane().setPreferredSize(new Dimension(800, 600));

        //menuBar
        menuBar = new JMenuBar();
        jMenu1 = new JMenu();
        jMenu2 = new JMenu();

        jMenu1.setText("File");
        menuBar.add(jMenu1);

        jMenu2.setText("Edit");
        menuBar.add(jMenu2);

        window.setJMenuBar(menuBar);
        //toolBar

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        mainToolBar = new JToolBar();
        mainToolBar.setFloatable(false);
        mainToolBar.setRollover(true);
        mainToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        mainToolBar.setBorderPainted(false);

        mainToolBar.add(new ToolBarButton().getJComponent());
        mainToolBar.add(new ToolBarButton().getJComponent());
        

        toolBarPanel.add(mainToolBar);

//        JToolBar teste = new JToolBar();
//        teste.setFloatable(false);
//        teste.setBorderPainted(false);
//        teste.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
//        teste.add(new JButton("asd"));
//        toolBarPanel.add(teste);

        window.getContentPane().add(toolBarPanel, BorderLayout.NORTH);
        //mainPanel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
        window.getContentPane().add(mainPanel, BorderLayout.CENTER);
        
        
        TabbedPaneView mainView = new TabbedPaneView();

        mainView.split(TabbedPaneView.VERTICAL, true);
        Project project = new Project("Projeto X");

        for (int i = 0; i < 10; i++) {
            project.addElement(new Main.TMPElement("asdl", UIManager.getIcon("FileView.fileIcon")));
        }

        mainView.add(new ProjectTreeTab(project));
        mainView.add(new s3f.base.ui.tab.MessageTab());
        addTabs(mainView);
        addTabs(mainView.get(TabbedPaneView.SECOND));
        mainPanel.add(mainView.getJPanel());
        
        //statusBar
        statusBar = new JToolBar();
        statusLabel = new JLabel();
        statusBar.setFloatable(false);
        statusBar.setRollover(true);
        statusLabel.setText("Bem Vindo!");
        statusBar.add(statusLabel);

        window.getContentPane().add(statusBar, BorderLayout.SOUTH);

        //finaliza
        window.pack();
    }

    private void show() {
        window.setVisible(true);
    }

    public void addToolBarButton(JComponent component) {

    }

    public static void setStatus() {

    }

    public static void main(String args[]) {
        final SplashScreen splashScreen = new SplashScreen("/resources/jifi5.png");
        splashScreen.splash();
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {

        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                UI ui = new UI();
                ui.show();
                splashScreen.dispose();
            }
        });
    }
}

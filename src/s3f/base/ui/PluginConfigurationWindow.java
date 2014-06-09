/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import s3f.base.plugin.PluginManager;

/**
 *
 * @author antunes
 */
public class PluginConfigurationWindow {

    private JFrame window;
    private JTabbedPane tabbedPane;

    public PluginConfigurationWindow() {
        createAndShowUI();
    }

    private void createAndShowUI() {
        //janela
        window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        window.getContentPane().setPreferredSize(new Dimension(600, 400));
        window.setTitle(PluginManager.getText("pcw.frame.name"));

        tabbedPane = new JTabbedPane();

        JPanel frameworkConfigurationPane = new JPanel();
        //lingua
        //adicionar remover plugins
        //scripts

        tabbedPane.add(frameworkConfigurationPane, PluginManager.getText("pcw.tab.general.name"));

        window.getContentPane().add(tabbedPane);

        //finaliza
        window.pack();
    }

    public void show(boolean show) {
        if (show) {
            //centraliza a janela
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle frame = window.getBounds();
            window.setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
        }
        //torna a janela visivel
        window.setVisible(show);
    }

}

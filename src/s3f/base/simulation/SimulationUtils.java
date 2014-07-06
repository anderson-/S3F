/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.simulation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import s3f.base.ui.MainUI;
import s3f.util.ColorUtils;

/**
 *
 * @author antunes
 */
public class SimulationUtils {

    private static final ImageIcon ICON_PLAY = new ImageIcon(SimulationUtils.class.getResource("/resources/tango/22x22/actions/media-playback-start.png"));
    private static final ImageIcon ICON_PAUSE = new ImageIcon(SimulationUtils.class.getResource("/resources/tango/22x22/actions/media-playback-pause.png"));
    private static final ImageIcon ICON_STOP = new ImageIcon(SimulationUtils.class.getResource("/resources/tango/22x22/actions/media-playback-stop.png"));
    private static final ImageIcon ICON_STEP = new ImageIcon(SimulationUtils.class.getResource("/resources/tango/22x22/actions/step.png"));

    private SimulationUtils() {

    }

    public static List<Component> createControlPanel(final Simulator simulator) {
        ArrayList<Component> components = new ArrayList<>();
        final JButton playButton = MainUI.createToolbarButton(null, "ashdas\nadadsy\niasdaus", ICON_PLAY);
        final JButton stepButton = MainUI.createToolbarButton(null, "ashdas\nadadsy\niasdaus", ICON_STEP);
        final JButton stopButton = MainUI.createToolbarButton(null, "ashdas\nadadsy\niasdaus", ICON_STOP);
        stopButton.setEnabled(false);

        AbstractAction playAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playButton.getIcon() == ICON_PLAY) {
                    java.lang.System.out.println("play");
                    playButton.setIcon(ICON_PAUSE);
                    playButton.setRolloverIcon(new ImageIcon(ColorUtils.imageHSBAchange(ICON_PAUSE.getImage(), 0, 0, .1f, 0)));
                    stopButton.setEnabled(true);
//                    if (!tooltip.isEmpty()) {
//                        if (tooltip.contains("\n")) {
//                            tooltip = "<html>" + tooltip.replace("\n", "<p>") + "</html>";
//                        }
//                        button.setToolTipText(tooltip);
//                    }
//                    simulator.setSystemState(Simulator.RUNNING);
                } else {
                    java.lang.System.out.println("pause");
                    playButton.setIcon(ICON_PLAY);
                    playButton.setRolloverIcon(new ImageIcon(ColorUtils.imageHSBAchange(ICON_PLAY.getImage(), 0, 0, .1f, 0)));
                    stopButton.setEnabled(true);
//                    simulator.setSystemState(Simulator.PAUSED);
                }
            }
        };

        AbstractAction stepAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.lang.System.out.println("step-pause");
                playButton.setIcon(ICON_PLAY);
                playButton.setRolloverIcon(new ImageIcon(ColorUtils.imageHSBAchange(ICON_PLAY.getImage(), 0, 0, .1f, 0)));
                stopButton.setEnabled(true);
            }
        };
        AbstractAction stopAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.lang.System.out.println("stop");
                playButton.setIcon(ICON_PLAY);
                playButton.setRolloverIcon(new ImageIcon(ColorUtils.imageHSBAchange(ICON_PLAY.getImage(), 0, 0, .1f, 0)));
                stopButton.setEnabled(false);
            }
        };

        playButton.addActionListener(playAction);
        stepButton.addActionListener(stepAction);
        stopButton.addActionListener(stopAction);

        components.add(MainUI.addTip(playButton, "dica :DDD"));
        components.add(MainUI.addTip(stepButton, "dica :DDD"));
        components.add(MainUI.addTip(stopButton, "dica :DDD"));
        return components;
    }

}

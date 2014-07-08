/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.simulation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import s3f.core.ui.MainUI;
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

    public static Simulator createTestSimulator() {
        Simulator sim = new MultiThreadSimulator();
        System[] s = new System[10];
        for (int i = 0; i < 10; i++) {
            s[i] = new DummySystem("Sistema " + i);
            sim.add(s[i]);
        }
        Thread t = new Thread(sim);
        t.start();
        return sim;
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
                if (playButton.getIcon() == ICON_PLAY) {;
                    simulator.setSystemState(Simulator.RUNNING);
                } else {
                    simulator.setSystemState(Simulator.PAUSED);
                }
            }
        };

        AbstractAction stepAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulator.setSystemState(MultiThreadSimulator.STEP);
            }
        };
        AbstractAction stopAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulator.reset();
            }
        };

        if (simulator instanceof MultiThreadSimulator) {
            MultiThreadSimulator multiThreadSimulator = (MultiThreadSimulator) simulator;
            multiThreadSimulator.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(MultiThreadSimulator.PROPERTY_STEP)) {
                        if (evt.getNewValue().equals(MultiThreadSimulator.PROPERTY_STEP_BEGIN)) {
                            stopButton.setEnabled(true);
                        } else if (evt.getNewValue().equals(MultiThreadSimulator.PROPERTY_STEP_END)) {

                        }
                    } else if (evt.getPropertyName().equals(MultiThreadSimulator.PROPERTY_STATUS)) {
                        if (evt.getNewValue().equals(MultiThreadSimulator.PROPERTY_STATUS_PAUSED)) {
                            playButton.setIcon(ICON_PLAY);
                            playButton.setRolloverIcon(new ImageIcon(ColorUtils.imageHSBAchange(ICON_PLAY.getImage(), 0, 0, .1f, 0)));
                        } else if (evt.getNewValue().equals(MultiThreadSimulator.PROPERTY_STATUS_RUNNING)) {

                        } else if (evt.getNewValue().equals(MultiThreadSimulator.PROPERTY_STATUS_RESETED)) {
                            stopButton.setEnabled(false);
                        }
                    } else if (evt.getPropertyName().equals(MultiThreadSimulator.PROPERTY_STATE)) {
                        if (evt.getNewValue().equals(System.RUNNING)) {
                            playButton.setIcon(ICON_PAUSE);
                            playButton.setRolloverIcon(new ImageIcon(ColorUtils.imageHSBAchange(ICON_PAUSE.getImage(), 0, 0, .1f, 0)));
                        } else if (evt.getNewValue().equals(System.PAUSED)) {

                        }
                    }
                }
            });
        }

        playButton.addActionListener(playAction);
        stepButton.addActionListener(stepAction);
        stopButton.addActionListener(stopAction);

        components.add(MainUI.addTip(playButton, "dica :DDD"));
        components.add(MainUI.addTip(stepButton, "dica :DDD"));
        components.add(MainUI.addTip(stopButton, "dica :DDD"));
        return components;
    }

}

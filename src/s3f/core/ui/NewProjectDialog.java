/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.ui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import s3f.core.plugin.EntityManager;
import s3f.core.plugin.PluginManager;
import s3f.core.project.Element;
import s3f.core.project.ProjectTemplateCategory;
import s3f.core.project.Project;
import s3f.core.project.ProjectTemplateCategory;

/**
 *
 * @author antunes
 */
public class NewProjectDialog extends JDialog {

    private static ImageIcon PROJECT_ICON = new ImageIcon(NewProjectDialog.class.getResource("/resources/icons/fugue/box.png"));

    public static Project createNewProjectDialog() {
        final JList plugins;
        final JList projectTemplates;
        final JOptionPane optionPane;

        String btnString1 = "Criar";
        String btnString2 = "Cancel";

        plugins = new JList();
        projectTemplates = new JList();

        final DefaultListModel cModel = new DefaultListModel();
        final DefaultListModel fModel = new DefaultListModel();

        plugins.setModel(cModel);
        projectTemplates.setModel(fModel);

        EntityManager em = PluginManager.getInstance().createFactoryManager(null);
        //java.util.List<Element.CategoryData> entities = em.getEntities("s3f.core.project.category.*", Element.CategoryData.class);
        java.util.List<ProjectTemplateCategory> entities = em.getEntities("s3f.core.project.template.*", ProjectTemplateCategory.class);
        for (final ProjectTemplateCategory c : entities) {
            cModel.addElement(c);
        }

        plugins.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ProjectTemplateCategory c = (ProjectTemplateCategory) value;
                label.setIcon(c.getIcon());
                return label;
            }
        });

        projectTemplates.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Project e = (Project) value;
                label.setIcon(PROJECT_ICON);
                return label;
            }
        });

        // Create an array components to be displayed.
        String msgString1 = "Please select the project type:";
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JPanel tmpPanel = new JPanel();
        GridLayout experimentLayout = new GridLayout(0, 2);
        tmpPanel.setLayout(experimentLayout);
        tmpPanel.add(new JScrollPane(plugins));
        tmpPanel.add(new JScrollPane(projectTemplates));
        panel.add(tmpPanel);
        panel.add(new JSeparator());

        JPanel tmpPanel2 = new JPanel();
        tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.LINE_AXIS));
        //tmpPanel.setLayout(layout);
        //tmpPanel2.setPreferredSize(new Dimension(350, 32));
        panel.add(tmpPanel2);
        tmpPanel.setPreferredSize(new Dimension(350, 120));
        final JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.red);

        Object[] array = {msgString1, tmpPanel, tmpPanel2, errorLabel};
        //Object[] array = {msgString1, panel};

        // Create an array specifying the number of dialog buttons
        // and their text.
        final Object[] options = {btnString1, btnString2};

        // Create the JOptionPane.
        optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, options, options[0]);

        final Object[] ret = new Object[]{null};

        plugins.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (plugins.getSelectedIndex() != -1) {
                        ProjectTemplateCategory ptc = (ProjectTemplateCategory) cModel.getElementAt(plugins.getSelectedIndex());
                        fModel.clear();
                        for (Project p : ptc.getTemplates()) {
                            fModel.addElement(p);
                        }
                        projectTemplates.setSelectedIndex(0);
                    }
                }
            }
        });

        projectTemplates.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (projectTemplates.getSelectedIndex() != -1) {
                        ret[0] = (Project) fModel.getElementAt(projectTemplates.getSelectedIndex());
                    }
                }
            }
        });

        plugins.setSelectedIndex(0);

        final JDialog dialog = new JDialog((Frame) null, "New project", true);

        dialog.setContentPane(optionPane);

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {

            }
        });

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();
                if (dialog.isVisible()
                        && (e.getSource() == optionPane)
                        && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                    //If you were going to check something
                    //before closing the window, you'd do
                    //it here.

                    if (optionPane.getValue() == JOptionPane.UNINITIALIZED_VALUE) {
                        return;
                    }

                    if (optionPane.getValue() != options[0]) {
                        ret[0] = null;
                        dialog.setVisible(false);
                        return;
                    }

                    if (errorLabel.getText().trim().isEmpty()) {
                        dialog.setVisible(false);
                        return;
                    }

                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                }
            }
        });

        dialog.pack();

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        return (Project) ret[0];
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import s3f.core.plugin.EntityManager;
import s3f.core.plugin.PluginManager;
import s3f.core.project.Element;
import s3f.core.project.Project;

/**
 *
 * @author antunes
 */
public class NewFileDialog extends JDialog {

    private Element selectedFileType;
    private JList categories;
    private JList filetypes;
    private JOptionPane optionPane;

    private String btnString1 = "Criar";
    private String btnString2 = "Cancel";

    /**
     * Returns null if the typed string was invalid; otherwise, returns the
     * string as the user entered it.
     */
    public Element getSelectedFileType() {
        return selectedFileType;
    }

    public static void main(String[] args) {
        new NewFileDialog(null).show();
    }

    /**
     * Creates the reusable dialog.
     */
    public NewFileDialog(Frame aFrame) {
        super(aFrame, true);

        setTitle("New Document");

        categories = new JList();
        filetypes = new JList();

        final DefaultListModel cModel = new DefaultListModel();
        final DefaultListModel fModel = new DefaultListModel();

        categories.setModel(cModel);
        filetypes.setModel(fModel);

        EntityManager em = PluginManager.getInstance().createFactoryManager(null);
        java.util.List<Element.CategoryData> entities = em.getEntities("s3f.core.project.category.*", Element.CategoryData.class);
        for (final Element.CategoryData c : entities) {
            cModel.addElement(c);
        }

        categories.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Element.CategoryData c = (Element.CategoryData) value;
                label.setIcon(c.getIcon());
                return label;
            }
        });

        filetypes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Element e = (Element) value;
                label.setIcon(e.getIcon());
                return label;
            }
        });

        categories.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (categories.getSelectedIndex() != -1) {
                        Element.CategoryData c = (Element.CategoryData) cModel.getElementAt(categories.getSelectedIndex());
                        fModel.clear();
                        for (Element el : c.getModels()) {
                            fModel.addElement(el);
                        }
                    }
                }
            }
        });

        filetypes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (filetypes.getSelectedIndex() != -1) {
                        selectedFileType = (Element) fModel.getElementAt(categories.getSelectedIndex());
                    }
                }
            }
        });

        // Create an array components to be displayed.
        String msgString1 = "Please select the document type:";
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JPanel tmpPanel = new JPanel();
        GridLayout experimentLayout = new GridLayout(0, 2);
        tmpPanel.setLayout(experimentLayout);
        tmpPanel.add(new JScrollPane(categories));
        tmpPanel.add(new JScrollPane(filetypes));
        panel.add(tmpPanel);
        panel.add(new JSeparator());

        JPanel tmpPanel2 = new JPanel();
        tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.LINE_AXIS));
        //tmpPanel.setLayout(layout);
        JLabel label = new JLabel("Nome: ");
        JTextField textfield = new JTextField();
        tmpPanel2.add(label);
        tmpPanel2.add(textfield);
        //tmpPanel2.setPreferredSize(new Dimension(300, 32));
        panel.add(tmpPanel2);
        tmpPanel.setPreferredSize(new Dimension(300, 180));

        Object[] array = {msgString1, tmpPanel, tmpPanel2};
        //Object[] array = {msgString1, panel};

        // Create an array specifying the number of dialog buttons
        // and their text.
        Object[] options = {btnString1, btnString2};

        // Create the JOptionPane.
        optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, options, options[0]);

        //optionPane.setPreferredSize(new Dimension(300, 400));
        // Make this dialog display it.
        setContentPane(optionPane);

        // Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window, we're going to
                 * change the JOptionPane's value property.
                 */
                optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
            }
        });

        pack();
    }

    public static Object[] createNewFileDialog() {
        final JList categories;
        final JList filetypes;
        final JOptionPane optionPane;

        String btnString1 = "Criar";
        String btnString2 = "Cancel";

        categories = new JList();
        filetypes = new JList();

        final DefaultListModel cModel = new DefaultListModel();
        final DefaultListModel fModel = new DefaultListModel();

        categories.setModel(cModel);
        filetypes.setModel(fModel);

        EntityManager em = PluginManager.getInstance().createFactoryManager(null);
        java.util.List<Element.CategoryData> entities = em.getEntities("s3f.core.project.category.*", Element.CategoryData.class);
        for (final Element.CategoryData c : entities) {
            cModel.addElement(c);
        }

        categories.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Element.CategoryData c = (Element.CategoryData) value;
                label.setIcon(c.getIcon());
                return label;
            }
        });

        filetypes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Element e = (Element) value;
                label.setIcon(e.getIcon());
                return label;
            }
        });

        final Project project = (Project) em.getProperty("s3f.core.project.tmp", "project");
        final ArrayList<String> names = new ArrayList<>();

        // Create an array components to be displayed.
        String msgString1 = "Please select the document type:";
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JPanel tmpPanel = new JPanel();
        GridLayout experimentLayout = new GridLayout(0, 2);
        tmpPanel.setLayout(experimentLayout);
        tmpPanel.add(new JScrollPane(categories));
        tmpPanel.add(new JScrollPane(filetypes));
        panel.add(tmpPanel);
        panel.add(new JSeparator());

        JPanel tmpPanel2 = new JPanel();
        tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.LINE_AXIS));
        //tmpPanel.setLayout(layout);
        JLabel label = new JLabel("Nome: ");
        final JTextField textfield = new JTextField();
        tmpPanel2.add(label);
        tmpPanel2.add(textfield);
        //tmpPanel2.setPreferredSize(new Dimension(300, 32));
        panel.add(tmpPanel2);
        tmpPanel.setPreferredSize(new Dimension(300, 120));
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

        final Object[] ret = new Object[]{null, ""};

        final CaretListener caretListener = new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                ret[1] = textfield.getText();
                optionPane.setInputValue(ret);
                if (ret[1].toString().isEmpty()) {
                    errorLabel.setText("Nome inválido");
                } else if (names.contains(ret[1].toString())) {
                    errorLabel.setText("Nome já existe");
                } else {
                    errorLabel.setText(" ");
                }
            }
        };

        categories.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (categories.getSelectedIndex() != -1) {
                        Element.CategoryData c = (Element.CategoryData) cModel.getElementAt(categories.getSelectedIndex());
                        fModel.clear();
                        for (Element el : c.getModels()) {
                            fModel.addElement(el);
                        }
                        filetypes.setSelectedIndex(0);
                        names.clear();
                        for (Element el : project.getElements(c.getName())) {
                            names.add(el.getName());
                        }
                    }
                    caretListener.caretUpdate(null);
                }
            }
        });

        filetypes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (filetypes.getSelectedIndex() != -1) {
                        Element el = (Element) fModel.getElementAt(filetypes.getSelectedIndex());
                        ret[0] = el;
                        textfield.setText(el.getName());
                        caretListener.caretUpdate(null);
                    }
                }
            }
        });

        textfield.addCaretListener(caretListener);

        categories.setSelectedIndex(0);

        final JDialog dialog = new JDialog((Frame) null, "Click a button", true);

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
                        ret[1] = null;
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

        return ret;
    }

    public static Object[] createNewProject() {
        final JList categories;
        final JList filetypes;
        final JOptionPane optionPane;

        String btnString1 = "Criar";
        String btnString2 = "Cancel";

        categories = new JList();
        filetypes = new JList();

        final DefaultListModel cModel = new DefaultListModel();
        final DefaultListModel fModel = new DefaultListModel();

        categories.setModel(cModel);
        filetypes.setModel(fModel);

        EntityManager em = PluginManager.getInstance().createFactoryManager(null);
        java.util.List<Element.CategoryData> entities = em.getEntities("s3f.core.project.category.*", Element.CategoryData.class);
        for (final Element.CategoryData c : entities) {
            cModel.addElement(c);
        }

        categories.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Element.CategoryData c = (Element.CategoryData) value;
                label.setIcon(c.getIcon());
                return label;
            }
        });

        filetypes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Element e = (Element) value;
                label.setIcon(e.getIcon());
                return label;
            }
        });

        final Project project = (Project) em.getProperty("s3f.core.project.tmp", "project");
        final ArrayList<String> names = new ArrayList<>();

        categories.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (categories.getSelectedIndex() != -1) {
                        Element.CategoryData c = (Element.CategoryData) cModel.getElementAt(categories.getSelectedIndex());
                        fModel.clear();
                        for (Element el : c.getModels()) {
                            fModel.addElement(el);
                        }
                        filetypes.setSelectedIndex(0);
                        names.clear();
                        for (Element el : project.getElements(c.getName())) {
                            names.add(el.getName());
                        }
                    }
                }
            }
        });

        // Create an array components to be displayed.
        String msgString1 = "Please select the document type:";
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JPanel tmpPanel = new JPanel();
        GridLayout experimentLayout = new GridLayout(0, 2);
        tmpPanel.setLayout(experimentLayout);
        tmpPanel.add(new JScrollPane(categories));
        tmpPanel.add(new JScrollPane(filetypes));
        panel.add(tmpPanel);
        panel.add(new JSeparator());

        JPanel tmpPanel2 = new JPanel();
        tmpPanel2.setLayout(new BoxLayout(tmpPanel2, BoxLayout.LINE_AXIS));
        //tmpPanel.setLayout(layout);
        JLabel label = new JLabel("Nome: ");
        final JTextField textfield = new JTextField();
        tmpPanel2.add(label);
        tmpPanel2.add(textfield);
        //tmpPanel2.setPreferredSize(new Dimension(300, 32));
        panel.add(tmpPanel2);
        tmpPanel.setPreferredSize(new Dimension(300, 120));
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

        final Object[] ret = new Object[]{null, ""};

        CaretListener caretListener = new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                ret[1] = textfield.getText();
                optionPane.setInputValue(ret);
                if (ret[1].toString().isEmpty()) {
                    errorLabel.setText("Nome inválido");
                } else if (names.contains(ret[1].toString())) {
                    errorLabel.setText("Nome já existe");
                } else {
                    errorLabel.setText(" ");
                }
            }
        };

        filetypes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (filetypes.getSelectedIndex() != -1) {
                        Element el = (Element) fModel.getElementAt(filetypes.getSelectedIndex());
                        ret[0] = el;
                        textfield.setText(el.getName());
                        textfield.setCaretPosition(0);
                    }
                }
            }
        });

        textfield.addCaretListener(caretListener);

        categories.setSelectedIndex(
                0);
        final JDialog dialog = new JDialog((Frame) null, "Click a button", true);

        dialog.setContentPane(optionPane);

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we
                    ) {

                    }
                });
        optionPane.addPropertyChangeListener(
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e
                    ) {
                        String prop = e.getPropertyName();
                        if (dialog.isVisible()
                        && (e.getSource() == optionPane)
                        && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                            //If you were going to check something
                            //before closing the window, you'd do
                            //it here.
                            if (optionPane.getValue() == options[1]) {
                                ret[0] = null;
                                ret[1] = null;
                                dialog.setVisible(false);
                            }
                            if (errorLabel.getText().trim().isEmpty()) {
                                dialog.setVisible(false);
                            }
                        }
                    }
                }
        );

        dialog.pack();

        dialog.setLocationRelativeTo(
                null);
        dialog.setVisible(
                true);

        return ret;
    }

}

//
//public class NewFileDialog {
//
//    private JFrame window;
//    private JList list;
//
//    public NewFileDialog() {
//        createAndShowUI();
//    }
//
//    private void createAndShowUI() {
//        //janela
//        window = new JFrame();
//        window.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//        window.getContentPane().setPreferredSize(new Dimension(400, 200));
//        window.setTitle(PluginManager.getText("pcw.frame.name"));
//
//        list = new JList();
//        list.setListData(new Integer[]{2, 3, 4});
//        list.setCellRenderer(new FileListCellRenderer());
//
//        Container pane = window.getContentPane();
//        pane.add(new JScrollPane(list));
//
//        //finaliza
//        window.pack();
//    }
//
//    public void show(boolean show) {
//        if (show) {
//            //centraliza a janela
//            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//            Rectangle frame = window.getBounds();
//            window.setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
//        }
//        //torna a janela visivel
//        window.setVisible(show);
//    }
//
//    public static void main(String[] args) {
//        new NewFileDialog().show(true);
//    }
//
//    /**
//     * A FileListCellRenderer for a File.
//     */
//    class FileListCellRenderer extends DefaultListCellRenderer {
//
//        private static final long serialVersionUID = -7799441088157759804L;
//        private FileSystemView fileSystemView;
//        private JLabel label;
//        private Color textSelectionColor = Color.BLACK;
//        private Color backgroundSelectionColor = Color.CYAN;
//        private Color textNonSelectionColor = Color.BLACK;
//        private Color backgroundNonSelectionColor = Color.WHITE;
//
//        FileListCellRenderer() {
//            label = new JLabel();
//            label.setOpaque(true);
//            fileSystemView = FileSystemView.getFileSystemView();
//        }
//
//        @Override
//        public Component getListCellRendererComponent(
//                JList list,
//                Object value,
//                int index,
//                boolean selected,
//                boolean expanded) {
//
////            label.setIcon(fileSystemView.getSystemIcon(file));
//            label.setText(value.toString());
//            label.setToolTipText("asdasd");
//
//            if (selected) {
//                label.setBackground(backgroundSelectionColor);
//                label.setForeground(textSelectionColor);
//            } else {
//                label.setBackground(backgroundNonSelectionColor);
//                label.setForeground(textNonSelectionColor);
//            }
//
//            return label;
//        }
//    }
//}

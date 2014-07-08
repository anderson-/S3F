/**
 * CodeEditorTab.java
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
package s3f.core.code;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rtextarea.RTextScrollPane;
import s3f.core.plugin.Configurable;
import s3f.core.plugin.Data;
import s3f.core.plugin.EntityManager;
import s3f.core.plugin.Plugabble;
import s3f.core.project.Editor;
import s3f.core.project.Element;
import s3f.core.project.ProjectTreeTab;
import s3f.core.project.editormanager.TextFile;
import s3f.core.script.Script;
import s3f.core.ui.tab.TabProperty;

public class CodeEditorTab implements Editor {

    private static final ImageIcon ICON = new ImageIcon(ProjectTreeTab.class.getResource("/resources/icons/fugue/script-text.png"));

    /**
     *
     * @param name ex: "function"
     * @param classpath ex:
     * "robotinterface.gui.panels.editor.syntaxtextarea.FunctionTokenMaker"
     */
    public static void addNewLanguage(String name, String classpath) {
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/" + name, classpath);
    }

    private final Data data;
    private final RSyntaxTextArea textArea;
    private DefaultCompletionProvider completionProvider = null;
    private Component tabComponent;
    //private static final ArrayList<Class> functionTokenClass = new ArrayList<>();
    //private static final ArrayList<FunctionToken> functionTokenInstances = new ArrayList<>();

    public CodeEditorTab() {
        this("plain");
    }

    /**
     *
     * @param lang "plain", "javascript" ou "c"...
     */
    public CodeEditorTab(String lang) {
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setCodeFoldingEnabled(true);
        setLanguage(lang);

        Color cstring = Color.decode("#f07818");
        Color cfunction = Color.decode("#6a4a3c");
        Color cvar = Color.decode("#cc333f");
        Color cblocks = Color.decode("#00a0b0");
        Color cfunc = Color.decode("#8fbe00");
        Color cdevices = Color.decode("#00C12B");

        //monstrinho:
        {
            Style styleDATA_TYPE = textArea.getSyntaxScheme().getStyle(Token.DATA_TYPE);//device
            styleDATA_TYPE.font = textArea.getFontForTokenType(Token.RESERVED_WORD);
            styleDATA_TYPE.foreground = cstring;
            styleDATA_TYPE.underline = true;
            Style styleFUNCTION = textArea.getSyntaxScheme().getStyle(Token.FUNCTION);//funções
            styleFUNCTION.font = textArea.getFontForTokenType(Token.RESERVED_WORD);
            styleFUNCTION.foreground = Color.GREEN.darker();
            Style styleRESERVED_WORD = textArea.getSyntaxScheme().getStyle(Token.RESERVED_WORD);//if else...
            //styleRESERVED_WORD.foreground = cfunction;
            Style styleRESERVED_WORD_2 = textArea.getSyntaxScheme().getStyle(Token.RESERVED_WORD_2);//var func
            styleRESERVED_WORD_2.foreground = cvar;
        }

        AutoCompletion autoCompletion = new AutoCompletion(getCompletionProvider());
        autoCompletion.setTriggerKey(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK));
        autoCompletion.setShowDescWindow(true);
        //ac.setAutoCompleteEnabled(true);
        //ac.setAutoActivationDelay(500);
        //ac.setAutoActivationEnabled(true);
        //ac.setAutoCompleteSingleChoices(true);
        autoCompletion.install(textArea);

        data = new Data("editorTab", "s3f.base.code", "Editor Tab");
        tabComponent = new RTextScrollPane(textArea);
        TabProperty.put(data, "Editor", ICON, "Editor de código", tabComponent);
    }

    public final void setLanguage(String lang) {
        if (lang.startsWith("text/")) {
            textArea.setSyntaxEditingStyle(lang);
        } else {
            textArea.setSyntaxEditingStyle("text/" + lang);
        }
    }

    @Override
    public Data getData() {
        return data;
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }

    public void setCompletionProvider(DefaultCompletionProvider completionProvider) {
        this.completionProvider = completionProvider;
    }

    public final DefaultCompletionProvider getCompletionProvider() {
        if (completionProvider == null) {
            completionProvider = new DefaultCompletionProvider();
        }
        return completionProvider;
    }

    /**
     * para o editor do jifi usar:
     *
     * updateCompletionProvider("s3f.jifi.functions.*","tokenInfo");
     * updateCompletionProvider("s3f.jifi.functions.*","tokenInfo");
     *
     *
     * @param em
     * @param path
     * @param property
     */
    public final void updateCompletionProvider(EntityManager em, String path, String property) {

        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        TokenMaker tokenMaker = atmf.getTokenMaker(textArea.getSyntaxEditingStyle());
        TokenMap tokenMap = null;
        if (tokenMaker instanceof ExtensibleTokenMaker) {
            tokenMap = ((ExtensibleTokenMaker) tokenMaker).getTokenMap();
        }

        for (Object o : em.getAllProperties(path, property, Object.class)) {
            if (o instanceof Completion) {
                completionProvider.addCompletion((Completion) o);
                if (tokenMap != null) {
                    //TODO: verificar!
                    tokenMap.put(((Completion) o).getReplacementText(), Token.FUNCTION);
                }
            } else {
                completionProvider.addCompletion(
                        new BasicCompletion(completionProvider, o.toString())
                );
                if (tokenMap != null) {
                    //TODO: verificar!
                    tokenMap.put(o.toString(), Token.DATA_TYPE);
                }
            }
        }
    }

//    /**
//     * Atualiza o CompletionProvider e adiciona todas as funções e dispositivos,
//     * disponiveis no momento para o destaque da sintaxe.
//     *
//     */
//    public static void updateCompletionProvider() {
//
//        TokenMap tokenMap = FunctionTokenMaker.getTokenMap();
//
//        /* provider = */ getCompletionProvider();
//        provider.clear();
//
//        for (Class c : PluginManager.getPluginsAlpha("robotinterface/plugin/cmdpack/plugin.txt", FunctionToken.class
//        )) {
//            int index = functionTokenClass.indexOf(c);
//            if (index == -1) {
//                functionTokenClass.add(c);
//                try {
//                    FunctionToken ft = (FunctionToken) c.newInstance();
//                    index = functionTokenInstances.size();
//                    functionTokenInstances.add(ft);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    return;
//                }
//            }
//            FunctionToken ft = functionTokenInstances.get(index);
//            Completion completion = ft.getInfo(provider);
//            if (completion != null) {
//                provider.addCompletion(completion);
//                tokenMap.put(ft.getToken(), Token.FUNCTION);
//            }
//        }
//
//        for (Class<? extends Device> c : RobotControlPanel.getAvailableDevices()) {
//            String str = c.getSimpleName();
//            try {
//                str = c.newInstance().getName();
//            } catch (Exception ex) {
//            }
//            provider.addCompletion(new BasicCompletion(provider, str));
//            tokenMap.put(str, Token.DATA_TYPE);
//        }
//    }
//    /**
//     * Create a simple provider that adds some Java-related completions.
//     */
//    private static CompletionProvider getCompletionProvider() {
//        if (completionProvider == null) {
//            // A DefaultCompletionProvider is the simplest concrete implementation
//            // of CompletionProvider. This provider has no understanding of
//            // language semantics. It simply checks the text entered up to the
//            // caret position for a match against known completions. This is all
//            // that is needed in the majority of cases.
//            completionProvider = new DefaultCompletionProvider();
//
//            // Add completions for all Java keywords. A BasicCompletion is just
//            // a straightforward word completion.
//            completionProvider.addCompletion(new BasicCompletion(completionProvider, "while"));
//
//            // Add a couple of "shorthand" completions. These completions don't
//            // require the input text to be the same thing as the replacement text.
//            completionProvider.addCompletion(new ShorthandCompletion(completionProvider, "rd",
//                    "read(Distance);", "read(Distance);"));
//        }
//        return completionProvider;
//    }
    @Override
    public void init() {

    }

    @Override
    public Plugabble createInstance() {
        return new CodeEditorTab();
    }

    @Override
    public void setContent(Element content) {
        if (content instanceof TextFile) {
            final TextFile textFile = (TextFile) content;
            textArea.setText(textFile.getText());
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    textFile.setText(textArea.getText());
                }
            });
            
            content.setCurrentEditor(this);
            
            switch (content.getCategoryData().getExtension()) {
                case "js":
                    setLanguage("javascript");
                    break;
                default:
                    setLanguage("plain");
                    break;
            }

            TabProperty.put(data, content.getName(), ICON, "Editor de código", tabComponent);
        }
    }

    @Override
    public Element getContent() {
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void selected() {

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.ui;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;
import s3f.core.plugin.Data;
import s3f.core.ui.tab.Tab;
import s3f.core.ui.tab.TabProperty;

/**
 *
 * @author anderson
 */
public class HTMLTab implements Tab {

    private Data data;

    public HTMLTab(String html, String style) {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(jEditorPane);
        
        HTMLEditorKit kit = new HTMLEditorKit();
        jEditorPane.setEditorKit(kit);
        
        StyleSheet styleSheet = kit.getStyleSheet();
        for (String rule : style.split("\n")){
            styleSheet.addRule(rule);
        }
//        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
//        styleSheet.addRule("h1 {color: blue;}");
//        styleSheet.addRule("h2 {color: #ff0000;}");
//        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
        
//        String htmlString = "<html>\n"
//                + "<body>\n"
//                + "<h1>Welcome!</h1>\n"
//                + "<h2>This is an H2 header</h2>\n"
//                + "<p>This is some sample text</p>\n"
//                + "<p><a href=\"https://github.com/anderson-\">teste</a></p>\n"
//                + "</body>\n";

        // create a document, set it on the jeditorpane, then add the html
        Document doc = kit.createDefaultDocument();
        jEditorPane.setDocument(doc);
        jEditorPane.setText(html);
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    JEditorPane pane = (JEditorPane) e.getSource();
                    if (e instanceof HTMLFrameHyperlinkEvent) {
                        HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                        HTMLDocument doc = (HTMLDocument) pane.getDocument();
                        doc.processHTMLFrameHyperlinkEvent(evt);
                    } else {
                        try {
                            pane.setPage(e.getURL());
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            }
        });

        data = new Data("messageTab", "s3f.core.ui", "MessageTab");
        TabProperty.put(data, "Welcome", null, "testet", scrollPane);
        data.setReference(this);
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void update() {
        
    }

    @Override
    public void selected() {
        
    }
}

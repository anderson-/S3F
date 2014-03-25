/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.ui.tab;

import java.awt.Component;
import javax.swing.Icon;
import s3f.base.plugin.AbstractData;

/**
 *
 * @author anderson
 */
public class TabData extends AbstractData {
    public static String TITLE = "title";
    public static String ICON = "icon";
    public static String TOOL_TIP = "toolTip";
    public static String COMPONENT = "component";
    public static String TAB_COMPONENT = "tabComponent";

    public TabData(String path, String name, String dependencies, String title, Icon icon, String toolTip, Component component) {
        super(path, name, dependencies);
        setProperty(TITLE, title);
        setProperty(ICON, icon);
        setProperty(TOOL_TIP, toolTip);
        setProperty(COMPONENT, component);
    }
    
    public TabData(String title, Icon icon, String toolTip, Component component) {
        super("s3f.teste", "TmpTab", AbstractData._EMPTY_FIELD);
        setProperty(TITLE, title);
        setProperty(ICON, icon);
        setProperty(TOOL_TIP, toolTip);
        setProperty(COMPONENT, component);
    }
    
}

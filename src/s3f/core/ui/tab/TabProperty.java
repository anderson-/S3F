/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.ui.tab;

import java.awt.Component;
import javax.swing.Icon;
import s3f.core.plugin.Data;

/**
 *
 * @author anderson
 */
public final class TabProperty {
    public static String TITLE = "title";
    public static String ICON = "icon";
    public static String TOOL_TIP = "toolTip";
    public static String COMPONENT = "component";
    public static String TAB_COMPONENT = "tabComponent";
    
    private TabProperty(){
        
    }
    
    public static void put(Data data, String title, Icon icon, String toolTip, Component component) {
        data.setProperty(TITLE, title);
        data.setProperty(ICON, icon);
        data.setProperty(TOOL_TIP, toolTip);
        data.setProperty(COMPONENT, component);
    }
    
//    public static void put(Data data, String title, Icon icon, String toolTip, Component component) {
//        super("s3f.teste", "TmpTab", Data._EMPTY_FIELD);
//        setProperty(TITLE, title);
//        setProperty(ICON, icon);
//        setProperty(TOOL_TIP, toolTip);
//        setProperty(COMPONENT, component);
//    }
//    
}

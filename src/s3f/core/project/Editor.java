/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import s3f.core.plugin.Configurable;
import s3f.core.plugin.Plugabble;
import s3f.core.ui.tab.Tab;

/**
 *
 * @author antunes
 */
public interface Editor extends Tab, Plugabble {

    public void setContent(Element content);

    public Element getContent();

}

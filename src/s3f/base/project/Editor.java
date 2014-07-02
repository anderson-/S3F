/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.project;

import s3f.base.plugin.Configurable;
import s3f.base.plugin.Plugabble;
import s3f.base.ui.tab.Tab;

/**
 *
 * @author antunes
 */
public interface Editor extends Tab, Plugabble {

    public void setContent(Element content);

    public Element getContent();

}

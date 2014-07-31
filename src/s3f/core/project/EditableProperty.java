/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import java.util.ArrayList;
import java.util.Collection;
import s3f.core.plugin.Data;

/**
 *
 * @author antunes
 */
public class EditableProperty {

    public static final String EDITORS = "editors";

    private EditableProperty() {

    }

    public static void put(Data data, Class<? extends Editor> editor) {
        Collection<Class<? extends Editor>> o = data.getProperty(EditableProperty.EDITORS);
        if (o == null) {
            o = new ArrayList<>();
            data.setProperty(EditableProperty.EDITORS, o);
        }
        if (!o.contains(editor)) {
            o.add(editor);
        }
    }

}

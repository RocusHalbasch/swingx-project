/*
 * InnerShadowPathEffect.java
 *
 * Created on October 18, 2006, 10:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author joshy
 */
public class InnerShadowPathEffect extends AbstractPathEffect {
    
    /** Creates a new instance of InnerShadowPathEffect */
    public InnerShadowPathEffect() {
        super();
        setRenderInsideShape(true);
        setBrushColor(Color.BLACK);
        setOffset(new Point(2,2));
    }
    
}
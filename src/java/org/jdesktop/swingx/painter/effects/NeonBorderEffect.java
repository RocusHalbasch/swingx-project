/*
 * NeonBorderEffect.java
 *
 * Created on October 20, 2006, 1:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 *
 * @author joshy
 */
public class NeonBorderEffect extends AbstractPathEffect {
    
    private Color edgeColor;
    private Color centerColor;
    private BorderPosition borderPosition = BorderPosition.Outside;
    
    public enum BorderPosition { Inside, Centered, Outside };
    
    
    public NeonBorderEffect() {
        this(Color.GREEN, Color.WHITE, 10);
    }
    
    /** Creates a new instance of NeonBorderEffect */
    public NeonBorderEffect(Color edgeColor, Color centerColor, int effectWidth) {
        super();
        setEffectWidth(effectWidth);
        this.setEdgeColor(edgeColor);
        this.setCenterColor(centerColor);
        this.setRenderInsideShape(false);
        this.setShouldFillShape(false);
        this.setOffset(new Point(0,0));
    }
    
    protected void paintBorderGlow(Graphics2D gfx, Shape clipShape, int width, int height) {
        
        /*
        // draw the effect
        for(float i=steps-1; i>=0; i=i-1f) {
            float brushWidth = i * getEffectWidth()/steps;
            gfx.setPaint(interpolateColor(i/steps,edgeColor,centerColor));
            gfx.setStroke(new BasicStroke(brushWidth,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            gfx.draw(clipShape);
        }*/
        
        /* // an interesting outline effect. stroke the shape with a wide brush
         * // then stroke again with slightly less wide one, then don't fill the middle
        for(int i=0; i<2; i++) {
            float brushWidth = (2-i)*5;
            p("widdth = " + brushWidth);
            gfx.setPaint(interpolateColor((float)(1-i), Color.BLACK, Color.WHITE));
            gfx.setStroke(new BasicStroke(brushWidth));
            gfx.draw(clipShape);
        }
         */
        gfx.translate(getOffset().getX(), getOffset().getY());
        gfx.setComposite(AlphaComposite.SrcOver);
        int steps = getEffectWidth();
        if(borderPosition == BorderPosition.Centered) {
            steps = steps/2;
        }
        float brushAlpha = 1f/steps;
        for(int i=0; i<steps; i++) {
            
            // make the brush width smaller each time until there is nothing left
            float brushWidth = (float)(steps+1-i);
            float half = steps/2;
            
            if(borderPosition == BorderPosition.Centered) {
                gfx.setPaint(interpolateColor((float)(steps-i)/steps, getEdgeColor(), getCenterColor()));
            } else {
                if(i<half) {
                    gfx.setPaint(interpolateColor((float)(half-i)/half, getEdgeColor(), getCenterColor()));
                } else {
                    gfx.setPaint(interpolateColor((float)(i-half)/half, getEdgeColor(), getCenterColor()));
                }
            }
            
            // to make the effect softer use a different stroke
            gfx.setStroke(new BasicStroke(brushWidth,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            //gfx.setStroke(new BasicStroke(brushWidth));
            gfx.draw(clipShape);
        }
        gfx.translate(-getOffset().getX(), -getOffset().getY());
        
    }
    
    public static void p(String str) {
        System.out.println(str);
    }
    
    protected Color interpolateColor(float t, Color start, Color end) {
        float[] partsS = start.getRGBComponents(null);
        float[] partsE = end.getRGBComponents(null);
        float[] partsR = new float[4];
        for(int i=0; i<4; i++) {
            partsR[i] = (partsS[i] - partsE[i])*t + partsE[i];
        }
        Color c = new Color(partsR[0],partsR[1],partsR[2],partsR[3]);
        return c;
    }
    
    public Color getEdgeColor() {
        return edgeColor;
    }
    
    public void setEdgeColor(Color edgeColor) {
        this.edgeColor = edgeColor;
    }
    
    public Color getCenterColor() {
        return centerColor;
    }
    
    public void setCenterColor(Color centerColor) {
        this.centerColor = centerColor;
    }
    
    public BorderPosition getBorderPosition() {
        return borderPosition;
    }
    
    public void setBorderPosition(BorderPosition borderPosition) {
        this.borderPosition = borderPosition;
        switch(borderPosition) {
            case Centered : 
                setShapeMasked(false);
                break;
            case Inside :
                setShapeMasked(true);
                setRenderInsideShape(true);
                break;
            case Outside :
                setShapeMasked(true);
                setRenderInsideShape(false);
                break;
        }
        if(borderPosition == BorderPosition.Centered) {
        }
    }
}
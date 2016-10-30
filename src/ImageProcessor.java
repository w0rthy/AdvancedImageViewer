package advancedimageviewer;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/*
    IMAGE PROCESSOR COLOR SETTINGS CONTENTS
    INDEX       NAME        DEFAULT VALUE
    0           Brightness  1.0
    1           Contrast    1.0
    2           Gamma       1.0
    3           Red Sat.    1.0
    4           Green Sat.  1.0
    5           Blue Sat.   1.0
    6           Iso. Col.   0.0
*/

public class ImageProcessor {
    
    public static float[] defaultColorSettings;
    public static float[] cs;
    
    //EVERYTHING IS DONE HERE
    public static int[] ProcessPixel(int[] c){
        c = Gamma(c, cs[2]);
        c = Contrast(c, cs[1]);
        c = Brightness(c, cs[0]);
        c = ColorSat(c, cs[3], cs[4], cs[5], (int)cs[6]);
        return c;
    }
    //YES RIGHT UP HERE, EVERYTHING
    
    public static void init(){
        defaultColorSettings = new float[]{1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,0.0f};
    }
    
    public static void setColorSettings(float[] csettings){
        cs = csettings;
    }
    
    public static BufferedImage ProcessImage(BufferedImage img) {
        WritableRaster r = (WritableRaster)img.getData();
        
        for(int x = 0; x < r.getWidth(); x++)
            for(int y = 0; y < r.getHeight(); y++){
                //BEGIN PROCESSING
                int[] c = new int[4];
                r.getPixel(x, y, c);
                c = ProcessPixel(c);
                r.setPixel(x, y, c);
            }
        
        BufferedImage i = new BufferedImage(r.getWidth(), r.getHeight(), BufferedImage.TYPE_INT_RGB);
        i.setData(r);
        return i;
    }
    
    public static double getGammaDiv(float a){
        return Math.pow(255d, a)/255d;
    }

    //PROCESSING METHODS BELOW PROCESSING METHODS BELOW PROCESSING METHODS BELOW
    //PROCESSING METHODS BELOW PROCESSING METHODS BELOW PROCESSING METHODS BELOW
    //PROCESSING METHODS BELOW PROCESSING METHODS BELOW PROCESSING METHODS BELOW
    
    //keep colors in bounds of 0-255
    private static int CClamp(int a){return Math.min(Math.max(a,0),255);}
    private static int CClamp(float a){return CClamp(Math.round(a));}
    private static int CClamp(double a){return CClamp(Math.round(a));}
    
    private static int[] Gamma(int[] c, float a) {
        if(a == 1f) return c;
        double div = getGammaDiv(a);
        c[0] = CClamp((Math.pow(c[0], a)/div));
        c[1] = CClamp((Math.pow(c[1], a)/div));
        c[2] = CClamp((Math.pow(c[2], a)/div));
        return c;
    }

    private static int[] Contrast(int[] c, float a) {
        if(a == 1f) return c;
        c[0] = CClamp((c[0]*a));
        c[1] = CClamp((c[1]*a));
        c[2] = CClamp((c[2]*a));
        return c;
    }

    private static int[] Brightness(int[] c, float a) {
        if(a == 1f) return c;
        int amt = (int)((a-1f)*255f);
        c[0] = CClamp(c[0]+amt);
        c[1] = CClamp(c[1]+amt);
        c[2] = CClamp(c[2]+amt);
        return c;
    }

    private static int[] ColorSat(int[] c, float rs, float gs, float bs, int iso) {
        if(rs == 1f && gs == 1f && bs == 1f && iso == 0) return c;
        c[0] = CClamp((c[0]*rs));
        c[1] = CClamp((c[1]*gs));
        c[2] = CClamp((c[2]*bs));
        if(iso != 0){
            c = IsoCol(c, iso);
        }
        return c;
    }
    
    private static int[] IsoCol(int[] c, int iso){
        
        int isoc = c[iso-1];
        
        int sum = (c[0]+c[1]+c[2])/3;
        c[0] = sum;
        c[1] = sum;
        c[2] = sum;
        
        c[iso-1] = Math.max(isoc,c[iso-1]);
        
        return c;
    }
    
    //SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL
    //SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL
    //SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL SPECIAL
    
    //CONTIGUOUS VARIANCE CONTIGUOUS VARIANCE CONTIGUOUS VARIANCE CONTIGUOUS VARIANCE
    //CONTIGUOUS VARIANCE CONTIGUOUS VARIANCE CONTIGUOUS VARIANCE CONTIGUOUS VARIANCE
    
    public static float VARIANCEMUL = 3f;
    public static float VARIANCEGAMMA = 1.85f;
    public static boolean VARIANCECOLORMODE = false;
    
    private static int[] getVariance(Raster r, int x, int y){
        
        double gammadiv = getGammaDiv(VARIANCEGAMMA);
        
        int[] c = new int[4];
        c[3] = 255;
        r.getPixel(x, y, c);
        
        int[] ctmp = new int[4];
        c[3] = 255;
        
        float diff = 0;
        float diffr = 0;
        float diffg = 0;
        float diffb = 0;
        
        for(int i = x-1; i <= x+1; i++)
            for(int j = y-1; j <= y+1; j++){
                if(i==x && j==y || i < 0 || j < 0 || i >= r.getWidth() || j >= r.getHeight())
                    continue;
                
                r.getPixel(i, j, ctmp);
                
                if(!VARIANCECOLORMODE){
                    diff+=Math.abs(c[0]-ctmp[0]);
                    diff+=Math.abs(c[1]-ctmp[1]);
                    diff+=Math.abs(c[2]-ctmp[2]);
                }else{
                    diffr=Math.abs(c[0]-ctmp[0]);
                    diffg=Math.abs(c[1]-ctmp[1]);
                    diffb=Math.abs(c[2]-ctmp[2]);
                }

            }
        
        int[] ret = new int[3];
        
        if(!VARIANCECOLORMODE){
            diff/=24f;
            if(VARIANCEGAMMA!=1.0f)
                diff = (float)(Math.pow(diff, VARIANCEGAMMA)/gammadiv);
            diff = (diff*VARIANCEMUL);
            diff = Math.min(diff, 255);
            ret = new int[]{(int)diff,(int)diff,(int)diff};
        }else{
            diffr/=2f;
            diffg/=2f;
            diffb/=2f;
            
            if(VARIANCEGAMMA != 1.0f){
                diffr = (float)(Math.pow(diffr,VARIANCEGAMMA)/gammadiv);
                diffg = (float)(Math.pow(diffg,VARIANCEGAMMA)/gammadiv);
                diffb = (float)(Math.pow(diffb,VARIANCEGAMMA)/gammadiv);
            }
            
            diffr = (diffr*VARIANCEMUL);
            diffg = (diffg*VARIANCEMUL);
            diffb = (diffb*VARIANCEMUL);
            
            diffr = Math.min(diffr, 255);
            diffg = Math.min(diffg, 255);
            diffb = Math.min(diffb, 255);
            
            ret = new int[]{(int)diffr,(int)diffg,(int)diffb};
        }
        
        return ret;
    }
    
    public static BufferedImage applyVarianceDots(BufferedImage img){
        Raster read = img.getData();
        WritableRaster r = (WritableRaster)img.getData();
        
        for(int x = 0; x < r.getWidth(); x++)
            for(int y = 0; y < r.getHeight(); y++){
                int[] v = getVariance(read, x, y);
                r.setPixel(x, y, new int[]{v[0],v[1],v[2]});
            }
        
        BufferedImage i = new BufferedImage(r.getWidth(), r.getHeight(), BufferedImage.TYPE_INT_RGB);
        i.setData(r);
        return i;
    }

}

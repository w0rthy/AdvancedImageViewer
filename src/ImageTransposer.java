package advancedimageviewer;

import java.awt.image.BufferedImage;

public class ImageTransposer {
    public static int TRANSPOSEMODE = 3;
    public static float DELAYMS = 0.001f;
    
    public static void transpose(AIVFrame frame, BufferedImage img){
        new Thread(){
            public void run(){
                if(TRANSPOSEMODE == 0 )
                    defaultInstant(frame,img);
                if(TRANSPOSEMODE == 1)
                    sequential(frame,img);
                if(TRANSPOSEMODE == 2)
                    sequentialVert(frame, img);
                if(TRANSPOSEMODE == 3)
                    random(frame, img);
            }
        }.start();
    }
    
    private static float rem = 0f;
    private static void sleep(float amt){
        rem+=amt-(int)amt;
        try{Thread.sleep((int)amt+(int)rem);}catch(Exception e){}
        rem-=(int)rem;
    }
    
    private static int[] rianr(int size){
        int[] a = new int[size];
        for(int i = 0; i < size; i++)
            a[i] = i;
        for(int i = 0; i < size; i++){
            int r = (int)(Math.random()*(size-1));
            int tmp = a[r];
            a[r] = a[i];
            a[i] = tmp;
        }
        
        return a;
    }
    
    public static void defaultInstant(AIVFrame frame, BufferedImage img){
        frame.vimg = img;
        frame.repaint();
    }
    
    public static void sequential(AIVFrame frame, BufferedImage img){
        for(int x = 0; x < img.getWidth(); x++)
            for(int y = 0; y < img.getHeight(); y++){
                sleep(DELAYMS);
                frame.vimg.setRGB(x, y, img.getRGB(x, y));
                frame.repaint();
            }
    }
    
    public static void sequentialVert(AIVFrame frame, BufferedImage img){
        for(int y = 0; y < img.getHeight(); y++)
            for(int x = 0; x < img.getWidth(); x++){
                sleep(DELAYMS);
                frame.vimg.setRGB(x, y, img.getRGB(x, y));
                frame.repaint();
            }
    }
    
    public static void random(AIVFrame frame, BufferedImage img){
        int[] xi = rianr(img.getWidth());
        int[] yi = rianr(img.getHeight());
        
        int offset = 0;
        int loc = 0;
        
        for(int i = 0; i < yi.length; i++){
            for(int j = 0; j < xi.length; j++){
                int y = yi[(loc+offset)%yi.length];
                int x = xi[j];
                sleep(DELAYMS);
                frame.vimg.setRGB(x, y, img.getRGB(x, y));
                frame.repaint();
                loc++;
            }
            loc = 0;
            offset++;
        }
    }
}

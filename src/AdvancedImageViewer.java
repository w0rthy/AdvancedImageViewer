package advancedimageviewer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class AdvancedImageViewer {
    
    public static void main(String[] args) {
        ImageProcessor.init();
        
        BufferedImage bi = OpenImage();
        if(bi == null)
            Open();
        else
            Open(bi);
    }
    
    public static void Open(){
        new AIVFrame();
    }
    
    public static void Open(BufferedImage bi){
        AIVFrame a = new AIVFrame();
        a.img = bi;
        a.vimg = a.img;
        a.formToImage();
    }

    private static String getFileName(String path){
        for(int i = path.length()-1; i > 0; i--)
            if(path.charAt(i)=='\\' || path.charAt(i)=='/')
                return path.substring(i);
        return path;
    }
    
    public static BufferedImage OpenImage(){
        BufferedImage bi;
        JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
        if(jfc.showOpenDialog(null)!=JFileChooser.APPROVE_OPTION)
            return null;
        File f = jfc.getSelectedFile();
        if(!f.exists())
            return null;
        if(getFileName(f.getAbsolutePath()).contains(".pci"))
            try {bi = pci_utilities.PCI_Read.PCI_Read(f);} catch (Exception ex) {return null;}
        else
            try{bi = ImageIO.read(f);}catch(Exception ex){return null;}
        
        bi = ensureRGB(bi);
        
        return bi;
    }
    
    public static BufferedImage ensureRGB(BufferedImage bi){
        BufferedImage img = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        
        return img;
    }

    static void SaveImage(BufferedImage img) {
        Object[] options = {"PCI", "PNG"};
        int a = JOptionPane.showOptionDialog(null, "Choose a file type.", "File Type", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if(a == JOptionPane.CLOSED_OPTION)
            return;
        JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
        if(jfc.showSaveDialog(null)!=JFileChooser.APPROVE_OPTION)
            return;
        File f = jfc.getSelectedFile();
        if(f == null)
            return;
        
        if(a==0)
            try{pci_utilities.PCI_Write.WritePCI(f, img);}catch(Exception e){System.err.println("Failed to write pci file");}
        else
            try{ImageIO.write(img, "png", f);}catch(Exception e){}
        
    }
    
}

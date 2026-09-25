import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class PanneauIP extends Box implements Additionnable {
    private JLabel aLabelIP;
    private AbstractList aListeLabelNom;
    
    PanneauIP() {
        super(BoxLayout.Y_AXIS);
        aListeLabelNom = new LinkedList();
    }
    
    public void asgnIP(String pIP) {
        aLabelIP = new JLabel(pIP);
        add(aLabelIP);
        
        setSize(getPreferredSize());
        
        System.out.println("P : " + getPreferredSize() + ", " + getMinimumSize());
    }
    
    public String reqIP() {
        return aLabelIP.getText();
    }
    
    public void additionneNom(String pNom) {
        JLabel lLabel = new JLabel("-> " + pNom);
        aListeLabelNom.add(lLabel);
        add(lLabel);
        
        setSize(getPreferredSize());
        
        System.out.println("P : " + getPreferredSize() + ", " + getMinimumSize());
    }
    
    public void enleveNom(String pNom) {
        Iterator lIterateur = aListeLabelNom.listIterator();
        while (lIterateur.hasNext()) {
            JLabel lLabel = (JLabel)lIterateur.next();
            
            if (lLabel.getText().compareTo("-> " + pNom) == 0) {
                remove(lLabel);
                lIterateur.remove();
                break;
            }
        }
        
        setSize(getPreferredSize());
    }

    public void changeNom(String pOriginal, String pNouveau) {
        Iterator lIterateur = aListeLabelNom.listIterator();
        while (lIterateur.hasNext()) {
            JLabel lLabel = (JLabel)lIterateur.next();
            
            if (lLabel.getText().compareTo(pOriginal) == 0) {
                lLabel.setText(pNouveau);
                break;
            }
        }
    }
    
    public boolean estMaitre() {
        return false;
    }

    public static void main(String pArgs[]) {
        JFrame lApplication = new JFrame("Espace MiniGame");
        
        PanneauIP lPanneauIP= new PanneauIP();
        lPanneauIP.asgnIP("215.243.3.134");
        lPanneauIP.additionneNom("Patou");
        lPanneauIP.additionneNom("Annie");
        lPanneauIP.additionneNom("Louif");
        
        lApplication.getContentPane().add(lPanneauIP, BorderLayout.CENTER);
        lApplication.addWindowListener
        (
        new WindowAdapter() {
            public void windowClosing(WindowEvent pEvenement) {
                System.exit(0);
            }
        }
        );
        
        lApplication.setSize(290, 265);
        lApplication.show();
    }
}
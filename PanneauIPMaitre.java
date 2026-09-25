import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class PanneauIPMaitre extends Box implements Additionnable {
    private JLabel aLabelIP;
    private AbstractList aListeRadioButtonNom;
    private ButtonGroup aGroupeBoutons;
    
    PanneauIPMaitre() {
        super(BoxLayout.Y_AXIS);
        aListeRadioButtonNom = new LinkedList();
        aGroupeBoutons = new ButtonGroup();
    }
    
    public void asgnIP(String pIP) {
        if (aLabelIP == null) {
            aLabelIP = new JLabel(pIP);
            add(aLabelIP);
        } else {
            aLabelIP.setText(pIP);
        }
        
        setSize(getPreferredSize());
    }
    
    public String reqIP() {
        return aLabelIP.getText();
    }
    
    public void additionneNom(String pNom) {
        JRadioButton lRadioButton = new JRadioButton(pNom);
        aListeRadioButtonNom.add(lRadioButton);
        add(lRadioButton);
        aGroupeBoutons.add(lRadioButton);
        
        setSize(getPreferredSize());
    }
    
    public void enleveNom(String pNom) {
        Iterator lIterateur = aListeRadioButtonNom.listIterator();
        while (lIterateur.hasNext()) {
            JRadioButton lRadioButton = (JRadioButton)lIterateur.next();
            
            if (lRadioButton.getText().compareTo(pNom) == 0) {
                remove(lRadioButton);
                aGroupeBoutons.remove(lRadioButton);
                lIterateur.remove();
                break;
            }
        }
        
        setSize(getPreferredSize());
    }
    
    public void changeNom(String pOriginal, String pNouveau) {
        Iterator lIterateur = aListeRadioButtonNom.listIterator();
        while (lIterateur.hasNext()) {
            JRadioButton lRadioButton = (JRadioButton)lIterateur.next();
            
            if (lRadioButton.getText().compareTo(pOriginal) == 0) {
                lRadioButton.setText(pNouveau);
                break;
            }
        }
    }
    
    public static void main(String pArgs[]) {
        JFrame lApplication = new JFrame("Espace MiniGame");
        
        PanneauIPMaitre lPanneauIP = new PanneauIPMaitre();

        lPanneauIP.asgnIP("215.243.3.134");
        lPanneauIP.additionneNom("Patou");
        lPanneauIP.additionneNom("Annie");
        lPanneauIP.additionneNom("Louif");
        lPanneauIP.changeNom("Annie", "Mananie");
        
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
    
    public boolean estMaitre() {
        return true;
    }
    
}

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class ZoneIP extends Box {
    AbstractList aListePanneaux;
    
    ZoneIP() {
        super(BoxLayout.Y_AXIS);
        
        aListePanneaux = new LinkedList();
    }
    
    public void additionne(Additionnable pAdditionnable) {
        aListePanneaux.add(pAdditionnable);
        add((Box)pAdditionnable);
        
        setSize(getPreferredSize());
    }
    
    public void enleve(Additionnable pAdditionnable) {
        ListIterator lIterateur = aListePanneaux.listIterator();
        
        while(lIterateur.hasNext()) {
            if (lIterateur.next() == pAdditionnable) {
                lIterateur.remove();
                break;
            }
        }
        
        setSize(getPreferredSize());
    }
    
    public Additionnable reqMaitre() {
        ListIterator lIterateur = aListePanneaux.listIterator();
        
        while(lIterateur.hasNext()) {
            Additionnable lAdditionnable = (Additionnable)lIterateur.next();
            if (lAdditionnable.estMaitre()) {
                return lAdditionnable;
            }
        }
        
        return null;
    }
    
}
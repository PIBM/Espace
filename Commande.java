import java.util.AbstractList;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Commande extends Object {
    private Planete aPlaneteSource, aPlaneteDestination;
    private int aNbVaisseau;
    
    Commande(Planete pSource, Planete pDestination, int pNbVaisseau) {
        aPlaneteSource = pSource;
        aPlaneteDestination = pDestination;
        aNbVaisseau = pNbVaisseau;
    }
    
    Commande(DataInputStream pFichier, AbstractList pListePlanete) throws IOException {
        if (pFichier.readUTF().compareTo("CMD") != 0) throw new IOException("On essai de lire une Commande alors que ça n'en est pas une !");
        aNbVaisseau = pFichier.readInt();
        aPlaneteSource = (Planete)pListePlanete.get(pFichier.readInt());
        aPlaneteDestination = (Planete)pListePlanete.get(pFichier.readInt());
    }
    
    public void enregistre(DataOutputStream pFichier) throws IOException {    
        pFichier.writeUTF("CMD");
        pFichier.writeInt(aNbVaisseau);
        pFichier.writeInt(aPlaneteSource.reqID());
        pFichier.writeInt(aPlaneteDestination.reqID());
    }
    
    public Planete reqPlaneteSource() {
        return aPlaneteSource;
    }
    
    public Planete reqPlaneteDestination() {
        return aPlaneteDestination;
    }
    
    public int reqNbVaisseau() {
        return aNbVaisseau;
    }
}
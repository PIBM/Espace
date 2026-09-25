import java.util.AbstractList;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Flotte extends Object {
    private int mNbVaisseaux, mTempsRestant;
    private Planete mDestination;
    Proprietaire mProprietaire; 
    
    Flotte(Proprietaire pProprietaire, int pNbVaisseaux, int pTemps, Planete pDestination) {
        mProprietaire = pProprietaire;
        mNbVaisseaux = pNbVaisseaux;
        mTempsRestant = pTemps;
        
        mDestination = pDestination;
    }

    Flotte(DataInputStream pFichier, AbstractList pListePlanete, AbstractList pListeProprietaire) throws IOException {
        if (pFichier.readUTF().compareTo("FLOTTE") != 0) throw new IOException("On essai de lire une flotte alors que ça n'en est pas une !");
        mProprietaire = (Proprietaire)pListeProprietaire.get(pFichier.readInt());
        mDestination = (Planete)pListePlanete.get(pFichier.readInt());
        mNbVaisseaux = pFichier.readInt();
        mTempsRestant = pFichier.readInt();
    }
    
    public void enregistre(DataOutputStream pFichier) throws IOException {
        pFichier.writeUTF("FLOTTE");
        pFichier.writeInt(mProprietaire.reqID());
        pFichier.writeInt(mDestination.reqID());
        pFichier.writeInt(mNbVaisseaux);
        pFichier.writeInt(mTempsRestant);
    }
    
    public boolean avanceTour() {
        if (--mTempsRestant <= 0) return true;
        return false;
    }
    
    public Planete reqDestination() {
        return mDestination;
    }
    
    public Proprietaire reqProprietaire() {
        return mProprietaire;
    }
    
    public int reqNbVaisseau() {
        return mNbVaisseaux;
    }
        
}

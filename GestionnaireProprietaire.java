import java.util.AbstractList;
import java.util.ListIterator;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class GestionnaireProprietaire extends Object {
    AbstractList mListeProprietaire;
    ListIterator mIterateurProprietaire;
    Proprietaire mProprietaireCourant;
    
    GestionnaireProprietaire(AbstractList pListeProprietaire) {
        mListeProprietaire = pListeProprietaire;        
        mProprietaireCourant = new Proprietaire();
    }
    
    public void charge(DataInputStream pFichier) throws IOException {
        if (pFichier.readUTF().compareTo("GESTIONPROPRIO") != 0) throw new IOException("On essai de lire un GestionnaireProprietaire alors que ça n'en est pas un !");
        int lID = pFichier.readInt();
        mIterateurProprietaire = mListeProprietaire.listIterator(lID);
        mProprietaireCourant = (Proprietaire)mIterateurProprietaire.next();
    }
    
    public void enregistre(DataOutputStream pFichier) throws IOException {
        pFichier.writeUTF("GESTIONPROPRIO");
        pFichier.writeInt(mProprietaireCourant.reqID());
    }
    
    public void initialise() {
        mIterateurProprietaire = mListeProprietaire.listIterator(0);
    }
    
    public boolean avanceProprietaire() {
        if (mIterateurProprietaire.hasNext()) {
            mProprietaireCourant = (Proprietaire)mIterateurProprietaire.next();
            return true;
        }
        
        return false;
    }
    
    public boolean reculeProprietaire() {
        if (mIterateurProprietaire.hasPrevious()) {
            mProprietaireCourant = (Proprietaire)mIterateurProprietaire.previous();
            return true;
        }
        
        return false;
    }

    public Proprietaire reqProprietaireCourant() {
        return mProprietaireCourant;
    }
    
    public void libereRessources() {
        mListeProprietaire = null;
        mIterateurProprietaire = null;
        mProprietaireCourant = null;
    }
}

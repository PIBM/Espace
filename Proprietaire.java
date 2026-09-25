import java.awt.Color;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Proprietaire extends Object {
    private Color mCouleur;
    private String mNom;
    private Race mRace;
    private boolean mEstJoueur;

    private int mNbVaisseau, mNbPlanete, mNbFlotte, mNbProduction, mID;

    Proprietaire(String pNom, Color pCouleur) {
        mNom = pNom;
        mCouleur = pCouleur;
        
        initialiseVariables();
    }
        
    Proprietaire() {
        initialiseVariables();
    }
    
    Proprietaire(DataInputStream pFichier) throws IOException {
        if (pFichier.readUTF().compareTo("PROPRIO") != 0) throw new IOException("On essai de lire un Proprietaire alors que ça n'en est pas un !");
        mNom = pFichier.readUTF();
        mID = pFichier.readInt();
        mEstJoueur = pFichier.readBoolean();
        
        mNbVaisseau = pFichier.readInt();
        mNbPlanete = pFichier.readInt();
        mNbFlotte = pFichier.readInt();
        mNbProduction = pFichier.readInt();
        
        mRace = new Race(pFichier);
        mCouleur = new Color(pFichier.readInt());
    }
    
    private void initialiseVariables() {
        mNbVaisseau = 0;
        mNbPlanete = 0;
        mNbFlotte = 0;
        mNbProduction = 0;
        mID = 0;
        
        mRace = null;
        mEstJoueur = true;
    }		public Race reqRace() {		return mRace;	}

    public void asgnID(int pID) {
        mID = pID;
    }
    
    public int reqID() {
        return mID;
    }
    
    public void enregistre(DataOutputStream pFichier) throws IOException {
        pFichier.writeUTF("PROPRIO");
        pFichier.writeUTF(mNom);
        pFichier.writeInt(mID);
        pFichier.writeBoolean(mEstJoueur);
        
        pFichier.writeInt(mNbVaisseau);
        pFichier.writeInt(mNbPlanete);
        pFichier.writeInt(mNbFlotte);
        pFichier.writeInt(mNbProduction);
        mRace.enregistre(pFichier);
        
        // Possible pour les non-joueurs
        if (mCouleur != null) {
            pFichier.writeInt(mCouleur.getRGB());                
        } else {
            pFichier.writeInt(0);
        }
    }
    
    public void asgnEstJoueur(boolean pValeur) {
        mEstJoueur = pValeur;
    }
    
    public boolean reqEstJoueur() {
        return mEstJoueur;
    }
    
    public void asgnRace(Race pRace) {
        mRace = pRace;
    }

    public String reqNom() {
        return mNom;
    }
    
    public void asgnNom(String pNom) {
        mNom = pNom;
    }
    
    public Color reqCouleur() {
        return mCouleur;
    }
    
    public void asgnCouleur(Color pCouleur) {
        mCouleur = pCouleur;
    }

    public void asgnNbVaisseau(int pNbVaisseau) {
        mNbVaisseau = pNbVaisseau;
    }
    
    public void asgnNbPlanete(int pNbPlanete) {
        mNbPlanete = pNbPlanete;
    }
    
    public void asgnNbFlotte(int pNbFlotte) {
        mNbFlotte = pNbFlotte;
    }
    
    public void asgnNbProduction(int pNbProduction) {
        mNbProduction = pNbProduction;
    }

    public void addNbVaisseau(int pNbVaisseau) {
        mNbVaisseau += pNbVaisseau;
    }
    
    public void addNbPlanete(int pNbPlanete) {
        mNbPlanete += pNbPlanete;
    }
    
    public void addNbFlotte(int pNbFlotte) {
        mNbFlotte += pNbFlotte;
    }

    public void addNbProduction(int pNbProduction) {
        mNbProduction += pNbProduction;
    }

    public int reqNbVaisseau() {
        return mNbVaisseau;
    }
    
    public int reqNbPlanete() {
        return mNbPlanete;
    }
    
    public int reqNbFlotte() {
        return mNbFlotte;
    }
    
    public int reqNbProduction() {
        return mNbProduction;
    }
    
    public double reqBonusAttaque() {
        if (mRace != null) {
            return mRace.reqAttaque();
        } else {
            return 1.0;
        }
    }
    
    public double reqBonusDefense() {
        if (mRace != null) {        
            return mRace.reqDefense();
        } else {
            return 1.0;
        }
    }
    
    public double reqBonusProduction() {
        if (mRace != null) {        
            return mRace.reqProduction();
        } else {
            return 1.0;
        }
    }
    
    public double reqBonusDeplacement() {
        if (mRace != null) {        
            return mRace.reqDeplacement();
        } else {
            return 1.0;
        }
    }
}

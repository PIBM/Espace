import javax.swing.JOptionPane;
import java.util.AbstractList;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Color;

public class Planete extends Object {
    private int mPositionX, mPositionY;
    private int mProduction, mNbVaisseau, mID;
    private double mBonusAttaque, mBonusDefense, mBonusProduction, mBonusDeplacement;
    private double mForce;
    private boolean mEstCommandee;
    private Color mCouleur;
    private String mNom;
    private Proprietaire mProprietaire;
    
    Planete(int pPositionX, int pPositionY, String pNom) {
        mNom = pNom;
        mPositionX = pPositionX;
        mPositionY = pPositionY;
        
        // mCouleur = new Color(150+(int)(Math.random() * 100), 150+(int)(Math.random() * 100), 150+(int)(Math.random() * 100));
        mCouleur = new Color(100+(int)(Math.random() * 100), 100+(int)(Math.random() * 100), 100+(int)(Math.random() * 100));
        mNbVaisseau = 0;
        mForce = 0.75 + (Math.random() * 0.5);
        mProduction = 6 + (int)Math.pow(Math.random() * 4, 2);

		mBonusAttaque = Math.random() * 0.05;
		mBonusDefense = Math.random() * 0.05;
		mBonusProduction = Math.random() * 0.05;
		mBonusDeplacement = Math.random() * 0.05;
		
        mID = 0;
        
        mEstCommandee = false;
    }
    
    Planete(DataInputStream pFichier, AbstractList pListeProprietaire) throws IOException {
        if (pFichier.readUTF().compareTo("PLANETE") != 0) throw new IOException("On essai de lire une Planete alors que ça n'en est pas une !");
        mNom = pFichier.readUTF();
        mID = pFichier.readInt();
        mProprietaire = (Proprietaire)pListeProprietaire.get(pFichier.readInt());
        mPositionX = pFichier.readInt();
        mPositionY = pFichier.readInt();
        mProduction = pFichier.readInt();
        mNbVaisseau = pFichier.readInt();
		mBonusAttaque = pFichier.readDouble();
		mBonusDefense = pFichier.readDouble();
		mBonusProduction = pFichier.readDouble();
		mBonusDeplacement = pFichier.readDouble();		
        mCouleur = new Color(pFichier.readInt());
        mForce = pFichier.readDouble();
        mEstCommandee = pFichier.readBoolean();
    }
    
    public void enregistre(DataOutputStream pFichier) throws IOException {
        pFichier.writeUTF("PLANETE");
        pFichier.writeUTF(mNom);
        pFichier.writeInt(mID);
        pFichier.writeInt(mProprietaire.reqID());        
        pFichier.writeInt(mPositionX);
        pFichier.writeInt(mPositionY);
        pFichier.writeInt(mProduction);
        pFichier.writeInt(mNbVaisseau);
		pFichier.writeDouble(mBonusAttaque);
		pFichier.writeDouble(mBonusDefense);
		pFichier.writeDouble(mBonusProduction);
		pFichier.writeDouble(mBonusDeplacement);
        pFichier.writeInt(mCouleur.getRGB());
        pFichier.writeDouble(mForce);
        pFichier.writeBoolean(mEstCommandee);
    }

    public void asgnID(int pID) {
        mID = pID;
    }
    
    public int reqID() {
        return mID;
    }
    
    public int reqPositionX() {
        return mPositionX;
    }
    
    public int reqPositionY() {
        return mPositionY;
    }
    
    public void paint(Graphics pGraphics) {
        pGraphics.setColor(mCouleur);
        pGraphics.fillOval(10 * mPositionX, 10*mPositionY, 10, 10);
    }
    
    public void asgnProprietaire(Proprietaire pProprietaire) {
        mProprietaire = pProprietaire;
        asgnCouleur();
    }
    
    public void asgnCouleur() {
        if (mProprietaire.reqEstJoueur()) {
            mCouleur = mProprietaire.reqCouleur();
        }
    }        
    
    public Proprietaire reqProprietaire() {
        return mProprietaire;
    }

    public void asgnProduction(int pProduction) {
        mProduction = pProduction;
    }
    
    public int reqProduction() {
        return mProduction;
    }
    
    public int reqNbVaisseau() {
        return mNbVaisseau;
    }
    
    public void soustraitNbVaisseau(int pNbVaisseau) {
        mNbVaisseau -= pNbVaisseau;
    }
    
    public void asgnNbVaisseau(int pNbVaisseau) {
        mNbVaisseau = pNbVaisseau;
    }
    
    public String reqNom() {
        return mNom;
    }
    
    public void asgnEstCommandee(boolean pValeur) {
        mEstCommandee = pValeur;
    }
    
    public boolean reqEstCommandee() {
        return mEstCommandee;
    }
    
    public int reqTemps(Planete pDestination) {
        int lDistanceX = pDestination.mPositionX - mPositionX;
        int lDistanceY = pDestination.mPositionY - mPositionY;
        
        double lDistance = Math.sqrt((lDistanceX * lDistanceX) + (lDistanceY * lDistanceY));

        int lTemps = 1+(int)(0.39 * lDistance / mProprietaire.reqBonusDeplacement());
        
        return lTemps;
    }
    
    public String toString() {
        String lString = "Planète '" + mNom + "', ";
        
        if (mProprietaire == null) 
            lString += "libre.";
        else
            lString += "appartenant à " + mProprietaire.reqNom() + " et produisant " + mProduction + " unités par tour.";
        
        return lString;
    }
        
    public void avanceTour() {
        mNbVaisseau += (int)(mProduction * mProprietaire.reqBonusProduction());
    }
    
    public String recoitFlotte(Flotte pFlotte) {
        String lMessage;
        if (mProprietaire == pFlotte.reqProprietaire()) {
            mNbVaisseau += pFlotte.reqNbVaisseau();
            lMessage = "Renfort arrivés à la planete " + mNom + ", propriété de " + mProprietaire.reqNom() + " !";
        } else {
            double lAttaque = pFlotte.reqNbVaisseau() * pFlotte.reqProprietaire().reqBonusAttaque();
            double lDefense = mNbVaisseau * mForce * mProprietaire.reqBonusDefense();
            
            String lNom;
            lNom = mProprietaire.reqNom();
            
            if (lAttaque > lDefense) {
                lMessage = pFlotte.reqProprietaire().reqNom() + " a pris la planète " + mNom + ", ancienne propriété de " + lNom + " !";
				if (mProprietaire.reqEstJoueur()) {
						
 				} else {
					Race lRace = pFlotte.reqProprietaire().reqRace();
					
					double lCoefAttaque = ((4+lRace.reqIntelligence()-lRace.reqAttaque())*lRace.reqIntelligence()/(3.9+lRace.reqIntelligence()));
					double lCoefDefense = ((4+lRace.reqIntelligence()-lRace.reqDefense())*lRace.reqIntelligence()/(3.9+lRace.reqIntelligence()));
					double lCoefProduction = ((4+lRace.reqIntelligence()-lRace.reqProduction())*lRace.reqIntelligence()/(3.9+lRace.reqIntelligence()));
					double lCoefDeplacement = ((4+lRace.reqIntelligence()-lRace.reqDeplacement())*lRace.reqIntelligence()/(3.9+lRace.reqIntelligence()));
					
					lRace.additionne(1, mBonusAttaque*lCoefAttaque);
					lRace.additionne(2, mBonusDefense*lCoefDefense);
					lRace.additionne(4, mBonusProduction*lCoefProduction);
					lRace.additionne(8, mBonusDeplacement*lCoefDeplacement);
				}
				
                mProprietaire = pFlotte.reqProprietaire();
                mCouleur = mProprietaire.reqCouleur();
                
                mNbVaisseau = (int)((lAttaque - lDefense*0.66)/mProprietaire.reqBonusAttaque());
                mEstCommandee = false;
            } else {
                mNbVaisseau = (int)((lDefense - (0.66 * lAttaque))/(mForce*mProprietaire.reqBonusDefense()));
                lMessage = pFlotte.reqProprietaire().reqNom() + " a attaqué la planète " + mNom + ", propriété de " + lNom + " !";
            }
        }
        return lMessage;
    }
    
    public boolean estDansRectangle(int lX1, int lY1, int lX2, int lY2) {
        return ((lX1 <= mPositionX) && (lX2 >= mPositionX) && (lY1 <= mPositionY) && (lY2 >= mPositionY));
    }
    
}
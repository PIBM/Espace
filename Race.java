import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Race extends Object {
    
    private String mNom;
    private double mPoints, mAttaque, mDefense, mProduction, mIntelligence, mDeplacement;
    private boolean mModifiable;
    
    Race(String pNom, double pPoints) {
        mNom = pNom;
        mAttaque = pPoints / 5;
        mDefense = mAttaque;
        mProduction = mAttaque;
        mDeplacement = mAttaque;
        mIntelligence = mAttaque;
        
        mModifiable = true;
    }
    
    Race(String pNom, double pAttaque, double pDefense, double pProduction, double pDeplacement, double pIntelligence) {
        mNom = pNom;
        
        mAttaque = pAttaque;
        mDefense = pDefense;
        mProduction = pProduction;
        mDeplacement = pDeplacement;
        mIntelligence = pIntelligence;
        
        mModifiable = false;
    }
    
    Race(DataInputStream pFichier) throws IOException {
        if (pFichier.readUTF().compareTo("RACE") != 0) throw new IOException("On essai de lire une Race alors que ça n'en est pas une !");
        mNom = pFichier.readUTF();
        mPoints = pFichier.readDouble();
        mAttaque = pFichier.readDouble();
        mDefense = pFichier.readDouble();
        mProduction = pFichier.readDouble();
        mDeplacement = pFichier.readDouble();
        mIntelligence = pFichier.readDouble();
        mModifiable = pFichier.readBoolean();
    }
    
    public void enregistre(DataOutputStream pFichier) throws IOException {
        pFichier.writeUTF("RACE");
        pFichier.writeUTF(mNom);
        pFichier.writeDouble(mPoints);
        pFichier.writeDouble(mAttaque);
        pFichier.writeDouble(mDefense);
        pFichier.writeDouble(mProduction);
        pFichier.writeDouble(mDeplacement);
        pFichier.writeDouble(mIntelligence);
        pFichier.writeBoolean(mModifiable);
    }
    
    public String toString() {
        return mNom;
    }
    
    public boolean reqModifiable() {
        return mModifiable;
    }
    
    public double reqNorme() {
        return (mAttaque + mDefense + mProduction + mDeplacement + mIntelligence);
    }
    
    public double reqMax() {
        return Math.max(Math.max(Math.max(mAttaque, mDefense), Math.max(mProduction, mDeplacement)), mIntelligence);
    }
    
    public double reqAttaque() {
        return mAttaque;
    }
    
    public double reqDefense() {
        return mDefense;
    }
    
    public double reqProduction() {
        return mProduction;
    }
    
    public double reqDeplacement() {
        return mDeplacement;
    }
    
    public double reqIntelligence() {
        return mIntelligence;
    }
    
    private double diminue(int pNum, double pDemande) {
        double lValeur = 0;
        
        if ((pNum & 1) != 0) {
            if (mAttaque - 0.25 > pDemande) {
                lValeur += pDemande;
                mAttaque -= pDemande;
            } else if (mAttaque > 0.25) {
                lValeur += mAttaque - 0.25;
                mAttaque = 0.25;
            }
        }
        
        if ((pNum & 2) != 0) {
            if (mDefense - 0.25 > pDemande) {
                lValeur += pDemande;
                mDefense -= pDemande;
            } else if (mDefense > 0.25) {
                lValeur += mDefense - 0.25;
                mDefense = 0.25;
            }
        }
        
        if ((pNum & 4) != 0) {
            if (mProduction - 0.25 > pDemande) {
                lValeur += pDemande;
                mProduction -= pDemande;
            } else if (mProduction > 0.25) {
                lValeur += mProduction - 0.25;
                mProduction = 0.25;
            }
        }
        
        if ((pNum & 8) != 0) {
            if (mDeplacement - 0.25 > pDemande) {
                lValeur += pDemande;
                mDeplacement -= pDemande;
            } else if (mDeplacement > 0.25) {
                lValeur += mDeplacement - 0.25;
                mDeplacement = 0.25;
            }
        }
        
        if ((pNum & 16) != 0) {
            if (mIntelligence - 0.25 > pDemande) {
                lValeur += pDemande;
                mIntelligence -= pDemande;
            } else if (mIntelligence > 0.25) {
                lValeur += mDeplacement - 0.25;
                mIntelligence = 0.25;
            }
        }
        
        return lValeur;
    }
    
    public void additionne(int pNum, double pValeur) {
        if ((pNum & 1) != 0) {
            mAttaque += pValeur;
        }
        
        if ((pNum & 2) != 0) {
            mDefense += pValeur;
        }
        
        if ((pNum & 4) != 0) {
            mProduction += pValeur;
        }
        
        if ((pNum & 8) != 0) {
            mDeplacement += pValeur;
        }
        
        if ((pNum & 16) != 0) {
            mIntelligence += pValeur;
        }
    }
    
    public void incAttaque() {
        if (mModifiable) mAttaque += diminue(30, 0.04);
    }
    
    public void decAttaque() {
        if (mModifiable) {
            double lValeur = 0;
            if ((mAttaque - 0.12) > 0.25) {
                lValeur = 0.12;
            } else if (mAttaque > 0.25) {
                lValeur = (mAttaque - 0.25);
            }
            additionne(30, lValeur/4);
            mAttaque -= lValeur;
        }
    }
    
    public void incDefense() {
        if (mModifiable) mDefense += diminue(29, 0.04);
    }
    
    public void decDefense() {
        if (mModifiable) {
            double lValeur = 0;
            if ((mDefense - 0.12) > 0.25) {
                lValeur = 0.12;
            } else if (mDefense > 0.25) {
                lValeur = (mDefense - 0.25);
            }
            additionne(29, lValeur/4);
            mDefense -= lValeur;
        }
    }
    
    public void incProduction() {
        if (mModifiable) mProduction += diminue(27, 0.04);
    }
    
    public void decProduction() {
        if (mModifiable) {
            double lValeur = 0;
            if ((mProduction - 0.12) > 0.25) {
                lValeur = 0.12;
            } else if (mProduction > 0.25) {
                lValeur = (mProduction - 0.25);
            }
            additionne(27, lValeur/4);
            mProduction -= lValeur;
        }
    }
    
    public void incDeplacement() {
        if (mModifiable) mDeplacement += diminue(23, 0.04);
    }
    
    public void decDeplacement() {
        if (mModifiable) {
            double lValeur = 0;
            if ((mDeplacement - 0.12) > 0.25) {
                lValeur = 0.12;
            } else if (mDeplacement > 0.25) {
                lValeur = (mDeplacement - 0.25);
            }
            additionne(23, lValeur/4);
            mDeplacement -= lValeur;
        }
    }
    
    public void incIntelligence() {
        if (mModifiable) mIntelligence += diminue(15, 0.04);
    }
    
    public void decIntelligence() {
        if (mModifiable) {
            double lValeur = 0;
            if ((mIntelligence - 0.12) > 0.25) {
                lValeur = 0.12;
            } else if (mIntelligence > 0.25) {
                lValeur = (mIntelligence - 0.25);
            }
            additionne(15, lValeur/4);
            mIntelligence -= lValeur;
        }
    }
    
}

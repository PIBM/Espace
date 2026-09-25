import java.awt.Graphics;
import java.awt.Color;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Stars extends Object {
    final static private Vecteur2D mCentre = new Vecteur2D(320,200);
    private Color mCouleur;
    private Vecteur2D mPosition, mVitesse;
    
    Stars(double pX, double pY, double pVX, double pVY) {
        int lV = 150 + (int)(Math.random() * 100);
        mCouleur = new Color(lV, lV, lV);
        
        mPosition = new Vecteur2D(pX, pY);
        mVitesse = new Vecteur2D(pVX, pVY);
    }

    Stars(double pVitesse, double pAngle) {
        double lPhi = 0;

        double lX = (Math.random() * 640);
        double lY = (Math.random() * 400);
        
        int lChoix = (int)(Math.random() * 4);
        
        if (lChoix == 0) {
            lY = 0;
        } else if (lChoix == 1) {
            lX = 0;
            lPhi = -Math.PI*0.5;
        } else if (lChoix == 2) {
            lY = 400;
            lPhi = Math.PI;            
        } else if (lChoix == 3) {
            lX = 640;
            lPhi = Math.PI*0.5;
        }
        
        mPosition = new Vecteur2D(lX, lY);
        mVitesse = new Vecteur2D(Math.cos(pAngle+lPhi)*pVitesse, Math.sin(pAngle+lPhi)*pVitesse);

        int lV = 150 + (int)(Math.random() * 100);
        mCouleur = new Color(lV, lV, lV);
    }
    
    Stars() {
        double lX = (Math.random() * 640);
        double lY = (Math.random() * 400);
        
        int lChoix = (int)(Math.random() * 4);
        
        if (lChoix == 0) lY = 0;
        else if (lChoix == 1) lX = 0;
        else if (lChoix == 2) lY = 400;
        else if (lChoix == 3) lX = 640;
        
        mPosition = new Vecteur2D(lX, lY);
        mVitesse = new Vecteur2D(mCentre.reqX() * (1.5*Math.random() + 0.25), mCentre.reqY() * (1.5*Math.random() + 0.25));
        mVitesse.soustrait(mPosition);
        mVitesse.normaliser();
        mVitesse.multiplier(Math.random()*5+0.5);
        
        int lV = 150 + (int)(Math.random() * 100);
        mCouleur = new Color(lV, lV, lV);
    }

    Stars(DataInputStream pFichier) throws IOException {
        if (pFichier.readUTF().compareTo("ETOILE") != 0) throw new IOException("On essai de lire une Stars alors que ça n'en est pas une !");
        mCouleur = new Color(pFichier.readInt());
        mPosition = new Vecteur2D(pFichier);
        mVitesse = new Vecteur2D(pFichier);
    }
    
    public void enregistre(DataOutputStream pFichier) throws IOException {
        pFichier.writeUTF("ETOILE");
        pFichier.writeInt(mCouleur.getRGB());
        mPosition.enregistre(pFichier);
        mVitesse.enregistre(pFichier);
    }
    
    public boolean paint(Graphics pGraphics) {
        mPosition.additionne(mVitesse);
        
        if (mPosition.estDansRectangle(0, 0, 639, 399)) {
            pGraphics.setColor(mCouleur);
            pGraphics.drawRect((int)mPosition.reqX(), (int)mPosition.reqY(), 1, 1);
            return true;
        }
        
        return false;
    }
}

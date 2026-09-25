import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Vecteur2D {
    private double mX, mY;

    Vecteur2D() {
        mX = 0;
        mY = 0;
    }
    
    Vecteur2D(double pX, double pY) {
        mX = pX;
        mY = pY;
    }
    
    Vecteur2D(Vecteur2D pVecteur) {
        mX = pVecteur.mX;
        mY = pVecteur.mY;
    }
    
    Vecteur2D(DataInputStream pFichier) throws IOException {
        if (pFichier.readUTF().compareTo("V2D") != 0) throw new IOException("On essai de lire un Vecteur2D alors que ça n'en est pas un !");
        mX = pFichier.readDouble();
        mY = pFichier.readDouble();
    }
    
    public void additionne(Vecteur2D pVecteur) {
        mX += pVecteur.mX;
        mY += pVecteur.mY;
    }
    
    public void soustrait(Vecteur2D pVecteur) {
        mX -= pVecteur.mX;
        mY -= pVecteur.mY;
    }
    
    public void multiplier(double pV) {
        mX *= pV;
        mY *= pV;
    }
    
    public double reqX() {
        return mX;
    }
    
    public double reqY() {
        return mY;
    }
    
    public void asgnX(double pX) {
        mX = pX;
    }
    
    public void asngY(double pY) {
        mY = pY;
    }
    
    public void normaliser() {
        double lNorme = Math.sqrt(mX*mX + mY*mY);
        
        mX /= lNorme;
        mY /= lNorme;
    }
    
    public boolean estDansRectangle(int pXMin, int pYMin, int pXMax, int pYMax) {
        if ((mX > pXMax) || (mX < pXMin)) return false;
        if ((mY > pYMax) || (mY < pYMin)) return false;
        return true;
    }
    
    public void enregistre(DataOutputStream pFichier) throws IOException {    
        pFichier.writeUTF("V2D");
        pFichier.writeDouble(mX);
        pFichier.writeDouble(mY);
    }
}

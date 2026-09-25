/*
 * Additionnable.java
 *
 * Created on 17 février 2002, 01:04
 */

/**
 *
 * @author  Administrateur
 * @version 
 */
public interface Additionnable {
    public String reqIP();
    
    public void additionneNom(String pNom);
    public void changeNom(String pOriginal, String pNouveau);
    public void enleveNom(String pNom);
    
    public boolean estMaitre();
}


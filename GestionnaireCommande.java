import java.util.AbstractList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class GestionnaireCommande extends Object {
    private AbstractList aListeCommande;
    private Espace aEspace;
    
    GestionnaireCommande() {
        aListeCommande = new LinkedList();
    }
    
    public void charge(DataInputStream pFichier, AbstractList pListePlanete) throws IOException {
        if (pFichier.readUTF().compareTo("GESTIONCOMMANDE") != 0) throw new IOException("On essai de lire un GestionnaireCommande alors que ça n'en est pas un !");

        aListeCommande.clear();
        
        int lNbCommande = pFichier.readInt();
        for (int i = 0; i<lNbCommande; ++i) {
            aListeCommande.add(new Commande(pFichier, pListePlanete));
        }
        
        if (pFichier.readUTF().compareTo("GESTIONCMDFIN") != 0) throw new IOException("Erreur lors de l'arret de la lecture d'un GestionnaireCommande : la fin n'était pas atteinte ou dépassée !");
    }
    
    public void enregistre(DataOutputStream pFichier) throws IOException {    
        pFichier.writeUTF("GESTIONCOMMANDE");
        
        pFichier.writeInt(aListeCommande.size());
        ListIterator lIterateur = aListeCommande.listIterator();
        while (lIterateur.hasNext()) {
            ((Commande)lIterateur.next()).enregistre(pFichier);
        }
        
        pFichier.writeUTF("GESTIONCMDFIN");
    }
    
    public void additionneCommande(Planete pPlaneteSource, Planete pPlaneteDestination, int pNbVaisseau) {
        // enlevePlanete(pPlaneteSource);
        
        aListeCommande.add(new Commande(pPlaneteSource, pPlaneteDestination, pNbVaisseau));
        pPlaneteSource.asgnEstCommandee(true);
    }
    
    public void enlevePlanete(Planete pPlanete) {
        ListIterator lIterateur = aListeCommande.listIterator();
        
        while(lIterateur.hasNext()) {
            Commande lCommande = (Commande)lIterateur.next();
            
            if (lCommande.reqPlaneteSource() == pPlanete) {
                lIterateur.remove();
            }
        }
        
        pPlanete.asgnEstCommandee(false);
    }
    
    public void envoieFlottes() {
        ListIterator lIterateur = aListeCommande.listIterator();
        
        while(lIterateur.hasNext()) {
            Commande lCommande = (Commande)lIterateur.next();
            if (lCommande.reqPlaneteSource().reqEstCommandee()) {
                aEspace.envoieFlotte(lCommande.reqPlaneteSource(), lCommande.reqPlaneteDestination(), lCommande.reqNbVaisseau());
            } else {
                lIterateur.remove();
            }
        }
    }
    
    public void asgnEspace(Espace pEspace) {
        aEspace = pEspace;
    }
    
    public void libereRessources() {
        aEspace = null;
        aListeCommande = null;
    }
}
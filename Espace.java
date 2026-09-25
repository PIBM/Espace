import java.util.AbstractList;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.zip.*;
import javax.swing.Timer;
import javax.swing.JOptionPane;


public class Espace extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    final static private Color aCouleurs[] = {Color.red, Color.blue, Color.green, Color.yellow, Color.magenta, Color.cyan, Color.orange, Color.pink};
    private double aPourcentConcentration;
    private int aRectangle, aRectangleDebut[], aRectangleFin[];
    private Timer aTimer;
    private Planete aEspace[][], aPlaneteSource, aPlaneteDestination, aPlaneteConcentration;
    private AbstractList aListePlanete;
    private AbstractList aListeFlotte;
    private AbstractList aListeProprietaire;
    private Vector aListeEtoiles;
    private AbstractList aListeNomPlanete;
    
    private GestionnaireProprietaire aGestionnaireProprietaire;
    private boolean aEnregistreCommande, aTrichageAlloue, aConcentre;
    private Donnees aDonnees;
    private Statistiques aStats;
    private GestionnaireCommande aGestionnaireCommande;

    public void setVariables() {
        setPreferredSize(new java.awt.Dimension(640, 400));
        setMinimumSize(new java.awt.Dimension(640, 400));
        
        setSize(640, 400);
        
        aRectangle = 0;
        aRectangleDebut = new int[2];
        aRectangleFin = new int[2];
    }
    
    public Espace() {
        setVariables();
        
        int lNbJoueurs = Integer.parseInt(JOptionPane.showInputDialog("Combien de joueurs ? [2,8]"));
        if (lNbJoueurs < 2) lNbJoueurs = 2;
        if (lNbJoueurs > 8) lNbJoueurs = 8;
        
        int lNbPlanetes = Integer.parseInt(JOptionPane.showInputDialog("Combien de planetes ? ["+ 4*lNbJoueurs + ", 200]"));
        if (lNbPlanetes < 4 * lNbJoueurs) lNbPlanetes = lNbJoueurs * 4;
        if (lNbPlanetes > 200) lNbPlanetes = 200;
        
        aListeFlotte = new Vector();
        aListeProprietaire = new Vector();
        aGestionnaireProprietaire = new GestionnaireProprietaire(aListeProprietaire);
        aGestionnaireCommande = new GestionnaireCommande();
        
        lisNomPlanetes();
        creePlanetes(lNbPlanetes, 64, 40);
        
        for (int i = 0; i<lNbJoueurs; ++i) {
            Proprietaire lProprietaire = new Proprietaire(JOptionPane.showInputDialog("Nom du joueur " + i + " :"), aCouleurs[i]);
            aListeProprietaire.add(lProprietaire);
            
            Planete lPlanete;
            do {
                lPlanete = (Planete)aListePlanete.get((int)(Math.random() * aListePlanete.size()));
            } while (lPlanete.reqProprietaire() != null);
            
            lPlanete.asgnProprietaire(lProprietaire);
            lPlanete.asgnProduction(10);
        }
        
        attribuePlanetesLibres();
        
        additionneEcouteurs();
        
        creeEtoiles(300);
        asgnRafraichissementEtoiles(50);
    }
    
    public Espace(ConfigurationEspace pConfig, Vector pVecteurProprietaire) {
        setVariables();        
        
        aListeFlotte = new Vector();
        aListeProprietaire = new Vector();
        aGestionnaireProprietaire = new GestionnaireProprietaire(aListeProprietaire);
        aGestionnaireCommande = new GestionnaireCommande();
        
        creeEtoiles(pConfig.reqNbEtoiles());
        
        randomize(pConfig, pVecteurProprietaire);
        
        additionneEcouteurs();
        
        System.out.println("Terminé la construction d'un Espace !");
    }
    
    public void asgnTrichage(boolean pAlloue) {
        aTrichageAlloue = pAlloue;
    }
    
    public boolean reqTrichage() {
        return aTrichageAlloue;
    }
    
    public boolean trichageAsgnProprietaire() {
        if (aTrichageAlloue) {
            if (aPlaneteDestination != null) {
                aPlaneteDestination.asgnProprietaire(aGestionnaireProprietaire.reqProprietaireCourant());
                repaint();
            } else {
                return false;
            }
        }
        
        return aTrichageAlloue;
    }
    
    public boolean trichageAsgnNbVaisseau(int pNbVaisseau) {
        if (aTrichageAlloue) {
            if (aPlaneteDestination != null) {
                aPlaneteDestination.asgnNbVaisseau(pNbVaisseau);
            } else {
                return false;
            }
        }
        
        return aTrichageAlloue;
    }
    
    public boolean trichageAsgnProduction(int pProduction) {
        if (aTrichageAlloue) {
            if (aPlaneteDestination != null) {
                aPlaneteDestination.asgnProduction(pProduction);
            } else {
                return false;
            }
        }
        
        return aTrichageAlloue;
    }
    
    public boolean trichageAddFlotte(int pNombre, int pTemps) {
        if (aTrichageAlloue) {
            if (aPlaneteDestination != null) {
                aListeFlotte.add(new Flotte(aGestionnaireProprietaire.reqProprietaireCourant(), pNombre, pTemps, aPlaneteDestination));
                aGestionnaireProprietaire.reqProprietaireCourant().addNbFlotte(1);
                aStats.affiche(aGestionnaireProprietaire.reqProprietaireCourant());
            } else {
                return false;
            }
        }
        
        return aTrichageAlloue;
    }
    
    public boolean trichageIncPropriete(int pNum, double pValeur) {
        if (aTrichageAlloue) {
            aGestionnaireProprietaire.reqProprietaireCourant().reqRace().additionne(pNum, pValeur);
            aStats.affiche(aGestionnaireProprietaire.reqProprietaireCourant());
        }
        
        return aTrichageAlloue;
    }
    
    public boolean trichageAvanceProprietaire() {
        if (aTrichageAlloue) {
            aEnregistreCommande = false;
            aDonnees.reinitialise();
            
            aPlaneteSource = null;
            aPlaneteDestination = null;
            
            aGestionnaireProprietaire.avanceProprietaire();
            aStats.affiche(aGestionnaireProprietaire.reqProprietaireCourant());
            aDonnees.affiche();
        }
        
        return aTrichageAlloue;
    }
    
    public boolean trichageReculeProprietaire() {
        if (aTrichageAlloue) {
            aEnregistreCommande = false;
            aDonnees.reinitialise();
            
            aPlaneteSource = null;
            aPlaneteDestination = null;
            
            aGestionnaireProprietaire.reculeProprietaire();
            aStats.affiche(aGestionnaireProprietaire.reqProprietaireCourant());
            aDonnees.affiche();
        }
        
        return aTrichageAlloue;
    }
    
    public void randomize(ConfigurationEspace pConfig, Vector pVecteurProprietaire) {
        lisNomPlanetes();
        
        if (aEspace != null) {
            for (int i = 0; i<aEspace.length; ++i) {
                for (int j = 0; j<aEspace[i].length; ++j) {
                    aEspace[i][j] = null;
                }
            }
        }
        
        if (aListePlanete != null) {
            aListePlanete.clear();
        }
        
        creePlanetes(pConfig.reqNbPlanetes(), 64, 40);
        
        aListeProprietaire.clear();
        for (int i = 0; i<pVecteurProprietaire.size(); ++i) {
            Proprietaire lProprietaire = (Proprietaire)pVecteurProprietaire.get(i);
            aListeProprietaire.add(lProprietaire);
            
            Planete lPlanete;
            do {
                lPlanete = (Planete)aListePlanete.get((int)(Math.random() * aListePlanete.size()));
            } while (lPlanete.reqProprietaire() != null);
            
            lPlanete.asgnProprietaire(lProprietaire);
            lPlanete.asgnProduction(10);
        }
        
        aListeProprietaire.add(attribuePlanetesLibres());
        
        if (pConfig.reqNbEtoiles() != 0) {
            asgnNbEtoiles(pConfig.reqNbEtoiles());
            asgnRafraichissementEtoiles(pConfig.reqVitesse());
        } else {
            asgnRafraichissementEtoiles(0);
        }
        
        System.out.println("Terminé la randomisation d'un Espace !");
    }
    
    public Proprietaire attribuePlanetesLibres() {
        Proprietaire lProprietaire = new Proprietaire();
        lProprietaire.asgnNom("certains pirates inconnus..");
        lProprietaire.asgnRace(new Race("Pirates", 0.0, 0.5, 0.5, 0.0, 0.0)); // Produisent moins d'unités et se défendent moins bien
        lProprietaire.asgnEstJoueur(false);
        
        Planete lPlanete;
        for (int i = 0; i<aListePlanete.size(); ++i) {
            lPlanete = (Planete)aListePlanete.get(i);
            if (lPlanete.reqProprietaire() == null) {
                lPlanete.asgnProprietaire(lProprietaire);
            }
        }
        
        return lProprietaire;
    }
    
    public void libereRessourcesMinimales() {
        aListePlanete = null;
        aListeFlotte = null;
        aListeProprietaire = null;
        aListeNomPlanete = null;
        aListeEtoiles = null;
        
        if (aGestionnaireCommande != null) {
            aGestionnaireCommande.libereRessources();
            aGestionnaireCommande = null;
        }
        
        if (aGestionnaireProprietaire != null) {
            aGestionnaireProprietaire.libereRessources();
            aGestionnaireProprietaire = null;
        }
        
        for (int i = 0; i<aEspace.length; ++i) {
            for (int j = 0; j<aEspace[i].length; ++j) {
                aEspace[i][j] = null;
            }
        }
        
    }
    
    public void libereRessources() {
        libereRessourcesMinimales();
        
        if (aTimer != null) {
            aTimer.stop();
            aTimer.removeActionListener(this);
            aTimer = null;
        }
        
        if (aDonnees != null) {
            aDonnees.libereRessources();
            aDonnees = null;
        }
        
        aStats = null;
        
        aPlaneteSource = null;
        aPlaneteDestination = null;
        
        removeMouseListener(this);
        removeMouseMotionListener(this);
    }
    
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Mort de l'Espace !");
    }
    
    private void additionneEcouteurs() {
        addMouseListener(this);
        addMouseMotionListener(this);
        
        aTrichageAlloue = false;
        
        ToolTipManager lManager = ToolTipManager.sharedInstance();
        lManager.setInitialDelay(20);
    }
    
    public void mousePressed(MouseEvent pEvenement) {
        // Asgn la source ..
        int lPosX = (int)(pEvenement.getX()/10);
        int lPosY = (int)(pEvenement.getY()/10);
        
        if ((lPosX < 64) && (lPosY < 40) && (lPosX >= 0) && (lPosY >= 0)) {
            Planete lPlanete = aEspace[lPosX][lPosY];
            if (lPlanete != null) {
                if (lPlanete.reqProprietaire() == aGestionnaireProprietaire.reqProprietaireCourant()) {
                    // On a le droit de la prendre comme source.
                    aPlaneteSource = lPlanete;
                    aDonnees.afficheSource(lPlanete);
                    
                    if (aPlaneteSource == aPlaneteDestination) {
                        aPlaneteDestination = null;
                        aDonnees.afficheDestination("");
                    }
                    
                    aEnregistreCommande = false;
                    if (pEvenement.getModifiers() == 4) {
                        // On dis d'enregistrer cette commande
                        aEnregistreCommande = true;
                    } else if (pEvenement.getModifiers() == 10) {
                        // On enleve la commande sur cette planete
                        if (lPlanete.reqEstCommandee()){
                            aGestionnaireCommande.enlevePlanete(lPlanete);
                            aDonnees.affiche();
                        }
                    }
                }
            }
        }

        if (((pEvenement.getModifiers() & 1) >= 0) && (lPosX >= 0) && (lPosX < 64) && (lPosY >= 0) && (lPosY < 40)) {
            if (aRectangle == 0) {
                aRectangleDebut[0] = lPosX;
                aRectangleDebut[1] = lPosY;
                aRectangle = 1;
            }
            repaint();
        }

        
        setToolTipText("");
    }
    
    public void mouseReleased(MouseEvent pEvenement) {
        // Asgn la destination ..
        int lPosX = (int)(pEvenement.getX()/10);
        int lPosY = (int)(pEvenement.getY()/10);
        
        if ((lPosX < 64) && (lPosY < 40) && (lPosX >= 0) && (lPosY >= 0)) {
            Planete lPlanete = aEspace[lPosX][lPosY];
            if ((lPlanete != null) && (lPlanete != aPlaneteSource)) {
                // On a le droit de la prendre comme destination
                aPlaneteDestination = lPlanete;
                aDonnees.afficheDestination(lPlanete);
            }
        }

        if (((pEvenement.getModifiers() & 1) > 0) && (lPosX >= 0) && (lPosX < 64) && (lPosY >= 0) && (lPosY < 40)) {
            if (aRectangle == 2) {
                aRectangleFin[0] = lPosX;
                aRectangleFin[1] = lPosY;
                aRectangle = 3;
            }
            repaint();
        }
        
        setToolTipText("");
    }
    
    public void mouseClicked(MouseEvent pEvenement) {
        setToolTipText("");
        
        int lPosX = (int)(pEvenement.getX()/10);
        int lPosY = (int)(pEvenement.getY()/10);
        
        if (((pEvenement.getModifiers() & 1) > 0) && (lPosX >= 0) && (lPosX < 64) && (lPosY >= 0) && (lPosY < 40)) {
            if (aRectangle == 3) {
                System.out.println("annule");
                aRectangle = 0;
            }
            repaint();
        }
    }
    
    public void afficheProprietes(Planete pPlanete, int pPositionX, int pPositionY) {
        String lMessage = "<HTML>";
        
        if (pPlanete.reqEstCommandee()) {
            lMessage += "<FONT COLOR=RED>";
        }
        
        lMessage += pPlanete.reqNom();
        
        if (pPlanete.reqEstCommandee()) {
            lMessage += "</FONT>";
        }
        
        if (pPlanete.reqProprietaire().reqEstJoueur()) {
            lMessage += "<BR>Proprietaire : " + pPlanete.reqProprietaire().reqNom() + "<BR>Nb Vaisseaux : " + pPlanete.reqNbVaisseau() +  " (" + ((int)(pPlanete.reqProduction() * pPlanete.reqProprietaire().reqBonusProduction())) + ")";
        }
        
        if ((aPlaneteSource != null) && (aPlaneteSource != pPlanete)) {
            lMessage += "<BR>Nb Tours : " + aPlaneteSource.reqTemps(pPlanete);
        }
        
        lMessage += "</HTML>";
        
        setToolTipText(lMessage);
    }
    
    public void mouseMoved(MouseEvent pEvenement) {
        int lPosX = (int)(pEvenement.getX()/10);
        int lPosY = (int)(pEvenement.getY()/10);
        
        if ((lPosX < 64) && (lPosY < 40) && (lPosX >= 0) && (lPosY >= 0)) {
            if ((aEspace[lPosX][lPosY] != null) && (aEspace[lPosX][lPosY] != aDonnees.reqPlaneteAffichee())) {
                // On est vis-a-vis une planete
                aDonnees.affichePlanete(aEspace[lPosX][lPosY]);
                afficheProprietes(aEspace[lPosX][lPosY], pEvenement.getX(), pEvenement.getY());
            }
        }
    }
    
    public void mouseDragged(MouseEvent pEvenement) {
        int lPosX = (int)(pEvenement.getX()/10);
        int lPosY = (int)(pEvenement.getY()/10);
        
        if ((lPosX < 64) && (lPosY < 40) && (lPosX >= 0) && (lPosY >= 0)) {
            if ((aEspace[lPosX][lPosY] != null) && (aEspace[lPosX][lPosY] != aDonnees.reqPlaneteAffichee())) {
                // On est vis-a-vis une planete
                aDonnees.affichePlanete(aEspace[lPosX][lPosY]);
                afficheProprietes(aEspace[lPosX][lPosY], pEvenement.getX(), pEvenement.getY());
            }
        }
        
        if (((pEvenement.getModifiers() & 1) > 0) && (lPosX >= 0) && (lPosX < 64) && (lPosY >= 0) && (lPosY < 40)) {
            if ((aRectangle == 1) || (aRectangle == 2)) {
                aRectangleFin[0] = lPosX;
                aRectangleFin[1] = lPosY;
                aRectangle = 2;
            }
            repaint();
        }
    }
    
    public void mouseExited(MouseEvent pEvenement) {
        setToolTipText("");
    }
    
    public void mouseEntered(MouseEvent pEvenement) {
        
    }
    
    public void asgnUtilitaires(Donnees pDonnees, Statistiques pStatistiques) {
        aDonnees = pDonnees;
        aStats = pStatistiques;
        
        aGestionnaireCommande.asgnEspace(this);
    }
    
    public boolean lisNomPlanetes() {
        aListeNomPlanete = new Vector();
        try {
            RandomAccessFile lFichier = new RandomAccessFile("starname.txt", "r");
            
            int lLongueur = (int)lFichier.length();
            byte lBytes[] = new byte[lLongueur];
            
            lFichier.readFully(lBytes);
            int lDebut = 0;
            for (int i = 0; i<lBytes.length; ++i) {
                // System.out.println("C : '" + (int)lBytes[i] + "'");
                if (lBytes[i] == 10) {
                    aListeNomPlanete.add(new String(lBytes, lDebut, i-lDebut-1, "US-ASCII"));
                    lDebut = i+1;
                }
            }
        } catch (Exception pException) {
            System.out.println("Exception avec le fichier de nom d'étoiles.");
            return false;
        }
        
        return true;
    }
    
    public boolean creePlanetes(int pNbPlanetes, int pLargeur, int pHauteur) {
        if (aListePlanete == null) {
            aListePlanete = new Vector();
        }
        
        if (aEspace == null) {
            aEspace = new Planete[pLargeur][pHauteur];
        } else {
            if ((aEspace.length != pLargeur) || (aEspace[0].length != pHauteur)) return false;
            if (((pLargeur * pHauteur) - aListePlanete.size()) < pNbPlanetes) return false;
        }
        
        for (int i = 0; i<pNbPlanetes; ++i) {
            int lX, lY, lN;
            do {
                lX = (int)(Math.random() * pLargeur);
                lY = (int)(Math.random() * pHauteur);
            } while (aEspace[lX][lY] != null);
            
            lN = (int)(Math.random() * aListeNomPlanete.size());
            aEspace[lX][lY] = new Planete(lX, lY, (String)aListeNomPlanete.get(lN));
            aListePlanete.add(aEspace[lX][lY]);
            aListeNomPlanete.remove(lN);
        }
        
        return true;
    }
    
    public void creeEtoiles(int pNbEtoiles) {
        if (aListeEtoiles == null) {
            aListeEtoiles = new Vector();
        } else {
            aListeEtoiles.clear();
        }
        
        additionneEtoiles(pNbEtoiles);
    }
    
    public void additionneEtoiles(int pNbEtoiles) {
        double lPosX, lPosY, lVitesse, lAngle;
        for (int i = 0; i<pNbEtoiles; ++i) {
            lPosX = 640 * Math.random();
            lPosY = 400 * Math.random();
            lVitesse = 0.75 + 5 * Math.random();
            lAngle = 2 * Math.PI * Math.random();
            
            aListeEtoiles.add(new Stars(lPosX, lPosY, lVitesse * Math.cos(lAngle), lVitesse * Math.sin(lAngle)));
        }
    }
    
    public void asgnNbEtoiles(int pNbEtoiles) {
        if (aListeEtoiles.size() <= pNbEtoiles) {
            additionneEtoiles(pNbEtoiles - aListeEtoiles.size());
        } else {
            aListeEtoiles.setSize(pNbEtoiles);
        }
        
        if (pNbEtoiles == 0) {
            asgnRafraichissementEtoiles(0);
        }
    }
    
    public void asgnRafraichissementEtoiles(int pTemps) {
        if (pTemps == 0) {
            if (aTimer != null) {
                aTimer.stop();
                aTimer = null;
            }
        } else {
            // System.out.println("Timer : " + pTemps);
            if (aTimer == null) {
                aTimer = new Timer(pTemps, this);
            } else {
                aTimer.setDelay(pTemps);
            }
            aTimer.start();
        }
    }
    
    public void reassigneCouleurs() {
        Iterator lIterateur = aListePlanete.listIterator();
        
        while (lIterateur.hasNext()) {
            ((Planete)lIterateur.next()).asgnCouleur();
        }
    }
    
    public void actionPerformed(ActionEvent pEvenement) {
        repaint();
    }
    
    public void concentrate(double pPourcent) {
        if (aPlaneteDestination != null) {
            aConcentre = true;
            aPlaneteConcentration = aPlaneteDestination;
            aPourcentConcentration = pPourcent;
        }
    }
    
    public void avanceJoueur() {
        // On avance au prochain joueur ..
        if ((aConcentre) && (aPlaneteConcentration != null)) {
            Proprietaire lProprietaire = aGestionnaireProprietaire.reqProprietaireCourant();
            for (int i = 0; i<aListePlanete.size(); ++i) {
                Planete lPlanete = (Planete)aListePlanete.get(i);
                if (lProprietaire == lPlanete.reqProprietaire()) {
                    envoieFlotte(lPlanete, aPlaneteConcentration, (int)(lPlanete.reqNbVaisseau()*aPourcentConcentration));
                }
            }
        }
        
        aEnregistreCommande = false;
        aConcentre = false;
        aDonnees.reinitialise();
        
        aPlaneteSource = null;
        aPlaneteDestination = null;
        aPlaneteConcentration = null;
        
        Proprietaire lProprietaire;
        boolean lAvanceTour = false;
        do {
            if (aGestionnaireProprietaire.avanceProprietaire()) {
                lProprietaire = aGestionnaireProprietaire.reqProprietaireCourant();
            } else {
                // On a terminé le tour ..
                lAvanceTour = true;
                lProprietaire = new Proprietaire();
            }
        } while ((!lProprietaire.reqEstJoueur()) || ((!lAvanceTour) && (lProprietaire.reqNbPlanete() == 0)));
        
        if (lAvanceTour) {
            avanceTour();
        } else {
            aStats.affiche(lProprietaire);
            aDonnees.affiche();
        }
    }
    
    public void avanceTour() {
        // Toutes les planetes produisent leur armée
        for (int i = 0; i<aListePlanete.size(); ++i) {
            ((Planete)aListePlanete.get(i)).avanceTour();
        }
        
        // Toutes les flottes avances et se battent
        String lMessage = new String("");
        Iterator lIterateur = aListeFlotte.listIterator();
        while (lIterateur.hasNext()) {
            Flotte lFlotte = (Flotte)lIterateur.next();
            if (lFlotte.avanceTour()) {
                // On est arrivé a destination !
                lMessage += lFlotte.reqDestination().recoitFlotte(lFlotte) + "\n";
                lIterateur.remove();
            }
        }
        
        if (lMessage.compareTo("") != 0) {
            JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea(lMessage, 10, 45)), "Résultats", JOptionPane.PLAIN_MESSAGE);
        }
        
        // On réaffiche les planetes afin de s'assurer qu'elles sont de la bonne couleur !
        repaint();
        
        // On remet les propriétés des proprios à 0..
        Iterator lIterateurProprietaire = aListeProprietaire.listIterator(0);
        while (lIterateurProprietaire.hasNext()) {
            Proprietaire lProprietaire = (Proprietaire)lIterateurProprietaire.next();
            
            lProprietaire.asgnNbPlanete(0);
            lProprietaire.asgnNbFlotte(0);
            lProprietaire.asgnNbVaisseau(0);
            lProprietaire.asgnNbProduction(0);
        }
        
        // On compte le Nb de Vaisseaux & de Planètes  ..
        for (int i = 0; i<aListePlanete.size(); ++i) {
            Planete lPlanete = (Planete)aListePlanete.get(i);
            Proprietaire lProprietaire = lPlanete.reqProprietaire();
            if (lProprietaire != null) {
                lProprietaire.addNbVaisseau(lPlanete.reqNbVaisseau());
                lProprietaire.addNbPlanete(1);
                lProprietaire.addNbProduction((int)(lPlanete.reqProduction()*lProprietaire.reqBonusProduction()));
            }
        }
        
        // On compte le Nb de Flottes ..
        for (int i = 0; i<aListeFlotte.size(); ++i) {
            ((Flotte)aListeFlotte.get(i)).reqProprietaire().addNbFlotte(1);
        }
        
        // On élimine les corps morts ..
        int lNbJoueursVivants = 0;
        lIterateurProprietaire = aListeProprietaire.listIterator(0);
        while (lIterateurProprietaire.hasNext()) {
            Proprietaire lProprietaire = (Proprietaire)lIterateurProprietaire.next();
            if ((lProprietaire.reqNbPlanete() == 0) && (lProprietaire.reqNbFlotte() == 0) && (lProprietaire.reqEstJoueur())) {
                JOptionPane.showMessageDialog(null, lProprietaire.reqNom() + " n'as plus de planète ni de flotte. Il a donc perdu.", "Looser !", JOptionPane.PLAIN_MESSAGE);
                lIterateurProprietaire.remove();
            } else if (lProprietaire.reqEstJoueur()) {
                ++lNbJoueursVivants;
            }
        }
        
        // S'il ne reste qu'un propriétaire vivant il a gagné ..
        if (lNbJoueursVivants == 1) {
            Proprietaire lProprietaire;
            lIterateurProprietaire = aListeProprietaire.listIterator(0);
            while(lIterateurProprietaire.hasNext()) {
                lProprietaire = (Proprietaire)lIterateurProprietaire.next();
                if (lProprietaire.reqEstJoueur()) {
                    JOptionPane.showMessageDialog(null, lProprietaire.reqNom() + " a gagné !", "Winner !", JOptionPane.PLAIN_MESSAGE);
                    aDonnees.arreteBoutonTermine();
                }
            }
        } else {
            // Sinon il faut avancer au prochain propriétaire ..
            aEnregistreCommande = false;
            aGestionnaireProprietaire.initialise();
            aGestionnaireCommande.envoieFlottes();
            avanceJoueur();
        }
    }
    
    public void envoieFlotte(Planete pPlaneteSource, Planete pPlaneteDestination, int pNbVaisseau) {
        if ((pPlaneteSource != null) && (pPlaneteDestination != null) && (pNbVaisseau > 0)) {
            if (pPlaneteSource.reqNbVaisseau() >= pNbVaisseau) {
                Proprietaire lProprietaire = pPlaneteSource.reqProprietaire();
                aListeFlotte.add(new Flotte(lProprietaire,
                pNbVaisseau,
                (int)(pPlaneteSource.reqTemps(pPlaneteDestination)),
                pPlaneteDestination));
                
                if (aEnregistreCommande) {
                    aGestionnaireCommande.additionneCommande(pPlaneteSource, pPlaneteDestination, pNbVaisseau);
                }
                
                pPlaneteSource.soustraitNbVaisseau(pNbVaisseau);
                aDonnees.affiche();
                
                lProprietaire.addNbVaisseau(-pNbVaisseau);
                lProprietaire.addNbFlotte(1);
                
                aStats.affiche(lProprietaire);
            }
        }
    }
    
    public void envoieFlotte(int pNbVaisseau) {
        envoieFlotte(aPlaneteSource, aPlaneteDestination, pNbVaisseau);
    }
    
    public boolean charge() {
        String lNomFichier = JOptionPane.showInputDialog("Entrez le nom du fichier");
        
        aListePlanete.clear();
        aListeProprietaire.clear();
        aListeFlotte.clear();
        aListeEtoiles.clear();
        
        try {
            DataInputStream lFichier = new DataInputStream(new GZIPInputStream(new FileInputStream(lNomFichier)));
            
            try {
                String lString = lFichier.readUTF();
                if (lString.compareTo("SAUVEGARDE DEBUT") == 0) {
                    
                    int lNbEtoiles = lFichier.readInt();
                    for (int i = 0; i<lNbEtoiles; ++i) {
                        aListeEtoiles.add(new Stars(lFichier));
                    }
                    
                    if (lNbEtoiles > 0) {
                        if (aTimer == null) {
                            aTimer = new Timer(lFichier.readInt(), this);
                        } else {
                            aTimer.setDelay(lFichier.readInt());
                        }
                    } else {
                        if (aTimer != null) {
                            aTimer.stop();
                            aTimer = null;
                        }
                    }
                    
                    int lNbProprietaire = lFichier.readInt();
                    for (int i = 0; i<lNbProprietaire; ++i) {
                        aListeProprietaire.add(new Proprietaire(lFichier));
                    }
                    
                    int lNbPlanete = lFichier.readInt();
                    for (int i = 0; i<lNbPlanete; ++i) {
                        aListePlanete.add(new Planete(lFichier, aListeProprietaire));
                    }
                    
                    int lNbFlotte = lFichier.readInt();
                    for (int i = 0; i<lNbFlotte; ++i) {
                        aListeFlotte.add(new Flotte(lFichier, aListePlanete, aListeProprietaire));
                    }
                    
                    aGestionnaireCommande.charge(lFichier, aListePlanete);
                    aGestionnaireProprietaire.charge(lFichier);
                    
                    aEspace = new Planete[lFichier.readInt()][lFichier.readInt()];
                    for (int i = 0; i<lNbPlanete; ++i) {
                        Planete lPlanete = (Planete)aListePlanete.get(i);
                        aEspace[lPlanete.reqPositionX()][lPlanete.reqPositionY()] = lPlanete;
                    }
                    
                    if (lFichier.readUTF().compareTo("SAUVEGARDE FIN") != 0) throw new IOException("On aurais termine de lire le fichier de sauvegarde mais la fin n'est pas atteinte !");
                    
                    aStats.affiche(aGestionnaireProprietaire.reqProprietaireCourant());
                    repaint();
                } else {
                    // Ce n'est pas un fichier de sauvegarde ..
                }
            } catch (IOException pEvenement) {
                System.out.println("Erreur lors de la lecture : " + pEvenement.toString());
            } finally {
                lFichier.close();
            }
        } catch (FileNotFoundException pEvenement) {
            System.out.println("Impossible d'ouvrir le fichier : " + pEvenement.toString());
            return false;
        } catch (IOException pEvenement) {
            System.out.println("Problème avec GZIP : " + pEvenement.toString());
            return false;
        }
        
        return true;
    }
    
    public void enregistre(String pNomFichier) {
        // Enregistre le fichier contenant toutes les informations
        if ((aListeEtoiles != null) && (aListeProprietaire != null) && (aListePlanete != null) && (aListeFlotte != null) && (aGestionnaireProprietaire != null) && (aGestionnaireCommande != null) && (aEspace != null)) {
            try {
                DataOutputStream lFichier = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(pNomFichier)));
                
                try {
                    lFichier.writeUTF("SAUVEGARDE DEBUT");
                    
                    // Etoiles
                    lFichier.writeInt(aListeEtoiles.size());
                    if (aListeEtoiles.size() != 0) {
                        Iterator lIterateur = aListeEtoiles.listIterator();
                        while (lIterateur.hasNext()) {
                            ((Stars)lIterateur.next()).enregistre(lFichier);
                        }
                        lFichier.writeInt(aTimer.getDelay());
                    }
                    
                    // Proprietaires
                    lFichier.writeInt(aListeProprietaire.size());
                    for (int i = 0; i <aListeProprietaire.size(); ++i) {
                        Proprietaire lProprietaire = (Proprietaire)aListeProprietaire.get(i);
                        lProprietaire.asgnID(i);
                        lProprietaire.enregistre(lFichier);
                    }
                    
                    // Planetes
                    lFichier.writeInt(aListePlanete.size());
                    for (int i = 0; i < aListePlanete.size(); ++i) {
                        Planete lPlanete = (Planete)aListePlanete.get(i);
                        lPlanete.asgnID(i);
                        lPlanete.enregistre(lFichier);
                    }
                    
                    // Flottes
                    lFichier.writeInt(aListeFlotte.size());
                    if (aListeFlotte.size() != 0) {
                        Iterator lIterateur = aListeFlotte.listIterator();
                        while (lIterateur.hasNext()) {
                            ((Flotte)lIterateur.next()).enregistre(lFichier);
                        }
                    }
                    
                    // Gestion Commandes
                    aGestionnaireCommande.enregistre(lFichier);
                    
                    // Gestion Proprietaire
                    aGestionnaireProprietaire.enregistre(lFichier);
                    
                    lFichier.writeInt(aEspace.length);
                    lFichier.writeInt(aEspace[0].length);
                    
                    lFichier.writeUTF("SAUVEGARDE FIN");
                } catch (IOException pEvenement) {
                    System.out.println("Erreur lors de l'écriture : " + pEvenement.toString());
                } finally {
                    lFichier.close();
                }
            } catch (FileNotFoundException pEvenement) {
                System.out.println("Impossible d'écrire le fichier : " + pEvenement.toString());
            } catch (IOException pEvenement) {
                System.out.println("Problème avec GZIP : " + pEvenement.toString());
            }
        }
    }
    
    public void paint(Graphics pGraphics) {
        pGraphics.setColor(Color.black);
        pGraphics.fillRect(0, 0, 900, 600);
        // pGraphics.fillRect(0, 0, 640, 400);
        
        if ((aListeEtoiles != null) && (aTimer != null)){
            for (int i = 0; i<aListeEtoiles.size(); ++i) {
                if (!((Stars)aListeEtoiles.get(i)).paint(pGraphics)) {
                    double lVitesse = 0.75 + 5 * Math.random();
                    double lAngle = Math.PI * Math.random();
                    
                    aListeEtoiles.set(i, new Stars(lVitesse, lAngle));
                }
            }
        }
     
        if (aRectangle >= 2) {
            int lX1, lX2, lY1, lY2;
            lX1 = Math.min(aRectangleDebut[0], aRectangleFin[0]);
            lX2 = Math.max(aRectangleDebut[0], aRectangleFin[0]);
            lY1 = Math.min(aRectangleDebut[1], aRectangleFin[1]);
            lY2 = Math.max(aRectangleDebut[1], aRectangleFin[1]);
            
            pGraphics.setColor(Color.lightGray);
            pGraphics.drawRect(lX1*10+5, lY1*10+5, (lX2-lX1)*10, (lY2-lY1)*10);
        }
        
        for (int i = 0; i<aListePlanete.size(); ++i) {
            ((Planete)(aListePlanete.get(i))).paint(pGraphics);
        }
    }
    
    public void envoieRectangle(double pPourcent) {
        if ((aPlaneteDestination != null) && (aRectangle == 3)) {
            int lX1, lX2, lY1, lY2;
            lX1 = Math.min(aRectangleDebut[0], aRectangleFin[0]);
            lX2 = Math.max(aRectangleDebut[0], aRectangleFin[0]);
            lY1 = Math.min(aRectangleDebut[1], aRectangleFin[1]);
            lY2 = Math.max(aRectangleDebut[1], aRectangleFin[1]);

            Proprietaire lProprietaire = aGestionnaireProprietaire.reqProprietaireCourant();
            for (int i = 0; i<aListePlanete.size(); ++i) {
                Planete lPlanete = (Planete)aListePlanete.get(i);
                if (lProprietaire == lPlanete.reqProprietaire()) {
                    if (lPlanete.estDansRectangle(lX1, lY1, lX2, lY2)) {
                        envoieFlotte(lPlanete, aPlaneteDestination, (int)(lPlanete.reqNbVaisseau()*pPourcent));
                    }
                }
            }
        }
    }
    
    /**
     * Procédure Test créant un objet d'affichage et le plaçant sur un JFrame
     *
     * @param args Arguments de la ligne de commande, non utilisé
     */
    public static void main(String args[]) {
        JFrame lApplication = new JFrame("Espace MiniGame");
        
        Donnees lDonnees = new Donnees();
        Statistiques lStats = new Statistiques();
        Espace lEspace = new Espace();
        
        lDonnees.asgnEspace(lEspace);
        lEspace.asgnUtilitaires(lDonnees, lStats);
        lEspace.avanceTour();
        
        lApplication.getContentPane().add(lEspace, BorderLayout.CENTER);
        lApplication.getContentPane().add(lDonnees, BorderLayout.SOUTH);
        lApplication.getContentPane().add(lStats, BorderLayout.NORTH);
        lApplication.addWindowListener
        (
        new WindowAdapter() {
            public void windowClosing(WindowEvent pEvenement) {
                System.exit(0);
            }
        }
        );
        
        lApplication.setSize(648, 508);
        lApplication.show();
    }
}


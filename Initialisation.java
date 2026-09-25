/*
 * Initialisation.java
 *
 * Created on 10 février 2002, 18:40
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
/**
 *
 * @author  Administrateur
 */
public class Initialisation extends javax.swing.JPanel {
    private Color aCouleurs[] = {Color.red, Color.blue, Color.green, Color.yellow, Color.magenta, Color.cyan, Color.orange, Color.pink, Color.lightGray, Color.white};
    private String aNomCouleurs[] = {"Rouge", "Bleu", "Vert", "Jaune", "Magenta", "Cyan", "Orange", "Rose", "Gris", "Blanc"};
    private boolean aCouleursActives[] = {true, true, true, true, true, true, true, true, true, true};
    private boolean aLockDisponible, aTrichageAlloue;
    
    private ConfigurationEspace aConfigEspace;
    private Donnees aDonnees;
    private Statistiques aStats;
    private Espace aEspace;
    
    private JFrame aApplication;
    
    public Initialisation() {
        initComponents();
        
        aConfigEspace = new ConfigurationEspace();
        aPaneProprietaire.addTab("Config", aConfigEspace);
        
        aLockDisponible = true;
        aTrichageAlloue = false;
    }
    
    public void initialiseConfigEspace() {
        aConfigEspace.asgnInitialisation(this);
    }
    
    public void creeConfigs(int pNum) {
        for (int i = 0; i<pNum; ++i) {
            ConfigurationJoueur lConfig = new ConfigurationJoueur();
            lConfig.asgnPaneEtOwner(aPaneProprietaire, this);
        }
        
        aConfigEspace.asgnNbJoueur(aPaneProprietaire.getTabCount()-1);
    }
    
    public void asgnTrichage(boolean pAlloue) {
        aTrichageAlloue = pAlloue;
    }
    
    public void enleveConfigs(int pNum) {
        for (int i = 0; i<pNum; ++i) {
            ((ConfigurationJoueur)aPaneProprietaire.getComponentAt(aPaneProprietaire.getTabCount()-1)).relacheCouleur();
            aPaneProprietaire.removeTabAt(aPaneProprietaire.getTabCount()-1);
        }
        
        aConfigEspace.asgnNbJoueur(aPaneProprietaire.getTabCount()-1);
    }
    
    public Vector reqCouleursLibres() {
        Vector lVecteur = new Vector();
        
        for (int i = 0; i<aCouleurs.length; ++i) {
            if (aCouleursActives[i]) {
                lVecteur.add(aNomCouleurs[i]);
            }
        }
        
        return lVecteur;
    }
    
    public Color utiliseCouleur(String pNouvelle, String pAncienne) {
        Color lCouleur = null;
        for (int i = 0; i<aCouleurs.length; ++i) {
            if (aNomCouleurs[i] == pNouvelle) {
                aCouleursActives[i] = false;
                lCouleur = aCouleurs[i];
            } else if (aNomCouleurs[i] == pAncienne) {
                aCouleursActives[i] = true;
            }
        }
        
        for (int i = 1; i < aPaneProprietaire.getTabCount(); ++i) {
            ((ConfigurationJoueur)aPaneProprietaire.getComponentAt(i)).reinitialiseChoixCouleurs();
        }
        
        if (aEspace != null) aEspace.repaint();
        
        return lCouleur;
    }
    
    public void reafficheCouleurs() {
        if (aEspace != null) {
            aEspace.reassigneCouleurs();
        }
    }        
    
    public void afficheNouveauTerrain() {
        Vector lVecteurProprietaire = new Vector();
        for (int i = 1; i<aPaneProprietaire.getTabCount(); ++i) {
            lVecteurProprietaire.add(((ConfigurationJoueur)aPaneProprietaire.getComponentAt(i)).reqProprietaire());
        }
        
        if (aApplication == null) {
            aApplication = new JFrame("Espace MiniGame");
            
            aDonnees = new Donnees();
            aDonnees.asgnEstDebute(false);
            
            aStats = new Statistiques();
            
            aApplication.addWindowListener
            (
            new WindowAdapter() {
                public void windowClosing(WindowEvent pEvenement) {
                    System.exit(0);
                }
            }
            );
            
            aApplication.setSize(648, 508);
            aApplication.show();
            
            aEspace = new Espace(aConfigEspace, lVecteurProprietaire);
            
            aDonnees.asgnEspace(aEspace);
            aEspace.asgnUtilitaires(aDonnees, aStats);
            aEspace.asgnTrichage(aTrichageAlloue);
            
            aApplication.getContentPane().add(aEspace, BorderLayout.CENTER);
            aApplication.getContentPane().add(aDonnees, BorderLayout.SOUTH);
            aApplication.getContentPane().add(aStats, BorderLayout.NORTH);
        } else {
            // Il faut juste refaire un randomize avec les nouvelles proprietes.
            aEspace.randomize(aConfigEspace, lVecteurProprietaire);
        }
        
        aStats.refresh();
        aEspace.repaint();
    }
    
    public void reaffiche() {
        if (aApplication != null) {
            afficheNouveauTerrain();
        }
    }
    
    public void asgnNbEtoiles(int pNbEtoiles) {
        if (aEspace != null) {
            aEspace.asgnNbEtoiles(pNbEtoiles);
            aEspace.repaint();
        }
    }
    
    public void asgnVitesseEtoiles(int pVitesse) {
        if (aEspace != null) {
            aEspace.asgnRafraichissementEtoiles(pVitesse);
            aEspace.repaint();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        aBoutonCharge = new javax.swing.JButton();
        aBoutonIncJoueur = new javax.swing.JButton();
        aBoutonDecJoueur = new javax.swing.JButton();
        aBoutonRandom = new javax.swing.JButton();
        aBoutonDemarrer = new javax.swing.JButton();
        aPaneProprietaire = new javax.swing.JTabbedPane();
        
        setLayout(new java.awt.BorderLayout());
        
        setPreferredSize(new java.awt.Dimension(293, 390));
        setMinimumSize(new java.awt.Dimension(293, 390));
        setMaximumSize(new java.awt.Dimension(293, 390));
        jPanel1.setPreferredSize(new java.awt.Dimension(293, 74));
        jPanel1.setMinimumSize(new java.awt.Dimension(293, 74));
        jPanel1.setMaximumSize(new java.awt.Dimension(293, 74));
        aBoutonCharge.setText("Charge");
        aBoutonCharge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aBoutonChargeActionPerformed(evt);
            }
        });
        
        jPanel1.add(aBoutonCharge);
        
        aBoutonIncJoueur.setText("Joueur+");
        aBoutonIncJoueur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aBoutonIncJoueurActionPerformed(evt);
            }
        });
        
        jPanel1.add(aBoutonIncJoueur);
        
        aBoutonDecJoueur.setText("Joueur-");
        aBoutonDecJoueur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aBoutonDecJoueurActionPerformed(evt);
            }
        });
        
        jPanel1.add(aBoutonDecJoueur);
        
        aBoutonRandom.setText("Randomize");
        aBoutonRandom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aBoutonRandomActionPerformed(evt);
            }
        });
        
        jPanel1.add(aBoutonRandom);
        
        aBoutonDemarrer.setText("Ok");
        aBoutonDemarrer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aBoutonDemarrerActionPerformed(evt);
            }
        });
        
        jPanel1.add(aBoutonDemarrer);
        
        add(jPanel1, java.awt.BorderLayout.SOUTH);
        
        add(aPaneProprietaire, java.awt.BorderLayout.CENTER);
        
    }//GEN-END:initComponents

    private void aBoutonChargeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aBoutonChargeActionPerformed
        if (aApplication == null) {
            afficheNouveauTerrain();
        }
        aDonnees.asgnEstDebute(true);
        aEspace.charge();
        
        lApplication.hide();
    }//GEN-LAST:event_aBoutonChargeActionPerformed
    
    private void aBoutonRandomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aBoutonRandomActionPerformed
        afficheNouveauTerrain();
    }//GEN-LAST:event_aBoutonRandomActionPerformed
    
    private void aBoutonDemarrerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aBoutonDemarrerActionPerformed
        if (aApplication == null) {
            afficheNouveauTerrain();
        }
        
        aDonnees.asgnEstDebute(true);
        aEspace.avanceTour();
        
        lApplication.hide();
    }//GEN-LAST:event_aBoutonDemarrerActionPerformed
    
    private void aBoutonDecJoueurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aBoutonDecJoueurActionPerformed
        if (aPaneProprietaire.getTabCount() > 3) {
            enleveConfigs(1);
        }
        
        reaffiche();
    }//GEN-LAST:event_aBoutonDecJoueurActionPerformed
    
    private void aBoutonIncJoueurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aBoutonIncJoueurActionPerformed
        if (aPaneProprietaire.getTabCount() <= 8) {
            creeConfigs(1);
        }
        
        reaffiche();
    }//GEN-LAST:event_aBoutonIncJoueurActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton aBoutonCharge;
    private javax.swing.JButton aBoutonIncJoueur;
    private javax.swing.JButton aBoutonDecJoueur;
    private javax.swing.JButton aBoutonRandom;
    private javax.swing.JButton aBoutonDemarrer;
    private javax.swing.JTabbedPane aPaneProprietaire;
    // End of variables declaration//GEN-END:variables
    
    private static JFrame lApplication;
    
    public static void main(String args[]) {
        lApplication = new JFrame("Espace MiniGame");
        
        Initialisation lInit = new Initialisation();
        lInit.initialiseConfigEspace();
        lInit.creeConfigs(2);
        
        if (args.length > 0) {
            if (args[0].compareTo("--allowCheats") == 0) lInit.asgnTrichage(true);
        }
        
        lApplication.getContentPane().add(lInit, BorderLayout.CENTER);
        lApplication.addWindowListener
        (
        new WindowAdapter() {
            public void windowClosing(WindowEvent pEvenement) {
                System.exit(0);
            }
        }
        );
        
        lApplication.setSize(293, 410);
        lApplication.show();
    }
}

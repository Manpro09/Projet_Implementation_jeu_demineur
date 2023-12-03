import java.awt.*;

import java.awt.event.*;

import javax.swing.*;

import java.nio.file.Files;

import java.nio.file.Path;

import java.util.Random;

import java.nio.file.*;

import java.util.List;

import java.util.Scanner;

import java.util.EnumMap;


public class Grille extends JFrame {
  
    private int largeur;
  
    private int hauteur;
  
    private int nbMines;
  
    private boolean[][] mines;
  
    private int[][] voisinsMines;
  
    private boolean[][] decouvertes;
  
    private boolean[][] marques;
  
    private JButton[][] cases;
  

    public Grille(int largeur, int hauteur, int nbMines) {
      
        this.largeur = largeur;
      
        this.hauteur = hauteur;
      
        this.nbMines = nbMines;
      
        this.mines = new boolean[largeur][hauteur];
      
        this.voisinsMines = new int[largeur][hauteur];
      
        this.decouvertes = new boolean[largeur][hauteur];
      
        this.marques = new boolean[largeur][hauteur];
      
        this.cases = new JButton[largeur][hauteur];
        
    }

    public void initialiser() {
      
        for (int x = 0; x < largeur; x++) {
          
            for (int y = 0; y < hauteur; y++) {
              
                mines[x][y] = false;
              
                decouvertes[x][y] = false;
              
                voisinsMines[x][y] = 0;
              
            }
        }
    }

  
    public void placerMines() {
      
        Random random = new Random();
      
        int minesPlacees = 0;

        while (minesPlacees < nbMines) {
          
            int x = random.nextInt(largeur);
          
            int y = random.nextInt(hauteur);

            if (!mines[x][y]) {
              
                mines[x][y] = true;
              
                minesPlacees++;

                for (int i = -1; i <= 1; i++) {
                  
                    for (int j = -1; j <= 1; j++) {
                      
                        if (x + i >= 0 && x + i < largeur && y + j >= 0 && y + j < hauteur) {
                            voisinsMines[x + i][y + j]++;
                        }
                    }
                }
            }
        }
    }

      public void decouvrirCase(int x, int y) {
        
        if (mines[x][y]) {
          
            afficherToutes();
          
            JOptionPane.showMessageDialog(null, "Vous avez perdu ! La partie est finie.", "Partie terminée", JOptionPane.INFORMATION_MESSAGE);
          
            System.exit(0);
        }
        
        decouvertes[x][y] = true;

        if (voisinsMines[x][y] == 0) {
          
            for (int i = -1; i <= 1; i++) {
              
                for (int j = -1; j <= 1; j++) {
                  
                    if (x + i >= 0 && x + i < largeur && y + j >= 0 && y + j < hauteur && !decouvertes[x + i][y + j]) {
                        decouvrirCase(x + i, y + j);
                    }
                }
            }
        }
        
        afficher();
    }

  public void marquerCase(int x, int y) {
    
    marques[x][y] = true;
    
    cases[x][y].setText("M");
}


public void afficher() {
  
    JFrame frame = new JFrame("Démineur");
  
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  
    frame.setLayout(new BorderLayout());

    // Ajout de la grille
    JPanel grillePanel = new JPanel(new GridLayout(hauteur, largeur));
  
    for (int y = 0; y < hauteur; y++) {
      
        for (int x = 0; x < largeur; x++) {
          
            JButton bouton = new JButton();
          
            bouton.setPreferredSize(new Dimension(50, 50));

            if (!decouvertes[x][y] && !marques[x][y]) {
                bouton.setText("");
              
            } else if (marques[x][y]) {
                bouton.setText("M");
              
            } else if (mines[x][y]) {
                bouton.setText("X");
              
            } else {
                bouton.setText(Integer.toString(voisinsMines[x][y]));
            }

            final int finalX = x;
            final int finalY = y;

            bouton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    decouvrirCase(finalX, finalY);
                }
            });

            grillePanel.add(bouton);
            cases[x][y] = bouton;
        }
    }
    // Ajout de la barre numérotée verticale
    JPanel numerotationVerticalePanel = new JPanel(new GridLayout(hauteur, 1));
    for (int y = 0; y < hauteur; y++) {
      
        JLabel label = new JLabel(Integer.toString(y));
      
        label.setHorizontalAlignment(JLabel.CENTER);
      
        numerotationVerticalePanel.add(label);
    }

    // Ajout de la barre numérotée horizontale
    JPanel numerotationHorizontalePanel = new JPanel(new GridLayout(1, largeur));
  
    for (int x = 0; x < largeur; x++) {
      
        JLabel label = new JLabel(Integer.toString(x));
      
        label.setHorizontalAlignment(JLabel.CENTER);
      
        numerotationHorizontalePanel.add(label);
    }

    // Ajout des panneaux à la frame
    frame.add(numerotationVerticalePanel, BorderLayout.WEST);
    frame.add(numerotationHorizontalePanel, BorderLayout.NORTH);
    frame.add(grillePanel, BorderLayout.CENTER);

    frame.pack();
    frame.setVisible(true);
}


    public void afficherToutes() {
      
    for (int y = 0; y < hauteur; y++) {
      
        for (int x = 0; x < largeur; x++) {
          
            decouvertes[x][y] = true;
          
            if (mines[x][y]) {
              
                cases[x][y].setText("X");
              
            } else {
              
               cases[x][y].setText(Integer.toString(voisinsMines[x][y]));
            }
        }
    }
}

    

 public boolean estTerminee() {
     
    for (int x = 0; x < largeur; x++) {
        for (int y = 0; y < hauteur; y++) {
            if (mines[x][y] && !marques[x][y]) {
                // une mine n'est pas marquée, donc le jeu n'est pas terminé
                return false;
            }
            if (!mines[x][y] && !decouvertes[x][y]) {
                // une case non minée n'a pas été découverte, donc le jeu n'est pas terminé
                return false;
            }
        }
    }

    // toutes les cases non-minées sont découvertes et toutes les cases minées sont marquées
    return true;
}


public void save(Path chemin) throws Exception {
  
        StringBuilder sb = new StringBuilder();
  
        sb.append(largeur).append(" ").append(hauteur).append(" ").append(nbMines).append("\n");
  
        for (int y = 0; y < hauteur; y++) {
          
            for (int x = 0; x < largeur; x++) {
              
                if (!decouvertes[x][y]) {
                  
                    // Si la case n'est pas découverte, on sauvegarde la valeur d'origine de la case
                    // avec le caractère "?"
                    sb.append("?").append(" ");
                  
                } else if (mines[x][y]) {
                    sb.append("X").append(" ");
                  
                } else {
                    sb.append(voisinsMines[x][y]).append(" ");
                }
            }
            sb.append("\n");
        }
        Files.write(chemin, sb.toString().getBytes());
    }

    public void load(Path chemin) throws Exception {
      
    List<String> lignes = Files.readAllLines(chemin);
      
    String[] entetes = lignes.get(0).split(" ");
      
    largeur = Integer.parseInt(entetes[0]);
      
    hauteur = Integer.parseInt(entetes[1]);
      
    nbMines = Integer.parseInt(entetes[2]);
      
    mines = new boolean[largeur][hauteur];
      
    voisinsMines = new int[largeur][hauteur];
      
    decouvertes = new boolean[largeur][hauteur];

    // initialisation des valeurs par défaut
    for (int y = 0; y < hauteur; y++) {
      
        for (int x = 0; x < largeur; x++) {
          
            mines[x][y] = false;
          
            voisinsMines[x][y] = 0;
        }
    }

    // chargement des cases du plateau
    for (int y = 0; y < hauteur; y++) {
      
        String[] cases = lignes.get(y + 1).split(" ");
      
        for (int x = 0; x < largeur; x++) {
          
            switch (cases[x]) {
                
                case "?":
                    decouvertes[x][y] = false;
                    break;
                case "X":
                    mines[x][y] = true;
                    decouvertes[x][y] = true;
                    break;
                
                default:
                    voisinsMines[x][y] = Integer.parseInt(cases[x]);
                    decouvertes[x][y] = true;
                    break;
            }
        }
    }
}

   public static void main(String[] args) {
     
    Scanner sc = new Scanner(System.in);
     
    System.out.println("Entrez la largeur de la grille : ");
    int largeur = sc.nextInt();
     
    System.out.println("Entrez la hauteur de la grille : ");
    int hauteur = sc.nextInt();
     
    System.out.println("Entrez le pourcentage de mines : ");
    int pourcentageMines = sc.nextInt();

    Grille grille = new Grille(largeur, hauteur, pourcentageMines);
     
    grille.initialiser();
     
    grille.placerMines();

    String choix = "";
     
    do {
        grille.afficher();
      
        System.out.println("\nQue voulez-vous faire ?");
      
        System.out.println("  m : marquer/démarquer une case");
      
        System.out.println("  s : sauvegarder l'état de la grille");
      
        System.out.println("  c : charger un état précédemment sauvegardé");
      
      

        choix = sc.next();

        if (choix.equals("m")) {
          
            System.out.println("Entrez les coordonnées de la case à marquer/démarquer :");
          
            int x = sc.nextInt();
            int y = sc.nextInt();
            grille.marquerCase(x, y);
          
        } else if (choix.equals("s")) { // nouvelle option pour sauvegarder l'état courant de la grille
            System.out.println("Entrez le chemin du fichier où sauvegarder l'état de la grille :");
          
            String chemin = sc.next();
          
            Path path = Paths.get(chemin);
          
            try {
                grille.save(path);
            } catch (Exception e) {
                System.out.println("Erreur lors de la sauvegarde de l'état de la grille.");
                e.printStackTrace();
            }
        } else if (choix.equals("c")) { // nouvelle option pour charger un état précédemment sauvegardé
          
            System.out.println("Entrez le chemin du fichier contenant l'état de la grille à charger :");
          
            String chemin = sc.next();
          
            Path path = Paths.get(chemin);
          
            try {
                grille.load(path);
                grille.afficher();
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'état de la grille.");
              
                e.printStackTrace();
            }
        }
    } while ( !grille.estTerminee());

    grille.afficher();
     
    if (grille.estTerminee()) {
      
        JOptionPane.showMessageDialog(null, "Félicitations, vous avez gagné !", "Partie terminée", JOptionPane.INFORMATION_MESSAGE);
    }
     
    System.out.println("Au revoir !");
}
}

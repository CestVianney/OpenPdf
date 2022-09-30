package org.openpdf;

import javax.swing.*;
import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Hello world!
 */
public class App {
    private static final String DATE_FORMATTER = "HH:mm:ss";
    private static final String SPACE = "\n----------------------------------------------------------------";

    public static void main(String[] args) {
        AtomicReference<String> directoryAchats = new AtomicReference<>("Choisir le fichier des achats");
        AtomicReference<String> directoryVentes = new AtomicReference<>("Choisir le fichier des ventes");
        JTextArea ta = new JTextArea();
        JFileChooser chooser = setChooser();
        JFrame frame = createFrame();
        JMenuBar mb = createMenuBar(chooser, directoryAchats, directoryVentes, ta);
        Desktop desktop = Desktop.getDesktop();

        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel labela = new JLabel("Achats");
        JLabel labelv = new JLabel("Ventes");
        JTextField tfa = new JTextField(5); // accepts upto 10 characters
        JTextField tfv = new JTextField(5); // accepts upto 10 characters
        JButton send = new JButton("Find");
        send.addActionListener(s -> {
            String path = tfa.getText().isEmpty() ? directoryVentes.get() : directoryAchats.get();
            String pdf = tfa.getText().isEmpty() ? tfv.getText() : tfa.getText();
            tfa.setText("");
            tfv.setText("");
            openFile(path, pdf, desktop, ta);
        });
        panel.add(labela); // Components Added using Flow Layout
        panel.add(tfa);
        panel.add(labelv); // Components Added using Flow Layout
        panel.add(tfv);
        panel.add(send);

        ta.append("   Veuillez définir les dossiers Achats et Ventes en sélectionnant un PDF parmi chaque dossier");
        ta.append(SPACE);

        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        JScrollPane sp = new JScrollPane(ta);
        frame.getContentPane().add(BorderLayout.CENTER, sp);
        frame.getRootPane().setDefaultButton(send);
        frame.setVisible(true);

    }

    private static JFileChooser setChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("select folder");
        chooser.setAcceptAllFileFilterUsed(false);
        return chooser;
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("OpenPDF");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(550, 400);
        return frame;
    }


    private static JMenuBar createMenuBar(JFileChooser chooser, AtomicReference<String> dirAchat, AtomicReference<String> dirVentes, JTextArea ta) {
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Selection des dossiers...");
        mb.add(m1);
        JMenuItem m11 = new JMenuItem("Achats");
        m11.addActionListener(al -> {
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                dirAchat.set(chooser.getCurrentDirectory().toString());
                ta.append("\n   Chemin achats = " + dirAchat.get());
                ta.append(SPACE);
            }
        });
        JMenuItem m12 = new JMenuItem("Ventes");
        m12.addActionListener(al -> {
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                dirVentes.set(chooser.getCurrentDirectory().toString());
                ta.append("\n   Chemin ventes = " + dirVentes.get());
                ta.append(SPACE);
            }
        });
        m1.add(m11);
        m1.add(m12);

        JMenu m2 = new JMenu("A propos");
        JMenuItem m21 = new JMenuItem("Utilisation");
        m21.addActionListener(m -> {
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, getInstructions(), "Utilisation", JOptionPane.INFORMATION_MESSAGE);
        });
        JMenuItem m22 = new JMenuItem("Credits");
        m22.addActionListener(m -> {
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "Vianney Charpentier - cestvianney@proton.me - 2022   ", "Credits", JOptionPane.INFORMATION_MESSAGE);
        });
        m2.add(m21);
        m2.add(m22);
        mb.add(m2);
        return mb;
    }

    private static void openFile(String dir, String file, Desktop desktop, JTextArea ta) {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter frmtr = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        File opened = new File(dir);
        try {
            File fileToOpen = Arrays.stream(Objects.requireNonNull(opened.listFiles()))
                    .filter(f -> f.toString().endsWith(file + ".pdf"))
                    .findFirst().get();
            desktop.open(fileToOpen);
            ta.append("\n   " + "[" + frmtr.format(ldt) + "]" + " Ouverture du fichier " + fileToOpen.toString());
        } catch (NoSuchElementException | IOException ioe) {
            ta.append("\nCe fichier n'existe pas !");
            ta.append(SPACE);
        }
    }

    private static String getInstructions() {
        return "                    UTILISATION DE L OUTIL :" +
                "\n - Cliquer en haut à gauche sur 'Selection des dossiers...' afin de définir la localisation des PDFs, " +
                "selon si on cherche les Achats, les Ventes, ou les deux" +
                "\n - Renseigner le numéro du fichier (ex : si AC00009876, alors renseigner 9876) dans l'une des cases " +
                "selon si l'on recherche un achat ou une vente, en bas de l'écran" +
                "\n - Le document s'ouvrira alors avec le lecteur PDF par défaut après avoir pressé Entrée ou cliqué sur 'Find'" +
                "\n - Les champs de recherche redeviendront vides et seront prêts à être réutilisés" +
                "\n" +
                "\n" +
                "                   CONDITIONS D UTILISATION :" +
                "\n - Chaque document a un numéro unique (si doublon, un seul PDF sera ouvert)" +
                "\n - Ne pas renseigner les deux champs (achat + vente). Ca ne plantera pas, mais ça n'en ouvrira qu'un seul !" +
                "\n - L'écran central de log est une zone de texte sur laquelle il est possible d'écrire, sans influence sur le programme" +
                "\n - Les dossiers contenant les PDFs sont à renseigner à chaque lancement du programme" +
                "\n - Le programme n'ouvre QUE les PDFs, mais est très facilement améliorable selon l'utilisation souhaitée"
                ;
    }
}

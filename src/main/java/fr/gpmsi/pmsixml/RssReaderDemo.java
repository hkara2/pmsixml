package fr.gpmsi.pmsixml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Démonstration très simple d'utilisation de RssReader.
 * Lit un fichier de RUMs/RSS et imprime pour chaque ligne (RUM) le numéro de RUM, le numéro de RSS, le numéro de dossier administratif.
 * 
 */
public class RssReaderDemo {

    /**
     * Mini application de démo pour analyser un fichier de RSS (groupés ou non)
     * @param args Il ne doit y avoir qu'un seul argument, le chemin du fichier à analyser
     * @throws IOException si erreur E/S
     * @throws FieldParseException Si erreur dans les métadonnées
     * @throws MissingMetafileException Si pas de métadonnées trouvées pour un RUM/RSS
     */
    public static void main(String[] args)
    throws IOException, FieldParseException, MissingMetafileException
    {
        RssReader rdr = new RssReader();
        String fichierRss = args[0];
        try (FileReader fr = new FileReader(fichierRss)) {
            BufferedReader br = new BufferedReader(fr);
            System.out.println("Num.dossier;Num.RSS;Num.RUM");
            String rss;
            int lineNr = 1;
            while ((rss = br.readLine()) != null) {
                FszGroup gn = (FszGroup) rdr.readOne(rss, lineNr);
                String nrss = gn.getChildField("NRSS").getValue();
                String nrum = gn.getChildField("NRUM").getValue();
                String nadl = gn.getChildField("NADL").getValue();
                System.out.println(nadl+";"+nrss+";"+nrum);
                lineNr++;
            }
        }
    }

}

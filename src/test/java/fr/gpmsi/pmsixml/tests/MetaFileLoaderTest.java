package fr.gpmsi.pmsixml.tests;

import java.io.IOException;

import fr.gpmsi.pmsixml.MetaFileLoader;

class MetaFileLoaderTest {

    /**
     * Ne marche que lorsque pmsixml est exécuté depuis un jar.
     * @throws IOException Si erreur E/S
     */
    public static void listResourceFiles()
            throws IOException
    {
        String[] resourceFiles = MetaFileLoader.listResourceFiles();
        System.out.println("Resource files :");
        for (String resourceFile : resourceFiles) {
            System.out.println(""+resourceFile);
        }
        System.out.println("End.");
        //fail("Not yet implemented");
    }

}

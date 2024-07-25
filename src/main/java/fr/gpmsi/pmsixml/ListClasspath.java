package fr.gpmsi.pmsixml;

import java.net.URL;

/**
 * Classe à usage interne, pour vérifier si 'root.txt' a été trouvé correctement.
 * C'est une façon de voir si les librairies sont bien accessible à d'autres moteurs notamment Groovy.
 */
public class ListClasspath {

  /**
   * 
   */
	public ListClasspath() {
	}

	/**
	 * 
	 */
	private void run() {
		URL u = ListClasspath.class.getResource("/root.txt");
		//in Eclipse : file:/C:/hkchse/dev/pmsixml/ec-classes/root.txt
		//in Cmd : jar:file:/C:/app/pmsixml/1.6/lib/pmsixml-1.6.jar!/root.txt
		System.out.println("URL for '/root.txt' : "+u);
	}
	
	/**
	 * 
	 * @param args Arguments
	 */
	public static void main(String[] args) {
		ListClasspath app = new ListClasspath();
		app.run();
	}

}

// --------------------------------------------------------
// Code generated by Papyrus Java
// --------------------------------------------------------

package modele;

import modele.Intersection;

/************************************************************/
/**
 * 
 */
public class Segment {
	/**
	 * 
	 * 
	 */
	public Intersection origine;
	/**
	 * 
	 */
	public Intersection fin;
	/**
	 * 
	 */
	private int longueur;
	/**
	 * 
	 */
	private String nom;
	public Segment(Intersection origine, Intersection fin, int longueur, String nom) {
		super();
		this.origine = origine;
		this.fin = fin;
		this.longueur = longueur;
		this.nom = nom;
	}
	public Segment() {
		super();
	}
	
	
	
};

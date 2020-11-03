// --------------------------------------------------------
// Code generated by Papyrus Java
// --------------------------------------------------------

package modele;


/**
 * 
 * Cette classe repr�sente un segment.
 * @author romain
 */
public class Segment {
	/**
	 * 
	 * Intersection d'origine du segment
	 */
	public Intersection origine;
	/**
	 * Intersection finale du segment
	 */
	public Intersection fin;
	/**
	 * Longueur du segment (en m�tres)
	 */
	private Double longueur;
	/**
	 * Nom du segment lorsqu'il existe
	 */	
	private String nom;
	
	
	public Segment(Intersection origine, Intersection fin, Double longueur, String nom) {
		super();
		this.origine = origine;
		this.fin = fin;
		this.longueur = longueur;
		this.nom = nom;
	}
	public Segment() {
		super();
	}
	public Intersection getOrigine() {
		return origine;
	}
	public void setOrigine(Intersection origine) {
		this.origine = origine;
	}
	public Intersection getFin() {
		return fin;
	}
	public void setFin(Intersection fin) {
		this.fin = fin;
	}
	public Double getLongueur() {
		return longueur;
	}
	public void setLongueur(Double longueur) {
		this.longueur = longueur;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	@Override
	public String toString() {
		return "Segment [origine=" + origine + ", fin=" + fin + ", longueur=" + longueur + ", nom=" + nom + "]";
	}
	
	
	
};

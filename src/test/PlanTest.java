package test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;

import modele.EnsembleRequete;
import modele.Intersection;
import modele.Itineraire;
import modele.Lecteur;
import modele.Livraison;
import modele.Plan;

public class PlanTest {

	private Plan plan ;
	private EnsembleRequete requetes;
	
	@Before
	public void init() {
		Lecteur lecteur = new Lecteur();
		this.plan =  lecteur.LirePlan("ressources/testMap.xml");
		this.requetes = lecteur.LireRequete("ressources/requestsTest.xml", plan) ;
	}
	
	@Test
	public void testSupprimerRequete() {
		Livraison livraison =  this.plan.getMatriceCout(requetes);
		Intersection intersection = livraison.getRequetes().getListeRequete().get(0).getPointDeLivraison();
		Livraison nouvelleLivraison = this.plan.supprimerRequete(livraison, intersection);
		
		assertTrue(nouvelleLivraison.getListeItineraires().size()==1);	
	}

	@Test
	public void testAjouterRequete() {
		Livraison livraison =  this.plan.getMatriceCout(requetes);

		Intersection debut = this.plan.getIntersectionById(Integer.toUnsignedLong(2));
		Intersection fin = this.plan.getIntersectionById(Integer.toUnsignedLong(4));
		Intersection depot = this.plan.getIntersectionById(Integer.toUnsignedLong(1));
		
		Livraison nouvelleLivraison = this.plan.ajouterRequete(livraison, depot, depot, debut, fin, Integer.toUnsignedLong(100), Integer.toUnsignedLong(100));
		
		assertTrue(nouvelleLivraison.getListeItineraires().size()==5);	
	}
		
	
	@Test
	public void testGetMatriceCout() {
		
		Livraison livraison = this.plan.getMatriceCout(requetes);
		assertTrue(livraison.getListeItineraires().size() == 3);
	}

	@Test
	public void testGetNomRue() {
		Intersection resultatRecherche = this.plan.getIntersectionById(Integer.toUnsignedLong(2));
		String NomRue  = this.plan.getNomRue(resultatRecherche);
		assertTrue(NomRue.equals("Rue de l'Abondance"));
	}




	@Test
	public void testAEtoile() {
		Intersection depart = this.plan.getIntersectionById(Integer.toUnsignedLong(2));
		Intersection arrivee = this.plan.getIntersectionById(Integer.toUnsignedLong(3));
		Itineraire itineraire = this.plan.aEtoile(depart, arrivee);
		assertTrue(itineraire.getListeIntersections().size() == 2);
	}
	



	@Test
	public void testGetIntersectionById() {
		Intersection resultatRecherche = this.plan.getIntersectionById(Integer.toUnsignedLong(2));
		assertTrue(resultatRecherche.getId() == 2);
	}

	@Test
	public void testLongitudeMax() {
		assertTrue(this.plan.longitudeMax() == 4.87);
	}

	@Test
	public void testLatitudeMax() {
		assertTrue(this.plan.latitudeMax() == 45.754);
	}

	@Test
	public void testLongitudeMin() {
		assertTrue(this.plan.longitudeMin() == 4.865);
	}

	@Test
	public void testLatitudeMin() {
		assertTrue(this.plan.latitudeMin() == 45.75);
	}


}

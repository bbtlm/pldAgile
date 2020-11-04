package controleur;


import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * 
 * @author Mario
 * Repr�sente l'�tat de l'application dans lequel l'itin�raire vient d'�tre calcul�, qui h�rite de l'�tat g�n�ral
 * 
 */


public class EtatItineraireCalcule extends Etat{

	public void choisirFichierPlan(Canvas planCanvas, Pane requetePane, Canvas requeteCanvas) {
		super.choisirFichierPlan(planCanvas, requetePane, requeteCanvas);
	}
	
	public void calculerItineraire() {
		//super.calculerItineraire();
		System.out.println("Charger d'abord un plan");
	}
	
	public void choisirFichierRequetes() {
		//super.choisirFichierRequetes();
	}
	
}

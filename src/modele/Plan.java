// --------------------------------------------------------
// Code generated by Papyrus Java
// --------------------------------------------------------

package modele;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;


/************************************************************/
/**
 * 
 */
public class Plan {
	/**
	 * 
	 */
	public ArrayList<Long> intersectionId;	
	/**
	 * 
	 */
	public HashMap<Long,Integer> intersectionIdRetourne = new HashMap<Long, Integer>();
	/**
	 * 
	 */
	public ArrayList<Intersection> intersection;
	/**
	 * 
	 */
	public ArrayList<Segment> segment;
	
	public HashMap<Intersection, ArrayList<Segment>> listeAdjacence = new HashMap<Intersection, ArrayList<Segment>>();
	
	public HashMap<Intersection, ArrayList<Segment>> listeAdjacenceInverse = new HashMap<Intersection, ArrayList<Segment>>();
	 
	
	public HashMap<Intersection, ArrayList<Segment>> getListeAdjacence() {
		return listeAdjacence;
	}
	public void setListeAdjacence(HashMap<Intersection, ArrayList<Segment>> listeAdjacence) {
		this.listeAdjacence = listeAdjacence;
	}
	/* Method */
	
	public double[][] getMatriceCout(EnsembleRequete requetes) {
        ArrayList<Intersection> listeIntersection = new ArrayList<Intersection>();
        listeIntersection.add(requetes.getLieuDepart().getPointDeDepart());
        double[][] matriceCout = new double[listeIntersection.size()][listeIntersection.size()];
        for(int i=0; i<requetes.listeRequete.size();i++) {
            listeIntersection.add(requetes.listeRequete.get(i).getPointDeLivraison());
            listeIntersection.add(requetes.listeRequete.get(i).getPointDeLivraison());
        }
        Pair<Double,ArrayList<Intersection>> itineraire;
        for(int i=0;i<listeIntersection.size();i++) {
            for(int j=0;j<listeIntersection.size();j++) {
                if(i==j)
                    matriceCout[i][j] = -1;
                else {
                    itineraire = calcDijsktra(listeIntersection.get(i),listeIntersection.get(j));
                    matriceCout[i][j] = itineraire.getKey();
                }
            }
        }
    }
	
	public Pair<Double, ArrayList<Intersection>> calcDijsktra(Intersection depart, Intersection arrivee){
		
		
		HashMap<Intersection,Double> tab = new HashMap<Intersection,Double>();
		HashMap<Intersection, ArrayList<Intersection>> tabIntersection = new HashMap<Intersection, ArrayList<Intersection>>();
		ArrayList<Intersection> visitee = new ArrayList<Intersection>();
		ArrayList<Intersection> nonVisitee = (ArrayList<Intersection>) this.intersection.clone();
		ArrayList<Intersection> voisinArrivee = new ArrayList<Intersection>();
		ArrayList<Segment> voisins = new ArrayList<Segment>();
		

		for(int i=0;i<this.segment.size();i++) {	
			if(this.segment.get(i).getFin().getId() == arrivee.getId()) {
				voisinArrivee.add(segment.get(i).getOrigine());
			}
		}
		
		for(int i=0;i<this.intersection.size();i++) {
			ArrayList<Intersection> vide = new ArrayList<Intersection>();
			tab.put(this.intersection.get(i),100000.0);
			tabIntersection.put(this.intersection.get(i), vide);
		}
		
		tab.put(depart, 0.0);
		
		
		for(int i=0;i<this.listeAdjacence.get(depart).size();i++) {
			Segment s = this.listeAdjacence.get(depart).get(i);
			ArrayList<Intersection> vide = new ArrayList<Intersection>();
			tab.put(s.getFin(),s.getLongueur());
			vide.add(depart);
			tabIntersection.put(s.getFin(), vide);
		}

		visitee.add(depart);
		nonVisitee.remove(depart);
		voisinArrivee.remove(depart);
		
		while(voisinArrivee.size() != 0 ) { // Tant que tous les voisins de l'intersection d'arriv�e ne sont pas visit�e
			
			System.out.println("sizeVoisin : " + voisinArrivee.size());
			//System.out.println(voisinArrivee.size());
			Double mino = 1000000000.;
			Integer index = 0;

			
			
			for(int i=0;i<this.intersection.size();i++) {
				Intersection intersectionAVisiter = this.intersection.get(i);
				
				if(tab.get(intersectionAVisiter) < mino && !visitee.contains(this.intersection.get(i))){
					mino = tab.get(this.intersection.get(i));
					index = i;
				}
				
			}
			
			
			System.out.println(mino);
			for(int i  = 0 ;  i < tab.size(); i++) {
				if(tab.get(this.intersection.get(i)) < 100000) {
					System.out.println(this.intersection.get(i)+" : " + tab.get(this.intersection.get(i)));
				}
			}
			
			System.out.println(tab);
			Intersection nouveauDepart = this.intersection.get(index);
			
			for(int i=0;i<this.listeAdjacence.get(nouveauDepart).size();i++) {

				Segment s = this.listeAdjacence.get(nouveauDepart).get(i);

					//System.out.println(s.toString());
					//System.out.println(tab.get(arrivee));
					if(tab.get(nouveauDepart) + s.getLongueur() < tab.get(s.getFin())) {
						
						
						tab.put(s.getFin(),tab.get(nouveauDepart) + s.getLongueur());	
						ArrayList<Intersection> liste = (ArrayList<Intersection>) tabIntersection.get(nouveauDepart).clone();
						liste.add(s.getFin());
						tabIntersection.put(s.getFin(), liste);
						
					}

				
			}
			
			visitee.add(nouveauDepart);
			nonVisitee.remove(nouveauDepart);
			voisinArrivee.remove(nouveauDepart);
		}
		Pair<Double, ArrayList<Intersection>> ret = new Pair<Double, ArrayList<Intersection>>(tab.get(arrivee),tabIntersection.get(arrivee));
		
		
		return ret;
	}
	
	public ArrayList<Long> getIntersectionId() {
		return intersectionId;
	}
	public void setIntersectionId(ArrayList<Long> intersectionId) {
		this.intersectionId = intersectionId;
	}
	public ArrayList<Intersection> getIntersection() {
		return intersection;
	}
	public void setIntersection(ArrayList<Intersection> intersection) {
		this.intersection = intersection;
	}
	public ArrayList<Segment> getSegment() {
		return segment;
	}
	public void setSegment(ArrayList<Segment> segment) {
		this.segment = segment;
	}

	
	public HashMap<Long, Integer> getIntersectionIdRetourne() {
		return intersectionIdRetourne;
	}
	public void setIntersectionIdRetourne(HashMap<Long, Integer> intersectionIdRetourne) {
		this.intersectionIdRetourne = intersectionIdRetourne;
	}
	public Plan(ArrayList<Long> intersectionId, ArrayList<Intersection> intersection, ArrayList<Segment> segment) {
		super();

		this.intersectionId = intersectionId;
		this.intersection = intersection;
		this.segment = segment;
		for(int i = 0; i < this.intersectionId.size(); i++) {
			this.intersectionIdRetourne.put(intersectionId.get(i), i);	
		}
		
		for(int i = 0; i < this.intersectionId.size(); i++) {
			ArrayList<Segment> vide = new ArrayList<Segment>();
			this.listeAdjacence.put(this.intersection.get(i), vide);
		}
		
		for(int j = 0 ; j < this.segment.size(); j ++)
		{
			ArrayList<Segment> listeSegmentsAssocies = this.listeAdjacence.get(this.segment.get(j).getOrigine());
			listeSegmentsAssocies.add(this.segment.get(j));
			this.listeAdjacence.put(this.segment.get(j).getOrigine(), listeSegmentsAssocies );
		}
		
		
		for(int i = 0; i < this.intersectionId.size(); i++) {
			ArrayList<Segment> videDeux = new ArrayList<Segment>();
			this.listeAdjacenceInverse.put(this.intersection.get(i), videDeux);
		}
		
		for(int j = 0 ; j < this.segment.size(); j ++)
		{
			ArrayList<Segment> listeSegmentsAssocies = this.listeAdjacenceInverse.get(this.segment.get(j).getFin());
			listeSegmentsAssocies.add(this.segment.get(j));
			this.listeAdjacenceInverse.put(this.segment.get(j).getFin(), listeSegmentsAssocies );
		}
	} 
	
	public Plan(ArrayList<Long> intersectionId, HashMap<Long, Integer> intersectionIdRetourne,
			ArrayList<Intersection> intersection, ArrayList<Segment> segment) {
		super();
		this.intersectionId = intersectionId;
		this.intersectionIdRetourne = intersectionIdRetourne;
		this.intersection = intersection;
		this.segment = segment;
	}
	public Plan() {
		super();
	}

	@Override
	public String toString() {
		return "Plan [intersectionId=" + intersectionId + ", intersection=" + intersection + " ]";
	}
	
	
	
	public Intersection getIntersectionById(Long id){

		return this.intersection.get(this.intersectionIdRetourne.get(id));
	}
	
	public Double[][] getMatrice(){
		int nbIntersection = this.intersectionId.size();
		int nbSegment = this.segment.size();
		Double[][] matriceCouts = new Double[nbIntersection+1][nbIntersection+1];
		
		for(int i = 0 ; i < nbIntersection ; i++) {
			for(int j = 0 ; j < nbIntersection ; j++) {
				matriceCouts[i][j] = 1000000.;
			}
			
		}
		
		for(int i = 0 ; i < nbSegment ; i++) {
			Segment segmentCourant = this.segment.get(i);
			matriceCouts[intersectionIdRetourne.get(segmentCourant.getOrigine().getId())][intersectionIdRetourne.get(segmentCourant.getFin().getId())] = segmentCourant.getLongueur();
		}
		return matriceCouts;
	}
	
	public Double longitudeMax(){
		Double max = this.intersection.get(0).getLongitude();
		for(int i = 0; i < this.intersectionId.size(); i++) {
			if(max < this.intersection.get(i).getLongitude()) {
				max = this.intersection.get(i).getLongitude();
			}
		}
		return max;
	}
	
	public Double latitudeMax(){
		Double max = this.intersection.get(0).getLatitude();
		for(int i = 0; i < this.intersectionId.size(); i++) {
			if(max < this.intersection.get(i).getLatitude()) {
				max = this.intersection.get(i).getLatitude();
			}
		}
		return max;
	}
	
	
	public Double longitudeMin(){
		Double min = this.intersection.get(0).getLongitude();
		for(int i = 0; i < this.intersectionId.size(); i++) {
			if(min > this.intersection.get(i).getLongitude()) {
				min = this.intersection.get(i).getLongitude();
			}
		}
		return min;
	}
	
	public Double latitudeMin(){
		Double min = this.intersection.get(0).getLatitude();
		for(int i = 0; i < this.intersectionId.size(); i++) {
			if(min > this.intersection.get(i).getLatitude()) {
				min = this.intersection.get(i).getLatitude();
			}
		}
		return min;
	}
	
	public Itineraire getItineraire(ArrayList<Integer> listeSommets){
		Itineraire itineraire = new Itineraire();
		
		for (int i= 0; i < listeSommets.size() ; i++) {
			itineraire.addIntersection(this.intersection.get(listeSommets.get(i)));
		}
		
		
		return itineraire;
	}
	
};





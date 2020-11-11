// --------------------------------------------------------
// Code generated by Papyrus Java
// --------------------------------------------------------

package modele;

import java.sql.Time;
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
	private ArrayList<Long> intersectionId;	
	/**
	 * 
	 */
	private HashMap<Long,Integer> intersectionIdRetourne = new HashMap<Long, Integer>();
	/**
	 * 
	 */
	private ArrayList<Intersection> intersection;
	/**
	 * 
	 */
	private ArrayList<Segment> segment;
	
	private HashMap<Intersection, ArrayList<Segment>> listeAdjacence = new HashMap<Intersection, ArrayList<Segment>>();
	
	private HashMap<Intersection, ArrayList<Segment>> listeAdjacenceInverse = new HashMap<Intersection, ArrayList<Segment>>();
	 
	//public HashMap<Intersection, Integer> indexIdInteger = new HashMap<Intersection, Integer>();
	
	
	
	public HashMap<Intersection, ArrayList<Segment>> getListeAdjacence() {
		return listeAdjacence;
	}
	public void setListeAdjacence(HashMap<Intersection, ArrayList<Segment>> listeAdjacence) {
		this.listeAdjacence = listeAdjacence;
	}
	/* Method */
	public void modifierIntersectionsPertinentes(EnsembleRequete requetes) {
		ArrayList<Requete> listeRequetes = requetes.getListeRequete();
		for(int i = 0 ; i < requetes.getListeRequete().size() ; i++) {
			Intersection pointRecuperation  = listeRequetes.get(i).getPointDeRecuperation();
			Intersection pointLivraison  = listeRequetes.get(i).getPointDeRecuperation();
			
			Integer positionPointRecuperation = this.intersectionIdRetourne.get(pointRecuperation.getId());
			Integer positionPointLivraison = this.intersectionIdRetourne.get(pointLivraison.getId());
			
			this.intersection.set(positionPointRecuperation, pointRecuperation);
			this.intersection.set(positionPointLivraison, pointLivraison);
			
		}
	
	Intersection pointDeDepart = requetes.getLieuDepart().getPointDeDepart();
	
	Integer positionPointDeDepart =this.intersectionIdRetourne.get(pointDeDepart.getId());
	
	this.intersection.set(positionPointDeDepart, pointDeDepart);
	
	}

	public Livraison getMatriceCout(EnsembleRequete requetes) {
		
		this.modifierIntersectionsPertinentes(requetes);
        
		ArrayList<Intersection> listeIntersection = new ArrayList<Intersection>();
        ArrayList<Pair<Integer,Integer>> listePaires = new ArrayList<Pair<Integer,Integer>>();
        listeIntersection.add(requetes.getLieuDepart().getPointDeDepart());
        for(int i=0; i<requetes.getListeRequete().size();i++) {
            listeIntersection.add(requetes.getListeRequete().get(i).getPointDeRecuperation());
            listeIntersection.add(requetes.getListeRequete().get(i).getPointDeLivraison());
            listePaires.add(new Pair<Integer,Integer>(listeIntersection.indexOf(requetes.getListeRequete().get(i).getPointDeRecuperation()),listeIntersection.indexOf(requetes.getListeRequete().get(i).getPointDeLivraison())));
        }
        double[][] matriceCout = new double[listeIntersection.size()][listeIntersection.size()];
        

        HashMap<Pair<Intersection, Intersection>, Itineraire> matriceItineraire = new HashMap<Pair<Intersection, Intersection>, Itineraire>();
        //System.out.println("calculs dans A*");
        for(int i=0;i<listeIntersection.size();i++) {
            for(int j=0;j<listeIntersection.size();j++) {
                if(i==j)
                    matriceCout[i][j] = -1;
                else {
                	//System.out.println(listeIntersection.get(i));
                	//System.out.println(listeIntersection.get(j));
                	Itineraire itineraire = aEtoile(listeIntersection.get(i),listeIntersection.get(j));
                	Pair<Intersection, Intersection> cle = new Pair<Intersection,Intersection>(listeIntersection.get(i),listeIntersection.get(j));
                	matriceItineraire.put(cle, itineraire);
                    matriceCout[i][j] = itineraire.getCout();
                }
            }
        }
        //System.out.println("r�sultat tsp : ");    
        TSP tsp = new TSP1();
        Integer[] resultat = tsp.searchSolution(30000, matriceCout, listePaires, 0);
        for(int i=0;i<resultat.length;i++){
        	
        	//System.out.println(i);
        	//System.out.println(resultat[i]);
        }
        
        //System.out.println("contenu matrice itineraire");
        
        for (HashMap.Entry<Pair<Intersection, Intersection>, Itineraire> entry : matriceItineraire.entrySet()) {
            //System.out.println("Key = " + entry.getKey());
            //System.out.println("Value debut = " + entry.getValue().getListeIntersections().get(0));
            //System.out.println("Value fin = " + entry.getValue().getListeIntersections().get(entry.getValue().getListeIntersections().size()-1));
        }

        
        Itineraire itineraireOpti = new Itineraire();
        
        for(int i=0;i<resultat.length;i++) {
        	itineraireOpti.addIntersection(listeIntersection.get(resultat[i]));	
        }
        
        // La liste des sommets � parcourir dans l'ordre 
        itineraireOpti.addIntersection(requetes.getLieuDepart().getPointDeDepart());
        
        
        ArrayList<Itineraire> itineraireComplet = new ArrayList<Itineraire>();
        
        for(int i=0;i<itineraireOpti.getListeIntersections().size()-1;i++) {
        	Pair<Intersection, Intersection> cle = new Pair<Intersection, Intersection>(itineraireOpti.getListeIntersections().get(i), itineraireOpti.getListeIntersections().get(i+1));
        	itineraireComplet.add(matriceItineraire.get(cle));
        }
        Livraison ret = new Livraison(itineraireComplet,requetes);
        ret.calculArrivees();
        return(ret);
    }
	
	public HashMap<Pair<Intersection, Intersection>, Itineraire> FloydWarshall(ArrayList<Intersection> listeIntersection, double[][] matriceCout){
		HashMap<Pair<Intersection, Intersection>, Itineraire> matriceItineraire = new HashMap<Pair<Intersection, Intersection>, Itineraire>();
		int planSize = intersection.size();
		double[][][] d = new double[planSize+1][planSize+1][planSize+1]; //cout de touts les chemins
		int[][][] pi = new int[planSize+1][planSize+1][planSize+1]; //matrice de liaison
		
		for(int i = 0; i < planSize; ++i) { //initialisation d et pi (pi[0] est la matrice d'adjacence du plan
			for(int j = 0; j < planSize; ++j) {
				if(i==j) {
					d[0][i][j] = 1000000.0;
					pi[0][i][j] = -1;
				}else {
					for(Segment s : segment) {
						if(s.isSegment(intersection.get(i), intersection.get(j))) {
							d[0][i][j] = s.getLongueur();
							pi[0][i][j] = i;
							break;
						}else {
							d[0][i][j] = 1000000.0;
							pi[0][i][j] = -1;
						}
					}
				}
			}
		}
		
		
		for(int k = 1; k <= planSize; ++k) {
			for(int i = 0; i < planSize; ++i) {
				for(int j = 0; j < planSize; ++j) {
					if(i==j) {
						pi[k][i][j]=-1;
						d[k][i][j]=1000000.0;
						continue;
					}
					if(d[k-1][i][j] < d[k-1][i][k-1] + d[k-1][k-1][j]) {
						d[k][i][j] = d[k-1][i][j];
						pi[k][i][j] = pi[k-1][i][j]; //antecedant de j est le meme que pour k-1
					}
					else {
						d[k][i][j] = d[k-1][i][k-1] + d[k-1][k-1][j];
						pi[k][i][j] = pi[k-1][k-1][j]; //antecedant de j est l'antecedent de j dans le plus court chemin de k a j
					}
				}
			}
		}
		for(int i =0; i<10; ++i) {
			for(int j = 0; j<10; ++j) {
				System.out.print(pi[1][i][j]+", ");
			}
			System.out.print("\n");
		}
		
		
				
		
		for(int i = 0; i < planSize; ++i) {
			Intersection depart = intersection.get(i);
			if(listeIntersection.contains(depart)) {
				for(int j = 0; j < planSize; ++j) {
					if(i == j) {
						matriceCout[listeIntersection.indexOf(depart)][listeIntersection.indexOf(depart)]=-1;
						continue;
					}else {
						Intersection arrivee = intersection.get(j);
						if(listeIntersection.contains(arrivee)) { // si les deux appartiennent a la liste des intersections qui nous interessent
		                	Pair<Intersection, Intersection> cle = new Pair<Intersection,Intersection>(depart, arrivee);
		                	ArrayList<Intersection> chemin = new ArrayList<Intersection>(planSize);
		                	Intersection pivot = arrivee;
		                	while(pivot!=depart) {
		                		//System.out.println(intersection.indexOf(pivot) +"--"+ i);
		                		//System.out.println(d[planSize][i][intersection.indexOf(pivot)]);
		                		chemin.add(0, pivot);
		                		pivot = intersection.get(pi[planSize][i][intersection.indexOf(pivot)]);
		                	}
		                	chemin.add(0, depart);
		                	Itineraire itineraire = new Itineraire(chemin, d[planSize][i][j]);
		                	matriceItineraire.put(cle, itineraire);
		                	matriceCout[listeIntersection.indexOf(depart)][listeIntersection.indexOf(arrivee)] = d[planSize][i][j];
						}
					}
				}
			}
		}
		
		return matriceItineraire;
	}
	
	@SuppressWarnings("unchecked")
	public String getNomRue(Intersection intersections) {
		
		String nomRue = new String();
		for(int i = 0; i< this.getSegment().size() ; i++) {
			Segment segment = this.getSegment().get(i);
			if(segment.getOrigine().getId() == intersections.getId() || segment.getFin().getId() == intersections.getId()  ) {
				nomRue = segment.getNom();
			}
		}
		 
		
		return nomRue;
	}
	public Itineraire calcDijsktra(Intersection depart, Intersection arrivee){
		
		
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
			
			//System.out.println("sizeVoisin : " + voisinArrivee.size());
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
		//Pair<Double, ArrayList<Intersection>> ret = new Pair<Double, ArrayList<Intersection>>(tab.get(arrivee),tabIntersection.get(arrivee));
		Itineraire itineraire = new Itineraire(tabIntersection.get(arrivee), tab.get(arrivee));
		
		return itineraire;
	}
	/**
	 * Cette fonction va permettre d'ajouter un sommet au graphe parcourus par le cycliste
	 * @return Livraison
	 */
	public Livraison ajouterSommet(Livraison ancienneLivraison, Intersection nouveauSommet, Intersection intersectionPrecedente, Long duree) {
		
		ArrayList<Itineraire> nouvelleListeItineraire = new ArrayList<Itineraire>();
		EnsembleRequete requetes = ancienneLivraison.getRequetes();
		Time heureDepart = requetes.getLieuDepart().getHeureDepart();
		HashMap<Itineraire,Time>dictionnaireArriveesItineraires = new HashMap<Itineraire,Time>();
		
		for(int i = 0 ; i < ancienneLivraison.getListeItineraires().size() ; i++) {
			if(ancienneLivraison.getListeItineraires().get(i).getListeIntersections().get(0).getId() == intersectionPrecedente.getId()) {
				Intersection depart = ancienneLivraison.getListeItineraires().get(i).getListeIntersections().get(0);
				Intersection arrivee = ancienneLivraison.getListeItineraires().get(i).getListeIntersections().get(ancienneLivraison.getListeItineraires().get(i).getListeIntersections().size()-1);
				Itineraire nouvelItineraire1 = this.calcDijsktra(depart, nouveauSommet);
				Itineraire nouvelItineraire2 = this.calcDijsktra(nouveauSommet, arrivee);
				nouvelleListeItineraire.add(nouvelItineraire1);
				nouvelleListeItineraire.add(nouvelItineraire2);
			}
			else {
				nouvelleListeItineraire.add(ancienneLivraison.getListeItineraires().get(i));
			}
		}
		
		HashMap<Intersection,Long>tempsAssocieIntersection = new HashMap<Intersection,Long>();
		
		for(int i=0;i<requetes.getListeRequete().size();i++) {
			tempsAssocieIntersection.put(requetes.getListeRequete().get(i).getPointDeRecuperation(), requetes.getListeRequete().get(i).getDureeRecuperation());
			tempsAssocieIntersection.put(requetes.getListeRequete().get(i).getPointDeLivraison(), requetes.getListeRequete().get(i).getDureeLivraison());
		}
		dictionnaireArriveesItineraires.put(nouvelleListeItineraire.get(0), new Time(heureDepart.getTime()+nouvelleListeItineraire.get(0).getTemps().longValue()));
		
		if(nouvelleListeItineraire.size() > 0) {
			

			for(int i=1;i<nouvelleListeItineraire.size();i++) {
				Time temps = new Time(
						dictionnaireArriveesItineraires.get(nouvelleListeItineraire.get(i-1)).getTime()+
						nouvelleListeItineraire.get(i).getTemps().longValue() + 
						tempsAssocieIntersection.get(nouvelleListeItineraire.get(i).getListeIntersections().get(0))				
						);
				
				dictionnaireArriveesItineraires.put(nouvelleListeItineraire.get(i),temps);
			}
		
		}

		return new Livraison(nouvelleListeItineraire, heureDepart, dictionnaireArriveesItineraires, requetes);
		
	}

	
	
	public Livraison supprimerSommet(Livraison ancienneLivraison, Intersection sommetASupprimer) {
		
		EnsembleRequete requetes = ancienneLivraison.getRequetes();
		Requete requete = new Requete();
		for(int i = 0; i < requetes.getListeRequete().size() ; i ++) {
			requete = requetes.getListeRequete().get(i);
			if(requete.getPointDeLivraison().getId() == sommetASupprimer.getId() || requete.getPointDeRecuperation().getId() == sommetASupprimer.getId()) {
				
				ArrayList<Requete> listeRequete = requetes.getListeRequete();
				listeRequete.remove(requete);
				requetes.setListeRequete(listeRequete);
				
				break;
			}
		}
		
		//on supprime d'abord le point de livraison
		
		ArrayList<Itineraire> ancienneListeItineraire = ancienneLivraison.getListeItineraires();
		ArrayList<Itineraire> nouvelleListeItineraire = new ArrayList<Itineraire>();
		int i = 0;
		
		
// || requete.getPointDeRecuperation().getId() == ancienneListeItineraire.get(i).getListeIntersections().get(0).getId()		
		while(i < ancienneLivraison.getListeItineraires().size()) {
			
			if(requete.getPointDeLivraison().getId() == ancienneListeItineraire.get(i).getListeIntersections().get(0).getId()) {
				Intersection intersectionPrecedente = ancienneListeItineraire.get(i-1).getListeIntersections().get(0);
				Intersection intersectionSuivante = ancienneListeItineraire.get(i).getListeIntersections().get(ancienneListeItineraire.get(i).getListeIntersections().size()-1);
				nouvelleListeItineraire.add(this.aEtoile(intersectionPrecedente, intersectionSuivante));
				i++;
				i++;
			}
			else {
				nouvelleListeItineraire.add(ancienneListeItineraire.get(i));
				i++;
			}
		}
		
		
		// on supprime ensuite le point de r�cup�rations
		
		
//		ancienneListeItineraire = nouvelleListeItineraire;
//		nouvelleListeItineraire = new ArrayList<Itineraire>();
//		i = 0;
//		
//		
//		while(i < ancienneLivraison.getListeItineraires().size()) {
//			
//			if(requete.getPointDeRecuperation().getId() == ancienneListeItineraire.get(i).getListeIntersections().get(0).getId()	) {
//				Intersection intersectionPrecedente = ancienneListeItineraire.get(i-1).getListeIntersections().get(0);
//				Intersection intersectionSuivante = ancienneListeItineraire.get(i).getListeIntersections().get(ancienneListeItineraire.get(i).getListeIntersections().size()-1);
//				nouvelleListeItineraire.add(this.aEtoile(intersectionPrecedente, intersectionSuivante));
//				i++;
//				i++;
//			}
//			else {
//				nouvelleListeItineraire.add(ancienneListeItineraire.get(i));
//				i++;
//			}
//		}
		
		

		

		System.out.println("Liste des intersections importantes du nouvel itineraire de livraison");
		
		for(int i1 = 0 ; i1 < nouvelleListeItineraire.size(); i1 ++) {
			
			System.out.println("depart : " +nouvelleListeItineraire.get(i1).getListeIntersections().get(0));
			System.out.println("arriv�e : " +nouvelleListeItineraire.get(i1).getListeIntersections().get(nouvelleListeItineraire.get(i1).getListeIntersections().size() -1));
		}
		
		Livraison nouvelleLivraison = new Livraison( nouvelleListeItineraire , requetes);
		//nouvelleLivraison.calculArrivees();
		return nouvelleLivraison;
		
	}
	
public Itineraire aEtoile(Intersection depart, Intersection arrivee){
		
		// On g�re pr�alablemet le cas limites ou le d�part et l'arriv�e sont confondus
	
		if(depart.getId() == arrivee.getId()) {
			ArrayList<Intersection> listeIntersections = new ArrayList<Intersection>();
			listeIntersections.add(arrivee);
			Double cout = 0.;
			return new Itineraire(listeIntersections, cout);
		}
		
		
	
	
		HashMap<Intersection,Double> tab = new HashMap<Intersection,Double>();
		HashMap<Intersection,Double> heuristique = new HashMap<Intersection,Double>();
		HashMap<Intersection, ArrayList<Intersection>> tabIntersection = new HashMap<Intersection, ArrayList<Intersection>>();
		HashMap<Intersection, ArrayList<String>> tabNomRues = new HashMap<Intersection, ArrayList<String>>();
		ArrayList<Intersection> visitee = new ArrayList<Intersection>();
		ArrayList<Intersection> nonVisitee = (ArrayList<Intersection>) this.intersection.clone();
		ArrayList<Intersection> voisinArrivee = new ArrayList<Intersection>();
		ArrayList<Segment> voisins = new ArrayList<Segment>();
		
		double latitudeArrivee = arrivee.getLatitude();
		double longitudeArrivee = arrivee.getLongitude();
		
		for(int i = 0 ; i < this.intersection.size(); i++) {
			Intersection intersectionCourante = this.intersection.get(i);
			double latitudeCourante = intersectionCourante.getLatitude();
			double longitudeCourante = intersectionCourante.getLongitude();
			Double distanceArrivee = 5000000*((latitudeCourante - latitudeArrivee)*(latitudeCourante - latitudeArrivee) + (longitudeCourante - longitudeArrivee)*(longitudeCourante - longitudeArrivee));
			//System.out.println(intersectionCourante.getLatitude());
			//System.out.println(intersectionCourante.getLongitude());
			//System.out.println(distanceArrivee);
			heuristique.put(intersectionCourante, distanceArrivee);
		}
		//System.out.println(heuristique.toString());
		//System.out.println("fini heuristique");
		
		for(int i=0;i<this.segment.size();i++) {	
			if(this.segment.get(i).getFin().getId() == arrivee.getId()) {
				voisinArrivee.add(segment.get(i).getOrigine());
			}
		}
		
		for(int i=0;i<this.intersection.size();i++) {
			ArrayList<Intersection> vide = new ArrayList<Intersection>();
			ArrayList<String> chaineVide = new ArrayList<String>(); 
			tab.put(this.intersection.get(i),100000.0);
			tabIntersection.put(this.intersection.get(i), vide);
			tabNomRues.put(this.intersection.get(i), chaineVide);
		}
		
		tab.put(depart, 0.0);
		
		
		
		boolean arriveDepartContigue = false;
		Segment departArrivee = new Segment();
		for(int i=0;i<this.listeAdjacence.get(depart).size();i++) {
			Segment s = this.listeAdjacence.get(depart).get(i);
			ArrayList<Intersection> vide = new ArrayList<Intersection>();
			ArrayList<String> chaineVide = new ArrayList<String>();
			tab.put(s.getFin(),s.getLongueur());
			vide.add(depart);
			chaineVide.add(s.getNom());
			if(s.getFin().getId() == arrivee.getId()){
				arriveDepartContigue = true;
				departArrivee = s;
			}
			tabIntersection.put(s.getFin(), vide);
			tabNomRues.put(s.getFin(), chaineVide);
		}
		
		// Si les sommets de d�part et d'arriv�e sont reli�s, on renvoie juste le segment qui les s�pare
		
		if(arriveDepartContigue) {
			ArrayList<Intersection> listeInter = new ArrayList<Intersection>();
			listeInter.add(depart);
			listeInter.add(arrivee);
			
			ArrayList<String> listeNoms = new ArrayList<String>();
			listeNoms.add(departArrivee.getNom());
			return new Itineraire(listeInter, listeNoms, departArrivee.getLongueur());
		}
		
		
		

		visitee.add(depart);
		nonVisitee.remove(depart);
		voisinArrivee.remove(depart);
		
		while(voisinArrivee.size() != 0 ) { // Tant que tous les voisins de l'intersection d'arriv�e ne sont pas visit�e
			//System.out.println("sizeVoisin : " + voisinArrivee.size());
			//System.out.println(voisinArrivee.size());
			Double mino = 100000000.;
			Integer index = 0;

			
			
			for(int i=0;i<this.intersection.size();i++) {
				Intersection intersectionAVisiter = this.intersection.get(i);
				
				if(tab.get(intersectionAVisiter) + heuristique.get(intersectionAVisiter) < mino && !visitee.contains(this.intersection.get(i))){
					mino = tab.get(this.intersection.get(i))+ heuristique.get(intersectionAVisiter);
					index = i;
				}
				
			}
			
			
			//System.out.println(mino);
			for(int i  = 0 ;  i < tab.size(); i++) {
				if(tab.get(this.intersection.get(i)) < 100000) {
					//System.out.println(this.intersection.get(i)+" : " + tab.get(this.intersection.get(i)));
				}
			}
			
			//System.out.println(tab);
			Intersection nouveauDepart = this.intersection.get(index);
			
			for(int i=0;i<this.listeAdjacence.get(nouveauDepart).size();i++) {

				Segment s = this.listeAdjacence.get(nouveauDepart).get(i);

					//System.out.println(s.toString());
					//System.out.println(tab.get(arrivee));
					if(tab.get(nouveauDepart) + s.getLongueur() < tab.get(s.getFin())) {
						
						tab.put(s.getFin(),tab.get(nouveauDepart) + s.getLongueur());	
						ArrayList<Intersection> liste = (ArrayList<Intersection>) tabIntersection.get(nouveauDepart).clone();
						ArrayList<String> listeNomRues = (ArrayList<String>) tabNomRues.get(nouveauDepart).clone();
						liste.add(s.getFin());
						listeNomRues.add(s.getNom());
						tabIntersection.put(s.getFin(), liste);
						tabNomRues.put(s.getFin(), listeNomRues);
					}
			}
			
			visitee.add(nouveauDepart);
			nonVisitee.remove(nouveauDepart);
			voisinArrivee.remove(nouveauDepart);
		}
		//Pair<Double, ArrayList<Intersection>> ret = new Pair<Double, ArrayList<Intersection>>(tab.get(arrivee),tabIntersection.get(arrivee));
		Itineraire itineraire = new Itineraire(tabIntersection.get(arrivee),tabNomRues.get(arrivee) ,tab.get(arrivee));
		
		return itineraire;
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
	

};





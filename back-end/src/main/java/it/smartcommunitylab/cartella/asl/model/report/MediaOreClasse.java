package it.smartcommunitylab.cartella.asl.model.report;

public class MediaOreClasse {
	long oreSvolte = 0;
	int numStudenti = 0;
	double media = 0.0;
	
	public void addOreStudente(long ore) {
		oreSvolte += ore;
		numStudenti++;
		media = (double) oreSvolte / numStudenti; 
	}
	
	public long getOreSvolte() {
		return oreSvolte;
	}
	public void setOreSvolte(long oreSvolte) {
		this.oreSvolte = oreSvolte;
	}
	public int getNumStudenti() {
		return numStudenti;
	}
	public void setNumStudenti(int numStudenti) {
		this.numStudenti = numStudenti;
	}
	public double getMedia() {
		return media;
	}
	public void setMedia(double media) {
		this.media = media;
	}
}

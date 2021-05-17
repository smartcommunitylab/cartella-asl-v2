package it.smartcommunitylab.cartella.asl.model.report;

public class MediaOreClasse {
	String classe;
	long oreSvolte = 0;
	int numStudenti = 0;
	double media = 0.0;
	
	public void addOreStudente(long ore) {
		oreSvolte += ore;
		numStudenti++;
		media = (double) oreSvolte / numStudenti; 
		media = Math.round(media*100)/100.0d;
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

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}
}

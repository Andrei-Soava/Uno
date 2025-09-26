package onegame.modello.giocatori;

import java.io.Serializable;

/**
 * Statistica basilare per un giocatore.
 * Contiene contatori essenziali aggiornati a fine partita.
 */
public class Statistica implements Serializable {
    private static final long serialVersionUID = 1L;

    private int partiteGiocate;
    private int vittorie;
    private int sconfitte;
    private int puntiTotali; // somma punti delle mani finali degli avversari alla vittoria

    // costruttore vuoto
    public Statistica() {
        this.partiteGiocate = 0;
        this.vittorie = 0;
        this.sconfitte = 0;
        this.puntiTotali = 0;
    }

    public int getPartiteGiocate() {
        return partiteGiocate;
    }

    public void setPartiteGiocate(int partiteGiocate) {
        this.partiteGiocate = partiteGiocate;
    }

    public int getVittorie() {
        return vittorie;
    }

    public void setVittorie(int vittorie) {
        this.vittorie = vittorie;
    }

    public int getSconfitte() {
        return sconfitte;
    }

    public void setSconfitte(int sconfitte) {
        this.sconfitte = sconfitte;
    }

    public int getPuntiTotali() {
        return puntiTotali;
    }

    public void setPuntiTotali(int puntiTotali) {
        this.puntiTotali = puntiTotali;
    }

    // metodi utili
    public void registraVittoria(int puntiGuadagnati) {
        this.partiteGiocate++;
        this.vittorie++;
        this.puntiTotali += puntiGuadagnati;
    }

    public void registraSconfitta() {
        this.partiteGiocate++;
        this.sconfitte++;
    }

    @Override
    public String toString() {
        return "Statistica [partiteGiocate=" + partiteGiocate + ", vittorie=" + vittorie + ", sconfitte=" + sconfitte
                + ", puntiTotali=" + puntiTotali + "]";
    }
}

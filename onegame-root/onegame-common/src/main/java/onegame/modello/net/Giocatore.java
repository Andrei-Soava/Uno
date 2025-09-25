package onegame.modello.net;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import onegame.modello.carte.Carta;
import onegame.modello.giocatori.Mano;
import onegame.modello.giocatori.Statistica;

/**
 * Rappresenta un giocatore (registrato o anonimo) nel sistema.
 */
public class Giocatore {
    private String idGiocatore;
    private String username;
    private boolean anonimo;
    private String tokenSessione;
    private Mano mano;
    private boolean connesso;
    private long ultimoHeartbeat; // epoch millis
    private Statistica statistica;

    // costruttore vuoto per Jackson
    public Giocatore() {
        this.idGiocatore = UUID.randomUUID().toString();
        this.mano = new Mano();
        this.anonimo = true;
        this.connesso = true;
        this.ultimoHeartbeat = Instant.now().toEpochMilli();
        this.statistica = new Statistica();
        this.tokenSessione = UUID.randomUUID().toString();
    }

    public Giocatore(String username, boolean anonimo) {
        this();
        this.username = username;
        this.anonimo = anonimo;
    }

    public String getIdGiocatore() {
        return idGiocatore;
    }

    public void setIdGiocatore(String idGiocatore) {
        this.idGiocatore = idGiocatore;
    }

    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAnonimo() {
        return anonimo;
    }

    public void setAnonimo(boolean anonimo) {
        this.anonimo = anonimo;
    }

    public String getTokenSessione() {
        return tokenSessione;
    }

    public void setTokenSessione(String tokenSessione) {
        this.tokenSessione = tokenSessione;
    }

    public Mano getMano() {
        return mano;
    }

    public void setMano(Mano mano) {
        this.mano = mano;
    }

    public boolean isConnesso() {
        return connesso;
    }

    public void setConnesso(boolean connesso) {
        this.connesso = connesso;
        if (connesso) this.ultimoHeartbeat = Instant.now().toEpochMilli();
    }

    public long getUltimoHeartbeat() {
        return ultimoHeartbeat;
    }

    public void setUltimoHeartbeat(long ultimoHeartbeat) {
        this.ultimoHeartbeat = ultimoHeartbeat;
    }

    public Statistica getStatistica() {
        return statistica;
    }

    public void setStatistica(Statistica statistica) {
        this.statistica = statistica;
    }

    @JsonIgnore
    public void aggiornaHeartbeat() {
        this.ultimoHeartbeat = Instant.now().toEpochMilli();
    }

    public void aggiungiCarta(Carta c) {
        this.mano.aggiungiCarta(c);
    }

    public void aggiungiCarta(List<Carta> carte) {
        this.mano.aggiungiCarte(carte);
    }

    public void rimuoveCarta(Carta c) {
        this.mano.rimuoviCarta(c);
    }

    @Override
    public String toString() {
        return "Giocatore[id=" + idGiocatore + ", username=" + username + ", anonimo=" + anonimo + ", connesso="
                + connesso + "]";
    }
}

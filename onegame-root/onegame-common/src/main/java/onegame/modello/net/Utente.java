package onegame.modello.net;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import onegame.modello.giocatori.Giocatore;
import onegame.modello.giocatori.Statistica;
import onegame.modello.PartitaIF;

/**
 * Rappresenta un giocatore nel sistema.
 * Usabile sia per giocatori registrati sia per anonimi.
 */
public class Utente {
    private String idGiocatore;
    private String username;
    private boolean anonimo;
    private String tokenSessione;
    private boolean connesso;
    private long ultimoHeartbeat;
    private Statistica statistica;
    private Giocatore giocatore;

    public Utente(boolean anonimo) {
        this.idGiocatore = UUID.randomUUID().toString();
        this.username = "anonimo";
        this.anonimo = anonimo;
        this.tokenSessione = UUID.randomUUID().toString();
        this.connesso = true;
        this.ultimoHeartbeat = Instant.now().toEpochMilli();
        this.statistica = new Statistica();
    }

    public Utente(String username, boolean anonimo) {
        this(anonimo);
        if (username != null && !username.isEmpty()) {
            this.username = username;
            this.anonimo = username.startsWith("anonimo-");
        }
    }

    @JsonProperty("idGiocatore")
    public String getIdGiocatore() {
        return idGiocatore;
    }

    public void setIdGiocatore(String idGiocatore) {
        this.idGiocatore = idGiocatore;
    }

    @JsonProperty("username")
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
    
    public PartitaIF getPartita() {
		if (giocatore != null) {
			return giocatore.getPartita();
		}
		return null;
	}

    public String getTokenSessione() {
        return tokenSessione;
    }

    public void setTokenSessione(String tokenSessione) {
        this.tokenSessione = tokenSessione;
    }

    public boolean isConnesso() {
        return connesso;
    }

    public void setConnesso(boolean connesso) {
        this.connesso = connesso;
        if (connesso) aggiornaHeartbeat();
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

    @Override
    public String toString() {
        return "Giocatore[id=" + idGiocatore + ", username=" + username + ", anonimo=" + anonimo + "]";
    }
}

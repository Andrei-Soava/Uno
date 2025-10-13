package onegame.modello.net;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import onegame.modello.giocatori.Giocatore;
import onegame.modello.giocatori.Statistica;
import onegame.modello.PartitaIF;

/**
 * Rappresenta un utente connesso al sistema.
 * Usabile sia per utenti registrati sia per anonimi.
 */
public class Utente {
    private String username;
    private boolean anonimo;
    private boolean connesso;
    private long ultimoHeartbeat;
    private Giocatore giocatore;

    public Utente(boolean anonimo) {
        this.username = "anonimo";
        this.anonimo = anonimo;
        this.connesso = true;
        this.ultimoHeartbeat = Instant.now().toEpochMilli();
    }

    public Utente(String username, boolean anonimo) {
        this(anonimo);
        if (username != null && !username.isEmpty()) {
            this.username = username;
            this.anonimo = username.startsWith("anonimo-");
        }
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

    public boolean isConnesso() {
        return connesso;
    }

    public void setConnesso(boolean connesso) {
        this.connesso = connesso;
        if (connesso) aggiornaHeartbeat();
    }
    
    @JsonIgnore
    public long getUltimoHeartbeat() {
        return ultimoHeartbeat;
    }

    public void setUltimoHeartbeat(long ultimoHeartbeat) {
        this.ultimoHeartbeat = ultimoHeartbeat;
    }
    
    @JsonIgnore
    public Giocatore getGiocatore() {
    	return giocatore;
    }
    
    public void setGiocatore(Giocatore giocatore) {
		this.giocatore = giocatore;
	}

    @JsonIgnore
    public void aggiornaHeartbeat() {
        this.ultimoHeartbeat = Instant.now().toEpochMilli();
    }

    @Override
    public String toString() {
        return "Giocatore[username=" + username + ", anonimo=" + anonimo + "]";
    }
}

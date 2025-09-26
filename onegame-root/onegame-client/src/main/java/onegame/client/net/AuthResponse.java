package onegame.client.net;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Risposta di autenticazione dal server
 */
public class AuthResponse {
    @JsonProperty("idGiocatore")
    public String idGiocatore;

    @JsonProperty("token")
    public String token;

    @JsonProperty("messaggio")
    public String messaggio;

    public AuthResponse() {
    	
    }
}

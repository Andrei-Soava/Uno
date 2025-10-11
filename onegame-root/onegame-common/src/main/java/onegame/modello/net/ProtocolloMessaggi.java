package onegame.modello.net;

public class ProtocolloMessaggi {

    // Eventi
    public static final String EVENT_AUTH_LOGIN = "auth:login";
    public static final String EVENT_AUTH_REGISTER = "auth:register";
    public static final String EVENT_AUTH_ANONIMO = "auth:anonimo";

    public static final String EVENT_STANZA_CREA = "stanza:crea";
    public static final String EVENT_STANZA_ENTRA = "stanza:entra";
    public static final String EVENT_STANZA_ESCI = "stanza:esci";

    public static final String EVENT_RICHIESTA_PARTITE_NON_CONCLUSE = "richiesta:partiteNonConcluse";
  
    //Eventi partite offline
    public static final String EVENT_SALVA_PARTITA = "partita:salva";
    public static final String EVENT_CARICA_PARTITA = "partita:carica";
    public static final String EVENT_ELIMINA_PARTITA = "partita:elimina";
    public static final String EVENT_LISTA_PARTITE = "partita:lista";
    
//    //Risposte partite offline
//    public static final String EVENT_PARTITA_OK = "partita:ok";
//    public static final String EVENT_PARTITA_FAIL = "partita:fail";
//
//    // Risposte
//    public static final String EVENT_AUTH_OK = "auth:ok";
//    public static final String EVENT_AUTH_FAIL = "auth:fail";
//
//    public static final String EVENT_STANZA_OK = "stanza:ok";
//    public static final String EVENT_STANZA_FAIL = "stanza:fail";

    
    // Richiesta di login / register
    public static class ReqAuth {
        private String username;
        private String password;

        public ReqAuth() {}

        public ReqAuth(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public String getUsername() {
        	return username;
        }
        
        public String getPassword() {
        	return password;
        }
        
        public void setUsername(String username) {
        	this.username = username;
        }
        
        public void setPassword(String password) {
        	this.password = password;
        }
    }
    
    public static class RespAuth {
    	public boolean success;
        public String idGiocatore;
		public String token;
        public String messaggio;
        
        public RespAuth() {
		}

		public RespAuth(boolean success, String idGiocatore, String token, String messaggio) {
        	this.success = success;
        	this.idGiocatore = idGiocatore;
        	this.token = token;
        	this.messaggio = messaggio;
        }
    }

//     Crea stanza
    public static class ReqCreaStanza {
        public String nomeStanza;
        public int maxGiocatori;

        public ReqCreaStanza() {
        }

        public ReqCreaStanza(String nomeStanza, int maxGiocatori) {
            this.nomeStanza = nomeStanza;
            this.maxGiocatori = maxGiocatori;
        }
    }

//     Entra stanza
    public static class ReqEntraStanza {
        public String idStanza;

        public ReqEntraStanza() {
        }

        public ReqEntraStanza(String idStanza) {
            this.idStanza = idStanza;
        }
    }

//     Stanza risposta semplice
    public static class RespStanza {
        public String idStanza;
        public String stato;
        public String messaggio;

        public RespStanza() {
        }

        public RespStanza(String idStanza, String stato, String messaggio) {
            this.idStanza = idStanza;
            this.stato = stato;
            this.messaggio = messaggio;
        }
    }
    
    //Richiesta di salvataggio
    public static class ReqSalvaPartita{
    	public String token; //token utente (per sapere chi salva)
    	public String nomeSalvataggio;
    	public String partitaSerializzata;
    	
    	public ReqSalvaPartita() {}
    	public ReqSalvaPartita(String token, String nomeSalvataggio, String partitaSerializzata) {
    		this.token = token;
    		this.nomeSalvataggio = nomeSalvataggio;
    		this.partitaSerializzata = partitaSerializzata;
    	}
    	
    	public String getToken() {
    		return token;
    	}
    	
    	public void setToken(String token) {
    		this.token = token;
    	}
    	
    	public String getNomeSalvataggio() {
    		return nomeSalvataggio;
    	}
    	
    	public void setNomeSalvataggio(String nomeSalvataggio) {
    		this.nomeSalvataggio = nomeSalvataggio;
    	}
    	
    	public String getPartitaSerializzata() {
    		return partitaSerializzata;
    	}
    	
    	public void setPartitaSerializzata(String partitaSerializzata) {
    		this.partitaSerializzata = partitaSerializzata;
    	}
    }
    
    //Richiesta di caricamento
    public static class ReqCaricaPartita{
    	public String token;
    	public String idSalvataggio;
    	
    	public ReqCaricaPartita() {}
    	public ReqCaricaPartita(String token, String idSalvataggio) {
    		this.token = token;
    		this.idSalvataggio = idSalvataggio;
    	}
    	public String getToken() {
    		return token;
    	}
    	
    	public void setToken(String token) {
    		this.token = token;
    	}
    	public String getIdSalvataggio() {
    		return idSalvataggio;
    	}
    	
    	public void setIdSalvataggio() {
    		this.idSalvataggio = idSalvataggio;
    	}
    }
    
    //richiesta di eliminazione
    public static class ReqEliminaPartita{
    	public String token;
    	public long idSalvataggio;
    	
    	public ReqEliminaPartita() {}
    	public ReqEliminaPartita(String token, long idSalvataggio) {
    		this.token = token;
    		this.idSalvataggio = idSalvataggio;
    	}
    }
    
    //richiesta elenco salvataggi
    public static class ReqListaPartite{
    	public String token;
    	
    	public ReqListaPartite() {}
    	public ReqListaPartite(String token) {
    		this.token = token;
    	}
    }
    
    //richiesta elenco partite
    public static class RespListaPartite {
        public java.util.List<String> nomiSalvataggi;

        public RespListaPartite() {}
        public RespListaPartite(java.util.List<String> nomiSalvataggi) {
            this.nomiSalvataggi = nomiSalvataggi;
        }
    
    }
}

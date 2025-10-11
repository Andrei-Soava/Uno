//package onegame.server;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import com.corundumstudio.socketio.SocketIOClient;
//import com.corundumstudio.socketio.SocketIOServer;
//
//import onegame.modello.net.ProtocolloMessaggi;
//import onegame.modello.net.Utente;
//import onegame.server.db.PartitaIncompletaDb;
///**
// * Gestisce il salvataggio e il caricaento delle partite offline per utenti registrati.
// * Le partite vengono salavate nel db come stringhe json serializzate.
// */
//public class GestorePartiteOffline {
//	private final PartitaIncompletaDb partitaDb;
//	private final GestoreConnessioni gestoreConnessioni;
//	
//	public GestorePartiteOffline(GestoreConnessioni gestoreConnessioni) {
//		this.partitaDb = new PartitaIncompletaDb();
//		this.gestoreConnessioni = gestoreConnessioni;
//	}
//	
//	//Salvataggio di una partita incompleta
//	public void handleSalvaPartita(SocketIOClient client, ProtocolloMessaggi.ReqSalvaPartita req) {
//		String token = client.get("token");
//		Utente utente = gestoreConnessioni.getUtenteByToken(token);
//		
//		if (utente == null || utente.isAnonimo()) {
//			client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_FAIL, "Solo gli utenti registrati possono salvare partite offline");
//			return;
//		}
//		
//		try {
//			partitaDb.createPartita(utente.getIdGiocatore(), req.getNomeSalvataggio(), req.getPartitaSerializzata());
//			client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_OK, "Partita salvata con successo");
//			System.out.println("[Server] Partita salvata per utente " + utente.getUsername() + " (" + req.getNomeSalvataggio() + ")");
//		} catch (SQLException e) {
//			e.printStackTrace();
//			client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_FAIL, "Errore durante il salvataggio");
//		}
//	}
//	
//	//Caricamento di una partita salvata
//	public void handleCaricaPartita(SocketIOClient client, ProtocolloMessaggi.ReqCaricaPartita req) {
//		String token = client.get("token");
//		Utente utente = gestoreConnessioni.getUtenteByToken(token);
//		
//		if(utente == null || utente.isAnonimo()) {
//			client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_FAIL, "Partita non trovata");
//			return;
//		}
//		
//		try {
//			String partitaJson = partitaDb.getPartitaById(req.getIdSalvataggio());
//			if(partitaJson != null) {
//				client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_OK, partitaJson);
//				System.out.println("[Server] Partita caricata per utente " + utente.getUsername());
//			}else {
//				client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_FAIL, "Partita non trovata");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_FAIL, "Errore durante il caricamento");
//		}
//	}
//	
//	//Restituisce la lista dei salvataggi per un utente
//    public void handleListaSalvataggi(SocketIOClient client) {
//        String token = client.get("token");
//        Utente utente = gestoreConnessioni.getUtenteByToken(token);
//
//        if (utente == null || utente.isAnonimo()) {
//            client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_FAIL, "Solo gli utenti registrati possono visualizzare i salvataggi");
//            return;
//        }
//
//        try {
//            List<String> nomi = partitaDb.getPartiteByUtente(utente.getIdGiocatore());
//            client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_OK, nomi);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            client.sendEvent(ProtocolloMessaggi.EVENT_PARTITA_FAIL, "Errore durante il recupero dei salvataggi");
//        }
//    }
//
//}

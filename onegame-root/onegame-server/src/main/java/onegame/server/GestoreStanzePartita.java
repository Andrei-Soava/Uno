package onegame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;

import onegame.net.MossaDTO;
import onegame.net.messaggi.MessaggiGioco.ReqEffettuaMossa;
import onegame.net.messaggi.MessaggiGioco.RespEffettuaMossa;
import onegame.net.messaggi.MessaggiGioco.RespIniziaPartita;
import onegame.net.util.JsonHelper;
import onegame.server.eccezioni.EccezionePartita;

/**
 * Gestisce le stanze di gioco per le partite.
 */
public class GestoreStanzePartita extends GestoreStanze<StanzaPartita> {

	private static final Logger logger = LoggerFactory.getLogger(GestoreStanzePartita.class);

	public GestoreStanzePartita() {
		super();
	}

	@Override
	protected StanzaPartita creaStanza(int codice, String nome, int maxUtenti) {
		return new StanzaPartita(codice, nome, maxUtenti);
	}

	/**
	 * Gestisce la richiesta di inizio partita da parte di un utente.
	 * @param sessione la sessione dell'utente che richiede l'inizio della partita
	 * @param ack l'oggetto per inviare la risposta di ack
	 */
	public void handleIniziaPartita(Sessione sessione, AckRequest ack) {
		try {
			logger.debug("Richiesta di inizio partita da utente {}",
					(sessione != null) ? sessione.getUsername() : "null");
			if (sessione == null) {
				ack.sendAckData(new RespIniziaPartita(false, "Utente non valido"));
				logger.warn("Accesso negato");
				return;
			}

			StanzaPartita stanza = getStanzaPerSessione(sessione);
			if (stanza == null) {
				ack.sendAckData(new RespIniziaPartita(false, "Stanza non trovata"));
				logger.warn("Sessione {} non associata a nessuna stanza", sessione.getToken());
				return;
			}

			if (!sessione.equals(stanza.getProprietario())) {
				ack.sendAckData(
						new RespIniziaPartita(false, "Solo il proprietario della stanza può avviare la partita"));
				logger.warn("Utente {} non proprietario della stanza {}", sessione.getUsername(), stanza.getNome());
				return;
			}
			if (stanza.getNumSessioni() < 2) {
				ack.sendAckData(
						new RespIniziaPartita(false, "Non ci sono abbastanza giocatori per iniziare la partita"));
				logger.warn("Tentativo di avviare la partita nella stanza {} con meno di 2 giocatori",
						stanza.getNome());
				return;
			}

			stanza.avviaPartita();
			logger.info("Partita avviata nella stanza {} da utente {}", stanza.getNome(), sessione.getUsername());
			ack.sendAckData(new RespIniziaPartita(true, "Partita avviata con successo"));
		} catch (EccezionePartita e) {
			logger.warn("Errore di stanza: {}", e.getMessage());
			ack.sendAckData(new RespIniziaPartita(false, e.getMessage()));
		} catch (Exception e) {
			logger.error("Errore durante l'elaborazione della richiesta di inizio partita", e);
			ack.sendAckData(new RespIniziaPartita(false, "Errore interno del server"));
		}
	}

	/**
	 * Gestisce la richiesta di effettuare una mossa da parte di un utente.
	 * @param sessione la sessione dell'utente che richiede di effettuare la mossa
	 * @param str la stringa JSON contenente i dati della mossa
	 * @param ack l'oggetto per inviare la risposta di ack
	 */
	public void handleEffettuaMossa(Sessione sessione, String str, AckRequest ack) {
		if (sessione == null) {
			ack.sendAckData(new RespEffettuaMossa(false, "Utente non valido"));
			logger.warn("Accesso negato");
			return;
		}

		try {
			ReqEffettuaMossa req = JsonHelper.fromJson(str, ReqEffettuaMossa.class);
			MossaDTO mossa = req.mossa;

			StanzaPartita stanza = getStanzaPerSessione(sessione);
			if (stanza == null) {
				ack.sendAckData(new RespEffettuaMossa(false, "Stanza non trovata"));
				logger.warn("Sessione {} non associata a nessuna stanza", sessione.getToken());
				return;
			}

			stanza.riceviMossa(sessione, mossa);
			ack.sendAckData(new RespEffettuaMossa(true, "Mossa effettuata con successo"));
			logger.info("Mossa ricevuta da utente {}: {}", sessione.getUsername(), mossa);
		} catch (EccezionePartita e) {
			logger.warn("Errore di gioco: {}", e.getMessage());
			ack.sendAckData(new RespEffettuaMossa(false, e.getMessage()));
		} catch (Exception e) {
			logger.error("Errore durante l'elaborazione della mossa", e);
			ack.sendAckData(new RespEffettuaMossa(false, "Errore interno del server"));
		}
	}
}

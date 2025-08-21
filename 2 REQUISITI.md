# 2-REQUISITI 


**Requisiti front-end (NON durante il gioco):**
1.	Gioco avviabile mediante un eseguibile (.jar)
2.	Schermata iniziale di accesso con due opzioni: 1) login (o registrazione) con tasto “Resta collegato”; 2) giocare senza login
3.	L’utente registrato può visualizzare le sue statistiche (es. partite vinte), mentre l’utente non registrato no;
4.	Una volta scelta la modalità d’accesso (login o meno), viene presentata una pagina principale (HOME) con un menù con cosa vogliamo giocare: 1) partita con amici (client-server); 2) partita contro il computer (bots); 3) modalità tutorial OPPURE pagina con regolamento; 4) pulsante “Esci dal gioco”; 5) modalità ESTREMA (a pagamento SOLO per utenti registrati stile gioco d’azzardo e punizioni realistiche) 
  4.1	Due opzioni: Crea una nuova partita (di cui viene fornito un link/codice d’accesso stile kahoot) OPPURE Collegati ad una partita (in cui bisogna inserire il codice d’accesso);
  4.2	Due opzioni: Caricare vecchia partita (solo utenti registrati log delle partite non completate) OPPURE Crea una nuova partita (di cui si scelgono il numero di bots contro cui si vuole giocare;
5.	Ci deve essere la possibilità di navigare (tramite bottoni) da una schermata all’altra del gioco, e anche un buttone che rimandi alla HOME;
6.	Ci deve anche essere l’opzione di log-out (se sì è già loggati) o di login (se si sta giocando senza accesso) SOLO quando non si sta giocando; 


**Requisiti front-end (durante il gioco):**
1.	Messaggio di notifica con scritto “Nome_giocatore, è il tuo turno:” in alto;
2.	Viene mostrata la mano del giocatore, la carta sul banco (con cui bisogna fare gli abbinamenti) e un’indicazione di chi tocca dopo di te oppure di quale è l’ordine dei giocatori (stile orologio);
3.	Ci devono essere due tipi di azioni: 1) pescare una carta dal mazzo (pulsante o con cursore) 2) scegliere con cursore o tasti una carta (evidenziandola) tra quelle nella mano e PROVARE a metterla sul banco (vedere se è compatibile con la carta già presente a livello di colore o numero) messaggio d’errore
  3.1	Di default, il pescaggio è automatico quindi si pesca finchè non c’è una carta che è compatibile con quella sul banco; quando esce una carta “buona” si hanno due opzioni, ossia tenerla nella mano oppure metterla direttamente sul banco; se si sceglie di tenere una carta “buona” (facendo controlli sulla mano per evitare abusi) il pescaggio automatico viene fermato e bisogna farlo ripartire (con le stesse modalità descritte sopra);
  possibile aggiunta di pescaggio manuale (per dare spazio al libero arbitrio);
  3.2	Quando la carta messa sul banco è la penultima, ci deve essere una finestra di tipo 2-3 secondi, in cui appare un pulsante temporaneo con la scritta “ONE!”, che deve essere premuto in quell’intervallo di tempo, altrimenti si pesca una carta in automatico;
4.	Per carte in cui il giocatore in turno deve scegliere un colore per il banco (es +4), appare una finestra dedicata in cui si sceglie uno dei colori con cui si vuole proseguire; 
5.	Una volta che si mette una carta sul banco, si passa al turno del giocatore successivo (quindi il giocatore attuale va in wait);
6.	Mentre è il turno di un altro giocatore, il messaggio di notifica in alto cambia in “E’ il turno di nome_giocatore”, con un campo (informativo) in cui viene detto quante carte ha attualmente in mano;
7.	Quando si usano carte che hanno effetto sul turno del giocatore successivo (es pesca carte oppure blocca turno), appare un messaggio generico in cui viene descritta l’effetto (es “Il giocatore successivo dovrà pescare n carte” oppure “Il giocatore successivo avrà il turno bloccato”);
8.	Quando un giocatore finisce la sua mano, vince (o appare un messaggio sullo schermo del vincitore in cui si dice che ha vinto, oppure appare un messaggio sullo schermo dei perdenti in cui si specifica il nome del vincitore);
9.	Una volta finita la partita, nel messaggio di conclusione si presentano due opzioni: tasto HOME (ti riporta alla schermata iniziale) OPPURE “Tasto rivincita/gioca di nuovo” (che, se premuto, fa partire una nuova partita o con gli amici ( da gestire casi in cui qualcuno smette di giocare) o con il computer (viene fatta partire una nuova partita con lo stesso numero di bots));
10.	Opzione durante il gioco di abbandonare la partita (bottone “Abbandona la partita” che apre un messaggio di conferma se si vuole abbandonare o meno la partita) si viene riportati sulla HOME qualora si premesse “Conferma”, mentre se si clicca “Annulla”, si continua a giocare;


**Requisiti back-end (generici):**
1.  Utilizzo di Java con Eclipse, in particolare di Maven (per la gestione delle dipendenze singolarmente);
2.  Utilizzo di Papyrus con Eclipse, per la creazione di diagrammi UML (in particolare generazione di codice Java a partire da diagramma di classe);
3.  Utilizzo della libreria Log4j per la gestione dei log;
4.  Utilizzo di Swing per l'interfaccia grafica;
5.  Utilizzo di PostgresSQL per il database utenti (si tiene conto degli account, eventuale saldo nel portafoglio, lo storico delle partite e le partite in sospeso);
6.  Utilizzo di Model-View-Controller come design pattern, visto che si sviluppa un gioco;

**Requisiti back-end del server:**
1.  Gestione delle richieste per partite multigiocatore mediante una pool di threads, responsabili della sincronizzazione sulle azioni delle singole partite;
2.  Il server di gioco deve essere sempre attivo, indipendemente che ci siano utenti o meno con l'applicazione aperta;
3.  Il server è responsabile del collegamento al database per la gestione dell'autenticazione e per il salvataggio delle partite (se vsBots: salva le partite incomplete);
4.  Il server viene richiamato ogni volta che un utente crea una nuova partita con amici (logica client-server)--> creazione lobby con associato un codice numerico (autogenerato) che serve agli altri utenti che si vogliono collegare
5.  Il server NON gestisce la partita con amici in sè--> quando la lobby "si chiude", la partita verrà gestita dagli utenti (host-p2p, magari con un server dedicato);
6.  Il server viene SEMPRE richiamato al termine di una partita (o dopo averne abbandonata una) per aggiornare le statistiche del database;

**Requisiti back-end del database:**
1. Nel database del gioco devono essere presenti:
  1.1  una tabella UTENTE con campi: id (chiave primaria, ad auto-incremento), nickname (stringa, unique), password (stringa), vittorie (intero positivo);
  1.2  una tabella PARTITA_INCOMPLETA con campi: id (chiave primaria, ad auto-incremento), idUtente (chiave esterna), dettagliPartita (stringa oppure oggetto JSON);
2. Il database del gioco NON gestisce le partite multigiocatore, ma SOLO quelle vsBots INCOMPLETE;
3. Il database deve essere aggiornato ogni volta che un utente vince o perde una partita (indipendemente che sia contro Bots o meno)--> aggiornamento delle statistiche;
4. Il database deve considerare la possibilità che gli utenti vengano eliminati (on delete cascade sulle partite) o che vengano aggiornati i rispettivi dati   (se nickname  --> verifica unicità);
5. Il database deve considerare la possibilità che le partite in sospeso vengano eliminate dagli utenti;

 







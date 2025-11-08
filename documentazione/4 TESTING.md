# Piano di Testing

## **Obiettivi del testing**
- Verificare la correttezza della logica di gioco (regole, turni, effetti carte)
- Assicurare la stabilità e l’assenza di errori runtime
- Validare l’integrazione progressiva di GUI, rete e persistenza
- Garantire la coerenza con i requisiti funzionali del documento 1 REQUISITI

---

## **Struttura del piano**
Per ogni fase di sviluppo verranno elencati:
- **Ambito**: cosa si testa
- **Casi di test principali**
- **Criteri di superamento**
- **Tabella riassuntiva con colonna "Risultati"**: dove verranno inseriti gli esiti man mano (acronimi: TBE--> To Be Evaluated; TBP--> To Be Proven)

---

## **Metodologie**
- I test verranno eseguiti in parte manualmente (GUI, UX) e in parte automatizzati (JUnit 4 per Model).
- I test automatizzati verranno messi nella cartella "tests" del progetto Maven.
- La colonna “Risultati” di ogni tabella verrà compilata man mano che si sa va avanti con l'implementazione delle fasi.

---

### **Fase 1 – Core logico (Model puro)**
**Ambito**: classi `Carta`, `Mazzo`, `PilaScarti`, `Mano`, `Giocatore` (+ sottoclassi), `Partita`.

**Casi di test**
1. Creazione mazzo completo e mescolamento
2. Pesca carta da mazzo → riduzione conteggio carte
3. Giocata carta valida/invalid → verifica regole colore/numero
4. Applicazione effetti carte speciali (+2, +4, cambio colore, inverti, salta)
5. Cambio turno e direzione
6. Ricostruzione mazzo
7. Rilevamento fine partita (mano vuota) e vittoria

**Criteri di superamento**
- Tutte le regole rispettate
- Nessun crash o comportamento anomalo
- Stato coerente dopo ogni azione

**Tabella riassuntiva**
| ID Test | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|----------------|---------------|-----------|
| T1.1    | Creazione mazzo completo e mescolamento. Verifico che la prima carta presente nel file mazzo sia diversa dalla prima carta di un nuovo mazzo | Istanziare `Mazzo` e chiamare `mescola()` | Mazzo con tutte le carte in ordine casuale | classe `MazzoTest`, test 1.5; positivo|
| T1.2    | Pesca carta da mazzo | Chiamare `pesca()` | Restituisce una carta e riduce il numero di carte nel mazzo | classe `MazzoTest`, test 1.3/1.4; positivi |
| T1.3    | Giocata carta valida | Giocatore gioca carta compatibile con quella sul banco | Carta accettata e aggiornata pila scarti | classe `PartitaFunzioniAvanzateTest`, test 1.6/1.15; positivi |
| T1.4    | Giocata carta non valida | Giocatore gioca carta incompatibile | Messaggio di errore, stato invariato | classe `PartitaFunzioniAvanzateTest`, test 1.16; positivo |
| T1.5    | Applicazione effetto carta speciale | Giocare +2, +4, inverti, salta | Effetto applicato correttamente | classe `PartitaFunzioniAvanzateTest`, test 1.14/1.22; positivi |
| T1.6    | Cambio turno | Chiamare `eseguiUnTurno()` | Passa al giocatore successivo (o precedente se invertito) | classe `PartitaFunzioniBaseTest`, test 1.10/1.11/1.12; positivi |
| T1.7    | Ricostruzione mazzo | Creare mazzo e pila, associandoli; svuotare mazzo; pescare dal mazzo | Mazzo passa da 0 carte a 107 (dopo aver pescato) | classe `MazzoTest`, test 1.7; positivo |
| T1.8    | Fine partita | Mano di un giocatore = 0 | Dichiarazione vincitore | classe `PartitaFunzioniAvanzateTest`, test 1.23; positivo |
---

### **Fase 2 – Controller offline (console)**
**Ambito**: interazione testuale con il Model.

**Casi di test**
1. Input valido → azione corretta sul Model
2. Input invalido → messaggio di errore e stato invariato
3. Gestione “ONE!” in modalità console

**Criteri di superamento**
- Comandi corretti eseguiti
- Errori gestiti senza bloccare il gioco

**Tabella riassuntiva**
| ID Test | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|----------------|---------------|-----------|
| T2.1    | Input valido da console | Comando “pesca” o “gioca” | Azione eseguita sul Model | classe `PartitaFunzioniAvanzateTest`, test 1.15/1.18/1.19/1.20/1.21; positivi |
| T2.2    | Input invalido da console | Comando errato | Messaggio di errore, stato invariato | test1.16; positivo |
| T2.3    | Gestione ONE in console | Chiamata ONE con 1 o più carte in mano | Messaggio di chiamata corretta o errata | Testato manualmente con gui; positivo |
---

### **Fase 3 – Persistenza locale**
**Ambito**: salvataggio/caricamento partite vs bot.

**Casi di test**
1. Salvataggio partita in corso → file/JSON creato
2. Caricamento partita salvata → stato identico a quello salvato
3. Caricamento con file corrotto → messaggio di errore

**Criteri di superamento**
- Stato ripristinato correttamente
- Errori gestiti senza crash

**Tabella riassuntiva**
| ID Test | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|----------------|---------------|-----------|
| T3.1    | Salvataggio partita | Stato partita in corso | File/JSON creato con dati corretti | Testato manualmente; positivo (ad ora NON esiste più persistenza locale nel branch default --> utilizzare eventualmente branch `ONEversioneJavaFX`) |
| T3.2    | Caricamento partita | File/JSON valido | Stato partita ripristinato | Testato manualmente; positivo (ad ora NON esiste più persistenza locale nel branch default --> utilizzare eventualmente branch `ONEversioneJavaFX`) |
| T3.3    | Caricamento file corrotto | File non valido | Messaggio di errore | TBE |
---

### **Fase 4 – GUI JavaFX (offline)**
**Ambito**: classi `view` e collegamento con Controller.

**Casi di test**
1. Navigazione tra schermate (Login → Home → Game)
2. Aggiornamento grafico mano giocatore dopo pesca/giocata
3. Visualizzazione notifiche di turno
4. Bottone “ONE!” e selezione colore funzionanti

**Criteri di superamento**
- Eventi GUI correttamente instradati al Controller
- Stato grafico coerente con il Model

**Tabella riassuntiva**
| ID Test | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|----------------|---------------|-----------|
| T4.1    | Navigazione schermate | Click pulsanti menu | Apertura schermata corretta | Testato manualmente; positivo |
| T4.2    | Aggiornamento mano grafica | Pesca o gioca carta | GUI aggiornata con nuove carte | Testato manualmente; positivo |
| T4.3    | Notifica turno | Cambio turno | Messaggio “È il tuo turno” aggiornato | Testato manualmente; positivo |
| T4.4    | Dialog ONE! | Penultima carta | Dialog mostrato, penalità se non premuto | Testato manualmente; positivo |
---

### **Fase 5 – Connessione e autenticazione client-server**
**Ambito**: avvio server, connessione, disconnessione, login, registrazione e logout dei client.
Package `onegame.server`: `ServerUno`, `GestoreConnessioni`, `GestoreSessioni`, `Sessione`
Package `onegame.server.db`
Modulo `onegame-client`: `ClientSocket`, gestori e viste collegati

Per tutti i test si controllano i log nel server.

**Tabella riassuntiva**
| ID Test | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|----------------|---------------|-----------|
| T5.1    | Avvio server e creazione db | Avvio server | Server avviato e database creato. | Testato manualmente, positivo |
| T5.2    | Connessione client | Avvio di più client con server attivo | I client si collegano al server. Il client mostra che è connesso. | Testato manualmente, positivo |
| T5.3    | Disconnessione client | Disconnessione di un client connesso | Il server logga la disconnessione. | Testato manualmente, positivo |
| T5.4    | Login corretto | Il client invia una richiesta di login corretta con username e password. | Il client mostra l'avvenuto login. | Testato manualmente, positivo |
| T5.5    | Login scorretto | Il client invia una richiesta di login scorretta con username e password sbagliati (username non esiste, password sbagliata). | Il client mostra l'errore. | Testato manualmente, positivo |
| T5.6    | Registrazione corretta | Il client invia una richiesta di registrazione corretta con username e password. | Il client mostra l'avvenuta registrazione. Il nuovo utente viene aggiunto nella tabella `utente`. | Testato manualmente, positivo |
| T5.7    | Registrazione scorretta | Il client invia una richiesta di registrazione scorretta con username già usato o username e password che non soddisfano i requisiti. | Il client mostra l'errore. | Testato manualmente, positivo |
| T5.8    | Logout | Il client autenticato invia una richiesta di logout. | Il client mostra il logout. | Testato manualmente, positivo |
---

### **Fase 6 – Integrazione DB H2**
**Ambito**: creazione nuovi utenti, login, cambio username, eliminazione account, gestione salvataggi delle partite incomplete
Pacchetto `onegame.server.db`
Classe `ClientSocket`
Gestori vari del server

Per tutti i test si controllano i log nel server.

**Tabella riassuntiva**
| ID Test | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|----------------|---------------|-----------|
| T6.1    | Registrazione utente | Dati validi | Record creato in tabella `utente`. | Testato manualmente; positivo |
| T6.2    | Login corretto | Credenziali valide | Accesso consentito. | Testato manualmente; positivo |
| T6.3    | Login errato | Credenziali errate | Accesso negato. | Testato manualmente; positivo |
| T6.4    | Aggiornamento numero di partite vinte | Fine partita | Numero di partite vinte incrementato per il vincitore. | Testato manualmente; positivo |
| T6.5    | Aggiornamento numero di partite giocate | Inizio partita | Numero di partite giocate incrementato per tutti gli utenti nella stanza di gioco. | Testato manualmente; positivo |
| T6.6    | Salvataggio partita incompleta | Stato partita | Record creato in `partita_incompleta` | Testato manualmente; positivo |
| T6.7    | Caricamento partita incompleta | Richiesta di caricamento dal client con nome del salvataggio | Salvataggio correttamente inviato al client. | Testato manualmente; positivo |
| T6.8    | Rinomina salvataggio | Richiesta di rinomina dal client con vecchio nome e nuovo nome del salvataggio | Salvataggio rinominato. | Testato manualmente; positivo |
| T6.9    | Eliminazione salvataggio | Richiesta di eliminazione dal client con nome del salvataggio | Salvataggio della partita incompleta eliminato. | Testato manualmente; positivo |
| T6.10   | Aggiornamento salvataggio | Richiesta di aggiornamento dal client | Salvataggio aggiornato. | Testato manualmente; positivo |
| T6.11   | Cambio username | Richiesta di cambio username | Username cambiato. | Testato manualmente; positivo |
| T6.12   | Eliminazione account | Richiesta di eliminazione account | Account eliminato. Eliminato record relativo nella tabella `utente` e i salvataggi dell'utente. | Testato manualmente; positivo |
---

### **Fase 7 – Gestione stanze**
**Ambito**: creazione stanze, ingresso stanza, abbandona stanza
Classi `ServerUno`, `Stanza`, `GestoreStanze`

Per tutti i test si controllano i log nel server.

**Tabella riassuntiva**
| ID Test | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|----------------|---------------|-----------|
| T7.1    | Creazione stanza | Richiesta di creazione stanza. | Stanza creata e creatore inserito nella stanza. Creatore è proprietario della stanza. | Testato manualmente; positivo |
| T7.2    | Ingresso stanza | Un utente richiede di entrare in una stanza. | Utente aggiunto se non nella stanza e tutti i membri della stanza notificati. Utente non aggiunto se già nella stanza. | Testato manualmente; positivo |
| T7.3    | Abbandona stanza | Un utente richiede di abbandonare la stanza. | L'utente abbandona la stanza e tutti i membri della stanza notificati. Se stanza vuota, viene eliminata. Altrimenti viene eletto un nuovo proprietario. | Testato manualmente; positivo |

### **Fase 8 – Gestione sessioni**
**Ambito**: gestione sessioni, eliminazione sessioni scadute
Classi: `GestoreSessioni`, `GestoreStanze`, `GestoreStanzePartita`, `StanzaPartita`

Per tutti i test si controllano i log nel server.

**Tabella riassuntiva**
| ID Test | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|----------------|---------------|-----------|
| T8.1    | Eliminazione sessione scaduta. | Sessione non connessa per un tempo superiore al tempo di timeout | La sessione viene eliminata da `GestoreSessioni`. Se la sessione è in una `Stanza`, viene rimossa dalla stanza. Se la sessione è in una `StanzaPartita` e la partita è in corso, il giocatore corrispondente diventa un bot. Se la stanza è vuota, la partita viene interrotta. | Testato manualmente; positivo |
| T8.1    | Sessione abbandona `StanzaPartita`. | Sessione abbandona `StanzaPartita`. | Se la partita è in corso, il giocatore corrispondente diventa un bot. Se la stanza è vuota, la partita viene interrotta. | Testato manualmente; positivo |

### **Fase 9 – Gioco online**
**Ambito**: gioco online tra più client nella stessa stanza
Modulo `onegame-server`: classi: `PartitaNET`, `GestoreStanzePartita`, `StanzaPartita`, modello di gioco del server
Modulo `onegame-client`: tutti i gestori e le viste della partita online

In questa fase si verifica il corretto funzionamento della partita di ONE quando clients e server sono sulla stessa macchina.
Viene verificato il corretto funzionamento attraverso le interfacce dei client e i log del server.
Sono state testate tutte le funzionalità:
- Inizio partita: distribuzione carte a tutti i giocatori
- Gioca carta: il client gioca una carta (consentito/non consentito, compatibile/non compatibile, in mano/non in mano)
- Pesca carta: il client pesca una carta (consentito/non consentito)
  - Gioca carta pescata: il client gioca la carte pescata
  - Passa turno: il client non gioca la carta pescata
- Timer del turno:
  - Avvio timer del turno all'inizio della partita e all'inizio di ogni turno
  - Effettua mossa di pesca allo scadere del timer
- Bot: un bot effettua mosse automaticamente
- Dichiara UNO:
  - Avvia timer dichiara UNO quando un giocatore gioca una carta e ha solo una carta in mano
  - Penalità di 2 carte se il giocatore non dichiara UNO in tempo
  - Il gioco continua senza penalità se il giocatore dichiara UNO in tempoù
- Fine partita: quando una partita finisce, viene annunciato il vincitore e la partita termina

I test effettuati hanno dato esito positivo.
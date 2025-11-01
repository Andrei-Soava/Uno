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
| ID Test | Fase di sviluppo | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|------------------|----------------|---------------|-----------|
| T1.1    | Fase 1 – Model   | Creazione mazzo completo e mescolamento. Verifico che la prima carta presente nel file mazzo sia diversa dalla prima carta di un nuovo mazzo | Istanziare `Mazzo` e chiamare `mescola()` | Mazzo con tutte le carte in ordine casuale | classe `MazzoTest`, test 1.5; positivo|
| T1.2    | Fase 1 – Model   | Pesca carta da mazzo | Chiamare `pesca()` | Restituisce una carta e riduce il numero di carte nel mazzo | classe `MazzoTest`, test 1.3/1.4; positivi |
| T1.3    | Fase 1 – Model   | Giocata carta valida | Giocatore gioca carta compatibile con quella sul banco | Carta accettata e aggiornata pila scarti | classe `PartitaFunzioniAvanzateTest`, test 1.6/1.15; positivi |
| T1.4    | Fase 1 – Model   | Giocata carta non valida | Giocatore gioca carta incompatibile | Messaggio di errore, stato invariato | classe `PartitaFunzioniAvanzateTest`, test 1.16; positivo |
| T1.5    | Fase 1 – Model   | Applicazione effetto carta speciale | Giocare +2, +4, inverti, salta | Effetto applicato correttamente | classe `PartitaFunzioniAvanzateTest`, test 1.14/1.22; positivi |
| T1.6    | Fase 1 – Model   | Cambio turno | Chiamare `eseguiUnTurno()` | Passa al giocatore successivo (o precedente se invertito) | classe `PartitaFunzioniBaseTest`, test 1.10/1.11/1.12; positivi |
| T1.7    | Fase 1 – Model   | Ricostruzione mazzo | Creare mazzo e pila, associandoli; svuotare mazzo; pescare dal mazzo | Mazzo passa da 0 carte a 107 (dopo aver pescato) | classe `MazzoTest`, test 1.7; positivo |
| T1.8    | Fase 1 – Model   | Fine partita | Mano di un giocatore = 0 | Dichiarazione vincitore | classe `PartitaFunzioniAvanzateTest`, test 1.23; positivo |
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
| ID Test | Fase di sviluppo | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|------------------|----------------|---------------|-----------|
| T2.1    | Fase 2 – Controller offline | Input valido da console | Comando “pesca” o “gioca” | Azione eseguita sul Model | classe `PartitaFunzioniAvanzateTest`, test 1.15/1.18/1.19/1.20/1.21; positivi |
| T2.2    | Fase 2 – Controller offline | Input invalido da console | Comando errato | Messaggio di errore, stato invariato | test1.16; positivo |
| T2.3    | Fase 2 – Controller offline | Gestione ONE in console | Chiamata ONE con 1 o più carte in mano | Messaggio di chiamata corretta o errata | Testato manualmente con gui; positivo |
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
| ID Test | Fase di sviluppo | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|------------------|----------------|---------------|-----------|
| T3.1    | Fase 3 – Persistenza locale | Salvataggio partita | Stato partita in corso | File/JSON creato con dati corretti | Testato manualmente; positivo (ad ora NON esiste più persistenza locale nel branch default --> utilizzare eventualmente branch `ONEversioneJavaFX`) |
| T3.2    | Fase 3 – Persistenza locale | Caricamento partita | File/JSON valido | Stato partita ripristinato | Testato manualmente; positivo (ad ora NON esiste più persistenza locale nel branch default --> utilizzare eventualmente branch `ONEversioneJavaFX`) |
| T3.3    | Fase 3 – Persistenza locale | Caricamento file corrotto | File non valido | Messaggio di errore | TBE |
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
| ID Test | Fase di sviluppo | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|------------------|----------------|---------------|-----------|
| T4.1    | Fase 4 – GUI JavaFX | Navigazione schermate | Click pulsanti menu | Apertura schermata corretta | Testato manualmente; positivo |
| T4.2    | Fase 4 – GUI JavaFX | Aggiornamento mano grafica | Pesca o gioca carta | GUI aggiornata con nuove carte | Testato manualmente; positivo |
| T4.3    | Fase 4 – GUI JavaFX | Notifica turno | Cambio turno | Messaggio “È il tuo turno” aggiornato | Testato manualmente; positivo |
| T4.4    | Fase 4 – GUI JavaFX | Dialog ONE! | Penultima carta | Dialog mostrato, penalità se non premuto | Testato manualmente; positivo |
---

### **Fase 5 – Server e multiplayer**
**Ambito**: `GameServer`, `ClientHandler`, `LobbyManager`, `Lobby`, `NetworkService`.

**Casi di test**
1. Avvio server e connessione di più client
2. Creazione lobby
3. Join lobby
4. Sincronizzazione stato partita tra client
5. Disconnessione di un giocatore → gestione corretta

**Criteri di superamento**
- Nessuna desincronizzazione
- Lobby gestite correttamente
- Errori di rete gestiti

**Tabella riassuntiva**
| ID Test | Fase di sviluppo | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|------------------|----------------|---------------|-----------|
| T5.1    | Fase 5 – Server   | Connessione client | Avvio server e connessione client | Connessione stabilita | |
| T5.2    | Fase 5 – Server   | Creazione lobby | Richiesta creazione | Lobby creata con codice univoco | |
| T5.3    | Fase 5 – Server   | Join lobby | Inserimento codice valido | Giocatore aggiunto alla lobby | |
| T5.4    | Fase 5 – Server   | Sincronizzazione stato | Giocata carta da un client | Stato aggiornato su tutti i client | |
| T5.5    | Fase 5 – Server   | Disconnessione giocatore | Giocatore abbandonda partita online | Creazione di bot al suo posto e aggiornamento statistiche | |
---

### **Fase 6 – Integrazione DB PostgreSQL**
**Ambito**: `DatabaseService`, `UtenteDAO`, `PartitaDAO`, `AuthService`, `StatsService`.

**Casi di test**
1. Registrazione nuovo utente → record creato
2. Login con credenziali corrette
3. Login con credenziali errate
4. Aggiornamento statistiche a fine partita
5. Salvataggio partite incomplete
6. Caricamento partite incomplete

**Criteri di superamento**
- Operazioni DB corrette e persistenti
- Errori gestiti senza crash

**Tabella riassuntiva**
| ID Test | Fase di sviluppo | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|------------------|----------------|---------------|-----------|
| T6.1    | Fase 6 – DB       | Registrazione utente | Dati validi | Record creato in tabella UTENTE | |
| T6.2    | Fase 6 – DB       | Login corretto | Credenziali valide | Accesso consentito | |
| T6.3    | Fase 6 – DB       | Login errato | Credenziali errate | Accesso negato | |
| T6.4    | Fase 6 – DB       | Aggiornamento statistiche | Fine partita | Vittorie incrementate in DB | |
| T6.5    | Fase 6 – DB       | Salvataggio partita incompleta | Stato partita | Record creato in PARTITA_INCOMPLETA | |
| T6.6    | Fase 6 – DB       | Caricamento partita incompleta | ID utente | Stato partita ripristinato | |
---

### **Fase 7 – Rifiniture**
**Ambito**: requisiti extra (tutorial, regolamento, UX).

**Casi di test**
1. Accesso e navigazione tutorial/regolamento
2. Messaggi di errore chiari e coerenti
3. Funzionalità “Rivincita” e “Abbandona partita”

**Criteri di superamento**
- Tutte le funzioni extra operative
- Esperienza utente fluida

**Tabella riassuntiva**
| ID Test | Fase di sviluppo | Descrizione test | Input / Azione | Output atteso | Risultato |
|---------|------------------|------------------|----------------|---------------|-----------|
| T7.1    | Fase 7 – Rifiniture | Accesso tutorial | Click su “Tutorial” | Mostra schermata regolamento | |
| T7.2    | Fase 7 – Rifiniture | Rivincita | Click su “Rivincita” | Nuova partita avviata | |
| T7.3    | Fase 7 – Rifiniture | Abbandona partita | Click su “Abbandona” | Conferma e ritorno a Home | |

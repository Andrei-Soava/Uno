# Specifica dei Requisiti

---

## 1. Introduzione

### 1.1 Obiettivo del documento
Il presente documento descrive in modo esaustivo e strutturato i requisiti funzionali e non funzionali del videogioco di carte “UNO-like” basato su Java.  
La specifica dei requisiti è redatta in conformità con lo standard IEEE 830 al fine di garantire una struttura chiara, tracciabile e comprensibile delle parti coinvolte.  
Il presente documento rappresenta un riferimento ufficiale per tutte le fasi successive del ciclo di vita del software, includendo progettazione, implementazione, test, validazione e manutenzione. Esso mira a garantire una comprensione condivisa del sistema tra tutti gli stakeholder riducendo ambiguità e assicurando la tracciabilità dei requisiti.

### 1.2 Scopo del gioco
Il gioco “UNO-like” (ispirato al classico gioco di carte “UNO”) è stato progettato per offrire un’esperienza interattiva, fluida e accessibile a qualsiasi tipo di utente.
Il sistema ha l’obiettivo di fornire una piattaforma modulare e scalabile, che permetta l’integrazione di nuove funzionalità in futuro senza compromettere la stabilità e le prestazioni del software.    

   - ### 1.2.1 Svolgimento del gioco
   Il gioco consente ai giocatori di partecipare a partite in modalità offline (contro dei bot) oppure in modalità multigiocatore online contro altri utenti.
   I giocatori possono scegliere se registrarsi o partecipare come utenti anonimi. Tuttavia, la modalità anonima comporta alcune limitazioni: i giocatori non registrati non possono accedere alle statistiche delle partite né salvare le partite non ancora terminate prima di abbandonarle.
### 1.3 Definizioni, acronimi e abbreviazioni
- Bot: avversario controllato da un computer (cioè un computer imita le mosse che farebbe un umano);
- Utente registrato: utente che, dopo l'avvio del gioco, effettua una registrazione (nel caso non fosse registrato) oppure un login (nel caso fosse già registrato). Esso può controllare le statistiche delle sue partite e può accedere ai salvataggi delle partite non terminate (solo quelle lato offline);
- Utente anonimo: utente che non si è registrato prima di iniziare a giocare. Esso sarà identificato con un username dato del server (ad esempio: utente#754);



## 2. Descrizione generale 
   ### 2.1 Prospettiva del prodotto
   "UNO-like” è una rivisitazione digitale del celebre gioco di carte UNO.  
   Il software è sviluppato interamente in Java e può essere eseguito su diversi sistemi operativi, purché sia installata una versione compatibile del Java Runtime Environment (JRE). 
   ### 2.2 Funzionalità del prodotto
   UNO-like avrà le seguenti funzionalità:
   - Registrazione e autenticazione: ogni utente può scegliere se registrarsi prima di giocare. Gli utenti già registrati possono autenticarsi tramite nome utente e password per accedere alle proprie partite e statistiche;
   - Modalità di gioco offline e online: il giocatore può partecipare a partite in modalità offline (contro un bot) oppure in modalità online (contro altri utenti connessi).
   - Salvataggio e ripresa delle partita non terminate: in modalità offline gli utenti registrati possono salvare una partita in corso e riprenderla successivamente. Gli utenti non registrati non dispongono di questa funzionalità;
   - Gestione delle statistiche personali: gli utenti registrati possono visualizzare le proprie statistiche, comprendenti numero di partite giocate, vinte e perse, e altre informazioni di riepilogo. Gli utenti non registrati non dispongono di questa funzionalità;
   - Regolamento: nel menù principale è disponibile una sezione dedicata al regolamento ufficiale del gioco, accessibile a tutti gli utenti;
   ### 2.3 Caratteristiche dell'utente
   Il gioco “UNO-like” è rivolto a un pubblico ampio, composto sia da giocatori occasionali sia da utenti abituali del gioco di carte UNO.  
   Non sono richieste competenze tecniche particolari per utilizzarlo: l’interfaccia grafica è progettata per essere intuitiva, chiara e facilmente comprensibile da qualsiasi tipo di utente.
   ### 2.4 Vincoli
   Il sistema "UNO-like" presenta i seguenti vincoli:
   - il gioco dove essere eseguito su un sistema dotato di JDK 21 o una versione successiva;
   - il software deve risultare compatibile con i sistemi operativi Windows, Linux e macOS;
   ### 2.5 Presupposti e dipendenze
   Il funzionamento del sistema "UNO-like" si basa sui seguenti presupposti e dipendenze.  
   Pressuposti: 
   - Si presuppone che l’utente disponga di un computer con JDK 21 o versione successiva installato;
   - Si presuppone che l’utente abbia familiarità con l’utilizzo di dispositivi informatici di base (es. computer, tastiera e mouse);
   - Si presuppone che ogni utente disponga di una connessione Internet stabile per poter giocare in modalità online;
   Dipendenze:
   - Il corretto funzionamento della modalità online dipende dalla disponibilità e stabilità del server di gioco;
   - Il sistema dipende dal database integrato nel server, utilizzato per la memorizzazione delle credenziali, delle statistiche e dello stato delle partite salvate;

## 3. Requisiti Specifici
### 3.1 Requisiti funzionali
   - ### 3.1.1 Gestione dell'autenticazione
      - RF1: il sitema deve permettere la registrazione di nuovi utenti tramite l'inserimento di nome utente e password;
      - RF2: il sistema deve consentire il login agli utenti già registrati;
      - RF3: il sistema deve permettere l'accesso come "ospite", ovvero l'accesso al gioco senza registrazione;
   - ### 3.1.2 Gestione della modalità di gioco
      - RF4: il sistema deve consintire la selezione della modalità di gioco: 
         - gioca con amici (online);
         - gioca contro computer (offline);
      - RF5: in modalità offline, il giocatore deve poter scegliere se iniziare una nuova partita o caricarne una salvata (solo per gli utenti registrati);
      - RF6: in modalità online, il sistema deve connettersi al server di gioco e gestire la comunicazione tra i giocatori.
      (completare la modalità online)
   - ### 3.1.3 Configurazione della partita offline
      - RF7: il sistema deve permettere la selezione del numero di giocatori (da 2 a 4);
      - RF8: dopo aver configurato la partita, l'utente può decidere se annulare la configurazione oppure avviare la partita;
      - RF9: il sistema deve distribuire casualmente le carte ad ogni giocatore;
   - ### 3.1.4 Svolgimento della partita
      - RF10: il sistema deve basarsi sul regolamento deciso (visibile nella home del gioco);
      - RF11: ogni giocatore deve poter effettuare una mossa scegliendo una carta compatibile con l’ultima giocata;
      - RF12: il sistema deve controllare la validità delle mosse e impedire quelle non consentite;
      - RF13: il sistema deve rilevare automaticamente la vittoria di un giocatore;
   - ###  3.1.5 Gestione del salvataggio nella modalità offline
      - RF14: gli utenti registrati devono poter salvare lo stato di una partita in corso;
      - RF15: 1l sistema deve consentire di caricare una partita precedentemente salvata;
   - ### 3.1.6 Visualizzazione delle statistiche
      - RF16: il sistema deve mostrare le statistiche personali degli utenti registrati (numero di partite giocate, vinte, perse, ecc.);
      - RF17: gli utenti anonimi non devono poter accedere alle statistiche;
   - ### 3.1.7 Visualizzazione del regolamento
      - RF18: il sistema deve offrire una sezione “Regolamento” accessibile dal menù principale;
      - RF19: il regolamento deve descrivere in modo chiaro le regole del gioco "UNO-like";
   - ### 3.1.8 Logout e disconnessione
      - RF20: il sistema deve permettere di effettuare il logout in qualsiasi momento;
### 3.2 Requisiti non funzionali
   - ### 3.2.1 Usabilità
      - RNF1: l'interfaccia grafica deve essere intuitiva, colorata e di facile comprensioneper gli utenti;
      - RNF2: i pulsanti devono avere un feedback visivo al passaggio del mouse o alla pressione;
   - ### 3.2.2 Prestazioni
      - RFN3: il sistema deve avviarsi entro 5 secondi dall'esecuzione;
      - RFN4: le mosse di gioco devono essere elaborate in meno di 1 secondo;
   - ### 3.2.3 Affidabilità
      - RFN5: il sistema deve garantire il salvataggio corretto della partita in caso di chiusura improvvisa (solo per gli utenti registrati);
      - RFN6: il server deve garantire un’elevata disponibilità e affidabilità nella maggior parte del tempo di utilizzo;
   - ### 3.2.4 Portabilità
      - RFN6: il gioco deve essere eseguibile su Windows, Linux e macOS, con JDK 21 o versione successiva installata;
   - ### 3.2.5 Sicurezza
      - RFN7: le credenziali, degli utenti registrati, devono essere memorizzate nel database. Le password devono essere crittografate;
      - RFN8: le comunicazioni tra client e server deveno avvenire tramite protocollo sicuro;
### 3.3 Requisiti di interfaccia 
   - ### 3.3.1 Schermata di Login
      - RI1: campi di testo:
         - nome utente;
         - password (campo oscurato);
      - RI2: pulsanti:
         - "Accedi": consente l'accesso agli utenti registrati;
         - "Registrati": permette ai nuovi utenti di creare un account;
         - "Entra come opsite": consente di avviare il gioco senza autenticazione;
      - RI3: in caso di errore nell’inserimento delle credenziali, deve comparire un messaggio di errore informativo;
   - ### 3.3.2 Menù principale
      - RI4: dopo l’accesso, l’utente visualizza il menù principale con le seguenti opzioni:
         - "Gioca con amici": pulsante che consente di entrare nella modalità online;
         - "Gioca contro computer": pulsante che consente di entrare nella modalità offline;
         - "Regolamento": pulsante che consente di leggere il regolamento del gioco;
         - "Mostra statistiche": pulsante che consente di leggere le statistiche personali (solo per gli utenti registrati);
         - "Logout": pulsante che consente la disconnessione dal gioco;
      - RI5: in basso alla schermata del menù principale si visualizzerà:
         - lo stato di connessione ("Connessione"/"Disconnessione") al server;
         - il tipo di utente (se l'utente è anonimo oppure registrato);
   - ### 3.3.3 Schermata "Gioca contro computer"
      - RI6: l'utente può scegliere:
         - se giocare una nuova partita (questa funzione è disponibile per tutti gli utenti);
         - se caricare una partita già iniziata (funzione disponibile solo per gli utenti registrati);
      - RI7: pulsanti di navigazione:
         - "Home": consente di tornare al menù principale;
         - "Logout": disconnette l'utente dal sistema;
   - ### 3.3.4 Configurazione nuova partita
      - RI8: l'utente può selezionare il numero di giocatori (minimo 2, massimo 4);
      - RI9: pulsanti: 
            - "Avvia partita": consente di avviare la partita;
            - "Annulla": consente di annullare la configurazione della partita e tornare alla schermata precedente;
   - ### 3.3.5 Regolamento
      - RI10: visualizza le regole del gioco "Uno-like", è composto da una parte testatuale e da delle immagini inerenti al gioco
      - RI11: pulsanti:
         - "Avanti": passaggio alla pagina successiva;
         - "Indietro": passaggio alla pagina precedente;
   - ### 3.3.6 Satistiche
      - RI12: mostra i dati relativi alle partite giocate: numero di vittorie, sconfitte, ecc.;
      - RI13: sezione disponibile solo per gli utenti registrati;
### 3.4 Requisiti interfaccia hardware e software
   - ### 3.4.1 Interfaccia hardware
      - RIHS1: il sistema non richiede dispositivi hardware specifici oltre a un computer, mouse e tastiera;
   - ### 3.4.2 Interccia software
      - RIHS2: il server utilizza la libreria Socket.IO per la gestione delle connessioni tra client e server;
      - RIHS3: la comunicazione è di tipo event-driven, cioè basata su eventi personalizzati (es. auth:login);
      - RIHS4: il server ha la funzione di:
         - Gestire la connessione e disconnessione dei client;
         - Gestire gli eventi di autenticazione e registrazione;
         - Creare ed eliminare le stanze di gioco per le partite online;
         - Gestire il salvataggio delle partite offline;
         - Gestire le mosse di gioco in tempo reale;
         - Gestire operazioni sull'account (modifica username/password, eliminazione account);
      - RIHS5: il server comunica con il database per gestire le operazioni di persistenza;
### 3.5 Requisiti di comunicazione
   - RC1: il server utilizza Socket.IO su protocollo TCP/IP per la comunicazione in tempo reale con i client;
   - RC2: la comunicazione è bidirezione, con conferma opzionale tramite ACK(acknowledgment) del messaggio;
   - RC3: il sistema registra ogni eventi importante tramite il sistema di logging SLF4J;
   - RC4: in caso di interruzione improvvisa, il server avrà un arresto controllato (chiudendo correttamente le connessioni);
### 3.6 Vincoli di progettazione
   - VP1: il server deve essere sviluppato in java, utilizzando la libreria Socket.IO;
   - VP2: l'archiettura deve seguire il modello client-server a eventi, in cui il server è responsabile dellinstradamento dei messaggi e della gestione delle sessioni;
   - VP3: deve essere garantita la modularità del codice;
   - VP4: deve essere presente un sistema di logging centralizzato tramite SLF4J per tracciare eventi e possibili errori;
   - VP5: l’inizializzazione del database deve essere eseguita all’avvio del server;
   - VP6: è previsto un hook di chiusura automatica del server in fase di spegnimento per garantire la chiusura ordinata delle connessioni;
### 3.7 Requisiti del database
   - ### 3.7.1 Tipologia
      - RDB1: il sistema utilizza un database H2 embedded, gestito localmente dal server;
   - ### 3.7.2 Struttura delle tabelle
   | Tabella               | Campo                | Tipo          | Descrizione                                               |
   |-----------------------|----------------------|---------------|-----------------------------------------------------------|
   | UTENTE                | id                   | IDENTITY      | Identificativo univoco dell'utente                        |
   |                       | username             | VARCHAR(50)   | Nome utente scelto dall’utente                            |
   |                       | password             | VARCHAR(250)  | Password crittografata dell’utente                        |
   |                       | created_at           | TIMESTAMP     | Data e ora di creazione dell' account                     |
   | PARTITA_INCOMPLETA    | id                   | IDENTITY      | Identificativo univoco della partita                      |
   |                       | utente_id            | BIGINT        | Riferimento all’utente proprietario                       |
   |                       | nome_salvataggio     | VARCHAR(100)  | Nome assegnato al salvataggio                             |
   |                       | partita_serializzata | CLOB          | Dati della partita salvata in formato serializzato        |
   |                       | created_at           | TIMESTAMP     | Data e ora del salvataggio della partita                  |

   - ### 3.7.3 Vincoli e integrità
      - RDB2: un utente non può avere due salvataggi con lo stesso nome;
      - RDB3: un salvataggio non può esistere senza un utente associato;
      - RDB4: la cancellazione di un utente comporta l’eliminazione automatica delle sue partite salvate;
   - ### 3.7.4 Requisiti e sicurezza
      - RDB5: le password devono essere crittografate (hash) prima dell’inserimento nel database;
      - RDB6: l’accesso diretto al database è limitato esclusivamente al server;
   - ### 3.7.5 Prestazioni
      - RDB7: l’inizializzazione del database deve avvenire entro 2 secondi;
      - RDB8: le query principali (lettura, scrittura, aggiornamento, eliminazione) devono completarsi entro 500 ms;
      - RDB9: il sistema deve supportare almeno 50 utenti concorrenti senza degrado significativo.
   - ### 3.7.6 Gestione degli errori
      - RDB10: gli errori di connessione o SQL devono essere loggati tramite SLF4J;
      - RDB11: in caso di errore critico all’avvio (inizializzazione fallita), il server deve terminare con un messaggio esplicativo;
### 3.8 Requisiti del server
   - ### 3.8.1 Architettura
      - RS1: il server è basato sulla libreria Socket.IO, e gestisce eventi asincroni di connessione e gioco;
   - ### 3.8.2 Requisiti funzionali
      - RS2: il server deve poter gestire almeno 10 stanze di gioco attive contemporaneamente;
      - RS3: deve garantire una disponibilità del servizio ≥ 95%;
   - ### 3.8.3 Sicurezza
      - RS4: le comunicazioni client–server devono avvenire tramite socket protette (protocollo TCP stabile);
      - RS5: le credenziali e le sessioni utente devono essere validate e mai trasmesse in chiaro;
      - RS6: ogni sessione deve avere un token univoco di autenticazione;
   - ### 3.8.5 Logging e monitoraggio
      - RS7: tutti gli eventi di connessione, errore e azione di gioco devono essere registrati tramite SLF4J;
      - RS8: i log devono contenere:
         - timestamp dell’evento;
         - identificativo del client;
         - tipo di operazione (connessione, messaggio, errore);
---

## 4. Stakeholder

- **Giocatori registrati**: usufruiscono di statistiche, salvataggio partite.  
- **Giocatori anonimi**: accedono solo alle modalità senza salvataggio di statistiche.  
- **Amministratori di sistema**: gestiscono server e database. 
- **Sviluppatori**: implementano, mantengono e testano il software.  

---

## 5. Glossario

- **Home**: schermata principale dopo accesso.  
- **Lobby**: stanza virtuale per partite client-server, accessibile tramite codice.  
- **Bot**: avversario controllato dal computer.  
- **ONE!**: pulsante temporaneo per segnalare penultima carta.  
- **MVC**: modello architetturale Model-View-Controller.  

---

## 6. Requisiti Funzionali

### 6.1 Front-End (prima del gioco)

1. RF1 – Avvio  
   - Il gioco deve partire da un eseguibile `.jar`.  

2. RF2 – Schermata di accesso  
   - Opzione Login/Registrazione con checkbox “Resta collegato”.  
   - Opzione Gioca senza login.  

3. RF3 – Statistiche  
   - Utente registrato visualizza partite vinte e giocate.  
   - Utente anonimo non visualizza statistiche.  

4. RF4 – Home post-accesso  
   - Menu con:  
     - Partita con amici (client-server)  
     - Partita contro bot  
     - Tutorial o Regolamento  
     - Esci dal gioco  

5. RF5 – Modalità client-server  
   - RF5.1 – Crea nuova lobby con codice numerico autogenerato.  
   - RF5.2 – Collegati a lobby esistente inserendo codice.  

6. RF6 – Modalità vs bot  
   - RF6.1 – Carica partita incompleta (registrati).  
   - RF6.2 – Crea nuova partita scegliendo numero di bot.  

7. RF7 – Navigazione  
   - Pulsanti per muoversi tra schermate e icona “Home”.  

8. RF8 – Logout/Login da Home  
   - Se utente loggato: pulsante “Logout”.  
   - Se anonimo: pulsante “Login”.  

### 6.2 Front-End (durante il gioco)

1. RF9 – Notifica di turno  
   - Testo in alto: “‹Nome_giocatore›, è il tuo turno:”.  

2. RF10 – Visualizzazioni  
   - Mano del giocatore.
   - Numero di carte degli altri giocatori  
   - Carta sul banco.
   - Indicatore dell’ordine dei turni (stile orologio o “Prossimo: ‹Nome›”).  

3. RF11 – Azioni di gioco  
   - RF11.1 – Pescare carta.  
   - RF11.2 – Selezionare carta in mano e tentare di giocarla. 
   - RF11.3 - Passare il turno.
   - RF11.4 – Messaggio di errore in caso di incompatibilità colore/numero.  

4. RF12 – Pescaggio  
   - Viene pescato una sola carta.  
   - All’arrivo carta “buona”:  
     - Metti sul banco.
     - Oppure tienila in mano e passa il turno.  

5. RF13 – ONE!  
   - Alla penultima carta, mostra finestra (2–3 s) con pulsante “ONE!”.  
   - Se non premuto, vengono pescate 2 carte.  

6. RF14 – Carte speciali  
   - Per +4 o cambio colore: visualizza finestra di selezione colore.  
   - Applicazione effetti (+2, salta turno, inverti) con notifica generica.  

7. RF15 – Cambio turno  
   - Passaggio a giocatore successivo e stato wait.  
   - Notifica: “È il turno di ‹Nome› – carte in mano: ‹n›”.  

8. RF16 – Esito partita  
   - Quando mano=0, mostra schermo vincitore; sui perdenti, nome del vincitore.  

9. RF17 – Fine partita  
   - Pulsanti:  
     - “Home” → schermata iniziale  
     - “Rivincita” → nuova partita (stessi amici o stesso numero di bot)  

10. RF18 – Abbandona partita  
    - Pulsante “Abbandona” con dialog di conferma.  
    - Conferma → ritorno a Home; Annulla → riprendi gioco.  

### 6.3 Back-End (generici)

1. RF19 – Linguaggio e strumenti  
   - Java 11+, Eclipse + Maven.  
   - Papyrus per UML e generazione di codice.  
   - Log4j per logging.  
   - JavaFX per GUI.  
   - Pattern MVC.  
   - PostgreSQL per persistenza.  

### 6.4 Server

1. RF20 – Availability  
   - Server sempre attivo, indipendente dagli utenti online.  

2. RF21 – Multithreading  
   - Pool di thread per gestire richieste multiplayer.  

3. RF22 – Autenticazione e salvataggio  
   - Connessione al DB per login, registrazione e statistiche.  

4. RF23 – Creazione lobby  
   - Endpoint per creare lobby con codice; notifiche di join.  

5. RF24 – Gestione partita 
   - La partita viene gestita dal server con cui i client comunicano.

6. RF25 – Chiusura partita  
   - Endpoint per aggiornare statistiche e rimuovere lobby.  

### 6.5 Database

| Tabella               | Campo                | Tipo          | Note                                       |
|-----------------------|----------------------|---------------|--------------------------------------------|
| UTENTE                | id                   | SERIAL PK     | Auto-increment                             |
|                       | nickname             | VARCHAR(50)   | Unico, non nullo                           |
|                       | password_hash        | VARCHAR(256)  | Hash bcrypt/scrypt                         |
|                       | vittorie             | INT ≥ 0       |                                            |
| PARTITA_INCOMPLETA    | id                   | SERIAL PK     |                                            |
|                       | idUtente             | INT FK        | References UTENTE(id) ON DELETE CASCADE    |
|                       | dettagli             | JSONB         | Stato esatto della partita vs bot          |

- Il DB non memorizza partite multiplayer.  
- Aggiornamento statistiche a ogni fine partita o abbandono.  
- Supporto cancellazione account con cascade dei record.  

---

## 7. Requisiti Non Funzionali

- **Qualità**:
  - Conformità allo standard ISO 9126
  - Supporto sviluppo mediante Stan4j e SonarLint

- **Performance**:  
  - Tempo di avvio ≤ 5 s  
  - Round-trip client-server ≤ 200 ms  

- **Portabilità**:  
  - Windows 10+, macOS 10.13+, Linux  

- **Usabilità**:  
  - Interfaccia intuitiva, apprendimento ≤ 5 min  
  - Supporto completo da tastiera  

- **Sicurezza**:  
  - HTTPS per comunicazioni server  
  - Hashing password   

- **Manutenibilità**:  
  - Architettura modulare MVC  
  - Documentazione UML aggiornata   

---

## 8. Use Case Principali (incompleto)
![UseCase](../UMLDiagrams/ONEuseCaseDiagram.PNG)
| UC ID | Nome                                    | Attori             | Descrizione sintetica                                          |
|-------|-----------------------------------------|--------------------|----------------------------------------------------------------|
| UC1   | Accesso al sistema                      | Utente             | Login/registrazione o gioco anonimo                            |
| UC2   | Visualizza statistiche                  | Utente registrato  | Consultazione storico e vittorie                               |
| UC3   | Crea/Entra in lobby                     | Utente             | Partita con amici tramite codice                              |
| UC4   | Avvia partita vs bot                    | Utente             | Seleziona numero di bot o riprendi partita incompleta         |
| UC5   | Svolgi turno                            | Utente             | Pescaggio, giocata, notifiche e gestione carte speciali       |
| UC6   | Segnala “ONE!”                          | Utente             | Premere pulsante entro 2–3 s alla penultima carta             |
| UC7   | Termina partita                        | Utente             | Messaggio di vittoria/perdita e opzioni Home o Rivincita      |
| UC8   | Abbandona partita                       | Utente             | Dialog di conferma e ritorno a Home                            |

# Specifica dei Requisiti

---
## Indice
#### [1 Introduzione](#1-introduzione)
- [1.1 Obiettivo del documento](#11-obiettivo-del-documento)
- [1.2 Elicitazione dei requisiti](#12-elicitazione-dei-requisiti)
- [1.3 Obiettivo generale](#13-obiettivo-generale)
- [1.4 Panoramica generale](#14-panoramica-generale)
- [1.5 Glossario](#15-glossario)

#### [2 Descrizione generale](#2-descrizione-generale)
- [2.1 Prospettiva del prodotto](#21-prospettiva-del-prodotto)
- [2.2 Funzionalità del prodotto](#22-funzionalità-del-prodotto)
- [2.3 Caratteristiche dell'utente](#23-caratteristiche-dellutente)
- [2.4 Vincoli](#24-vincoli)
- [2.5 Presupposti e dipendenze](#25-presupposti-e-dipendenze)
- [2.6 Architettura generale del sistema](#26-architettura-generale-del-sistema)

#### [3 Requisiti specifici](#3-requisiti-specifici)
- [3.1 Requisiti funzionali](#31-requisiti-funzionali)
  - [3.1.1 Gestione dell'autenticazione](#311-gestione-dellautenticazione)
  - [3.1.2 Gestione della modalità di gioco](#312-gestione-della-modalità-di-gioco)
  - [3.1.3 Configurazione della partita offline](#313-configurazione-della-partita-offline)
  - [3.1.4 Svolgimento della partita](#314-svolgimento-della-partita)
  - [3.1.5 Gestione del salvataggio nella modalità offline](#315-gestione-del-salvataggio-nella-modalità-offline)
  - [3.1.6 Visualizzazione delle statistiche](#316-visualizzazione-delle-statistiche)
  - [3.1.7 Visualizzazione del regolamento](#317-visualizzazione-del-regolamento)
  - [3.1.8 Logout e disconnessione](#318-logout-e-disconnessione)
  - [3.1.9 Politiche di gioco](#319-politiche-di-gioco)
- [3.2 Requisiti di interfaccia utente](#32-requisiti-di-interfaccia-utente)
  - [3.2.1 Schermata di Login](#321-schermata-di-login)
  - [3.2.2 Schermata di Registrazione](#322-schermata-di-registrazione)
  - [3.2.3 Menù principale](#323-menù-principale)
  - [3.2.4 Schermata "Gioca contro computer"](#324-schermata-gioca-contro-computer)
  - [3.2.5 Configurazione nuova partita](#325-configurazione-nuova-partita)
  - [3.2.6 Regolamento](#326-regolamento)
  - [3.2.7 Statistiche](#327-statistiche)
  - [3.2.8 Impostazioni](#328-impostazioni)
- [3.3 Requisiti interfaccia hardware e software](#33-requisiti-interfaccia-hardware-e-software)
  - [3.3.1 Interfaccia hardware](#331-interfaccia-hardware)
  - [3.3.2 Interfaccia software](#332-interfaccia-software)
- [3.4 Requisiti di comunicazione](#34-requisiti-di-comunicazione)
- [3.5 Vincoli di progettazione](#35-vincoli-di-progettazione)
- [3.6 Requisiti del database](#36-requisiti-del-database)
  - [3.6.1 Tipologia](#361-tipologia)
  - [3.6.2 Struttura delle tabelle](#362-struttura-delle-tabelle)
  - [3.6.3 Vincoli e integrità](#363-vincoli-e-integrità)
  - [3.6.4 Requisiti e sicurezza](#364-requisiti-e-sicurezza)
  - [3.6.5 Prestazioni](#365-prestazioni)
  - [3.6.6 Gestione degli errori](#366-gestione-degli-errori)
- [3.7 Requisiti del server](#37-requisiti-del-server)
  - [3.7.1 Architettura](#371-architettura)
  - [3.7.2 Requisiti funzionali](#372-requisiti-funzionali)
  - [3.7.3 Sicurezza](#373-sicurezza)
  - [3.7.5 Logging e monitoraggio](#375-logging-e-monitoraggio)
- [3.8 Requisiti non funzionali (in riferimento ad ISO 9126)](#38-requisiti-non-funzionali-in-riferimento-ad-iso-9126)
  - [3.8.1 Usabilità](#381-usabilità)
  - [3.8.2 Efficienza](#382-efficienza)
  - [3.8.3 Affidabilità](#383-affidabilità)
  - [3.8.4 Portabilità](#384-portabilità)
  - [3.8.5 Sicurezza](#385-sicurezza)

#### [4. Piano di testing](#4-piano-di-testing)

#### [5. Strumenti e standard di lavoro](#5-strumenti-e-standard-di-lavoro)

#### [6. Use Case Principali](#6-use-case-principali)


---
## 1. Introduzione

### 1.1 Obiettivo del documento
Il presente documento descrive in modo esaustivo e strutturato i requisiti funzionali e non funzionali del videogioco di carte “ONE” basato su Java.  
La specifica dei requisiti è redatta in conformità con lo standard IEEE 830 al fine di garantire una struttura chiara, tracciabile e comprensibile delle parti coinvolte.  
Il presente documento rappresenta un riferimento ufficiale per tutte le fasi successive del ciclo di vita del software, includendo progettazione, implementazione, test, validazione e manutenzione. Esso mira a garantire una comprensione condivisa del sistema tra tutti gli stakeholder riducendo ambiguità e assicurando la tracciabilità dei requisiti.

### 1.2 Elicitazione dei requisiti
L'attuale documento adotterà la seguente strategia per elicitare i requisiti del gioco "ONE":
- studio del gioco UNO (derivazione meccaniche di gioco dal corrispettivo originale);
- prototipazione (per interfaccia utente);
- discussioni tra membri (per specifiche più tecniche);

### 1.3 Obiettivo generale
Il gioco "ONE” (ispirato al classico gioco di carte “UNO”) è stato progettato per offrire un’esperienza interattiva, fluida e accessibile a qualsiasi tipo di utente.
Il sistema ha l’obiettivo di fornire una piattaforma modulare e scalabile, che permetta l’integrazione di nuove funzionalità in futuro senza compromettere la stabilità e le prestazioni del software.    

### 1.4 Panoramica generale
Il gioco "ONE" consente ai giocatori di partecipare a partite in modalità offline (contro dei bot) oppure in modalità multigiocatore online contro altri utenti.
I giocatori possono scegliere se registrarsi o partecipare come utenti anonimi. Tuttavia, la modalità anonima comporta alcune limitazioni: i giocatori non registrati non possono accedere alle statistiche delle partite, né salvare le partite contro il coomputer.


### 1.5 Glossario
- Bot: avversario controllato da un computer (cioè un computer imita le mosse che farebbe un umano);
- Utente registrato: utente che, dopo l'avvio del gioco, effettua una registrazione (nel caso non fosse registrato) oppure un login (nel caso fosse già registrato). Esso può controllare le statistiche delle sue partite e può accedere ai salvataggi delle partite non terminate (solo quelle lato offline);
- Utente anonimo: utente che non si è registrato prima di iniziare a giocare. Esso potrà scegliere un nome temporaneo, valido per quella sessione di gioco;



## 2. Descrizione generale 
   ### 2.1 Prospettiva del prodotto
   "ONE” è una rivisitazione digitale del celebre gioco di carte UNO.  
   Il software è sviluppato interamente in Java e può essere eseguito su diversi sistemi operativi, purché sia installata una versione compatibile del Java Runtime Environment (JRE). 
   ### 2.2 Funzionalità del prodotto
   ONE avrà le seguenti funzionalità:
   - Registrazione e autenticazione: ogni utente può scegliere se registrarsi prima di giocare. Gli utenti già registrati possono autenticarsi tramite nome utente e password per accedere alle proprie partite e statistiche;
   - Modalità di gioco offline e online: il giocatore può partecipare a partite in modalità offline (contro un bot) oppure in modalità online (contro altri utenti connessi).
   - Salvataggio e ripresa delle partita non terminate: in modalità offline gli utenti registrati possono salvare una partita in corso e riprenderla successivamente. Gli utenti non registrati non dispongono di questa funzionalità;
   - Gestione delle statistiche personali: gli utenti registrati possono visualizzare le proprie statistiche, che comprendono numero di partite giocate, vinte e perse, e altre informazioni di riepilogo. Gli utenti non registrati non dispongono di questa funzionalità;
   - Regolamento: nel menù principale è disponibile una sezione dedicata al regolamento ufficiale del gioco, accessibile a tutti gli utenti;
   - Impostazioni: (solo per utenti registrati) per modificare nome/password, oppure eliminare l'account.
   
   ### 2.3 Caratteristiche dell'utente
   Il gioco “ONE” è rivolto a un pubblico ampio, composto sia da giocatori occasionali sia da utenti abituali del gioco di carte UNO.  
   Non sono richieste competenze tecniche particolari per utilizzarlo: l’interfaccia grafica è progettata per essere intuitiva, chiara e facilmente comprensibile da qualsiasi tipo di utente.

   ### 2.4 Vincoli
   Il sistema "ONE" presenta i seguenti vincoli:
   - il gioco dove essere eseguito su un sistema dotato di JDK 21 o una versione successiva;
   - il software deve risultare compatibile con i sistemi operativi Windows, Linux e macOS;
   ### 2.5 Presupposti e dipendenze
   Il funzionamento del sistema "ONE" si basa sui seguenti presupposti e dipendenze.  
   Pressuposti: 
   - Si presuppone che l’utente disponga di un computer con JDK 21 o versione successiva installato;
   - Si presuppone che l’utente abbia familiarità con l’utilizzo di dispositivi informatici di base (es. computer, tastiera e mouse);
   - Si presuppone che ogni utente si debba connettere al server per poter giocare in modalità online (in caso contrario potrà solo giocare contro bots e senza possibilità di autenticarsi);
   Dipendenze:
   - Il corretto funzionamento della modalità online dipende dalla disponibilità e stabilità del server di gioco;
   - Il sistema dipende dal database integrato nel server, utilizzato per la memorizzazione delle credenziali, delle statistiche e dello stato delle partite salvate;
   ### 2.6 Architettura generale del sistema
   Il sistema “ONE” adotta un’architettura Client–Server basata sul paradigma MVC (Model–View–Controller).
   È composto da tre moduli principali:
      - Modulo Client (MVC locale):
         - Responsabile dell’interfaccia grafica (View);
         - Gestisce l’interazione dell’utente e invia eventi al Controller locale;
         - Comunica con il server tramite socket (Socket.IO);
         - Mantiene la logica di visualizzazione e validazione delle mosse.
      - Modulo Server:
         - Implementa la logica di business e la gestione delle partite online;
         - Riceve eventi dai client e li instrada alle stanze di gioco corrispondenti;
         - Gestisce persistenza (DB), autenticazione e sessioni.
      - Modulo Common:
         - Contiene classi condivise tra client e server;
         - Garantisce compatibilità e uniformità tra le due parti.

## 3. Requisiti Specifici
### 3.1 Requisiti funzionali
   - ### 3.1.1 Gestione dell'autenticazione
      - RF1: il sitema deve permettere la registrazione di nuovi utenti tramite l'inserimento di nome utente e password;
      - RF2: il sistema deve consentire il login agli utenti già registrati;
      - RF3: il sistema deve permettere l'accesso come "ospite", ovvero l'accesso al gioco senza registrazione (lasciando la possibilità di scegliere un nome temporaneo);
   - ### 3.1.2 Gestione della modalità di gioco
      - RF4: il sistema deve consentire la selezione della modalità di gioco: 
         - gioca con amici (online);
         - gioca contro computer (offline);
      - RF5: in modalità offline, il giocatore deve poter scegliere se iniziare una nuova partita o caricarne una salvata (solo per gli utenti registrati);
      - RF6: in modalità online, il sistema deve connettersi al server di gioco e gestire la comunicazione tra i giocatori.
      (completare la modalità online)
   - ### 3.1.3 Configurazione della partita offline
      - RF7: il sistema deve permettere la selezione del numero di giocatori (da 2 a 10);
      - RF8: dopo aver configurato la partita, l'utente può decidere se annulare la configurazione oppure avviare la partita;
      - RF9: (per utenti registrati) dopo aver premuto il pulsante di avvia partita, appare una schermata aggiuntiva in cui viene data l'opzione di salvare o meno la partita che viene avviata;
   - ### 3.1.4 Svolgimento della partita
      - RF10: il sistema deve basarsi sul regolamento deciso (visibile nella home del gioco);
      - RF11: il sistema deve distribuire casualmente le carte ad ogni giocatore;
      - RF12: ogni giocatore deve poter effettuare una mossa scegliendo una carta compatibile con l’ultima giocata;
      - RF13: il sistema deve controllare la validità delle mosse e impedire quelle non consentite;
      - RF14: il sistema deve rilevare automaticamente la vittoria di un giocatore;
   - ###  3.1.5 Gestione del salvataggio nella modalità offline
      - RF15: gli utenti registrati devono poter salvare lo stato di una partita in corso (qualora avessero deciso di salvare la partita, come spiegato in RF9);
      - RF16: 1l sistema deve consentire di caricare una partita precedentemente salvata;
   - ### 3.1.6 Visualizzazione delle statistiche
      - RF17: il sistema deve mostrare le statistiche personali degli utenti registrati (numero di partite giocate, vinte, perse, ecc.);
      - RF18: gli utenti anonimi non devono poter accedere alle statistiche;
   - ### 3.1.7 Visualizzazione del regolamento
      - RF19: il sistema deve offrire una sezione “Regolamento” accessibile dal menù principale;
      - RF20: il regolamento deve descrivere in modo chiaro le regole del gioco "ONE";
   - ### 3.1.8 Logout e disconnessione
      - RF21: il sistema deve permettere di effettuare il logout dalle schermate più importanti;
   - ### 3.1.9 Politiche di gioco
      - RF22: ogni giocatore dispone di 30 secondi di tempo per effettuare la propria mossa;
      - RF23: se il timer scade e il giocatore non ha selezionato alcuna carta, il sistema pescherà automaticamente una carta (che verrà aggiunta al mazzo del giocatore) e passerà il turno;
      - RF24: se il giocatore seleziona la carta jolly (che permette di scegliere il colore della carta sul banco) e non seleziona un colore entro la fine del timer, il sistema pescherà automaticamente una carta (che verrà aggiunta al mazzo del giocatore) e passerà il turno;
       - RF25: se viene pescata una carta giocabile, l’utente può decidere di:
          - giocarla immediatamente; oppure
          - conservarla nel mazzo (entro il tempo limite);
      - RF26: se il giocatore ha una sola carta e non preme il pulsante “ONE” entro 3 secondi, viene applicata una penalità: il sistema aggiungerà automaticamente 2 carte al suo mazzo;
      - RF27: se un giocatore clicca sul pulsante "Abbandona" si verificano i seguenti casi:
         - il timer di gioco viene temporaneamente sospeso;
         - compare una finestra di conferma (warning) in cui il giocatore può decidere se abbandonare definitivamente la partita o continuare a giocare;
            - se conferma l’abbandono, viene reindirizzato al menù principale;
            - se decide di continuare, il gioco riprende e il timer viene riattivato;
      - RF28: in caso di abbandono di una partita (solo per la modalità online) il giocatore viene sostituito da un bot. L'abbandono della partita, nelle statistiche personali, equivale ad una partita persa;
      - RF28: i bot seguono la seguente logica decisionale:
         - giocano sempre una carta valida, se disponibile nel mazzo;
         - se non hanno carte valide, pescano dal mazzo e giocano automaticamente la carta pescata (se è valida);
         - chiamano automaticamente "ONE" quando restano con una sola carta;
### 3.2 Requisiti di interfaccia utente
   - ### 3.2.1 Schermata di Login
      - RI1: campi di testo:
         - nome utente;
         - password (campo oscurato);
      - RI2: pulsanti:
         - "Accedi": consente l'accesso agli utenti registrati;
         - "Registrati": permette ai nuovi utenti di creare un account;
         - "Entra come opsite": consente di avviare il gioco senza autenticazione (con possibilità di scegliere un nome temporaneo);
      - RI3: in caso di errore nell’inserimento delle credenziali, deve comparire un messaggio di errore informativo;
   - ### 3.2.2 Schermata di Registrazione
      - RI4: campi di testo:
         - nome utente;
         - password (campo oscurato);
         - conferma password (campo oscurato);
      - RI5: pulsanti:
         - "Registrati": permette di provare a creare un account;
         - "Annulla": consente di ritornare alla schermata 3.3.1;
      - RI6: in caso di errore nel processo di registrazione, deve comparire un messaggio di errore informativo;
   - ### 3.2.3 Menù principale
      - RI7: dopo l’accesso, l’utente visualizza il menù principale con le seguenti opzioni:
         - "Gioca con amici": pulsante che consente di entrare nella modalità online;
         - "Gioca contro computer": pulsante che consente di entrare nella modalità offline;
         - "Regolamento": pulsante che consente di leggere il regolamento del gioco;
         - "Mostra statistiche": pulsante che consente di leggere le statistiche personali (solo per gli utenti registrati);
         - "Logout": pulsante che consente la disconnessione dal gioco;
      - RI8: in basso alla schermata del menù principale si visualizzerà:
         - lo stato di connessione ("Connessione"/"Disconnessione") al server;
         - il tipo di utente (se l'utente è anonimo oppure registrato);
         - se l'utente è registrato, il nome utente in basso a destra diventa un bottone che porta alle impostazioni dell'account;
   - ### 3.2.4 Schermata "Gioca contro computer"
      - RI9: l'utente può scegliere:
         - se giocare una nuova partita (questa funzione è disponibile per tutti gli utenti);
         - se caricare una partita già iniziata (funzione disponibile solo per gli utenti registrati);
      - RI10: pulsanti di navigazione:
         - "Home": consente di tornare al menù principale;
         - "Logout": disconnette l'utente dal sistema;
   - ### 3.2.5 Configurazione nuova partita
      - RI11: l'utente può selezionare il numero di giocatori (minimo 2, massimo 10);
      - RI12: pulsanti: 
            - "Avvia partita": consente di avviare la partita;
            - "Annulla": consente di annullare la configurazione della partita e tornare alla schermata precedente;
      - RI13: (solo per utenti registrati) schermata aggiuntiva in cui si seleziona il nome del nuovo salvataggio (lasciando vuoto viene generato il nome in automatico) oppure si dichiara di non voler salvare la partita che si vuole creare;
   - ### 3.2.6 Regolamento
      - RI14: visualizza le regole del gioco "ONE", è composto da una parte testuale e da delle immagini inerenti al gioco
      - RI15: pulsanti:
         - "Avanti": passaggio alla pagina successiva;
         - "Indietro": passaggio alla pagina precedente;
   - ### 3.2.7 Statistiche 
      - RI16: (solo per utenti registrati) mostra i dati relativi alle partite giocate: numero di vittorie, sconfitte, ecc.;
   - ### 3.2.8 Impostazioni
      - RI17: (solo per utenti registrati) pulsanti:
            - "Modifica nome": consente di modificare il nome utente;
            - "Modifica password": consente di modificare la password attutale (inserendo password vecchia, password nuova e conferma password nuova);
            - "Elimina utente": consente di eliminare l'utente corrente, dopo aver inserito la password;
### 3.3 Requisiti interfaccia hardware e software
   - ### 3.3.1 Interfaccia hardware
      - RIHS1: il sistema non richiede dispositivi hardware specifici oltre a un computer, mouse e tastiera;
   - ### 3.3.2 Interccia software
      - RIHS2: il server utilizza la libreria Socket.IO per la gestione delle connessioni tra client e server;
      - RIHS3: la comunicazione è di tipo event-driven, cioè basata su eventi personalizzati (es. auth:login);
      - RIHS4: il server ha la funzione di:
         - Gestire la connessione e disconnessione dei client;
         - Gestire gli eventi di autenticazione e registrazione;
         - Creare ed eliminare le stanze di gioco per le partite online;
         - Gestire il salvataggio delle partite offline;
         - Gestire le mosse di gioco in tempo reale;
         - Gestire operazioni sull'account (modifica nome/password, eliminazione account);
      - RIHS5: il server comunica con il database per gestire le operazioni di persistenza;
### 3.4 Requisiti di comunicazione
   - RC1: il server utilizza Socket.IO su protocollo TCP/IP per la comunicazione in tempo reale con i client;
   - RC2: la comunicazione è bidirezione, con conferma opzionale tramite ACK(acknowledgment) del messaggio;
   - RC3: il sistema registra ogni eventi importante tramite il sistema di logging SLF4J;
   - RC4: in caso di interruzione improvvisa, il server avrà un arresto controllato (chiudendo correttamente le connessioni);
### 3.5 Vincoli di progettazione
   - VP1: il server deve essere sviluppato in java, utilizzando la libreria Socket.IO;
   - VP2: l'archiettura deve seguire il modello client-server a eventi, in cui il server è responsabile dellinstradamento dei messaggi e della gestione delle sessioni;
   - VP3: deve essere garantita la modularità del codice;
   - VP4: deve essere presente un sistema di logging centralizzato tramite SLF4J per tracciare eventi e possibili errori;
   - VP5: l’inizializzazione del database deve essere eseguita all’avvio del server;
   - VP6: è previsto un hook di chiusura automatica del server in fase di spegnimento per garantire la chiusura ordinata delle connessioni;
### 3.6 Requisiti del database
   - ### 3.6.1 Tipologia
      - RDB1: il sistema utilizza un database H2 embedded, gestito localmente dal server;
   - ### 3.6.2 Struttura delle tabelle
   | Tabella               | Campo                | Tipo          | Descrizione                                               |
   |-----------------------|----------------------|---------------|-----------------------------------------------------------|
   | UTENTE                | id                   | IDENTITY      | Identificatore univoco dell'utente                        |
   |                       | username             | VARCHAR(50)   | Nome utente univoco scelto dall’utente                    |
   |                       | password             | VARCHAR(200)  | Password crittografata dell’utente                        |
   |                       | partite_giocate      | INT           | Numero partite giocate dall’utente                        |
   |                       | partite_vinte        | INT           | Numero partite vinte dall’utente                          |
   |                       | created_at           | TIMESTAMP     | Data e ora di creazione dell' account                     |
   | PARTITA_INCOMPLETA    | id                   | IDENTITY      | Identificatore univoco del salvataggio della partita      |
   |                       | utente_id            | BIGINT        | Riferimento all’utente proprietario del salvataggio       |
   |                       | nome_salvataggio     | VARCHAR(100)  | Nome assegnato al salvataggio                             |
   |                       | partita_serializzata | CLOB          | Dati della partita salvata in formato serializzato        |
   |                       | created_at           | TIMESTAMP     | Data e ora del salvataggio della partita                  |

   - ### 3.6.3 Vincoli e integrità
      - RDB2: un utente non può avere due salvataggi con lo stesso nome;
      - RDB3: un salvataggio non può esistere senza un utente associato;
      - RDB4: la cancellazione di un utente comporta l’eliminazione automatica delle sue partite salvate;
   - ### 3.6.4 Requisiti e sicurezza
      - RDB5: le password devono essere crittografate (hash) prima dell’inserimento nel database;
      - RDB6: l’accesso diretto al database è limitato esclusivamente al server;
   - ### 3.6.5 Prestazioni
      - RDB7: l’inizializzazione del database deve avvenire entro 2 secondi;
      - RDB8: le query principali (lettura, scrittura, aggiornamento, eliminazione) devono completarsi entro 500 ms;
      - RDB9: il sistema deve supportare almeno 50 utenti concorrenti senza degrado significativo.
   - ### 3.6.6 Gestione degli errori
      - RDB10: gli errori di connessione o SQL devono essere loggati tramite SLF4J;
      - RDB11: in caso di errore critico all’avvio (inizializzazione fallita), il server deve terminare con un messaggio esplicativo;
### 3.7 Requisiti del server
   - ### 3.7.1 Architettura
      - RS1: il server è basato sulla libreria Socket.IO, e gestisce eventi asincroni di connessione e gioco;
   - ### 3.7.2 Requisiti funzionali
      - RS2: il server deve poter gestire almeno 10 stanze di gioco attive contemporaneamente;
      - RS3: deve garantire una disponibilità del servizio ≥ 95%;
   - ### 3.7.3 Sicurezza
      - RS4: le comunicazioni client–server devono avvenire tramite socket protette (protocollo TCP stabile);
      - RS5: le credenziali e le sessioni utente devono essere validate e mai trasmesse in chiaro;
      - RS6: ogni sessione deve avere un token univoco di autenticazione;
   - ### 3.7.5 Logging e monitoraggio
      - RS7: tutti gli eventi di connessione, errore e azione di gioco devono essere registrati tramite SLF4J;
      - RS8: i log devono contenere:
         - timestamp dell’evento;
         - identificativo del client;
         - tipo di operazione (connessione, messaggio, errore);
### 3.8 Requisiti non funzionali (in riferimento ad ISO 9126)
   - ### 3.8.1 Usabilità
      - RNF1: l'interfaccia grafica deve essere intuitiva, colorata e di facile comprensione per gli utenti;
      - RNF2: i pulsanti devono avere un feedback visivo al passaggio del mouse o alla pressione;
   - ### 3.8.2 Efficienza
      - RFN3: il sistema deve avviarsi entro 5 secondi dall'esecuzione;
      - RFN4: le mosse di gioco devono essere elaborate in meno di 1 secondo;
   - ### 3.8.3 Affidabilità
      - RFN5: il sistema deve garantire il salvataggio corretto della partita in caso di chiusura improvvisa (solo per gli utenti registrati);
      - RFN6: il server deve garantire un’elevata disponibilità e affidabilità nella maggior parte del tempo di utilizzo;
   - ### 3.8.4 Portabilità
      - RFN6: il gioco deve essere eseguibile su Windows, Linux e macOS, con JDK 21 o versione successiva installata;
   - ### 3.8.5 Sicurezza
      - RFN7: le credenziali, degli utenti registrati, devono essere memorizzate nel database. Le password devono essere crittografate;
      - RFN8: le comunicazioni tra client e server deveno avvenire tramite protocollo sicuro;

## 4. Piano di testing
   |Area di test        | Descrizione                                                           | Tipo di test       |
   |--------------------|-----------------------------------------------------------------------|-----------------   |
   |Autenticazione      |Verifica di registrazione/login con credenziali corrette e scorrette	| Test funzionale    |
   |Salvataggio partite	|Verifica che il salvataggio e il caricamento funzionino correttamente	|Test di integrazione|
   |Timer di gioco	|Controllo scadenza del turno e gestione automatica mossa	|Test di stress|
   |Gestione bot	|Verifica delle scelte automatiche coerenti con le regole	|Test funzionale|
   |Connessione online	|Test della stabilità della connessione client-server	|Test di rete|
   |Persistenza DB	|Controllo integrità dati e tempi di risposta	|Test di performance|
   |Sicurezza	|Validazione dell’hash password e protezione connessione	|Test di sicurezza|

## 5. Strumenti e standard di lavoro
   - Linguaggio di programmazione: Java;
   - Server: Libreria Socket.IO;
   - Database: H2 embedded;
   - Logging: SLF4J;
   - Stile architetturale: client-server basato su MVC;
   - Standard di scrittura requisiti: IEEE 830;
   - Condivisione e consegna: GitHub;

## 6. Use Case Principali
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

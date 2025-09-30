# Gestione del Progetto

---

## Introduzione

Il presente progetto verrà svolto, perlomeno per quanto preventivato, utilizzando le metodologie "agili" per la modellazione e lo sviluppo di codice. 
In particolare, si conta di utilizzare SCRUM, con sprint settimanali e riunioni giornaliere (per aggiornarsi costantemente).
Qualora lo si ritenesse necessario, si considera la possibilità di attingere (in minore misura) ai modelli "tradizionali", come quello a cascata, ad esempio quando è necessario uno sviluppo più collettivo (ingegneria dei requisiti oppure progettazione).

---

## Politica di aggiornamento del documento

Il documento qui presente ha come obiettivo la monitorizzazione dell'attività svolta dai membri del progetto, mediante una descrizione sintetica di quanto fatto in un determinato giorno e di quali siano gli obiettivi per la volta dopo. 
La necessità di questo documento (rispetto al già presente log di GitHub) deriva dalla volontà di mantenere ben presente gli obiettivi lungo e breve termine di ciascuno membro del progetto, in modo da non avere ambiguità mentre si lavora in asincrono. 
In funzione di questo obiettivo, ciascuno dei membri **potrà** aggiornare questo documento e **dovrà** rispettare il seguente formato di messaggi:
1.  il messaggio **deve** iniziare con il carattere "-" e seguito da uno spazio " ";
2.  data messaggio (es. *25/08*) eventualmente con orario (quest'ultimo, non obbligatorio);
3.  responsabile/i del messaggio (es. *Andrei* se intervento individuale o *Andrei,Fabio,Giuseppe* se intervento di gruppo);
4.  descrizione **SINTETICA** di quanto fatto in quel giorno (es. *lavorato sul project plan*);
5.  descrizione **SINTETICA** di quanto ci si aspetta di fare la volta successiva (es. *iniziare a lavorare sulla specifica dei requisiti*) (opzionale, ma fortemente consigliato);
6.  **eventuale** scrittura tra parentesi () per chiarezza, nei punti 3.,4.,5.;
7.  l'intero messaggio deve essere posizionato **dopo** quello che lo precede e si deve chiudere con il carattere ";";
   
Durante la scrittura di questo documento, si riserva l'opportunità di aggiungere messaggi di **AVVISO** da e per membri, in cui si richiede l'attenzione di alcuni (o tutti) i membri per materie di notevole importanza (es. necessità di modifiche tempestive, chiusura di una determinata fase di sviluppo, sollecitazione di un incontro per chirimenti ecc.).
Il formato di messaggi di **AVVISO** è come quello di messaggio "normale" con le seguenti modifiche:
- scritta **AVVISO** tra 2. e 3.;
- descrizione **SINTETICA** dell'avviso al posto di 4. e 5.;
- i messaggi di avviso non devono essere **mai** cancellati, anche se il problema descritto viene risolto;

Esempio COMPLETO:
- 23/04, (Andrei,Fabio), formattazione specifica requisiti, inizio modelizzazione sistema;
- 23/04, **AVVISO**, Andrei, chiusura fase ingegneria del software--> non aggiornare documento 2;
---

## Log messaggi:

- 19/08, (Andrei, Fabio, Giuseppe), prima versione del project plan di ONE, inizio dell'ingegneria dei requisiti;
- 20/08, (Andrei, Fabio, Giuseppe), elicitazione dei requisiti front-end di ONE, continuazione dell'ingegneria dei requisiti;
- 21/08, (Andrei, Fabio, Giuseppe), revisione dei requisiti front-end e elicitazione (approssimativa) di quelli back-end, perfezionamento requisiti ed inizio modelizzazione;
- 21/08, Fabio, miglioramento formattazione dei requisiti (documento 2), null;
- 25/08 14-16:30, (Andrei, Giuseppe), miglioramento struttura progetto (cartella documentazione) e prima versione use case diagram dentro documento 2, continuazione use case diagram e modellizzazione;
- 25/08 16:30-18, Andrei, definizione politiche aggiornamento documento 1 (qui presente) e aggiunta log della settimana del 18/08, null;
- 25/08, **AVVISO**, Andrei, (ciascun membro dovrà fare i propri log d'ora in avanti, salvo sessioni di gruppo in cui li fa uno per tutti);
- 27/08 15:30, Andrei, descrizione generale architettura software--> documento 3, continuazione progettazione--> design vero e proprio;
- 27/08 19:30, Andrei, ottimizzazione piano del progetto e requisiti, consegnare project plan;
- 27/08, **AVVISO**, Andrei, (il project plan è stato finalizzato--> NON aggiornare documento ProjectPlan);
- 31/08, Andrei, (1. aggiunta progetto Papyrus con dentro Usa Case Diagram (migliorato rispetto a versione precedente) e Class Diagram (vers.1); 2. rimozione cartella immagini e visualizzazione diagrammi direttamente da cartella progetto Papyrus, in cui sono presenti i PNG esportati; 3. aggiornamento documenti 2 e 3), sequence/state machine diagram o inizio implementazione;
- 31/08, **AVVISO**, Andrei, (per costruire/modificare i diagrammi UML su Papyrus, clonare la repository su GitHub desktop e successivamente da Eclipse aprire il progetto clonato (nella cartella nome_utente--> Documenti--> GitHub--> ONE(che appare dopo aver clonato la repository)); per visualizzare i diagrammi uml fatti/modificati--> tasto dx su diagramma .di --> export --> papyrus --> export all diagrams --> finish --> appare file png nella cartella del progetto--> copiare dentro file .md di riferimento il comando `![nome_arbitrario](../UMLDiagrams/nomeimmagine.PNG))`);
- 31/08 23:00, Andrei, prima versione state machine diagram aggiunto a documento 3, null;
- 02/09, Andrei, seconda versione class diagram in vista di implementazione, null;
- 03/09 10:30, Andrei, prima versione documento 4 (Testing), null;
- 03/09, **AVVISO**, Andrei, (consultare il documento 4 (Testing) prima e durante l'implementazione di codice, eventualmente aggiornandolo);
- 03/09 12:00, Andrei, condivisione progetto Maven su repository + traduzione automatica package "modello" con Papyrus designer, sistemazione classi ed organizzazione + 1a fase di testing se possibile;
- 03/09 23.45, Andrei, (traduzione completa del gioco ONE in Java (solo da console e senza separazione MVC) + scrittura di un caso di test), separazione su vari package + inizio GUI + guardare persistenza con postgres;
- 03/09, **AVVISO**, Andrei, non ho avuto il tempo di fare la javadoc del package modello (per adesso);
- 05/09, Andrei, (implementazione base del pattern MVC da console (con un controllore temporaneo e con una view temporanea)+ eseguito refactoring generale del codice+ Pattern PLAYER-ROLE per modalità bot e non-bot), GUI o persistenza;
- 06/09, Andrei, (separazione mvc più netta, eliminazione temporanea delle modalità (e quindi del player role pattern) e prove con altre versione di avviaPartita()), null;
- 07/09, Andrei, (completamento del commit precedente: MVC puro tra le componenti, specialmente tra model e controller), GUI o persistenza;
- 07/09, Andrei, (sistemazione alcuni bug durante esecuzione + spostamento logica applicaEffettoCarta dentro CartaSpeciale +spostamento enumerato TipoSpeciale dentro CartaSpeciale ), null;
- 08/09, Andrei, (implementazione di salvataggio/caricamento Partita (utilizzando serializzazione/deserializzazione in JSON con Jackson)--> ristrutturazione classi in modo da permettere il processo; ulteriore modularizzazione del controllore e aggiunte a package 'persistenza'), GUI gioco;
- 09/09, Andrei, (preparazione/modifiche di alcuni elementi in vista di sviluppo gui con javaFX), inizio GUI gioco;
- 09/09 - 12/09, Andrei, (versione 1 di ONE offline avente interfaccia grafica con JavaFX -> adattamento ControlloreGioco all'approccio event-driven + gestore salvataggi a livello grafico + implementazioni + implementazioni specifiche (es bottone ONE) + piccoli miglioramenti a classi esistenti + aggiornamento documento 4 (testing)), inizio implementazione lato server;
- 18/09 14:15-16:00, (Andrei, Giuseppe e Fabio), (pianificazione lavoro prossimi giorni--> finire vista, implementare controllori client per mandare/ricevere messaggi server, discussione tecnologie per realizzazione del server websocket), inizio implementazione server;
- 18/09, **AVVISO**, Andrei, aggiornare il projectPlan e documento 2 (requisiti);
- 19/09, Andrei, (eliminazione di alcuni classi inutili + prima versione VistaAccesso), (continuazione varie viste, perlomeno basilari);
- 19/09, **AVVISO**, Andrei, il branch con javaFX è diventato quello di default--> lavorare solo su quello;
- 20/09, Andrei, continuazione interfaccia grafica senza gestione controllore--> aggiunta cartella stile in resources per css, (continuazione interfaccia grafica + gestione controllore);
- 20/09 19:00, Andrei, piccola implementazione del ControlloreAccesso e del ControlloreHome per accedere da VistaAccesso a VistaHome--> solo system.out.println per ora, (continuazione controllori e viste); 
- 20/09, Fabio, (aggiornamento documentazione e definizione messaggi di comunicazione, scelta della libreria di comunicazione), null;
- 20/09, Fabio, inizio divisione del progetto in moduli, continuazione divisione in moduli;
- 21/09, Fabio, (riorganizzazione moduli, prima versione database), null;
- 22/09, Andrei, (Aggiunta VistaRegistrazione e ControlloreRegistrazione + aggiunta erroreLabel per messaggi d'errore), collegamento al server per autenticazione;
- 22/09, **AVVISO**, Andrei, il branch `ONEdivisioneInModuli` è diventato quello di default--> lavorare solo su quello;
- 22/09 20:43, Giuseppe, inizializzazione del server con la creazione del gameserver (dove viene creato il sever per la gestione delle connessione e disconnesioni dei giocatori) e del ServerLaunch (usato per avviare il server), null;
- 22/09, Fabio, (ultimata riorganizzazione moduli, documentazione, creati casi di test per mazzo e partita), creazione nuovi casi di test e refactoring del codice di common e client;
- 22/09 20-21:30, Fabio, rinominati tutti i package secondo convenzione, null;
- 23/09, Andrei, eliminazione classi inutili + migliore leggibilità classe Partita, continuazione gui;
- 23/09, Fabio, (refactoring, aggiunta metodi per serializzare e deserializzare Partita), null;
- 24/09, Andrei, (javadoc alcuni parti del modello), null;
- 24/09 14:00-15:15, Giuseppe, piccola modifica alla classe GameServer e creazione della classe PlayerConnection (rappresenta il giocatore connesso al server), null;
- 24/09 15:00-20:00, Andrei, (aggiunta casi di test in Junit per il modello + aggiornamento documento 4 per fase 1 e 2), null;
- 24/09 9:00-14:00, Fabio, refactoring modello, null;
- 24/09, Fabio, (refactoring modello, aggiunta metodi safe per server, corrette dipendenze, introdotta nuova libreria per il client, documentazione), eventuale continuazione refactoring;
- 24/09 21:30-22:20, Giuseppe, modifica della classe PlayerConnection (uso anche delle classi di onegame-common), null;
- 25/09, Giuseppe, rinomina classi ConnessioneGiocatore e AvvioServer, null;
- 25/09 21:45, Giuseppe, (creazione della classe GestioneStanze, usata per gestire le diverse stanze in cui sono presenti i giocatori connessi), null;
- 25/09, Fabio, (creato ProtocolloMessaggi, Utente e GestoreConnessioni), continuo implementazione server;
- 26/09 11:30-13:30, Andrei, (continuazione gui--> alcune viste per gioco online + controllore per inserimento codice), continuazione gui;
- 26/09 15:00-15:45, Giuseppe, completamento della classe GestoreStanze, null;
- 26/09 18:45-19:20, Giuseppe, modifica alla classe GestoreStanze e cancellazione di vecchie classi, null;
- 26/09, Fabio, (creazione di ClientSocket, classi dei messaggi e ServerUno, refactoring di StanzaPartita e gestori), continuazione server;
- 27/09 0:00-1:00, Fabio, creazione file bat per eseguire server, null;
- 27/09 15:00-18:00, Giuseppe, aggiornamento GestoreDatabase e creazione delle classi UtenteDb e PartitaIncompletaDb, null;
- 28/09 19:00, Andrei, (collegamento client al server + testing base + piccole adattamenti a gui), continuazione gui;
- 28/09, Fabio, (unificazione messaggi e refactoring Giocatore), null;
- 29/09 12:00, Andrei, (continuazione gui--> aggiunta gestione asincrona per bottoni logout con una classe accessoria + label per verifica connessione, per ora solo su VistaAccesso), continuazione gui;
- 30/09 13:00, Andrei, (sperimentazione visualizzazione di stato connessione con strumenti javafx), continuazione gui;

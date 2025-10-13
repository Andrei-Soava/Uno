# Manutenzione del progetto
## Introduzione
Il presente piano di manutenzione descrive le attività per garantire la continuità, l'affidabilità e l'evoluzione del progetto.
La manutenzione del software comprende tutte le modifiche apportate al prodotto dopo la consegna, con lo scopo di mantenerlo funzionante, adattarlo a nuovi contesti e migliorarne le prestazioni e le funzionalità.
La manutenzione è suddivisa in quattro categorie principali: manutenzione correttiva, adattiva, preventiva e perfettiva.

## Politiche di aggiornamento del documento
Per monitorare le attività di manutenzione svolte dai membri del progetto, ciascun membro è tenuto ad aggiornare il presente documento inserendo, nei relativi log, una descrizione sintetica delle attività effettuate. Tale descrizione dovrà rispettare il seguente formato di messaggi:

1. il messaggio deve iniziare con il carattere "-" seguito da uno spazio " ";
2. data messaggio (es. 25/08), eventualmente accompagnata dall'orario (quest'ultimo, non obbligatorio);
3. responsabile/i del messaggio (es. Andrei se intervento individuale o Andrei,Fabio,Giuseppe se intervento di gruppo);
4. descrizione SINTETICA di quanto fatto in quel giorno (es. manutenzione server);
5. eventuale scrittura tra parentesi () per chiarezza, nei punti 3.,4.;
6. l'intero messaggio deve essere posizionato dopo quello che lo precede e si deve chiudere con il carattere ";";

## Manutenzione correttiva
L'obiettivo di questo tipo di manutenzione è individuare e correggere errori o malfunzionamenti durante l'uso del progetto.
In questo progetto, questo tipo di manutenzione verrà utilizzato per:
1. Correzione di crash imprevisti durante l'avvio o la chiusura di una partita;
2. Risoluzione di errori di connessione tra client e server;
3. Risoluzione di anomalie durante la fase di registrazione o di login degli utenti;
4. Risoluzione di bug nella logica di gioco;
### Log messaggi:

## Manutenzione adattiva
Questo tipo di manutenzione ha lo scopo di occuparsi dell'adattamento del software ai cambiamenti dell'ambiente.
La manutenzione adattiva non comporta modifiche alla funzionalità del sistema. 
Per questo prodotto, tale tipo di manutenzione sarà impiegato per:
1. Aggiornamento delle librerie;
2. Adattamento del sistema a nuove versioni di Java;
3. Configurazione del gioco per funzionare su sistemi operativi diversi o server aggiornati;
4. Eventuale migrazione del database verso un nuovo DBMS o un servizio cloud;
### Log messaggi:

## Manutenzione preventiva
Questo tipo di manutenzione riguarda le attività volte ad aumentare la manutenibilità del sistema, come l'aggiornamento della documentazione, l'aggiunta di commenti e il miglioramento della struttura modulare del sistema. 
In questo progetto, questo tipo di manutenzione verrà utilizzato per:
1. Refactoring di codice complesso per semplificarne la lettura;
2. Aggiunta di commenti o documentazione tecnica;
3. Ottimizzazione delle query SQL per migliorare le prestazioni del database;
4. Revisione e aggiornamento periodico dei test automatizzati;
### Log messaggi:

## Manutenzione perfettiva
Questo tipo di manutenzione si occupa principalmente di soddisfare i requisiti degli utenti nuovi o modificati. Si tratta di miglioramenti funzionali al sistema. La manutenzione perfettiva include anche attività volte ad aumentare le prestazioni del sistema o a migliorarne l'interfaccia utente.
Per questo prodotto, tale tipo di manutenzione sarà impiegato per:
1. Miglioramento dell’interfaccia utente (UI/UX);
2. Aggiunta di statistiche avanzate sulle partite degli utenti;
3. Ottimizzazione delle prestazioni del server per gestire un numero maggiore di giocatori.
### Log messaggi: 
- 13/10, Andrei, (miglioramento interfaccia grafica durante il gioco, mettendo al centro mazzo e carta corrente e tutto intorno i giocatori + chiara direzionalità con cerchio e frecce + evidenziazione del giocatore corrente con un glow sul suo nome);

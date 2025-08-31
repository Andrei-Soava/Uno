# Gestione del Progetto

---

## Introduzione

Il presente progetto verrà svolto, perlomeno per quanto preventivato, utilizzando le metodologie "agili" per la modellazione e lo sviluppo di codice. 
In particolare, si conta di utilizzare SCRUM, con sprint settimanali e riunioni giornaliere (per aggiornarsi constantemente).
Qualora lo si ritenesse necessario, si considera la possibilità di attingere (in minore misura) ai modelli "tradizionali", come quello a cascata, ad esempio quando è necessario uno sviluppo più collettivo (ingegneria dei requisiti oppure progettazione).

---

## Politica di aggiornamento del documento

Il documento qui presente ha come obiettivo la monitorizzazione dell'attività svolta dai membri del progetto, mediante una descrizione sintetica di quanto fatto in un determinato giorno e di quale siano gli obiettivi per la volta dopo. 
La necessità di questo documento (rispetto al già presente log di GitHub) deriva dalla volontà di mantenere ben presente gli obiettivi lungo e breve termine di ciascuno membro del progetto, in modo da non avere ambiguità mentre si lavora in asincrono. 
In funzione di questo obiettivo, ciascuno dei membri **potrà** aggiornare questo documento e **dovrà** rispettare il seguente formato di messaggi:
1.  il messaggio **deve** iniziare con il carattere "-" e seguito da uno spazio " ";
2.  data messaggio (es. *25/08*) eventualmente con orario (quest'ultimo, non obbligatorio);
3.  responsabile/i del messaggio (es. *Andrei* se intervento individuale o *Andrei,Fabio,Giuseppe* se intervento di gruppo);
4.  descrizione **SINTETICA** di quanto fatto in quel giorno (es. *lavorato sul project plan*);
5.  descrizione **SINTETICA** di quanto ci si aspetta di fare la volta successiva (es. *inziare a lavorare sulla specifica dei requisiti*) (opzionale, ma fortemente consigliato);
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
- 25/08 14-16:30, (Andrei, Giuseppe), miglioramento struttura progetto (cartella documentazione) e prima versione use case diagram dentro documento 2, continauzione use case diagram e modellizzazione;
- 25/08 16:30-18, Andrei, definizione politiche aggiornamento documento 1 (qui presente) e aggiunta log della settimana del 18/08, null;
- 25/08, **AVVISO**, Andrei, (ciascun membro dovrà fare i propri log d'ora in avanti, salvo sessioni di gruppo in cui li fa uno per tutti);
- 27/08 15:30, Andrei, descrizione generale architettura software--> documento 3, continuazione progettazione--> design vero e proprio;
- 27/08 19:30, Andrei, ottimizzazione piano del progetto e requisiti, consegnare project plan;
- 27/08, **AVVISO**, Andrei, (il project plan è stato finalizzato--> NON aggiornare documento ProjectPlan);
- 31/08, Andrei, (1. aggiunta progetto Papyrus con dentro Usa Case Diagram (migliorato rispetto a versione precedente) e Class Diagram (vers.1); 2. rimozione cartella immagini e visualizzazione diagrammi direttamente da cartella progetto Papyrus, in cui sono presenti i PNG esportati; 3. aggiornamento documenti 2 e 3), sequence diagram o inizio implementazione;
- 31/08, **AVVISO**, Andrei, (per costruire/modificare i diagrammi UML su Papyrus, clonare la repository su GitHub desktop e successivamente da Eclipse aprire il progetto clonato (nella cartella nome_utente--> Documenti--> GitHub--> ONE(che appare dopo aver clonato la repository)); per visualizzare i diagrammi uml fatti/modificati--> tasto dx su diagramma .di --> export --> papyrus --> export all diagrams --> finish --> apparte file png nella cartella del progetto--> copiare dentro file .md di riferimento il comando `![nome_arbitrario](../UMLDiagrams/nomeimmagine.PNG))`);


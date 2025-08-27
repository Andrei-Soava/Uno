# PROJECT PLAN “ONE”

---

## 1. Introduzione:
Con questo progetto si realizzerà un gioco fortemento ispirato al celebre gioco di carte *Uno*, le cui regole sono pressochè già universalmente conosciute.\
Lo sviluppo del gioco nasce dall’idea di creare una versione minimalista e digitale della sua controparte “fisica”.
A tale scopo, si prevede di dedicare molto tempo nella definizione delle componenti responsabili durante l’esecuzione del gioco, in modo da avere un’architettura modulare e facilmente estendibile.\
L’idea alla base dell’applicazione è quella di poter iniziare una nuova partita, selezionando un numero variabile di giocatori, i quali avranno l’opportunità di compiere diverse azioni in base alle carte che hanno in mano, riproponendo quindi le regole tipiche di Uno. Il giocatore avrà la possibilità di giocare contro amici (modalità multigiocatore) oppure contro il computer (modalità offline). Infine il giocatore avrà la possibilità di registrarsi (in modo da tenere conto delle sue statistiche e eventuali partite in sospeso), anche se non è obbligatorio per poter giocare.\
I responsabili per la realizzazione di suddetta applicazione, chiamata da qui in avanti come **One**,
saranno Locatelli Fabio, Luisi Giuseppe e Soava Andrei.


## 2. Modello di processo:
Durante lo sviluppo di One si prevede di usufruire in parte di un modello di processo "tradizionale", come il modello *a cascata* (ad esempio per le fasi di ingegneria dei requisiti e di progettazione), ed in parte di un modello di processo agile, come *SCRUM* (ad esempio per le fasi d'implementazione di codice e testing).\
Vista la dimensione ridotta dell’applicazione (perlomeno a priori) e del gruppo di sviluppo, si ritiene che questo approccio *ibrido* possa fornire un valido compromesso per lavorare in maniera efficiente sia in gruppo, che in autonomia.\
In particolare, considerato il tempo di realizzazione del software (poco più di un mese) e la cadenza d’incontro dei responsabili di sviluppo (tutti i giorni, da remoto o in presenza), si ritiene adatta la presenza di sprint settimanali e di daily scrum ogni qualvolta fosse necessario.\
Infine, considerando il numero ridotto di membri e l’elevata interazione tra essi, non verranno adottati meccanismi di Kanban boards per monitorare l’attività, bensì solo un log comune che farà da “cronologia di sviluppo” (vedi "1 GESTIONE DEL PROGETTO").


## 3. Organizzazione del progetto:
La squadra di sviluppo di One sarà organizzata come una tipica squadra agile, pertanto priva di gerarchie e/o ruoli esclusivi, bensì guidata dalla disciplina e dal desiderio di realizzazione dell’applicazione entro le scadenze prefissate. A tale scopo, come anticipato nel punto 2., si considera cruciale l'aspetto della **comunicazione** e del **dialogo**, ottenuto mediante incontri frequenti in presenza o da remoto.\
Eventuali lacune in campo tecnico verranno risolte mediante il dialogo di squadra e, qualora dei problemi persistessero, si farà appello ai docenti del corso di Ingegneria del software o suoi affiliati.


## 4. Standard, linee guida e procedure:
Per lo sviluppo di One verrà utilizzato Eclipse come ambiente di sviluppo. In particolare:
- il linguaggio di programmazione sarà Java (con Maven)
- Papyrus e Swing rispettivamente per la modellazione e lo sviluppo dell’interfaccia utente.
- JUnit verrà (eventualmente) utilizzato per il testing.\

Come database relazionale si utilizzerà PostgreSQL, vista la semplicità d'utilizzo.\
Come piattaforma di condivisione del codice e della documentazione si userà GitHub. In particoalre, per quanto riguarda la documentazione, gli standard di aggiornamento dei documenti verranno esplicitati (qualora fosse necessario) all'interno dei documenti stessi.\
In termine di procedure, si rimanda alla sezione 2. per le tempistiche e l’organizzazione delle attività.

## 5. Attività di gestione e 6. rischi:
L’obiettivo con cui la squadra si adopererà alla realizzazione di One è quello di rispettare le scadenze auto-imposte (ad esempio degli sprint) e quelle generali (per la consegna del progetto), per cui i rischi a cui si presuppone di andare incontro sono proprio quelli di carattere temporale, oltre che gli inevitabili rischi di carattere tecnico, come ad esempio la comprensione dell’uso di un tool o di un problema riscontrato durante lo sviluppo.\
Rischi di gestione errata di risorse non sono considerati in questo contesto. 


## 7. Personale:
Ci si riferisce al paragrafo 3., in quanto non si avranno membri esterni su cui si potrà fare riferimento durante lo sviluppo di One.\
In termine di suddivisione *tecniche* all'interno del gruppo di sviluppo, queste verranno stabilite durante progressivamente e riportate nel documento "1 GESTIONE DEL PROGETTO".


## 8. Metodi e tecniche:
Per la raccolta dei requisiti del software di One ci si baserà in un primo momento sullo studio accurato delle regole di base di Uno, per poi adattarle opportunamente ad una variante Java-like del gioco, con i dovuti accorgimenti in termini di struttura architetturale.\
Per quanto riguarda la progettazione essa sarà fondata sulle funzionalità da implementare, e che quindi saranno specificate durante l’ingegneria dei requisiti.
Per l’implementazione, ossia la traduzione in Java, si sfrutteranno diagrammi UML prodotti (useCase, class, stateMachine, sequence ecc.), design patterns (molto probabilmente ispirata ad un pattern architetturale Model-View-Controller) e pratiche opportune.\
L’ambiente di sviluppo utilizzato sarà Eclipse, mentre quello di condivisione di codice sarà GitHub.
Grazie alle funzionalità di quest’ultimo, sarà possibile anche gestire la versione del software (e delle sue componenti) mediante opportuni branch nel repository di riferimento.\
Infine, per la fase di testing si utilizzerà sempre Eclipse, mediante il tool JUnit, mentre i vari casi di testing saranno stabiliti nel documento dedicato ("4 TESTING").


## 9. Garanzia di qualità:
La soddisfazione dei requisiti di qualità, il cui riferimento sarà l’ISO 9126, verranno analizzati man mano durante l’implementazione, così da evitare problemi a prodotto concluso. In modo da garantire la soddisfazione dell'utente finale, il testing e lo spirito di auto-critica/riflessione sono considerati imprescindibili.   


## 10. Pacchetti di lavoro:
La suddivisione delle attività volte allo sviluppo di One sarà esplicitata man mano all’interno del documento di "1 GESTIONE DEL PROGETTO". Suddivisioni gerarchiche in questo senso non verranno applicate poiché poco affini ad una squadra di tre soli membri.


## 11. Budget e 12. risorse:
Il budget sarà esclusivamente in termine di tempo, da cui la necessità di pianificare in maniera attenta la ripartizione di ore-uomo per una parte del progetto (circa 50 ore per persona). 
Ci si attende comunque un’enfasi sulla parte di modellizzazione, sulla parte di implementazione di codice e sul testing (in proporzioni ancora da definire). 
In termine di risorse a disposizone del gruppo, verranno utilizzati:
-	Eclipse con Java (Maven) e Papyrus (UML), come ambiente di sviluppo
-	Swing per l'interfaccia grafica
-	JUnit per il testing
- PostgreSQL, come database
-	GitHub, come ambiente di condivisione
-	Discord e Whatsapp, come piattaforme di comunicazione

Nel documento "1 GESTIONE DEL PROGETTO" verrà eseguita una raccolta più dettagliata delle tempistiche, per cui si provvederà ad una valutazione più critica delle risorse necessarie effettivamente contro quelle attese.


## 13. Cambiamenti:
Nonostante venga utlizzato (in parte) SCRUM come modello di processo, si considera comunque importante tenere conto **esplicitamente** di eventuali cambiamenti (o per meglio dire, la *storia*) durante lo sviluppo, che verranno quindi opportunamente registrati nei vari documenti del progetto.\
Per quanto concerne le versioni dell’applicazione in cui cambia qualcosa di “importante”, verrà attuata la politica dei branch su GitHub, mentre eventuali miglioramenti (quali possibili espansioni di One, qualora ci fosse la possibilità di implementarle) verranno comunque evidenziati mediante degli appositi issue sempre su GitHub.


## 14. Consegna:
La consegna dell’applicazione One e della sua documentazione verrà effettuata su GitHub condividendo la repository con i docenti del corso prima dell’esame di Ingegneria del software, mentre questo documento verrà consegnato con circa un mese d’anticipo (rispetto all’esame), come è stato indicato dai docenti.




# Design

---

## Architettura generale
L’applicazione si basa su un’architettura client–server con elementi peer-to-peer per la gestione delle partite multiplayer in tempo reale.\
Client desktop Java costituito da un'applicazione Swing strutturata secondo il pattern MVC, dove:
- Model gestisce lo stato del gioco e dei dati utente.
- View rappresenta l’interfaccia grafica e visualizza lo stato.
- Controller coordina interazioni, logica e comunicazione.
- Server applicativo: gestisce autenticazione, statistiche, creazione e gestione delle lobby, salvataggio partite contro bot, e interfaccia verso il database.
- Database relazionale: PostgreSQL per conservare utenti, statistiche e snapshot delle partite incompiute.

Comunicazione in rete:
- HTTP(S) / WebSocket per interazione client–server (login, lobby, statistiche, notifiche).
- Connessioni P2P dirette tra client durante la partita per scambio rapido di mosse e stato di gioco.

Pattern architetturale di riferimento:
- Chiarezza di separazione tra presentazione (GUI), logica di gioco (engine) e gestione dati.
- Scalabilità orizzontale: server leggero, partite distribuite tra client.
- Indipendenza e modularità per facilitare manutenzione e testing.

---

## Views architetturali
Secondo i diversi punti di vista architetturali:

### 1. Vista logica
Mostra la suddivisione in macro‑blocchi:
- Client GUI (MVC).
- Motore di gioco (regole, turni, validazioni).
- Server (servizi remoti, gestione lobby, persistenza).
- Database (tabelle principali e relazioni).
- Evidenzia la separazione tra strato di presentazione, logica di dominio e persistenza.

### 2. Vista di processo
Rappresenta i principali flussi e interazioni concorrenti:
- Ciclo di vita di una partita multiplayer (creazione lobby → avvio → scambio mosse → conclusione).
- Partita contro bot e gestione salvataggi locali/remoti.
- Eventi asincroni (notifiche turno, ONE!, aggiornamenti stato).

### 3. Vista fisica
Mostra la distribuzione sui nodi:
- Postazioni client (Java + Swing) sui PC degli utenti.
- Server applicativo centralizzato.
- Database su macchina dedicata o cluster.
- Connessioni sicure client–server e P2P client–client per la fase di gioco.

### 4. Vista dei dati
Illustra le entità principali (utenti, statistiche, partite incompiute) e la loro persistenza.\
Mostra i flussi di aggiornamento (fine partita, abbandono, cancellazione account).

### 5. Vista delle interfacce (alto livello)
Descrive le API server disponibili (login, registrazione, lobby, statistiche, salvataggi).\
Sintetizza il protocollo base usato in P2P per scambio mosse e sincronizzazione.

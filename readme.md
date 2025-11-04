# 🃏 ONE – Gioco di Carte Multiplayer

Benvenuto nel progetto **ONE**, un gioco di carte multiplayer ispirato a UNO, sviluppato in Java con architettura modulare Maven e supporto per partite client-server.

---

## Struttura del Repository

```
├── UMLdiagrams/         # Diagrammi UML creati con Papyrus
├── documentazione/      # Documenti tecnici, regolamento di gioco e project plan
└── onegame-root/        # Progetto Maven principale con i moduli common, client e server
```

---

## Architettura del Progetto

Il progetto è organizzato in tre moduli Maven:

- **common**  
  Contiene:
  - Il modello di gioco per partite offline
  - Il protocollo di comunicazione tra client e server

- **client**  
  Contiene:
  - L'applicazione desktop sviluppata con JavaFX
  - Dipende dal modulo `common`

- **server**  
  Contiene:
  - Il server di gioco
  - Un database embedded per la gestione dei dati
  - Dipende dal modulo `common`

---

## Requisiti

Per eseguire correttamente il progetto, assicurati di avere:

- **Java**: versione **21** o superiore  
- **Maven**: versione **3.9** o superiore  
- **Sistema Operativo**:  
  - Windows 10 o superiore  
  - Linux (qualsiasi distribuzione recente)  
  - macOS

---

## Esecuzione del Progetto

Tutti i file di esecuzione si trovano nella cartella `onegame-root`.

1. **Compilazione del progetto**  
   Esegui lo script per installare tutti i moduli Maven:

   ```bash
   mvn_clean_install.bat
   ```

2. **Avvio del server**  
   Avvia il server con:

   ```bash
   run_server.bat
   ```

3. **Avvio del client**  
   Avvia uno o più client desktop con:

   ```bash
   run_client.vbs
   ```

   Alternativamente usa:
   ```bash
   run_client.bat
   ```

---

## 📚 Documentazione

Consulta la cartella `documentazione/` per:

- Il regolamento ufficiale del gioco ONE
- Il project plan
- Altri documenti tecnici

I diagrammi UML si trovano nella cartella `UMLdiagrams/` e sono stati realizzati con Papyrus.

## Creatori

- Fabio Locatelli (matricola 1081940)
- Giuseppe Luisi (matricola 1073970)
- Andrei Soava (matricola 1079636)

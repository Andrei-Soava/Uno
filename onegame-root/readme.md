# ONEGAME

## Requisiti di sistema

| Componente | Versione consigliata |
|------------|----------------------|
| Java JDK   | **25.0.1** (LTS)     |
| Maven      | **3.9.11**           |
| Eclipse    | **2023-12** o superiore |
| OS         | Windows 10/11, Linux, macOS |

> ⚠️ Non usare la JRE fornita da Eclipse: non supporta class file 69.0 e non è compatibile con Java 25.

---

## 🗂️ Struttura del progetto Maven

```
onegame-root/               → Modulo parent
├── onegame-common/         → Modello, protocollo di comunicazione
├── onegame-server/         → Backend multiplayer e database
└── onegame-client/         → Applicazione client JavaFX
```
---

## Configurazione Maven `onegame-root`

### Versioni

```xml
<properties>
  <maven.compiler.release>25</maven.compiler.release>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <javafx.version>21.0.2</javafx.version>
  <jackson.version>2.17.0</jackson.version>
  <log4j.version>2.23.1</log4j.version>
  <netty-socketio.version>1.7.17</netty-socketio.version>
  <socketio.client.version>1.0.0</socketio.client.version>
  <jwt.version>4.4.0</jwt.version>
  <h2.version>2.1.214</h2.version>
  <slf4j.version>1.7.36</slf4j.version>
  <junit.version>4.13.2</junit.version>
</properties>
```
---

## Modulo `onegame-common`

- Contiene modello, protocollo di comunicazione
- Nessuna dipendenza da JavaFX o Netty
- Usato da `onegame-client` e `onegame-server`

---

## Modulo `onegame-server`

- Backend multiplayer con:
  - `netty-socketio` per WebSocket
  - `h2` per persistenza embedded
- Funzionalità offerte: login, registrazione, matchmaking, gioco, salvataggi, statistiche

### Esecuzione

```bash
cd onegame-server
mvn clean compile exec:java
```
---

## Modulo `onegame-client`

- App client JavaFX con viste
- Connessione via `socket.io-client`

### Esecuzione

```bash
cd onegame-client
mvn clean javafx:run
```

> In Eclipse, usa una Run Configuration con `JAVA_HOME` impostata manualmente

---

## Test

- Tutti i moduli usano `junit` 4.13.2
- I test sono definiti in `src/test/java`
- Esegui con:

```bash
mvn test
```


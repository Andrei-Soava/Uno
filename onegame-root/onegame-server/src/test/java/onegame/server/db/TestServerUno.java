package onegame.server.db;

import io.socket.client.Ack;
import onegame.client.net.ClientSocket;
import onegame.modello.net.ProtocolloMessaggi;
import onegame.server.ServerUno;

public class TestServerUno {

    public static void main(String[] args) throws Exception {
        int porta = 8080;
        String host = "127.0.0.1";

        // AVVIO SERVER
        ServerUno server = new ServerUno(host, porta);
        server.avvia();

        // Attendi che il server sia completamente avviato
        Thread.sleep(2000);

        // CREAZIONE E CONNESSIONE CLIENT
        ClientSocket client = new ClientSocket("http://" + host + ":" + porta);
        client.connect();
        
        // Attendi la connessione
        Thread.sleep(1000);

        // TEST REGISTRAZIONE
        System.out.println("[TEST] Invio richiesta di registrazione...");
        
        client.register("thor", "password123", new Ack() {
            @Override
            public void call(Object... args) {
                if (args.length > 0 && args[0] != null) {
                    // REGISTRAZIONE FALLITA
                    String errore = args[0].toString();
                    System.out.println("[TEST] REGISTRAZIONE FALLITA: " + errore);
                    
                    try {
                        Thread.sleep(1000);
                        testLogin(client);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // REGISTRAZIONE RIUSCITA
                    System.out.println("[TEST] REGISTRAZIONE RIUSCITA");
                    
                    // Procedi con il login dopo un attimo
                    try {
                        Thread.sleep(1000);
                        testLogin(client);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Attendi che i test completino
        Thread.sleep(10000);

        // CHIUSURA
        client.disconnect();
        server.stop();
        System.out.println("[TEST] Test completato!");
    }

    private static void testLogin(ClientSocket client) throws Exception {
        System.out.println("[TEST] Invio richiesta di login...");
        
        client.login("thor", "password123", new Ack() {
            @Override
            public void call(Object... args) {
                if (args.length > 0 && args[0] != null) {
                    String response = args[0].toString();
                    if ("success".equals(response)) {
                        System.out.println("[TEST] LOGIN RIUSCITO!");
                    }
                } else {
                    System.out.println("[TEST] Nessuna risposta login dal server");
                }
            }
        });
    }
}
//package onegame.modello2;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Map;
//
//public class Messaggi {
//    public static class RichiestaAutenticazione implements Serializable {
//        public String username;
//        public String password;
//        public boolean signup;
//    }
//    public static class RispostaAutenticazione implements Serializable {
//        public boolean ok;
//        public String messaggio;
//        public String sessionId;
//    }
//    public static class RichiestaJoinRoom implements Serializable {
//        public String roomId; // optional; if null create new
//        public String sessionId;
//    }
//    public static class RispostaJoinRoom implements Serializable {
//        public boolean ok;
//        public String roomId;
//        public String messaggio;
//    }
//    public static class StatoGioco implements Serializable {
//        public String roomId;
//        public List<Carta> mano;
//        public Carta topCarta;
//        public String giocatoreCorrente;
//        public List<String> giocatori;
//        public Map<String,Integer> counts; // player->cardsCount
//        public boolean tuoTurno;
//        public int tempoMancanteInSecondi;
//        public boolean direzione;
//    }
//    public static class RichiestaGiocaCarta implements Serializable {
//        public String sessionId;
//        public String roomId;
//        public Carta carta;
//        public Carta.Colore chosenColor;
//    }
//    public static class DrawCardResponse implements Serializable {
//        public Carta carta;
//    }
//    public static class MessaggioGenerico implements Serializable {
//        public String testo;
//    }
//}

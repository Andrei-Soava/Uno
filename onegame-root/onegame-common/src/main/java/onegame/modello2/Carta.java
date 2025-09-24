package onegame.modello2;

import java.io.Serializable;
import java.util.Objects;

public class Carta implements Serializable {
    public enum Colore { ROSSO, GIALLO, VERDE, BLU, NERO }
    public enum TipoCarta { NUMERO, SKIP, CAMBIO_GIRO, PESCA_DUE, CAMPO_COLORE, PESCA_QUATTRO }

    private Colore colore;
    private TipoCarta tipo;
    private int numero; // valido solo per carte NUMERO

    public Carta() {}

    public Carta(Colore colore, TipoCarta tipo, int numero) {
        this.colore = colore;
        this.tipo = tipo;
        this.numero = numero;
    }

    public static Carta number(Colore color, int num) {
        return new Carta(color, TipoCarta.NUMERO, num);
    }
    public static Carta skip(Colore color){return new Carta(color, TipoCarta.SKIP,-1);}
    public static Carta cambioGiro(Colore color){return new Carta(color, TipoCarta.CAMBIO_GIRO,-1);}
    public static Carta pescaDue(Colore color){return new Carta(color, TipoCarta.PESCA_DUE,-1);}
    public static Carta cambioColore(){return new Carta(Colore.NERO, TipoCarta.CAMPO_COLORE,-1);}
    public static Carta pescaQuattro(){return new Carta(Colore.NERO, TipoCarta.PESCA_QUATTRO,-1);}

    public Colore getColore(){return colore;}
    public TipoCarta getTipo(){return tipo;}
    public int getNumero(){return numero;}

    @Override
    public String toString() {
        if (tipo==TipoCarta.NUMERO) return colore+" "+numero;
        return colore+" "+tipo;
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof Carta)) return false;
        Carta c=(Carta)o;
        return tipo==c.tipo && colore==c.colore && numero==c.numero;
    }
    @Override
    public int hashCode(){return Objects.hash(colore,tipo,numero);}
}

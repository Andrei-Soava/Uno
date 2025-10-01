package onegame.client.controllore.offline;

/**
 * classe di supporto per operazioni su salvataggi:
 * usato nella funzione di callback della VistaSalvataggi
 * usato dentro ControlloreSalvataggi per gestire il callback
 */
public class EventoSalvataggio {
    public enum Tipo {
        GIOCA,
        RINOMINA,
        ELIMINA
    }

    private final Tipo tipo;
    private final String nomeOriginale;
    private final String nuovoNome; // usato solo per rinomina

    public EventoSalvataggio(Tipo tipo, String nomeOriginale, String nuovoNome) {
        this.tipo = tipo;
        this.nomeOriginale = nomeOriginale;
        this.nuovoNome = nuovoNome;
    }

    public Tipo getTipo() { return tipo; }
    public String getNomeOriginale() { return nomeOriginale; }
    public String getNuovoNome() { return nuovoNome; }
}

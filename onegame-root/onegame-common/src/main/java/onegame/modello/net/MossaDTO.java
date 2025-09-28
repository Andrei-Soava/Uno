package onegame.modello.net;

public class MossaDTO {
    public enum TipoMossa {
        GIOCA_CARTA,
        PESCA,
        PASSA
    }

    public TipoMossa tipo;
    public CartaDTO carta;
    public String coloreScelto;

    public MossaDTO() {}

    public MossaDTO(TipoMossa tipo) { this.tipo = tipo; }

    public MossaDTO(TipoMossa tipo, CartaDTO carta) {
        this.tipo = tipo;
        this.carta = carta;
    }
}

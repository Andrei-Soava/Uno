package onegame.modello.net;

import onegame.modello.carte.Colore;

public class MossaDTO {
	public enum TipoMossa {
		GIOCA_CARTA, PESCA, PASSA, DICHIARA_UNO, PESCA_E_PASSA
	}

	public TipoMossa tipo;
	public CartaDTO carta;
	public Colore coloreScelto;

	public MossaDTO() {
	}

	public MossaDTO(TipoMossa tipo) {
		this.tipo = tipo;
	}

	public MossaDTO(TipoMossa tipo, CartaDTO carta) {
		this.tipo = tipo;
		this.carta = carta;
	}

	@Override
	public String toString() {
		return "MossaDTO [" + (tipo != null ? "tipo=" + tipo + ", " : "")
				+ (carta != null ? "carta=" + carta + ", " : "")
				+ (coloreScelto != null ? "coloreScelto=" + coloreScelto : "") + "]";
	}

}

package onegame.server;

public abstract class Stanza {
	private final int codice;
	private final long id;
	private final String nome;
	private final int maxUtenti;
	private final GestoreConnessioni gestoreConnessioni;

	public Stanza(int codice, long id, String nome, int maxUtenti, GestoreConnessioni gestoreConnessioni) {
		this.codice = codice;
		this.id = id;
		this.nome = nome;
		this.maxUtenti = maxUtenti;
		this.gestoreConnessioni = gestoreConnessioni;
	}

	public int getCodice() {
		return codice;
	}

	public long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public int getMaxUtenti() {
		return maxUtenti;
	}

}

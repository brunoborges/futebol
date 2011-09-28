package futebol;

public class Jogador {

	private String nome;
	private double pontuacao;

	public Jogador(String nome, double pontuacao) {
		this.nome = nome;
		this.pontuacao = pontuacao;
	}

	public String getNome() {
		return nome;
	}

	public double getForca() {
		return pontuacao;
	}

	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(getNome()).append(" (").append(getForca()).append(")");
		return string.toString();
	}
}
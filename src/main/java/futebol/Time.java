package futebol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Time {

	private List<Jogador> jogadores = new ArrayList<Jogador>();
	private int forca = 0;
	private String nome;
	private int limiteJogadores;

	public Time(String nome, int jogadoresPorTime) {
		this.nome = nome;
		this.limiteJogadores = jogadoresPorTime;
	}

	public boolean isCompleto() {
		return jogadores.size() == limiteJogadores;
	}

	public void addJogador(Jogador j) {
		if (isCompleto()) {
			throw new IllegalStateException("Time '" + getNome() + "' já está completo");
		}
		forca += j.getForca();
		jogadores.add(j);
	}

	public int getForca() {
		return forca;
	}

	public String getNome() {
		return nome;
	}

	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(getNome());
		string.append(" [forca = ").append(getForca()).append(", jogadores = {");
		for (Jogador j : jogadores) {
			string.append("\n\t");
			string.append(j.getNome());
			string.append(" (").append(j.getForca()).append(')');
			string.append(',');
		}
		string.deleteCharAt(string.length() - 1);
		string.append("}]");
		return string.toString();
	}

	public void reset() {
		this.forca = 0;
		jogadores.clear();
	}
}
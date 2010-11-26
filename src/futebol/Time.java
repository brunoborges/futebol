package futebol;

import java.util.ArrayList;
import java.util.Arrays;

public class Time {
	private ArrayList<Jogador> jogadores = new ArrayList<Jogador>();
	private int forca = 0;
	private String nome;

	public Time(String nome) {
		this.nome = nome;
	}

	public void addJogador(Jogador j) {
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
		string.append(" (").append(getForca()).append(")");
		string.append('\n');

		char[] linha = new char[string.indexOf("\n")];
		Arrays.fill(linha, '-');
		string.append(linha).append('\n');

		for (Jogador j : jogadores) {
			string.append(j.toString());
			string.append('\n');
		}

		return string.toString();
	}

	public void reset() {
		this.forca = 0;
		jogadores.clear();
	}
}
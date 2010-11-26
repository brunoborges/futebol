package futebol;


/**
 * seus merdinha... :P tah vendo?
 * ein cesinha!!
 * @author bzjwkb
 *
 */
public class Jogador {
	private String nome;
	private int pontos;

	public Jogador(String nome, int pontos) {
		this.nome = nome;
		this.pontos = pontos;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getForca() {
		return pontos;
	}

	public void setPontos(int pontos) {
		this.pontos = pontos;
	}

	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(getNome());
		//string.append(',').append(getForca());
		//return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
		return string.toString();
	}
}
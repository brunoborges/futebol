package futebol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/*
 import org.apache.commons.lang.builder.ToStringBuilder;
 import org.apache.commons.lang.builder.ToStringStyle;
 */

public class FazTime {

	static List<Jogador> jogadores = new ArrayList<Jogador>();
	static Time timeA = new Time("Zaneta");
	static Time timeB = new Time("Capricho");
	static Time timeDaVez = timeA;
	static Time outroTime = timeB;

	private static void inicializaJogadores() {
		jogadores.clear();
		jogadores.add(new Jogador("Thiago", 0));
		jogadores.add(new Jogador("Bruno B", 0));
		jogadores.add(new Jogador("Felipe", 5));
		jogadores.add(new Jogador("Vanderval", 2));
		jogadores.add(new Jogador("Leonardo", 4));
		jogadores.add(new Jogador("Bruno T", 3));
		jogadores.add(new Jogador("Marcelo", 2));
		jogadores.add(new Jogador("Carlos", 4));
		jogadores.add(new Jogador("Alisson", 5));
		jogadores.add(new Jogador("Fernando", 3));
		//jogadores.add(new Jogador("Mandinho", 1));
	}

	static boolean isEquilibrado(Time a, Time b) {
		int diff = Math.abs(a.getForca() - b.getForca());
		return (diff == 0 || diff < 3);
	}

	public static void main(String[] args) {
		int total = 10;
		
		ArrayList<Time[]> times = new ArrayList<Time[]>();
		for (int i = 0; i < total; i++) {
			inicializaJogadores();
			montaTimes();

			if (isEquilibrado(timeA, timeB)) {
				times.add(new Time[]{timeA, timeB});
			}

			//timeA.reset();
			//timeB.reset();
			//timeA = new Time("Time A");
			//timeB = new Time("Time B");
		}

		double percentual = (double) times.size() / (double) total;
		System.out.println(percentual * 100);
		
		System.out.println(timeA);
		System.out.println("-----");
		System.out.println(timeB);
	}

	private static void montaTimes() {
		// Sorteia goleiros
		int goleiro = new Random(524512434231L).nextInt(2);
		timeA.addJogador(jogadores.get(goleiro));
		timeB.addJogador(jogadores.get(goleiro == 1 ? 0 : 1));
		jogadores.remove(0);
		jogadores.remove(0);

		// Randomiza a lista de jogadores
		Collections.shuffle(jogadores, new Random(73761786786L));

		Random rand = new Random(67867355245L);
		while (jogadores.size() > 0) {
			Jogador j = getJogador();
			timeDaVez.addJogador(j);

			// Inverte times pra proxima escolha
			if (timeDaVez == timeA) {
				timeDaVez = timeB;
				outroTime = timeA;
			} else {
				timeDaVez = timeA;
				outroTime = timeB;
			}
		}
	}

	private static Jogador getJogador(int forca) {
		Jogador jj = null;
		if (jogadores.size() == 0)
			return null;
		else if(jogadores.size() == 1)
			return jogadores.remove(0);
		
		Iterator<Jogador> it = jogadores.iterator();
		while(it.hasNext()) {
			Jogador j = it.next();
			if (j.getForca() == forca) {
				jj = j;
				break;
			}
		}
		
		if(jj != null)
			jogadores.remove(jj);

		return jj;
	}

	private static Jogador getJogador() {
		double peso = Math.random();
		
		if (timeDaVez.getForca() < outroTime.getForca()) {
			peso += Math.random();
		} else {
			peso -= Math.random();
		}

		int forca = 0;
		if (peso >= 0 && peso < 0.10) {
			// 10%
			forca = 1;
		} else if (peso >= 0.10 && peso < 0.25) {
			// 15%
			forca = 2;
		} else if (peso >= 0.25 && peso < 0.65) {
			// 40%
			forca = 3;
		} else if (peso >= 0.65 && peso < 0.95) {
			// 30%
			forca = 4;
		} else if (peso >= 0.95) {
			// 5%
			forca = 5;
		}

		Jogador jj = getJogador(forca);
		while (jj == null && jogadores.size() > 0)
			jj = getJogador();

		return jj;
	}

}
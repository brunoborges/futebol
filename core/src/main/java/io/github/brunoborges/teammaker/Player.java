/*
 * Copyright (C) 2025 Bruno Borges
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package io.github.brunoborges.teammaker;

import java.util.Arrays;
import java.util.List;

public record Player(String name, double score) {

}

enum DefaultPlayers {

	PLAYER_1(new Player("Alex", 3)),
	PLAYER_2(new Player("Andre", 2)),
	PLAYER_3(new Player("Augusto", 3)),
	PLAYER_4(new Player("Bruno", 4)),
	PLAYER_5(new Player("Diego", 3)),
	PLAYER_6(new Player("Diogo", 4)),
	PLAYER_7(new Player("Duda", 4)),
	PLAYER_8(new Player("Felipe", 4)),
	PLAYER_9(new Player("Guilhermo", 3)),
	PLAYER_10(new Player("Jean", 3)),
	PLAYER_11(new Player("Juan", 2)),
	PLAYER_12(new Player("Leo", 4)),
	PLAYER_13(new Player("Leonardo", 3)),
	PLAYER_14(new Player("Lucio", 3)),
	PLAYER_15(new Player("Marcelo", 3)),
	PLAYER_16(new Player("Pedro", 3)),
	PLAYER_17(new Player("Rafael", 3)),
	PLAYER_18(new Player("Rodrigo", 4)),
	PLAYER_19(new Player("Tiago", 3)),
	PLAYER_20(new Player("Thiago", 2));

	private final Player player;

	DefaultPlayers(Player player) {
		this.player = player;
	}

	public Player player() {
		return player;
	}

	public static List<Player> get() {
		return Arrays.asList(DefaultPlayers.values()).stream().map(DefaultPlayers::player).toList();
	}
}

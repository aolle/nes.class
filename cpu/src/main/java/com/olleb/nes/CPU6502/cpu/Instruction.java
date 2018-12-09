/**
 * nes - NES / Famicom emulator
 * 
 * Copyright (C) 2018 Àngel Ollé Blázquez
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.olleb.nes.CPU6502.cpu;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import com.olleb.nes.CPU6502.mem.Memory;

@SuppressWarnings("unused")
public enum Instruction implements InstructionStrategy<Memory> {

	/**
	 * $ -> hex, ! -> dec, % -> binary # -> imm lower byte, / -> imm upper byte %1
	 * 1st byte, %2 2nd byte, %3 offset
	 */

	// format: opcode("name", bytes, registers) => cycles

	// TODO: refactor r.setA; Flags.setFlags.

	// Load/Store
	_A9("LDA #nn", 2, (var r, var m) -> {
		final int value = AddressingModes.IMMEDIATE.apply(r, m);
		r.setA(value);
		Flags.setFlags(r, value);
		return 2;
	}),

	_A5("LDA nn", 2, (var r, var m) -> {
		final int value = AddressingModes.ZERO_PAGE.apply(r, m);
		r.setA(value);
		Flags.setFlags(r, value);
		return 3;
	}),

	_B5("LDA nn,X", 2, (var r, var m) -> {
		final int value = AddressingModes.INDEXED_ZERO_PAGE_X.apply(r, m);
		r.setA(value);
		Flags.setFlags(r, value);
		return 4;
	});

	private final String opCode;
	private final int size;
	private final InstructionStrategy<Memory> instructionStrategy;

	Instruction(final String opCode, final int size, final InstructionStrategy<Memory> instructionStrategy) {
		this.opCode = opCode;
		this.size = size;
		this.instructionStrategy = instructionStrategy;
	}

	@Override
	public int exec(final Registers r, final Memory m) {
		return instructionStrategy.exec(r, m);
	}

	private static class Flags {
		private static final IntFunction<Boolean> ZERO = i -> (i == 0);
		// MSB 2^7 = 0x80
		private static final IntFunction<Boolean> NEGATIVE = i -> ((i & 0x0080) != 0);

		public static final void setFlags(final Registers registers, final int value) {
			registers.setZ(Flags.ZERO.apply(value));
			registers.setN(Flags.NEGATIVE.apply(value));
		}
	}

	private static class AddressingModes {
		// TODO: use RAM.Address to solve mem addresses like indexed zero page

		private static final BiFunction<Registers, Memory, Integer> IMMEDIATE = (r, m) -> m.read(r.inc());

		private static final BiFunction<Registers, Memory, Integer> ZERO_PAGE = (r, m) -> m.read(IMMEDIATE.apply(r, m));

		private static final BiFunction<Registers, Memory, IntFunction<Integer>> _INDEXED_ZERO_PAGE_PARAM = (r,
				m) -> i -> m.read(IMMEDIATE.apply(r, m) + i & 0x00FF);

		private static final BiFunction<Registers, Memory, Integer> INDEXED_ZERO_PAGE_X = (r,
				m) -> _INDEXED_ZERO_PAGE_PARAM.apply(r, m).apply(r.getX());

	}

}

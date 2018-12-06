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

import java.util.function.Consumer;
import java.util.function.Function;

import com.olleb.nes.CPU6502.mem.Memory;

@SuppressWarnings("unused")
public enum Instruction implements InstructionStrategy<Memory> {

	/**
	 * $ -> hex, ! -> dec, % -> binary # -> imm lower byte, / -> imm upper byte %1
	 * 1st byte, %2 2nd byte, %3 offset
	 */

	// format: opcode("name", bytes, registers => cycles

	// Load/Store
	A9("LDA #$%1", 2, (var r, var m) -> {
		AddressingModes.IMMEDIATE.accept(r);
		r.setA(m.read(r.getPc()));
		return 2;
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

	private static class AddressingModes {
		private static final Function<Integer, Boolean> ZERO = i -> (i == 0);
		// MSB 2^7 = 0x80
		private static final Function<Integer, Boolean> NEGATIVE = i -> ((i & 0x80) != 0);
		private static final Consumer<Registers> IMMEDIATE = r -> r.inc();
	}

}

/**
 * nes - 6502 CPU Emulator
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

@SuppressWarnings("unused")
public enum Instruction implements InstructionStrategy<Registers> {

	// $ -> hex, ! -> dec, % -> binary
	// # -> imm lower byte, / -> imm upper byte
	// %1 1st byte, %2 2nd byte, %3 offset

	A9("LDA #$%1", 2, (var r) -> {
		// TODO
		return 1;
	});

	private final String opCode;
	private final int size;
	private final InstructionStrategy<Registers> instructionStrategy;

	Instruction(final String opCode, final int size,
			final InstructionStrategy<Registers> instructionStrategy) {
		this.opCode = opCode;
		this.size = size;
		this.instructionStrategy = instructionStrategy;
	}

	@Override
	public int exec(final Registers r) {
		return instructionStrategy.exec(r);
	}

}

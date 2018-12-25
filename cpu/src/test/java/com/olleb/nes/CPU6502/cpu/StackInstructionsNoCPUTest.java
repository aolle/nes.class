/**
 * nes.class - NES / Famicom emulator
 * 
 * Copyright (c) 2018 Àngel Ollé Blázquez
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@DisplayName("Stack Instructions Test. No CPU.")
@TestInstance(Lifecycle.PER_CLASS)
class StackInstructionsNoCPUTest extends InstructionsTestBase {

	private final int offset = 0x0100;

	@Test
	@DisplayName("Processor status")
	void teststatus() {
		registers.setC(true);
		registers.setZ(false);
		registers.setI(false);
		registers.setD(true);
		registers.setB(true);
		registers.setV(true);
		registers.setN(false);

		Instruction.valueOf(0x08).exec(registers, ram);

		registers = new Registers();

		assertEquals(false, registers.isC());
		assertEquals(false, registers.isZ());
		assertEquals(false, registers.isI());
		assertEquals(false, registers.isD());
		assertEquals(false, registers.isB());
		assertEquals(false, registers.isV());
		assertEquals(false, registers.isN());

		Instruction.valueOf(0x28).exec(registers, ram);

		assertEquals(true, registers.isC());
		assertEquals(false, registers.isZ());
		assertEquals(false, registers.isI());
		assertEquals(true, registers.isD());
		assertEquals(true, registers.isB());
		assertEquals(true, registers.isV());
		assertEquals(false, registers.isN());

	}

	@Test
	@DisplayName("Push Accumulator - 48")
	void test48() {
		final int op = 0x048;
		final int cycles = 3;
		final Instruction instruction = Instruction.valueOf(op);

		registers.setA(0xCAFE);

		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.getOpCode());
		assertEquals(cycles, result);

		registers.setSP(0x01);
		assertEquals(0x01, registers.getSP());

		registers.setSP(0xFF);
		assertEquals(0xFF, registers.getSP());

		registers.setSP(0xFF + 1);
		assertEquals(0x00, registers.getSP());

		int stackValue = ram.read(offset + registers.getSP());

		assertEquals(registers.getA(), stackValue);

	}

}

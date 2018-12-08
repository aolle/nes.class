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

package com.olleb.nes.RP2A03.cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.olleb.nes.CPU6502.cpu.Instruction;
import com.olleb.nes.CPU6502.cpu.Registers;

@DisplayName("Intruction set tests")
@TestInstance(Lifecycle.PER_CLASS)
class InstructionsTest extends RAMTemplate {

	private Registers registers;

	@BeforeEach
	@Override
	public void reset() {
		super.reset();
		registers = new Registers();
	}

	@Test
	@DisplayName("Load Accumulator Zero Page - A5")
	void testA5() {
		final String op = "A5";
		final int addr = 0x300;
		final int valueAddr = 0x0A;
		final int value = 0xbb;
		final int cycles = 3;

		registers.setPc(addr - 1);
		ram.write(addr, valueAddr);
		ram.write(valueAddr, value);
		Instruction instruction = Instruction.valueOf(Instruction.class, op);
		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.toString());
		assertEquals(addr, registers.getPc());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

// TODO : test zero and negative flags

	}

	@Test
	@DisplayName("Load Accumulator Immediate - A9")
	void testA9() {
		final String op = "A9";
		final int addr = 0x300;
		final int value = 0x0A;
		final int cycles = 2;

		registers.setPc(addr - 1);
		ram.write(addr, value);
		Instruction instruction = Instruction.valueOf(Instruction.class, op);
		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.toString());
		assertEquals(addr, registers.getPc());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPc(addr - 1);
		ram.write(addr, 0);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPc(addr - 1);
		ram.write(addr, 0x80);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

}

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.olleb.nes.CPU6502.cpu.Instruction;

@DisplayName("LDA Instructions Test. No CPU.")
@TestInstance(Lifecycle.PER_CLASS)
class LDAInstructionsNoCPUTest extends InstructionsTestBase {

	// no CPU Address Adjustment
	private final int n = 1;

	@Test
	@DisplayName("Load Accumulator Absolute - AD")
	void testAD() {
		final String op = "_AD";
		final int addr = 0x0300;
		final int valueM = 0x0000;
		final int valueL = 0x0002;
		final int addrValue = 0x0200;
		final int value = 0xCAFE;
		final int cycles = 4;
		final Instruction instruction = Instruction.valueOf(Instruction.class, op);

		registers.setPc(addr);
		ram.write(addr + n, valueM);
		ram.write(addr + n + 1, valueL);
		ram.write(addrValue, value);
		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.toString());
		assertEquals(addr + instruction.getSize() - n, registers.getPc());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPc(addr);
		ram.write(addr + n, valueM);
		ram.write(addr + n + 1, valueL);
		ram.write(addrValue, 0);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPc(addr);
		ram.write(addr + n, valueM);
		ram.write(addr + n + 1, valueL);
		ram.write(addrValue, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

	@Test
	@DisplayName("Load Accumulator Indexed Zero Page X - B5")
	void testB5() {
		final String op = "_B5";
		final int addr = 0x0300;
		final int X = 0x0010;
		final int valueAddr = 0x000A;
		final int value = 0xCAFE;
		final int cycles = 4;
		final Instruction instruction = Instruction.valueOf(Instruction.class, op);

		registers.setPc(addr);
		registers.setX(X);
		ram.write(addr + n, valueAddr);
		ram.write(valueAddr + X, value);

		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.toString());
		assertEquals(addr + instruction.getSize() - n, registers.getPc());
		assertEquals(X, registers.getX());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPc(addr);
		ram.write(addr + n, valueAddr);
		ram.write(valueAddr + X, 0);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPc(addr);
		ram.write(addr + n, valueAddr);
		ram.write(valueAddr + X, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

	@Test
	@DisplayName("Load Accumulator Zero Page - A5")
	void testA5() {
		final String op = "_A5";
		final int addr = 0x0300;
		final int valueAddr = 0x000A;
		final int value = 0xCAFE;
		final int cycles = 3;
		final Instruction instruction = Instruction.valueOf(Instruction.class, op);

		registers.setPc(addr);
		ram.write(addr + n, valueAddr);
		ram.write(valueAddr, value);

		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.toString());
		assertEquals(addr + instruction.getSize() - n, registers.getPc());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPc(addr);
		ram.write(addr + n, valueAddr);
		ram.write(valueAddr, 0x0000);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPc(addr);
		ram.write(addr + n, valueAddr);
		ram.write(valueAddr, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

	@Test
	@DisplayName("Load Accumulator Immediate - A9")
	void testA9() {
		final String op = "_A9";
		final int addr = 0x0300;
		final int value = 0x000A;
		final int cycles = 2;
		final Instruction instruction = Instruction.valueOf(Instruction.class, op);

		registers.setPc(addr);
		ram.write(addr + n, value);

		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.toString());
		assertEquals(addr + instruction.getSize() - n, registers.getPc());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPc(addr);
		ram.write(addr + n, 0x0000);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPc(addr);
		ram.write(addr + n, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

}

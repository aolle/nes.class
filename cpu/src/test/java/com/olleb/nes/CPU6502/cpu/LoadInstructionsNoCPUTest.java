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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.olleb.nes.CPU6502.cpu.Instruction;

@DisplayName("Load Instructions Test. No CPU.")
@TestInstance(Lifecycle.PER_CLASS)
class LoadInstructionsNoCPUTest extends InstructionsTestBase {

	// no CPU Address Adjustment
	private final int n = 1;

	@Test
	@DisplayName("Load Accumulator Indirect Indexed - B1")
	void testB1() {
		final int op = 0xB1;
		final int ep = 0x0300;
		final int addrz = 0x000A;
		final int valueM = 0x0000;
		final int valueL = 0x0002;
		final int y = 0x0005;
		final int addrValue = 0x0200;
		final int value = 0xCAFE;
		final int cycles = 5;
		final Instruction instruction = Instruction.valueOf(op);

		registers.setPC(ep);
		registers.setY(y);
		ram.write(ep + n, addrz);
		ram.write(addrz, valueM);
		ram.write(addrz + 1, valueL);
		ram.write(addrValue + y, value);
		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.getOpCode());
		assertEquals(ep + instruction.getSize() - n, registers.getPC());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPC(ep);
		ram.write(ep + n, addrz);
		ram.write(addrz, valueM);
		ram.write(addrz + 1, valueL);
		ram.write(addrValue + y, 0);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPC(ep);
		ram.write(ep + n, addrz);
		ram.write(addrz, valueM);
		ram.write(addrz + 1, valueL);
		ram.write(addrValue + y, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

	@Test
	@DisplayName("Load Accumulator Indexed Indirect - A1")
	void testA1() {
		final int op = 0xA1;
		final int ep = 0x0300;
		final int addrz = 0x000A;
		final int valueM = 0x0000;
		final int valueL = 0x0002;
		final int x = 0x0005;
		final int addr = 0x0200;
		final int value = 0xCAFE;
		final int cycles = 6;
		final Instruction instruction = Instruction.valueOf(op);

		registers.setPC(ep);
		registers.setX(x);
		ram.write(ep + n, addrz);
		ram.write(addrz + x, valueM);
		ram.write(addrz + x + 1, valueL);
		ram.write(addr, value);
		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.getOpCode());
		assertEquals(ep + instruction.getSize() - n, registers.getPC());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPC(ep);
		ram.write(ep + n, addrz);
		ram.write(addrz + x, valueM);
		ram.write(addrz + x + 1, valueL);
		ram.write(addr, 0);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPC(ep);
		ram.write(ep + n, addrz);
		ram.write(addrz + x, valueM);
		ram.write(addrz + x + 1, valueL);
		ram.write(addr, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

	@Test
	@DisplayName("Load Accumulator Indexed Absolute X / Y - BD/B9")
	void testBDB9() {
		final int ep = 0x0300;
		final int valueM = 0x0000;
		final int valueL = 0x0002;
		final int addr = 0x0200;
		final int value = 0xCAFE;
		final int xy = 0x0010;
		final int xy_crossed = 0x0100;

		// LDA Absolute,X
		int op = 0xBD;
		Instruction instruction = Instruction.valueOf(op);
		int cycles = 4;

		// page not crossed
		registers.setPC(ep);
		registers.setX(xy);
		ram.write(ep + n, valueM);
		ram.write(ep + n + 1, valueL);
		ram.write(addr + xy, value);
		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.getOpCode());
		assertEquals(ep + instruction.getSize() - n, registers.getPC());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// page crossed
		reset();
		cycles = 5;
		registers.setPC(ep);
		registers.setX(xy_crossed);
		ram.write(ep + n, valueM);
		ram.write(ep + n + 1, valueL);
		ram.write(addr + xy_crossed, value);
		result = instruction.exec(registers, ram);

		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// LDA Absolute,Y
		reset();
		op = 0xB9;
		instruction = Instruction.valueOf(op);
		cycles = 4;

		// page not crossed
		registers.setPC(ep);
		registers.setY(xy);
		ram.write(ep + n, valueM);
		ram.write(ep + n + 1, valueL);
		ram.write(addr + xy, value);
		result = instruction.exec(registers, ram);

		assertEquals(op, instruction.getOpCode());
		assertEquals(ep + instruction.getSize() - n, registers.getPC());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// page crossed
		reset();
		cycles = 5;
		registers.setPC(ep);
		registers.setY(xy_crossed);
		ram.write(ep + n, valueM);
		ram.write(ep + n + 1, valueL);
		ram.write(addr + xy_crossed, value);
		result = instruction.exec(registers, ram);

		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPC(ep);
		ram.write(ep + n, valueM);
		ram.write(ep + n + 1, valueL);
		ram.write(addr + xy_crossed, 0);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPC(ep);
		ram.write(ep + n, valueM);
		ram.write(ep + n + 1, valueL);
		ram.write(addr + xy_crossed, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

	@Test
	@DisplayName("Load Accumulator Absolute - AD")
	void testAD() {
		final int op = 0xAD;
		final int ep = 0x0300;
		final int valueM = 0x0000;
		final int valueL = 0x0002;
		final int addr = 0x0200;
		final int value = 0xCAFE;
		final int cycles = 4;
		final Instruction instruction = Instruction.valueOf(op);

		registers.setPC(ep);
		ram.write(ep + n, valueM);
		ram.write(ep + n + 1, valueL);
		ram.write(addr, value);
		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.getOpCode());
		assertEquals(ep + instruction.getSize() - n, registers.getPC());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPC(ep);
		ram.write(ep + n, valueM);
		ram.write(ep + n + 1, valueL);
		ram.write(addr, 0);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPC(ep);
		ram.write(ep + n, valueM);
		ram.write(ep + n + 1, valueL);
		ram.write(addr, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

	@Test
	@DisplayName("Load Accumulator Indexed Zero Page X - B5")
	void testB5() {
		final int op = 0xB5;
		final int ep = 0x0300;
		final int X = 0x0010;
		final int addr = 0x000A;
		final int value = 0xCAFE;
		final int cycles = 4;
		final Instruction instruction = Instruction.valueOf(op);

		registers.setPC(ep);
		registers.setX(X);
		ram.write(ep + n, addr);
		ram.write(addr + X, value);

		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.getOpCode());
		assertEquals(ep + instruction.getSize() - n, registers.getPC());
		assertEquals(X, registers.getX());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPC(ep);
		ram.write(ep + n, addr);
		ram.write(addr + X, 0);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPC(ep);
		ram.write(ep + n, addr);
		ram.write(addr + X, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

	@Test
	@DisplayName("Load Accumulator Zero Page - A5")
	void testA5() {
		final int op = 0xA5;
		final int ep = 0x0300;
		final int addr = 0x000A;
		final int value = 0xCAFE;
		final int cycles = 3;
		final Instruction instruction = Instruction.valueOf(op);

		registers.setPC(ep);
		ram.write(ep + n, addr);
		ram.write(addr, value);

		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.getOpCode());
		assertEquals(ep + instruction.getSize() - n, registers.getPC());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPC(ep);
		ram.write(ep + n, addr);
		ram.write(addr, 0x0000);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPC(ep);
		ram.write(ep + n, addr);
		ram.write(addr, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

	@Test
	@DisplayName("Load Accumulator Immediate - A9")
	void testA9() {
		final int op = 0x0A9;
		final int ep = 0x0300;
		final int value = 0x000A;
		final int cycles = 2;
		final Instruction instruction = Instruction.valueOf(op);

		registers.setPC(ep);
		ram.write(ep + n, value);

		int result = instruction.exec(registers, ram);

		assertEquals(op, instruction.getOpCode());
		assertEquals(ep + instruction.getSize() - n, registers.getPC());
		assertEquals(cycles, result);
		assertEquals(value, registers.getA());

		// zero flag
		registers.setPC(ep);
		ram.write(ep + n, 0x0000);
		instruction.exec(registers, ram);
		assertEquals(0, registers.getA());
		assertTrue(registers.isZ());

		// negative flag
		registers.setPC(ep);
		ram.write(ep + n, 0x0080);
		instruction.exec(registers, ram);
		assertTrue(registers.isN());

	}

}

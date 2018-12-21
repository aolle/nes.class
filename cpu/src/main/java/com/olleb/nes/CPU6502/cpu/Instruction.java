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

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.IntPredicate;
import java.util.function.ToIntBiFunction;

import com.olleb.nes.CPU6502.mem.Memory;

public enum Instruction implements InstructionStrategy<Memory> {
	// Load
	_A9(0xA9, "LDA #nn", 2, (var r, var m) -> {
		load(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m), Registers::setA);
		return 2;
	}),

	_A5(0xA5, "LDA nn", 2, (var r, var m) -> {
		load(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m), Registers::setA);
		return 3;
	}),

	_B5(0xB5, "LDA nn,X", 2, (var r, var m) -> {
		load(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m), Registers::setA);
		return 4;
	}),

	_AD(0xAD, "LDA nnnn", 3, (var r, var m) -> {
		load(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m), Registers::setA);
		return 4;
	}),

	_BD(0xBD, "LDA nnnn,X", 3, (var r, var m) -> {
		load(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m), Registers::setA);
		return r.isPg() ? 5 : 4;
	}),

	_B9(0xB9, "LDA nnnn,Y", 3, (var r, var m) -> {
		load(r, m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m), Registers::setA);
		return r.isPg() ? 5 : 4;
	}),

	_A1(0xA1, "LDA (nn,X)", 2, (var r, var m) -> {
		load(r, m, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m), Registers::setA);
		return 6;
	}),

	_B1(0xB1, "LDA (nn),Y", 2, (var r, var m) -> {
		load(r, m, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m), Registers::setA);
		return r.isPg() ? 6 : 5;
	}),

	_A2(0xA2, "LDX #nn", 2, (var r, var m) -> {
		load(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m), Registers::setX);
		return 2;
	}),

	_A6(0xA6, "LDX nn", 2, (var r, var m) -> {
		load(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m), Registers::setX);
		return 3;
	}),

	_B6(0xB6, "LDX nn,Y", 2, (var r, var m) -> {
		load(r, m, AddressingMode.INDEXED_ZERO_PAGE_Y.applyAsInt(r, m), Registers::setX);
		return 4;
	}),

	_AE(0xAE, "LDX nnnn", 3, (var r, var m) -> {
		load(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m), Registers::setX);
		return 4;
	}),

	_BE(0xBE, "LDX nnnn,Y", 3, (var r, var m) -> {
		load(r, m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m), Registers::setX);
		return r.isPg() ? 5 : 4;
	}),
	
	_A0(0xA0,"LDY #nn",2,(var r, var m) -> {
		load(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m), Registers::setY);
		return 2;
	}),
	
	_A4(0xA4, "LDY nn", 2, (var r, var m) -> {
		load(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m), Registers::setY);
		return 3;
	}),
	
	_B4(0xB4, "LDY nn,X", 2, (var r, var m) -> {
		load(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m), Registers::setY);
		return 4;
	}),

	_AC(0xAC, "LDY nnnn", 3, (var r, var m) -> {
		load(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m), Registers::setY);
		return 4;
	}),
	
	_BC(0xBC, "LDY nnnn,X", 3, (var r, var m) -> {
		load(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m), Registers::setY);
		return r.isPg() ? 5 : 4;
	})
	
	;

	private static final Instruction[] instructions = new Instruction[256];

	static {
		Arrays.stream(Instruction.values())
				.forEach(i -> instructions[Integer.parseInt(i.toString().replace("_", ""), 16)] = i);
	}

	private final int opCode;
	private final String assemblerFormat;
	private final int size;
	private final InstructionStrategy<Memory> instructionStrategy;

	private Instruction(final int opCode, final String assemblerFormat, final int size,
			final InstructionStrategy<Memory> instructionStrategy) {
		this.opCode = opCode;
		this.size = size;
		this.assemblerFormat = assemblerFormat;
		this.instructionStrategy = instructionStrategy;
	}

	@Override
	public int exec(final Registers r, final Memory m) {
		return instructionStrategy.exec(r, m);
	}

	public String getAssemblerFormat() {
		return assemblerFormat;
	}

	public int getSize() {
		return size;
	}

	public int getOpCode() {
		return opCode;
	}

	public static Instruction valueOf(int opcode) {
		return instructions[opcode];
	}

	private static void load(final Registers registers, final Memory memory, final int address,
			BiConsumer<Registers, Integer> biConsumer) {
		final int result = memory.read(address);
		biConsumer.accept(registers, result);
		Flags.setFlags(registers, result);
	}

	private static class Flags {
		private static final IntPredicate ZERO = x -> (x == 0);
		// MSB 2^7 = 0x0080
		private static final IntPredicate NEGATIVE = x -> ((x & 0x0080) != 0);

		public static final void setFlags(final Registers registers, final int value) {
			registers.setZ(Flags.ZERO.test(value));
			registers.setN(Flags.NEGATIVE.test(value));
		}
	}

	private enum AddressingMode implements ToIntBiFunction<Registers, Memory> {
		// TODO: use RAM.Address to solve mem addresses like indexed zero page?
		IMMEDIATE((r, m) -> r.inc()),

		ZERO_PAGE((r, m) -> m.read(r.inc())),

		// wraparound zero page => the data addr always in zero page 0x000 - 0x00FF
		INDEXED_ZERO_PAGE_X((r, m) -> m.read(r.inc()) + r.getX() & 0x00FF),

		INDEXED_ZERO_PAGE_Y((r, m) -> m.read(r.inc()) + r.getY() & 0x00FF),

		// int 4 bytes (32 bits). Abs uses 16 bit address (2 x 8 bit).
		// LSB -> shift 2nd (least) value 8 bits to the left and add 1st.
		ABSOLUTE((r, m) -> m.read(r.inc()) + (m.read(r.inc()) << 8)),

		INDEXED_ABSOLUTE_X((r, m) -> {
			final int i = m.read(r.inc()) + (m.read(r.inc()) << 8) + r.getX();
			r.setPg(AddressingMode.PAGE_CROSSED.test(i, i + r.getX()));
			return i;
		}),

		INDEXED_ABSOLUTE_Y((r, m) -> {
			final int i = m.read(r.inc()) + (m.read(r.inc()) << 8) + r.getY();
			r.setPg(AddressingMode.PAGE_CROSSED.test(i, i + r.getY()));
			return i;
		}),

		// wraparound zero page
		INDEXED_INDIRECT((r, m) -> {
			final int i = m.read(r.inc()) + r.getX();
			return m.read(i & 0x00FF) + (m.read(i + 1 & 0x00FF) << 8);
		}),

		// wraparound zero page
		INDIRECT_INDEXED((r, m) -> {
			int i = m.read(r.inc());
			i = (m.read(i & 0x00FF) + (m.read(i + 1 & 0x00FF) << 8)) + r.getY();
			r.setPg(AddressingMode.PAGE_CROSSED.test(i, i + r.getY()));
			return i;
		});

		private final ToIntBiFunction<Registers, Memory> toIntBiFunction;

		private AddressingMode(final ToIntBiFunction<Registers, Memory> toIntBiFunction) {
			this.toIntBiFunction = toIntBiFunction;
		}

		// same page => high-byte of addresses have the same value
		// example: 0xFE00 - 0xFEFF, different page: 0xFE00 - 0xFF00
		// TODO: JMH >> vs &. (addr1 & 0xFF00) != (addr2 & 0xFF00);
		private static final BiPredicate<Integer, Integer> PAGE_CROSSED = (x, y) -> (x >> 8 != y >> 8);

		@Override
		public int applyAsInt(Registers r, Memory m) {
			return this.toIntBiFunction.applyAsInt(r, m);
		}
	}
}

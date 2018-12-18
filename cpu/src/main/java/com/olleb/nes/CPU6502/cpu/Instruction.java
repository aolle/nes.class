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

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.ToIntBiFunction;

import com.olleb.nes.CPU6502.mem.Memory;

@SuppressWarnings("unused")
public enum Instruction implements InstructionStrategy<Memory> {
	/**
	 * $ -> hex, ! -> dec, % -> binary # -> imm lower byte, / -> imm upper byte %1
	 *
	 * format: opcode("name", bytes, registers) => cycles
	 */

	/**
	 * Load/Store
	 */
	_A9("LDA #nn", 2, (var r, var m) -> {
		loadAccumulator(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m));
		return 2;
	}),

	_A5("LDA nn", 2, (var r, var m) -> {
		loadAccumulator(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 3;
	}),

	_B5("LDA nn,X", 2, (var r, var m) -> {
		loadAccumulator(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 4;
	}),

	_AD("LDA nnnn", 3, (var r, var m) -> {
		loadAccumulator(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 4;
	}),

	_BD("LDA nnnn,X", 3, (var r, var m) -> {
		loadAccumulator(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_B9("LDA nnnn,Y", 3, (var r, var m) -> {
		loadAccumulator(r, m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_A1("LDA (nn,X)", 2, (var r, var m) -> {
		loadAccumulator(r, m, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m));
		return 6;
	}),

	_B1("LDA (nn),Y", 2, (var r, var m) -> {
		loadAccumulator(r, m, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m));
		return r.isPg() ? 6 : 5;
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

	public String getOpCode() {
		return opCode;
	}

	public int getSize() {
		return size;
	}

	private static void loadAccumulator(final Registers registers, final Memory memory, final int address) {
		final int result = memory.read(address);
		registers.setA(result);
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
		private static final BiPredicate<Integer, Integer> PAGE_CROSSED = (x, y) -> (x >> 8 != y >> 8);

		@Override
		public int applyAsInt(Registers r, Memory m) {
			return this.toIntBiFunction.applyAsInt(r, m);
		}
	}
}

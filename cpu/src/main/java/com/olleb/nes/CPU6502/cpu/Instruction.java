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
//		loadAccumulator(r, AddressingModes.IMMEDIATE.applyAsInt(r, m));
		loadAccumulator(r, AddressingMode.IMMEDIATE.applyAsInt(r, m));
		return 2;
	}),

	_A5("LDA nn", 2, (var r, var m) -> {
		loadAccumulator(r, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 3;
	}),

	_B5("LDA nn,X", 2, (var r, var m) -> {
		loadAccumulator(r, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 4;
	}),

	_AD("LDA nnnn", 3, (var r, var m) -> {
		loadAccumulator(r, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 4;
	}),

	_BD("LDA nnnn,X", 3, (var r, var m) -> {
		loadAccumulator(r, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		// TODO: optimize this.
		final int addr = m.read(r.getPc() - 2) + (m.read(r.getPc() - 1) << 8);
		return AddressingMode.PAGE_CROSSED.test(addr, addr + r.getX()) ? 5 : 4;
	}),

	_B9("LDA nnnn,Y", 3, (var r, var m) -> {
		loadAccumulator(r, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m));
		// TODO: optimize this.
		final int addr = m.read(r.getPc() - 2) + (m.read(r.getPc() - 1) << 8);
		return AddressingMode.PAGE_CROSSED.test(addr, addr + r.getY()) ? 5 : 4;
	}),

	_A1("LDA (nn,X)", 2, (var r, var m) -> {
		loadAccumulator(r, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m));
		return 6;
	}),

	_B1("LDA (nn),Y", 2, (var r, var m) -> {
		// TODO
		loadAccumulator(r, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m));
		return -1;
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

	private static void loadAccumulator(final Registers registers, final int result) {
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

		IMMEDIATE((r, m) -> m.read(r.inc())),

		ZERO_PAGE((r, m) -> m.read(m.read(r.inc()))),

		// wraparound => the data addr always in zero page 0x000 - 0x00FF
		INDEXED_ZERO_PAGE_X((r, m) -> m.read(m.read(r.inc()) + r.getX() & 0x00FF)),

//		// TODO: join lambdas
//		private static final ToIntBiFunction<Registers, Memory> INDEXED_ZERO_PAGE_X = (r, m) -> _INDEXED_ZERO_PAGE_PARAM
//				.apply(r, m).apply(r.getX());
//
		// int 4 bytes (32 bits). Abs uses 16 bit address (2 x 8 bit).
		// LSB -> shift 2nd (least) value 8 bits to the left and add 1st.
		ABSOLUTE((r, m) -> m.read(m.read(r.inc()) + (m.read(r.inc()) << 8))),

		// TODO: optimize this. Very similar with ABSOLUTE
		INDEXED_ABSOLUTE_X((r, m) -> m.read(m.read(r.inc()) + (m.read(r.inc()) << 8) + r.getX())),

		INDEXED_ABSOLUTE_Y((r, m) -> m.read(m.read(r.inc()) + (m.read(r.inc()) << 8) + r.getY())),

		// wraparound
		INDEXED_INDIRECT((r, m) -> {
			final int i = m.read(r.inc()) + r.getX();
			return m.read(m.read(i & 0x00FF) + (m.read(i + 1 & 0x00FF) << 8));
		}),

		// wraparound
		INDIRECT_INDEXED((r, m) -> {
			// TODO
			return -1;
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

	/**private static class AddressingModes {
		// same page => high-byte of addresses have the same value
		// example: 0xFE00 - 0xFEFF, different page: 0xFE00 - 0xFF00
		private static final BiPredicate<Integer, Integer> PAGE_CROSSED = (x, y) -> (x >> 8 != y >> 8);

		// TODO: use RAM.Address to solve mem addresses like indexed zero page?

		private static final ToIntBiFunction<Registers, Memory> IMMEDIATE = (r, m) -> m.read(r.inc());

		private static final ToIntBiFunction<Registers, Memory> ZERO_PAGE = (r, m) -> m.read(m.read(r.inc()));

		// wraparound => the data addr always in zero page 0x000 - 0x00FF
		private static final BiFunction<Registers, Memory, IntFunction<Integer>> _INDEXED_ZERO_PAGE_PARAM = (r,
				m) -> x -> m.read(m.read(r.inc()) + x & 0x00FF);

		// TODO: join lambdas
		private static final ToIntBiFunction<Registers, Memory> INDEXED_ZERO_PAGE_X = (r, m) -> _INDEXED_ZERO_PAGE_PARAM
				.apply(r, m).apply(r.getX());

		// int 4 bytes (32 bits). Abs uses 16 bit address (2 x 8 bit).
		// LSB -> shift 2nd (least) value 8 bits to the left and add 1st.
		private static final ToIntBiFunction<Registers, Memory> ABSOLUTE = (r, m) -> m
				.read(m.read(r.inc()) + (m.read(r.inc()) << 8));

		// TODO: optimize this. Very similar with ABSOLUTE
		private static final BiFunction<Registers, Memory, IntFunction<Integer>> _INDEXED_ABSOLUTE_PARAM = (r,
				m) -> x -> m.read(m.read(r.inc()) + (m.read(r.inc()) << 8) + x);

		// TODO: join lambdas
		private static final ToIntBiFunction<Registers, Memory> INDEXED_ABSOLUTE_X = (r, m) -> _INDEXED_ABSOLUTE_PARAM
				.apply(r, m).apply(r.getX());

		private static final ToIntBiFunction<Registers, Memory> INDEXED_ABSOLUTE_Y = (r, m) -> _INDEXED_ABSOLUTE_PARAM
				.apply(r, m).apply(r.getY());

		// wraparound
		private static final ToIntBiFunction<Registers, Memory> INDEXED_INDIRECT = (r, m) -> {
			final int i = m.read(r.inc()) + r.getX();
			return m.read(m.read(i & 0x00FF) + (m.read(i + 1 & 0x00FF) << 8));
		};

		// wraparound
		private static final ToIntBiFunction<Registers, Memory> INDIRECT_INDEXED = (r, m) -> {
			// TODO
			return -1;
		};

	}**/

}

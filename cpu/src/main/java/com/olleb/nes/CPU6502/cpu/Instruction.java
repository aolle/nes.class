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
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.ToIntBiFunction;

import com.olleb.nes.CPU6502.mem.Memory;
import com.olleb.nes.CPU6502.mem.RAM.Address;

import static com.olleb.nes.CPU6502.mem.RAM.Address.*;

public enum Instruction implements InstructionStrategy<Memory> {
	// load
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

	_A0(0xA0, "LDY #nn", 2, (var r, var m) -> {
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
	}),

	// store
	_85(0x85, "STA nn", 2, (var r, var m) -> {
		store(m, AddressingMode.ZERO_PAGE.applyAsInt(r, m), r.getA());
		return 3;
	}),

	_95(0x95, "STA nn,X", 2, (var r, var m) -> {
		store(m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m), r.getA());
		return 4;
	}),

	_8D(0x8D, "STA nnnn", 3, (var r, var m) -> {
		store(m, AddressingMode.ABSOLUTE.applyAsInt(r, m), r.getA());
		return 4;
	}),

	_9D(0x9D, "STA nnnn,X", 3, (var r, var m) -> {
		store(m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m), r.getA());
		return 5;
	}),

	_99(0x99, "STA nnnn,Y", 3, (var r, var m) -> {
		store(m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m), r.getA());
		return 5;
	}),

	_81(0x81, "STA (nn,X)", 2, (var r, var m) -> {
		store(m, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m), r.getA());
		return 6;
	}),

	_91(0x91, "STA (nn),Y", 2, (var r, var m) -> {
		store(m, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m), r.getA());
		return 6;
	}),

	_86(0x86, "STX nn", 2, (var r, var m) -> {
		store(m, AddressingMode.ZERO_PAGE.applyAsInt(r, m), r.getX());
		return 3;
	}),

	_96(0x96, "STX nn,Y", 2, (var r, var m) -> {
		store(m, AddressingMode.INDEXED_ZERO_PAGE_Y.applyAsInt(r, m), r.getX());
		return 4;
	}),

	_8E(0x8E, "STX nnnn", 3, (var r, var m) -> {
		store(m, AddressingMode.ABSOLUTE.applyAsInt(r, m), r.getX());
		return 4;
	}),

	_84(0x84, "STY nn", 2, (var r, var m) -> {
		store(m, AddressingMode.ZERO_PAGE.applyAsInt(r, m), r.getY());
		return 3;
	}),

	_94(0x94, "STY nn,X", 2, (var r, var m) -> {
		store(m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m), r.getY());
		return 4;
	}),

	_8C(0x8C, "STY nnnn", 3, (var r, var m) -> {
		store(m, AddressingMode.ABSOLUTE.applyAsInt(r, m), r.getY());
		return 4;
	}),

	// register transfers
	_AA(0xAA, "TAX", 1, (var r, var m) -> {
		transfer(r, r.getA(), Registers::setX);
		return 2;
	}),

	_A8(0xA8, "TAY", 1, (var r, var m) -> {
		transfer(r, r.getA(), Registers::setY);
		return 2;
	}),

	_8A(0x8A, "TXA", 1, (var r, var m) -> {
		transfer(r, r.getX(), Registers::setA);
		return 2;
	}),

	_98(0x98, "TYA", 1, (var r, var m) -> {
		transfer(r, r.getY(), Registers::setA);
		return 2;
	}),

	// stack
	_BA(0xBA, "TSX", 1, (var r, var m) -> {
		stackPointerTransfer(r, r.getSP(), Registers::setX);
		return 2;
	}),

	_9A(0x9A, "TXS", 1, (var r, var m) -> {
		stackPointerTransfer(r, r.getX(), Registers::setSP);
		return 2;
	}),

	_48(0x48, "PHA", 1, (var r, var m) -> {
		stackPush(r, m, r.getA());
		return 3;
	}),

	_08(0x08, "PHP", 1, (var r, var m) -> {
		stackPush(r, m, r.getProcessorStatus());
		return 3;
	}),

	_68(0x68, "PLA", 1, (var r, var m) -> {
		r.setA(stackPull(r, m));
		return 4;
	}),

	_28(0x28, "PLP", 1, (var r, var m) -> {
		r.setProcessorStatus(stackPull(r, m));
		return 4;
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

	public static Instruction valueOf(final int opcode) {
		return instructions[opcode];
	}

	// TODO review args order
	private static void load(final Registers registers, final Memory memory, final int address,
			BiConsumer<Registers, Integer> destination) {
		final int result = memory.read(address);
		destination.accept(registers, result);
		Flags.setFlags(registers, result);
	}

	private static void store(final Memory memory, final int address, final int value) {
		memory.write(address, value);
	}

	private static void transfer(final Registers registers, final int value,
			final BiConsumer<Registers, Integer> destination) {
		destination.accept(registers, value);
		Flags.setFlags(registers, value);
	}

	// TODO refactor
	private static void stackPointerTransfer(final Registers registers, final int value,
			final BiConsumer<Registers, Integer> destination) {
		destination.accept(registers, value);
		Flags.setFlags(registers, value);
	}

	private static void stackPush(final Registers registers, final Memory memory, final int value) {
		memory.write(Address.STACK_BEGIN.getAddress() + registers.getSP(), value);
		registers.decrementSP();
	}

	private static int stackPull(final Registers registers, final Memory memory) {
		final int value = memory.read(Address.STACK_BEGIN.getAddress() + registers.getSP());
		registers.incrementPC();
		return value;
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

	// TODO: DECOUPLE
	private enum AddressingMode implements ToIntBiFunction<Registers, Memory> {
		// TODO: use RAM.Address to solve mem addresses like indexed zero page?
		IMMEDIATE((r, m) -> r.incrementPC()),

		ZERO_PAGE((r, m) -> m.read(r.incrementPC())),

		// wraparound zero page => the data addr always in zero page 0x000 - 0x00FF
		INDEXED_ZERO_PAGE_X((r, m) -> m.read(r.incrementPC()) + AddressingMode.WRAP_AROUND_ZERO_PAGE.apply(r.getX())),

		INDEXED_ZERO_PAGE_Y((r, m) -> m.read(r.incrementPC()) + AddressingMode.WRAP_AROUND_ZERO_PAGE.apply(r.getY())),

		// int 4 bytes (32 bits). Abs uses 16 bit address (2 x 8 bit).
		// LSB -> shift 2nd (least) value 8 bits to the left and add 1st.
		ABSOLUTE((r, m) -> m.read(r.incrementPC()) + (m.read(r.incrementPC()) << 8)),

		INDEXED_ABSOLUTE_X((r, m) -> {
			final int i = m.read(r.incrementPC()) + (m.read(r.incrementPC()) << 8) + r.getX();
			r.setPg(AddressingMode.PAGE_CROSSED.test(i, i + r.getX()));
			return i;
		}),

		INDEXED_ABSOLUTE_Y((r, m) -> {
			final int i = m.read(r.incrementPC()) + (m.read(r.incrementPC()) << 8) + r.getY();
			r.setPg(AddressingMode.PAGE_CROSSED.test(i, i + r.getY()));
			return i;
		}),

		// wraparound zero page
		INDEXED_INDIRECT((r, m) -> {
			final int i = m.read(r.incrementPC()) + r.getX();
			return m.read(i & ZERO_PAGE_END.getAddress())
					+ (m.read(AddressingMode.WRAP_AROUND_ZERO_PAGE.apply(i + 1)) << 8);
		}),

		// wraparound zero page
		INDIRECT_INDEXED((r, m) -> {
			int i = m.read(r.incrementPC());
			i = (m.read(i & 0x00FF) + (m.read(AddressingMode.WRAP_AROUND_ZERO_PAGE.apply(i + 1)) << 8)) + r.getY();
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

		private static final IntFunction<Integer> WRAP_AROUND_ZERO_PAGE = x -> x & ZERO_PAGE_END.getAddress();

		@Override
		public int applyAsInt(Registers r, Memory m) {
			return this.toIntBiFunction.applyAsInt(r, m);
		}
	}
}

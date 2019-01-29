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
	}),

	// logical
	_29(0x29, "AND #nn", 2, (var r, var m) -> {
		and(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m));
		return 2;
	}),

	_25(0x25, "AND nn", 2, (var r, var m) -> {
		and(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 3;
	}),

	_35(0x35, "AND nn,X", 2, (var r, var m) -> {
		and(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 4;
	}),

	_2D(0x2D, "AND nnnn", 3, (var r, var m) -> {
		and(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 4;
	}),

	_3D(0x3D, "AND nnnn,X", 3, (var r, var m) -> {
		and(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_39(0x39, "AND nnnn,Y", 3, (var r, var m) -> {
		and(r, m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_21(0x21, "AND (nn,X)", 2, (var r, var m) -> {
		and(r, m, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m));
		return 6;
	}),

	_31(0x31, "AND (nn),Y", 2, (var r, var m) -> {
		and(r, m, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m));
		return r.isPg() ? 6 : 5;
	}),

	_49(0x49, "EOR #nn", 2, (var r, var m) -> {
		xor(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m));
		return 2;
	}),

	_45(0x45, "EOR nn", 2, (var r, var m) -> {
		xor(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 3;
	}),

	_55(0x55, "EOR nn,X", 2, (var r, var m) -> {
		xor(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 4;
	}),

	_4D(0x4D, "EOR nnnn", 3, (var r, var m) -> {
		xor(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 4;
	}),

	_5D(0x5D, "EOR nnnn,X", 3, (var r, var m) -> {
		xor(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_59(0x59, "EOR nnnn,Y", 3, (var r, var m) -> {
		xor(r, m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_41(0x41, "EOR (nn,X)", 2, (var r, var m) -> {
		xor(r, m, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m));
		return 6;
	}),

	_51(0x51, "EOR (nn),Y", 2, (var r, var m) -> {
		xor(r, m, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m));
		return r.isPg() ? 6 : 5;
	}),

	_09(0x09, "ORA #nn", 2, (var r, var m) -> {
		or(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m));
		return 2;
	}),

	_05(0x05, "ORA nn", 2, (var r, var m) -> {
		or(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 3;
	}),

	_15(0x15, "ORA nn,X", 2, (var r, var m) -> {
		or(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 4;
	}),

	_0D(0x0D, "ORA nnnn", 3, (var r, var m) -> {
		or(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 4;
	}),

	_1D(0x1D, "ORA nnnn,X", 3, (var r, var m) -> {
		or(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_19(0x19, "ORA nnnn,Y", 3, (var r, var m) -> {
		or(r, m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_01(0x01, "ORA (nn,X)", 2, (var r, var m) -> {
		or(r, m, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m));
		return 6;
	}),

	_11(0x11, "ORA (nn),Y", 2, (var r, var m) -> {
		or(r, m, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m));
		return r.isPg() ? 6 : 5;
	}),

	_24(0x24, "BIT nn", 2, (var r, var m) -> {
		test(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 3;
	}),

	_2C(0x2C, "BIT nnnn", 3, (var r, var m) -> {
		test(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 4;
	}),

	// arithmetic
	_69(0x69, "ADC #nn", 2, (var r, var m) -> {
		add(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m));
		return 2;
	}),

	_65(0x65, "ADC nn", 2, (var r, var m) -> {
		add(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 3;
	}),

	_75(0x75, "ADC nn,X", 2, (var r, var m) -> {
		add(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 4;
	}),

	_6D(0x6D, "ADC nnnn", 3, (var r, var m) -> {
		add(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 4;
	}),

	_7D(0x7D, "ADC nnnn,X", 3, (var r, var m) -> {
		add(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_79(0x79, "ADC nnnn,Y", 3, (var r, var m) -> {
		add(r, m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_61(0x61, "ADC (nn,X)", 2, (var r, var m) -> {
		add(r, m, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m));
		return 6;
	}),

	_71(0x71, "ADC (nn),Y", 2, (var r, var m) -> {
		add(r, m, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m));
		return r.isPg() ? 6 : 5;
	}),

	_E9(0xE9, "SBC #nn", 2, (var r, var m) -> {
		sub(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m));
		return 2;
	}),

	_E5(0xE5, "SBC nn", 2, (var r, var m) -> {
		sub(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 3;
	}),

	_F5(0xF5, "SBC nn,X", 2, (var r, var m) -> {
		sub(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 4;
	}),

	_ED(0xED, "SBC nnnn", 3, (var r, var m) -> {
		sub(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 4;
	}),

	_FD(0xFD, "SBC nnnn,X", 3, (var r, var m) -> {
		sub(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_F9(0xF9, "SBC nnnn,Y", 3, (var r, var m) -> {
		sub(r, m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m));
		return r.isPg() ? 5 : 4;
	}),

	_E1(0xE1, "SBC (nn,X)", 2, (var r, var m) -> {
		sub(r, m, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m));
		return 6;
	}),

	_F1(0xF1, "SBC (nn),Y", 2, (var r, var m) -> {
		sub(r, m, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m));
		return r.isPg() ? 6 : 5;
	}),

	_C9(0xC9, "CMP #nn", 2, (var r, var m) -> {
		cmp(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m), r.getA());
		return 2;
	}),

	_C5(0xC5, "CMP nn", 2, (var r, var m) -> {
		cmp(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m), r.getA());
		return 3;
	}),

	_D5(0xD5, "CMP nn,X", 2, (var r, var m) -> {
		cmp(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m), r.getA());
		return 4;
	}),

	_CD(0xCD, "CMP nnnn", 3, (var r, var m) -> {
		cmp(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m), r.getA());
		return 4;
	}),

	_DD(0xDD, "CMP nnnn,X", 3, (var r, var m) -> {
		cmp(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m), r.getA());
		return r.isPg() ? 5 : 4;
	}),

	_D9(0xD9, "CMP nnnn,Y", 3, (var r, var m) -> {
		cmp(r, m, AddressingMode.INDEXED_ABSOLUTE_Y.applyAsInt(r, m), r.getA());
		return r.isPg() ? 5 : 4;
	}),

	_C1(0xC1, "CMP (nn,X)", 2, (var r, var m) -> {
		cmp(r, m, AddressingMode.INDEXED_INDIRECT.applyAsInt(r, m), r.getA());
		return 6;
	}),

	_D1(0xD1, "CMP (nn),Y", 2, (var r, var m) -> {
		cmp(r, m, AddressingMode.INDIRECT_INDEXED.applyAsInt(r, m), r.getA());
		return r.isPg() ? 6 : 5;
	}),

	_E0(0xE0, "CPX #nn", 2, (var r, var m) -> {
		cmp(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m), r.getX());
		return 2;
	}),

	_E4(0xE4, "CPX nn", 2, (var r, var m) -> {
		cmp(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m), r.getX());
		return 3;
	}),

	_EC(0xEC, "CPX nnnn", 3, (var r, var m) -> {
		cmp(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m), r.getX());
		return 4;
	}),

	_C0(0xC0, "CPY #nn", 2, (var r, var m) -> {
		cmp(r, m, AddressingMode.IMMEDIATE.applyAsInt(r, m), r.getY());
		return 2;
	}),

	_C4(0xC4, "CPY nn", 2, (var r, var m) -> {
		cmp(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m), r.getY());
		return 3;
	}),

	_CC(0xCC, "CPY nnnn", 3, (var r, var m) -> {
		cmp(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m), r.getY());
		return 4;
	}),

	// increments & decrements
	_E6(0xE6, "INC nn", 2, (var r, var m) -> {
		inc(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 5;
	}),

	_F6(0xF6, "INC nn,X", 2, (var r, var m) -> {
		inc(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 6;
	}),

	_EE(0xEE, "INC nnnn", 3, (var r, var m) -> {
		inc(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 6;
	}),

	_FE(0xFE, "INC nnnn,X", 3, (var r, var m) -> {
		inc(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return 7;
	}),

	_E8(0xE8, "INX", 1, (var r, var m) -> {
		inc(r, Registers::setX, r.getX());
		return 2;
	}),

	_C8(0xC8, "INY", 1, (var r, var m) -> {
		inc(r, Registers::setY, r.getY());
		return 2;
	}),

	_C6(0xC6, "DEC nn", 2, (var r, var m) -> {
		dec(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 5;
	}),

	_D6(0xD6, "DEC nn,X", 2, (var r, var m) -> {
		dec(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 6;
	}),

	_CE(0xCE, "DEC nnnn", 3, (var r, var m) -> {
		dec(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 6;
	}),

	_DE(0xDE, "DEC nnnn,X", 3, (var r, var m) -> {
		dec(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return 7;
	}),

	_CA(0xCA, "DEX", 1, (var r, var m) -> {
		dec(r, Registers::setX, r.getX());
		return 2;
	}),

	_88(0x88, "DEY", 1, (var r, var m) -> {
		dec(r, Registers::setY, r.getY());
		return 2;
	}),

	// shifts
	_0A(0x0A, "ASL A", 1, (var r, var m) -> {
		asl(r);
		return 2;
	}),

	_06(0x06, "ASL nn", 2, (var r, var m) -> {
		asl(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 5;
	}),

	_16(0x16, "ASL nn,X", 2, (var r, var m) -> {
		asl(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 6;
	}),

	_0E(0x0E, "ASL nnnn", 3, (var r, var m) -> {
		asl(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 6;
	}),

	_1E(0x1E, "ASL nnnn,X", 3, (var r, var m) -> {
		asl(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return 7;
	}),

	_4A(0x4A, "LSR A", 1, (var r, var m) -> {
		lsr(r);
		return 2;
	}),

	_46(0x46, "LSR nn", 2, (var r, var m) -> {
		lsr(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 5;
	}),

	_56(0x56, "LSR nn,X", 2, (var r, var m) -> {
		lsr(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 6;
	}),

	_4E(0x4E, "LSR nnnn", 3, (var r, var m) -> {
		lsr(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 6;
	}),

	_5E(0x5E, "LSR nnnn,X", 3, (var r, var m) -> {
		lsr(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return 7;
	}),

	_6A(0x6A, "ROR A", 1, (var r, var m) -> {
		ror(r);
		return 2;
	}),

	_66(0x66, "ROR nn", 2, (var r, var m) -> {
		ror(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 5;
	}),

	_76(0x76, "ROR nn,X", 2, (var r, var m) -> {
		ror(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 6;
	}),

	_6E(0x6E, "ROR nnnn", 3, (var r, var m) -> {
		ror(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 6;
	}),

	_7E(0x7E, "ROR nnnn,X", 3, (var r, var m) -> {
		ror(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return 7;
	}),

	_2A(0x2A, "ROL A", 1, (var r, var m) -> {
		rol(r);
		return 2;
	}),

	_26(0x26, "ROL nn", 2, (var r, var m) -> {
		rol(r, m, AddressingMode.ZERO_PAGE.applyAsInt(r, m));
		return 5;
	}),

	_36(0x36, "ROL nn,X", 2, (var r, var m) -> {
		ror(r, m, AddressingMode.INDEXED_ZERO_PAGE_X.applyAsInt(r, m));
		return 6;
	}),

	_2E(0x2E, "ROL nnnn", 3, (var r, var m) -> {
		rol(r, m, AddressingMode.ABSOLUTE.applyAsInt(r, m));
		return 6;
	}),

	_3E(0x3E, "ROL nnnn,X", 3, (var r, var m) -> {
		rol(r, m, AddressingMode.INDEXED_ABSOLUTE_X.applyAsInt(r, m));
		return 7;
	}),

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
//		Flags.setFlags(registers, result, 'n', 'z');
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void store(final Memory memory, final int address, final int value) {
		memory.write(address, value);
	}

	private static void transfer(final Registers registers, final int value,
			final BiConsumer<Registers, Integer> destination) {
		destination.accept(registers, value);
//		Flags.setFlags(registers, value, 'n', 'z');
		registers.setN(Flags.NEGATIVE.test(value));
		registers.setZ(Flags.ZERO.test(value));
	}

	// TODO refactor
	private static void stackPointerTransfer(final Registers registers, final int value,
			final BiConsumer<Registers, Integer> destination) {
		destination.accept(registers, value);
		registers.setN(Flags.NEGATIVE.test(value));
		registers.setZ(Flags.ZERO.test(value));
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

	private static void and(final Registers registers, final Memory memory, final int address) {
		final int result = registers.getA() & memory.read(address);
		registers.setA(result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void xor(final Registers registers, final Memory memory, final int address) {
		final int result = registers.getA() ^ memory.read(address);
		registers.setA(result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void or(final Registers registers, final Memory memory, final int address) {
		final int result = registers.getA() | memory.read(address);
		registers.setA(result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void test(final Registers registers, final Memory memory, final int address) {
		final int result = memory.read(address);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(registers.getA() & result));
		registers.setV(Flags.OVERFLOW_BIT.test(result));
	}

	private static void add(final Registers registers, final Memory memory, final int address) {
		final int result = memory.read(address) + registers.getA() + (registers.isC() ? 1 : 0);
		registers.setA(result);
		registers.setC(Flags.CARRY.test(result));
		registers.setZ(Flags.ZERO.test(result));
		registers.setV(Flags.OVERFLOW.test(result));
		registers.setN(Flags.NEGATIVE.test(result));
	}

	private static void sub(final Registers registers, final Memory memory, final int address) {
		final int result = registers.getA() - memory.read(address) - (!registers.isC() ? 1 : 0);
		registers.setA(result);
//		Flags.setFlags(registers, value, 'k', 'z', 'v', 'n');
		registers.setZ(Flags.ZERO.test(result));
		registers.setV(Flags.OVERFLOW.test(result));
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setC(!Flags.CARRY.test(result));
	}

	private static void cmp(final Registers registers, final Memory memory, final int address, final int value) {
		final int result = value - memory.read(address);
		registers.setN(Flags.NEGATIVE.test(result));
		if (result >= 0) {
			registers.setC(true);
		}
		registers.setZ(Flags.ZERO.test(result));
	}

	// unsigned integer => wrap around to 255/$FF
	// signed integer => wrap around to +127/$7F

	private static void inc(final Registers registers, final Memory memory, final int address) {
		final int result = (memory.read(address) + 1) & 0x00FF;
		memory.write(address, result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void inc(final Registers registers, final BiConsumer<Registers, Integer> destination,
			final int value) {
		final int result = (value + 1) & 0x00FF;
		destination.accept(registers, result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	// TODO refactor
	private static void dec(final Registers registers, final Memory memory, final int address) {
		final int result = (memory.read(address) - 1) & 0x00FF;
		memory.write(address, result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void dec(final Registers registers, final BiConsumer<Registers, Integer> destination,
			final int value) {
		final int result = (value - 1) & 0x00FF;
		destination.accept(registers, result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	// TODO refactor
	private static void asl(final Registers registers) {
		int result = registers.getA();
		registers.setC(Flags.CARRY_BY_BIT.test(result, 7));
		result = (result << 1) & 0x00FF;
		registers.setA(result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void asl(final Registers registers, final Memory memory, final int address) {
		int result = memory.read(address);
		registers.setC(Flags.CARRY_BY_BIT.test(result, 7));
		result = (result << 1) & 0x00FF;
		memory.write(address, result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	// TODO refactor
	private static void lsr(final Registers registers) {
		int result = registers.getA();
		registers.setC(Flags.CARRY_BY_BIT.test(result, 0));
		result >>= 1;
		registers.setA(result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void lsr(final Registers registers, final Memory memory, final int address) {
		int result = memory.read(address);
		registers.setC(Flags.CARRY_BY_BIT.test(result, 0));
		result >>= 1;
		memory.write(address, result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	// TODO refactor
	private static void ror(final Registers registers) {
		int result = registers.getA();
		if (registers.isC())
			result |= 0x0100;
		registers.setC(Flags.CARRY_BY_BIT.test(result, 0));
		result >>= 1;
		registers.setA(result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void ror(final Registers registers, final Memory memory, final int address) {
		int result = memory.read(address);
		if (registers.isC())
			result |= 0x0100;
		registers.setC(Flags.CARRY_BY_BIT.test(result, 0));
		result >>= 1;
		memory.write(address, result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	// TODO refactor
	private static void rol(final Registers registers) {
		int result = registers.getA() << 1;
		if (registers.isC())
			result |= 0x0001;
		registers.setC(Flags.CARRY_BY_BIT.test(result, 7));
		result &= 0x00FF;
		registers.setA(result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static void rol(final Registers registers, final Memory memory, final int address) {
		int result = memory.read(address) << 1;
		if (registers.isC())
			result |= 0x0001;
		registers.setC(Flags.CARRY_BY_BIT.test(result, 7));
		result &= 0x00FF;
		memory.write(address, result);
		registers.setN(Flags.NEGATIVE.test(result));
		registers.setZ(Flags.ZERO.test(result));
	}

	private static class Flags {
		private static final IntPredicate ZERO = x -> (x == 0);
		// MSB 2^7 = 0x0080
		private static final IntPredicate NEGATIVE = x -> ((x & 0x0080) != 0);
		// MSB 2^6 = 0x0040
		private static final IntPredicate OVERFLOW_BIT = x -> ((x & 0x0040) != 0);
		// +127 (0x007F) (01111111) <=> -128 (0x0080) (10000000)
		private static final IntPredicate OVERFLOW = x -> (x > 0x007F || x < 0x0080);
		// overflow in bit 7
		private static final IntPredicate CARRY = x -> (x >> 8 != 0);
		// contents of bit y
		private static final BiPredicate<Integer, Integer> CARRY_BY_BIT = (x, y) -> (((x >> y) & 1) != 0);

//		public static final void setFlags(final Registers registers, final int value, char... flags) {
//			for (char c : flags) {
//				switch (c) {
//				case 'c':
//					registers.setC(Flags.CARRY.test(value));
//					break;
//				// clear overflow flag if overflow in bit 7
//				case 'k':
//					registers.setC(!Flags.CARRY.test(value));
//					break;
//				case 'z':
//					registers.setZ(Flags.ZERO.test(value));
//					break;
//				case 'i':
//					break;
//				case 'd':
//					break;
//				case 'b':
//					break;
//				// overflow bit special use
//				case 'w':
//					registers.setV(Flags.OVERFLOW_BIT.test(value));
//					break;
//				case 'v':
//					registers.setV(Flags.OVERFLOW.test(value));
//					break;
//				case 'n':
//					registers.setN(Flags.NEGATIVE.test(value));
//					break;
//				default:
//					break;
//				}
//			}
//		}
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

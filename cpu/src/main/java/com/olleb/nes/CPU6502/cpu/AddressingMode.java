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

import static com.olleb.nes.CPU6502.mem.RAM.Address.ZERO_PAGE_END;

import java.util.function.BiPredicate;
import java.util.function.IntFunction;
import java.util.function.ToIntBiFunction;

import com.olleb.nes.CPU6502.mem.Memory;

/**
 * The 6502 addressing modes.
 *
 */
public enum AddressingMode implements ToIntBiFunction<Registers, Memory> {
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

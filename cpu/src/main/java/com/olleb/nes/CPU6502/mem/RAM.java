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

package com.olleb.nes.CPU6502.mem;

import java.util.Arrays;

/***
 * 6502 RAM Memory.
 *
 */
public final class RAM implements Memory {

	// Zero page: 0x0000 - 0x00FF
	// Stack: 0x0100 - 0x01FF
	// RAM: 0x0200 - 0x0800
	// Mirrors 0x0000:0x07FF => 0x0800 - 0x1FFF
	private final int mem[] = new int[Address.END.value + 1];

	public enum Address {
		TOTAL_BEGIN(0x0000),
		BEGIN(0x0200),
		END(0x07FF),
		MIRROR_BEGIN(0x0800),
		MIRROR_END(0x1FFF),
		TOTAL_END(0x1FFF),
		VECTOR_0_(0xFFFC),
		VECTOR_1_(0xFFFD),
		ZERO_PAGE_BEGIN(0x0000),
		ZERO_PAGE_END(0x00FF),
		STACK_BEGIN(0x0100),
		STACK_END(0x01FF);
		
		private final int value;

		private Address(final int address) {
			this.value = address;
		}
		
		public int getAddress() {
			return value;
		}
	}

	@Override
	public int read(final int address) {
		if (address <= Address.MIRROR_END.value) {
			return mem[address & Address.END.value];
		}
		return mem[address];
	}

	@Override
	public void write(final int address, final int value) {
		// write mirrors optimized. Write only once.
		if (address <= Address.MIRROR_END.value) {
			mem[address & Address.END.value] = value;
		} else
			mem[address] = value;
	}

	public int getSize() {
		return mem.length;
	}
	
	public void clear() {
		Arrays.fill(mem, 0x0);
	}

}

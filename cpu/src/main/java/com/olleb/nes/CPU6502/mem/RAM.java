/**
 * nes - 6502 CPU Emulator
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

package com.olleb.nes.CPU6502.mem;

public class RAM implements Memory {

	// Zero page: 0x0000 - 0x00FF
	// Stack: 0x0100 - 0x01FF
	// RAM: 0x0200 - 0x0800
	// Mirrors 0x000:0x07FF => 0x0800 - 0x1FFF
	private final int mem[] = new int[0x0800];

	@Override
	public void read(int address) {
		
	}

	@Override
	public void write(int address, int value) {
		
	}

	public int getSize() {
		return mem.length;
	}

}

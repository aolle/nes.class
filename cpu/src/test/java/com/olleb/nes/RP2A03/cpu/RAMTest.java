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

package com.olleb.nes.RP2A03.cpu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.olleb.nes.CPU6502.mem.RAM;

@DisplayName("RAM tests")
class RAMTest {

	private RAM ram;

	@BeforeAll
	public void init() {
		ram = new RAM();
	}

	@BeforeEach
	public void reset() {
		ram.clear();
	}

	@Test
	@DisplayName("Test RAM size")
	void testSize() {
		assertEquals(2048, ram.getSize());
	}

	@Test
	@DisplayName("Test RAM writes and reads")
	void testReadWrite() {
		// TODO
		final int end = 0x01FF;
		final int start = 0x000;
		final int[] mirrors = { 0x0800, 0x1000, 0x1800 };
		final int[] values = new int[0x01FF];
		for (int i = start; i <= end; i++) {
			values[i] = i;
			ram.write(i, i);
		}
		int[] test = new int[0x01FF];
		

	}

}

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

package com.olleb.nes.CPU6502.mem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@DisplayName("RAM tests")
@TestInstance(Lifecycle.PER_CLASS)
class RAMTest extends RAMTestBase {

	@Test
	@DisplayName("Test RAM size")
	void testSize() {
		assertEquals(2048, ram.getSize());
	}

	@Test
	@DisplayName("Test RAM writes and reads")
	void testReadWrite() {
		final int[] addr = { 0x0000, 0x0800, 0x1000, 0x1800, 0x2000 };

		// fill ram[0x0000] = 0x0000 to ram[0x01FF] = 0x01FF
		final List<Integer> values = generateValuesList(addr[0], addr[1]);
		values.forEach((i) -> ram.write(i, i));

		// check RAM values
		List<Integer> check = getReadedValuesList(values);
		assertTrue(check.equals(values));

		// check mirror 1
		List<Integer> mirror = generateValuesList(addr[1], addr[2]);
		check = getReadedValuesList(mirror);
		assertTrue(check.equals(values));

		// check mirror 2
		mirror = generateValuesList(addr[2], addr[3]);
		check = getReadedValuesList(mirror);
		assertTrue(check.equals(values));

		// check mirror 3
		mirror = generateValuesList(addr[3], addr[4]);
		check = getReadedValuesList(mirror);
		assertTrue(check.equals(values));
		
		// oom
		assertEquals(-1, ram.read(0xFFFFF));
	}

	private List<Integer> generateValuesList(int start, int end) {
		return IntStream.range(start, end).boxed().collect(Collectors.toList());
	}

	private List<Integer> getReadedValuesList(final List<Integer> values) {
		return values.stream().map(ram::read).collect(Collectors.toList());
	}

}

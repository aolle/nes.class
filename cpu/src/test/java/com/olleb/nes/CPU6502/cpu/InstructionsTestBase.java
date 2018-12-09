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

import org.junit.jupiter.api.BeforeEach;

import com.olleb.nes.CPU6502.cpu.Registers;
import com.olleb.nes.CPU6502.mem.RAMTestBase;

public abstract class InstructionsTestBase extends RAMTestBase {

	protected Registers registers;

	@BeforeEach
	@Override
	public void reset() {
		super.reset();
		registers = new Registers();
	}

}

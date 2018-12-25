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

public final class Registers {

	/**
	 * registers 
	 * pc -> program counter 
	 * sp -> stack pointer
	 * a -> accumulator
	 * x -> index register X
	 * y -> index register Y
	 */
	private int pc;
	private int sp;
	private int a;
	private int x;
	private int y;

	/**
	 * cpu status 
	 * c -> carry flag
	 * z -> zero flag
	 * i -> interrupt disable
	 * d -> decimal mode flag
	 * b -> break command
	 * v -> overflow flag
	 * n -> negative flag
	 */
	private boolean c;
	private boolean z;
	private boolean i;
	private boolean d;
	private boolean b;
	private boolean v;
	private boolean n;

	// page crossed
	private boolean pg;

	public int getPC() {
		return pc;
	}

	public void setPC(int pc) {
		this.pc = pc;
	}

	public int getSP() {
		return sp;
	}

	public void setSP(int sp) {
		this.sp = sp;
		wrapSP();
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isC() {
		return c;
	}

	public void setC(boolean c) {
		this.c = c;
	}

	public boolean isZ() {
		return z;
	}

	public void setZ(boolean z) {
		this.z = z;
	}

	public boolean isI() {
		return i;
	}

	public void setI(boolean i) {
		this.i = i;
	}

	public boolean isD() {
		return d;
	}

	public void setD(boolean d) {
		this.d = d;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public boolean isV() {
		return v;
	}

	public void setV(boolean v) {
		this.v = v;
	}

	public boolean isN() {
		return n;
	}

	public void setN(boolean n) {
		this.n = n;
	}

	public int incrementPC() {
		return ++pc;
	}
	
	public int incrementSP() {
		sp++;
		wrapSP();
		return sp;
	}

	public int decrementSP() {
		sp--;
		wrapSP();
		return sp;
	}
	
	// waparound 8 bit register. SP 0x00 - 0xFF. Serves as an offset from 0x0100.
	private void wrapSP() {
		sp &= 0xFF;
	}
	
	public boolean isPg() {
		return pg;
	}

	public void setPg(boolean pg) {
		this.pg = pg;
	}
	
	public int getProcessorStatus() {
		int r = 0;
		for (boolean b : new boolean[] { c, z, i, d, b, v, n }) {
			r = (r << 1) + (b ? 1 : 0);
		}
		return r;
	}
	
	public void setProcessorStatus(final int status) {
		c = ((status >> 6) & 1) == 1 ? true : false;
		z = ((status >> 5) & 1) == 1 ? true : false;
		i = ((status >> 4) & 1) == 1 ? true : false;
		d = ((status >> 3) & 1) == 1 ? true : false;
		b = ((status >> 2) & 1) == 1 ? true : false;
		v = ((status >> 1) & 1) == 1 ? true : false;
		n = ((status >> 0) & 1) == 1 ? true : false;
	}
	
}

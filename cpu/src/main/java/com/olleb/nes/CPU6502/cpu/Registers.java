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

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

	public int getSp() {
		return sp;
	}

	public void setSp(int sp) {
		this.sp = sp;
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

	public int inc() {
		return ++pc;
	}

	public boolean isPg() {
		return pg;
	}

	public void setPg(boolean pg) {
		this.pg = pg;
	}

}

/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.utils;

/** Typed runtime exception used throughout SlotPuzzle
 * 
 * @author mellis */
public class SPRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 6735854402467673117L;

	public SPRuntimeException (String message) {
		super(message);
	}

	public SPRuntimeException (Throwable t) {
		super(t);
	}

	public SPRuntimeException (String message, Throwable t) {
		super(message, t);
	}
}
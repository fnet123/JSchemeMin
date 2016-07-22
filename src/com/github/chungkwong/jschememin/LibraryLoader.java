/*
 * Copyright (C) 2016 Chan Chung Kwong <1m02math@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.chungkwong.jschememin;
import com.github.chungkwong.jschememin.lib.*;
import com.github.chungkwong.jschememin.type.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LibraryLoader{
	private static final HashMap<ScmPair,Library> LIBRARIES=new HashMap<ScmPair,Library>();
	private static final HashMap<ScmPair,NativeLibrary> NATIVE_LIBRARIES=new HashMap<ScmPair,NativeLibrary>();
	static{
		addNativeLibrary(Base.INSTANCE);
		addNativeLibrary(CaseLambda.INSTANCE);
		addNativeLibrary(Char.INSTANCE);
		addNativeLibrary(Complex.INSTANCE);
		addNativeLibrary(CxR.INSTANCE);
		addNativeLibrary(Eval.INSTANCE);
		addNativeLibrary(File.INSTANCE);
		addNativeLibrary(Inexact.INSTANCE);
		addNativeLibrary(Lazy.INSTANCE);
		addNativeLibrary(Load.INSTANCE);
		addNativeLibrary(ProcessContext.INSTANCE);
		addNativeLibrary(REPL.INSTANCE);
		addNativeLibrary(Read.INSTANCE);
		addNativeLibrary(Time.INSTANCE);
		addNativeLibrary(Write.INSTANCE);
	}
	private static void addNativeLibrary(NativeLibrary lib){
		NATIVE_LIBRARIES.put(lib.getName(),lib);
	}
	public static void addLibrary(Library lib){
		LIBRARIES.put(lib.getName(),lib);
	}
	public static Library getLibrary(ScmPair name){
		if(LIBRARIES.containsKey(name))
			return LIBRARIES.get(name);
		else if(NATIVE_LIBRARIES.containsKey(name))
			return NATIVE_LIBRARIES.get(name).getLibrary();
		else
			throw new RuntimeException();
	}
}
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
package com.github.chungkwong.jschememin.type;
import com.github.chungkwong.jschememin.*;
import com.github.chungkwong.jschememin.primitive.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class HygieneTransformer{

	public static ScmObject transform(ScmObject obj){
		return stripTimeStamp(obj);
	}
	private static ScmSymbolWithTimeStamp S(ScmSymbol id,int n){
		return new ScmSymbolWithTimeStamp(id.getValue(),n);
	}
	private static ScmObject replace(ScmSymbolWithTimeStamp tsvar,ScmSymbol var,ScmObject tsstree){
		if(tsstree instanceof ScmPair&&((ScmPair)tsstree).getCar()instanceof ScmSymbol){
			String keyword=((ScmSymbol)((ScmPair)tsstree).getCar()).getValue();
			if(keyword.equals("lambda")){
				if(tsvar.equals(ScmList.second(tsstree))){
					return ScmList.toList(Lambda.INSTANCE.getKeyword(),tsvar,replace(tsvar,var,((ScmPair)tsstree).getCddr()));
				}else if(ScmList.second(tsstree)instanceof ScmPair){
					if(ScmList.asStream((ScmPairOrNil)ScmList.second(tsstree)).anyMatch((o)->tsvar.equals(o)))
						return ScmList.toList(ScmList.first(tsstree),ScmList.second(tsstree),
								replace(tsvar,var,((ScmPair)tsstree).getCddr()));
				}
			}else{
				return new ScmPair(replace(tsvar,var,((ScmPair)tsstree).getCar()),
						replace(tsvar,var,((ScmPair)tsstree).getCdr()));
			}
		}else if(tsvar.equals(tsstree)){
			return var;
		}
		return tsstree;
	}
	private static ScmObject rename(ScmObject tsstree,Environment env){
		if(tsstree instanceof ScmPair){
			if(ScmList.first(tsstree).equals(Lambda.INSTANCE.getKeyword())){
				if(ScmList.second(tsstree)instanceof ScmSymbolWithTimeStamp){
					return ScmList.toList(ScmList.first(tsstree),ScmList.second(tsstree),
							rename(replace((ScmSymbolWithTimeStamp)ScmList.second(tsstree),env.getUnusedVariable(),((ScmPair)tsstree).getCddr()),env));
				}else if(ScmList.second(tsstree)instanceof ScmPair){
					/*if(ScmList.asStream((ScmPairOrNil)ScmList.second(tsstree)).anyMatch((o)->tsvar.equals(o)))
						return ScmList.toList(ScmList.first(tsstree),ScmList.second(tsstree),
								replace(tsvar,var,((ScmPair)tsstree).getCddr()));*/
				}
			}
			return new ScmPair(rename(((ScmPair)tsstree).getCar(),env),rename(((ScmPair)tsstree).getCdr(),env));
		}else{
			return tsstree;
		}
	}
	private static ScmObject stripTimeStamp(ScmObject tsstree){
		if(tsstree instanceof ScmSymbolWithTimeStamp){
			return new ScmSymbol(((ScmSymbolWithTimeStamp)tsstree).getValue());
		}else if(tsstree instanceof ScmPair){
			return new ScmPair(stripTimeStamp(((ScmPair)tsstree).getCar()),stripTimeStamp(((ScmPair)tsstree).getCdr()));
		}else{
			return tsstree;
		}
	}
	private static class ScmSymbolWithTimeStamp extends ScmSymbol{
		private final int ts;
		public ScmSymbolWithTimeStamp(String id,int ts){
			super(id);
			this.ts=ts;
		}
		public int getTimeStamp(){
			return ts;
		}
		@Override
		public boolean equals(Object obj){
			return obj instanceof ScmSymbolWithTimeStamp&&super.equals(obj)
					&&ts==((ScmSymbolWithTimeStamp)obj).ts;
		}
		@Override
		public int hashCode(){
			int hash=3;
			hash=19*hash+super.hashCode();
			hash=19*hash+this.ts;
			return hash;
		}
	}
//	private static final SyntaxTransformFunction S=(o)->;
	interface SyntaxTransformFunction{
		ScmObject apply(ScmObject o);
	}
}
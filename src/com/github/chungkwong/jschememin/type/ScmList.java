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
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScmList{
	public static int getLength(ScmObject list){
		int len=0;
		while(list instanceof ScmPair){
			list=((ScmPair)list).getCdr();
			++len;
		}
		if(list!=ScmNil.NIL)
			throw new RuntimeException();
		return len;
	}
	public static void forEach(ScmObject list,Consumer<ScmObject> proc){
		ScmObject pair=list;
		while(pair instanceof ScmPair){
			proc.accept(((ScmPair)pair).getCar());
			pair=((ScmPair)pair).getCdr();
		}
		if(list!=ScmNil.NIL)
			throw new RuntimeException();
	}
	public static boolean isList(ScmObject list){
		HashSet<ScmObject> found=new HashSet<>();
		while(list instanceof ScmPair){
			if(found.contains(list))
				return false;
			found.add(list);
			list=((ScmPair)list).getCdr();
		}
		return list==ScmNil.NIL;
	}
	public static ScmPairOrNil toList(List<? extends ScmObject> list){
		if(list.isEmpty()){
			return ScmNil.NIL;
		}
		ScmPair start=new ScmPair(list.get(0),ScmNil.NIL);
		ScmPair end=start;
		for(ScmObject o:list.subList(1,list.size())){
			ScmPair newend=new ScmPair(o,ScmNil.NIL);
			end.setCdr(newend);
			end=newend;
		}
		return start;
	}
	public static ScmPairOrNil toList(ScmObject... list){
		if(list.length==0){
			return ScmNil.NIL;
		}
		ScmPair start=new ScmPair(list[0],ScmNil.NIL);
		ScmPair end=start;
		for(int i=1;i<list.length;i++){
			ScmPair newend=new ScmPair(list[i],ScmNil.NIL);
			end.setCdr(newend);
			end=newend;
		}
		return start;
	}
	public static ScmPairOrNil fillList(ScmObject obj,ScmInteger count){
		return fillList(obj,count.getValue().intValueExact());
	}
	public static ScmPairOrNil fillList(ScmObject obj,int len){
		ScmPairOrNil curr=ScmNil.NIL;
		while(--len>=0)
			curr=new ScmPair(obj,curr);
		return curr;
	}
	public static ScmObject get(ScmPair obj,ScmInteger index){
		return get(obj,index.getValue().intValueExact());
	}
	public static ScmObject get(ScmPair obj,int index){
		while(--index>=0)
			obj=(ScmPair)obj.getCdr();
		return obj.getCar();
	}
	public static ScmPairOrNil reverse(ScmPairOrNil list){
		ScmPairOrNil rev=ScmNil.NIL;
		while(list instanceof ScmPair){
			rev=new ScmPair(((ScmPair)list).getCar(),rev);
			list=(ScmPairOrNil)((ScmPair)list).getCdr();
		}
		return rev;
	}
	public static ScmPairOrNil append(ScmPairOrNil... lists){
		ScmPairOrNil start=ScmNil.NIL,last=ScmNil.NIL;
		for(ScmPairOrNil list:lists){
			while(list instanceof ScmPair){
				ScmObject item=((ScmPair)list).getCar();
				if(last==ScmNil.NIL){
					start=last=new ScmPair(item,ScmNil.NIL);
				}else{
					ScmPair toAdd=new ScmPair(item,ScmNil.NIL);
					((ScmPair)last).setCdr(toAdd);
					last=toAdd;
				}
				list=(ScmPairOrNil)((ScmPair)list).getCdr();
			}
		}
		return start;
	}
	public static ScmPairOrNil copy(ScmPairOrNil list){
		if(list instanceof ScmPair){
			ScmPair start=new ScmPair(((ScmPair)list).getCar(),ScmNil.NIL),end=start;
			list=(ScmPairOrNil)((ScmPair)list).getCdr();
			while(list instanceof ScmPair){
				ScmPair toAdd=new ScmPair(((ScmPair)list).getCar(),ScmNil.NIL);
				end.setCdr(toAdd);
				end=toAdd;
				list=(ScmPairOrNil)((ScmPair)list).getCdr();
			}
			return start;
		}else
			return ScmNil.NIL;
	}
	public static void set(ScmPair list,ScmInteger index,ScmObject obj){
		set(list,index.getValue().intValueExact(),obj);
	}
	public static void set(ScmPair list,int index,ScmObject obj){
		while(--index>=0)
			list=(ScmPair)list.getCdr();
		list.setCar(obj);
	}
	public static ScmPair getLastListNode(ScmPair scmPair){
		ScmPair node=scmPair;
		while(node.getCdr() instanceof ScmPair){
			node=(ScmPair)node.getCdr();
		}
		return node;
	}
	public static ScmObject isMemberStrict(ScmObject item,ScmPairOrNil list){
		while(list instanceof ScmPair){
			if(((ScmPair)list).getCar().equalsStrict(item))
				return list;
			list=(ScmPairOrNil)((ScmPair)list).getCdr();
		}
		return ScmBoolean.FALSE;
	}
	public static ScmObject isMemberValue(ScmObject item,ScmPairOrNil list){
		while(list instanceof ScmPair){
			if(((ScmPair)list).getCar().equalsValue(item))
				return list;
			list=(ScmPairOrNil)((ScmPair)list).getCdr();
		}
		return ScmBoolean.FALSE;
	}
	public static ScmObject isMember(ScmObject item,ScmPairOrNil list){
		while(list instanceof ScmPair){
			if(((ScmPair)list).getCar().equals(item))
				return list;
			list=(ScmPairOrNil)((ScmPair)list).getCdr();
		}
		return ScmBoolean.FALSE;
	}

	public static ScmObject isKeyStrict(ScmObject item,ScmPairOrNil list){
		while(list instanceof ScmPair){
			if(((ScmPair)list).getCaar().equalsStrict(item))
				return ((ScmPair)list).getCar();
			list=(ScmPairOrNil)((ScmPair)list).getCdr();
		}
		return ScmBoolean.FALSE;
	}
	public static ScmObject isKeyValue(ScmObject item,ScmPairOrNil list){
		while(list instanceof ScmPair){
			if(((ScmPair)list).getCaar().equalsValue(item))
				return ((ScmPair)list).getCar();
			list=(ScmPairOrNil)((ScmPair)list).getCdr();
		}
		return ScmBoolean.FALSE;
	}
	public static ScmObject isKey(ScmObject item,ScmPairOrNil list){
		while(list instanceof ScmPair){
			if(((ScmPair)list).getCaar().equals(item))
				return ((ScmPair)list).getCar();
			list=(ScmPairOrNil)((ScmPair)list).getCdr();
		}
		return ScmBoolean.FALSE;
	}
	public static Stream<ScmObject> asStream(ScmPairOrNil list){
		Iterable iter=()->new Iterator() {
			ScmPairOrNil rest=list;
			@Override
			public boolean hasNext(){
				return list instanceof ScmPair;
			}
			@Override
			public ScmObject next(){
				ScmObject obj=((ScmPair)rest).getCar();
				rest=(ScmPairOrNil)((ScmPair)rest).getCdr();
				return obj;
			}
		};
		return StreamSupport.stream(iter.spliterator(),false);
	}
}
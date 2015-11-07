package net.qwertysam.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtil<T>
{
	public List<T> toList(T[] t)
	{
		List<T> list = new ArrayList<T>();
		
		for (T t2 : t)
		{
			list.add(t2);
		}
		
		return list;
	}
}

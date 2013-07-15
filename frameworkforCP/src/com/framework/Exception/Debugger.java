package com.framework.Exception;

import java.util.ArrayList;

public class Debugger {
	
	private static boolean debug = false;

	public static StackTraceElement[] getCallerStack()
	{
		if (debug)
		{
		ArrayList<StackTraceElement> ret = new ArrayList<StackTraceElement>();
		try
		{
			int a = 0;
			int b = 3 / a;
			a = b;
		}
		catch (Exception e)
		{
			StackTraceElement[] ste = e.getStackTrace();
			for (int n = 2; n < ste.length; n++)
			{
				ret.add(ste[n]);
			}
		}
		return (StackTraceElement[])ret.toArray();
		}
		else
		{
			return null; 
		}
	}
	
	public static void Assert(boolean bCondition, String msg)
	{
		if (debug) {
			if (bCondition)
				return ;
			DebuggerError e = new DebuggerError(msg);
			throw e;
		}
	}
}

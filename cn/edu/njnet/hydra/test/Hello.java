package cn.edu.njnet.hydra.test;

import java.io.File;


public class Hello {

    public Hello()
    {
    	String logname = Hello.class.getName();

    	System.out.println(logname);
    }
    public boolean hello() 
    {
        return false;
    }
    public static void main(String[] args)
    {
    	int l = 3;
    	String str;
    	switch(l)
    	{
    	   case 1:
    		   str = "1";
    	   case 2:
    		   str = "2";
    	   case 3:
    		   str = "3";
    	   default :
    		   str = "0";
    	}
    	System.out.print(str);
    	
    }
}  
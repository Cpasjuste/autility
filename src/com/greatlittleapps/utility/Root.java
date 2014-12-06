package com.greatlittleapps.utility;

import java.util.ArrayList;
import java.util.List;

import org.sufficientlysecure.rootcommands.RootCommands;
import org.sufficientlysecure.rootcommands.Shell;
import org.sufficientlysecure.rootcommands.command.SimpleCommand;

import com.greatlittleapps.utility.RootLS.FileEntry;

public class Root 
{
	public static boolean isRoot()
	{
		return RootCommands.rootAccessGiven();
	}

	public static void reboot()
	{
		Shell shell = null;
		
		try 
		{
			shell = Shell.startRootShell();
			SimpleCommand cmd = new SimpleCommand( "reboot" );
			shell.add(cmd).waitForFinish();
			shell.close();
		}
		catch( Exception e )
		{
			Utility.loge( e.getMessage() );
		}
	}

	public static void echo( String args, String path )
	{
		Shell shell = null;
		
		try 
		{
			shell = Shell.startRootShell();
			SimpleCommand cmd = new SimpleCommand( "echo " + args + " > " + path );
			shell.add(cmd).waitForFinish();
			shell.close();
		}
		catch( Exception e )
		{
			Utility.loge( e.getMessage() );
		}
	}
	
	public static void mkdir( String path )
	{
		Shell shell = null;
		
		try 
		{
			shell = Shell.startRootShell();
			SimpleCommand cmd = new SimpleCommand( "mkdir -p " + path );
			shell.add(cmd).waitForFinish();
			shell.close();
		}
		catch( Exception e )
		{
			Utility.loge( e.getMessage() );
		}
	}
	
	public static List<String> cat( String path )
	{
		List<String> files = new ArrayList<String>();
		Shell shell = null;
		
		try 
		{
			shell = Shell.startRootShell();
			SimpleCommand cmd = new SimpleCommand( "cat " + path );
			shell.add(cmd).waitForFinish();
			
			String[] splitted = cmd.getOutput().split( "\n" );
			if( splitted != null )
			{
				for( String line : splitted )
				{
					files.add( line );
				}
			}
			shell.close();
		}
		catch( Exception e )
		{
			Utility.loge( e.getMessage() );
		}
		return files;
	}
	
	public static List<FileEntry> getFiles( String path )
	{
		List<FileEntry> files = new ArrayList<FileEntry>();
		Shell shell = null;
		
		//if( !path.endsWith( "/" ) )
		//{
		//	path += "/";
		//}
		
		try 
		{
			shell = Shell.startRootShell();
			SimpleCommand cmd = new SimpleCommand( "ls -l " + path );
			shell.add(cmd).waitForFinish();
			
			if( cmd.getExitCode() != 0 )
			{
				return files;
			}
			
			String[] splitted = cmd.getOutput().split( "\n" );
			if( splitted != null )
			{
				files = new RootLS().processNewLines( path, splitted );
			}
			/*
			if( splitted != null )
			{
				for( String line : splitted )
				{
					String[] rowsDirty = line.split( " " );
					List<String> rows = new ArrayList<String>();
					
					for( int i=0; i<rowsDirty.length; i++ )
					{
						if( rowsDirty[i].trim().length() > 0  )
						{
							rows.add( rowsDirty[i].trim() );
						}
					}
					
					FileInfo file = new FileInfo();
					file.name = rows.get(rows.size()-1).trim();
					file.path = path + file.name;
					file.isDirectory = rows.get(0).startsWith( "d" );
					file.isFile = !file.isDirectory;
					if( file.isFile )
					{
						String length = rows.get(3).replace( " ", "" );
						if( isNumeric( length ) )
						{
							file.length = Long.valueOf( length );
						}
					}
					files.add( file );
				}
			}
			*/
			shell.close();
		}
		catch( Exception e )
		{
			Utility.loge( e.getMessage() );
		}
		return files;
	}
}

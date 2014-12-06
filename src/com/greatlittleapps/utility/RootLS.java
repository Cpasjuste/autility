package com.greatlittleapps.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RootLS 
{
	/** Entry type: File */
	public static final int TYPE_FILE = 0;
	/** Entry type: Directory */
	public static final int TYPE_DIRECTORY = 1;
	/** Entry type: Directory Link */
	public static final int TYPE_DIRECTORY_LINK = 2;
	/** Entry type: Block */
	public static final int TYPE_BLOCK = 3;
	/** Entry type: Character */
	public static final int TYPE_CHARACTER = 4;
	/** Entry type: Link */
	public static final int TYPE_LINK = 5;
	/** Entry type: Socket */
	public static final int TYPE_SOCKET = 6;
	/** Entry type: FIFO */
	public static final int TYPE_FIFO = 7;
	/** Entry type: Other */
	public static final int TYPE_OTHER = 8;
	/** Device side file separator. */
	public static final String FILE_SEPARATOR = "/"; //$NON-NLS-1$
	/**
	 * Regexp pattern to parse the result from ls.
	 */
	private static Pattern sLsPattern = Pattern
			.compile("^([bcdlsp-][-r][-w][-xsS][-r][-w][-xsS][-r][-w][-xstST])\\s+(\\S+)\\s+(\\S+)\\s+([\\d\\s,]*)\\s+(\\d{4}-\\d\\d-\\d\\d)\\s+(\\d\\d:\\d\\d)\\s+(.*)$"); //$NON-NLS-1$

	public List<FileEntry> processNewLines( String path, String[] lines )
	{ 
		List<FileEntry> listOfFiles = new ArrayList<FileEntry>();
		for (String line : lines) {
			// no need to handle empty lines.
			if (line.length() == 0) {
				continue;
			}
			// run the line through the regexp
			Matcher m = sLsPattern.matcher(line);
			if (m.matches() == false) {
				continue;
			}
			// get the name
			String name = m.group(7);
			// get the rest of the groups
			String permissions = m.group(1);
			String owner = m.group(2);
			String group = m.group(3);
			String size = m.group(4);
			String date = m.group(5);
			String time = m.group(6);
			String info = null;
			// and the type
			int objectType = TYPE_OTHER;
			switch (permissions.charAt(0)) {
			case '-':
				objectType = TYPE_FILE;
				break;
			case 'b':
				objectType = TYPE_BLOCK;
				break;
			case 'c':
				objectType = TYPE_CHARACTER;
				break;
			case 'd':
				objectType = TYPE_DIRECTORY;
				break;
			case 'l':
				objectType = TYPE_LINK;
				break;
			case 's':
				objectType = TYPE_SOCKET;
				break;
			case 'p':
				objectType = TYPE_FIFO;
				break;
			}
			// now check what we may be linking to
			if (objectType == TYPE_LINK) {
				String[] segments = name.split("\\s->\\s"); //$NON-NLS-1$
				// we should have 2 segments
				if (segments.length == 2) {
					// update the entry name to not contain the link
					name = segments[0];
					// and the link name
					info = segments[1];
					// now get the path to the link
					String[] pathSegments = info.split(FILE_SEPARATOR);
					if (pathSegments.length == 1) {
						// the link is to something in the same directory,
						// unless the link is ..
						if ("..".equals(pathSegments[0])) { //$NON-NLS-1$
							// set the type and we're done.
							objectType = TYPE_DIRECTORY_LINK;
						} else {
							// either we found the object already
							// or we'll find it later.
						}
					}
				}
				// add an arrow in front to specify it's a link.
				info = "-> " + info; //$NON-NLS-1$;
			}
			FileEntry entry = new FileEntry();
			entry.name = name;
			entry.path = path;
			entry.permissions = permissions;
			entry.size = size;
			entry.date = date;
			entry.time = time;
			entry.owner = owner;
			entry.group = group;
			entry.type = objectType;
			if (objectType == TYPE_LINK) {
				entry.info = info;
			}
			listOfFiles.add(entry);
		}
		return listOfFiles;
	}

	public final static class FileEntry {
		public String name;
		public String path;
		public String info;
		public String permissions;
		public String size;
		public String date;
		public String time;
		public String owner;
		public String group;
		public int type;
		
		public boolean isDirectory()
		{
			return type == TYPE_DIRECTORY;
		}
		
		public boolean isFile()
		{
			return type == TYPE_FILE;
		}
		
		public String sizeFormated()
	    {
			if( isNumeric( size ) )
			{
				return Utility.formatFileSize( Long.valueOf( size ) );
			}
			return "0";
	    }
	}
	
	private static boolean isNumeric(String str) {
	    if (str == null) {
	        return false;
	    }
	    int sz = str.length();
	    for (int i = 0; i < sz; i++) {
	        if (Character.isDigit(str.charAt(i)) == false) {
	            return false;
	        }
	    }
	    return true;
	}
}


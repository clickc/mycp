package org.click.media.speechcat.facility;

import java.io.File;

import com.google.common.io.Files;

/**
 * rename all files in a dir(prefixed by dir names) and move them into one dir
 * 
 * @author blue
 */
public class RenameAndMove {

	public static void renameAndMove(String dir, String ndir, String prefix) {

		try {

			File filesDir = new File(prefix + dir);
			System.err.println("dir:" + dir + " ndir:" + ndir + " prefix:" + prefix + " prefix+dir:" + (prefix + dir));

			

			if (filesDir.isFile()) {
				
				String nprefix=prefix.substring(prefix.indexOf('/'),prefix.length());
				if(nprefix.endsWith("/")){
					nprefix=nprefix.substring(0, nprefix.length()-1);
				}
				
				if(nprefix.startsWith("/")){
					nprefix=nprefix.substring(1, nprefix.length());
				}
				
				nprefix=nprefix.replaceAll("/", "_");
				
				System.err.println("nprefix:"+nprefix);

				Files.copy(new File(prefix + (dir)),new File(ndir+"/"+nprefix+"_"+dir) );
				return;
			}

			File[] subFiles = filesDir.listFiles();

			for (int i = 0; i < subFiles.length; i++) {

				renameAndMove(subFiles[i].getName(), ndir, prefix+dir + "/");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		
		if(args.length!=2){
			System.err.println("usage: <oldDir> <newDir>");
			System.exit(1);
		}
		
		//renameAndMove("olddir", "ndir", "");
		renameAndMove(args[0], args[1], "");
	}

}

package io.oasp.ide.eclipse.configurator.core;

import io.oasp.ide.eclipse.configurator.logging.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


/**
 * Class to resolve variables within text files.
 * @author abennani
 *
 */
public class TextHandler {

  /**
   * {@link Resolver} to resolve variables within text files.
   */
  private Resolver resolver;

  /**
   * Creates a new {@link XmlHandler} that uses the given {@link Resolver}
   * to resolve variables within text files..
   * @param resolver - resolver for variables.
   */
  public TextHandler(Resolver resolver) {

    this.resolver = resolver;
  }

  /**
   * Writes the resolved content of the textfile to the destination file.
   * @param textFile - textFile to be solved and written to the destination.
   * @param destination - destination file for the resolved content of the textFile.
   */
  public void update(File textFile, File destination) {

	  FileInputStream fstream = null;
	  DataInputStream in = null;
	  BufferedReader br = null;
	  try {

    	fstream = new FileInputStream(textFile);
        in = new DataInputStream(fstream);
        br = new BufferedReader(new InputStreamReader(in));

       resolveVariables(br,destination);

    } catch (IOException e) {
      Log.LOGGER
          .log(Level.ALL, "An io error occurred during read of file: " + textFile.getAbsolutePath(), e.getStackTrace());
    } finally {
    	try{
            if( br != null ){
            	br.close(); // Will close bw and fw too
            }
            else if( in != null ){
            	in.close(); // Will close fw too
            }
            else if( fstream != null ){
            	fstream.close();
            }
         }
         catch( IOException e ){
            // Closing the file writers failed for some obscure reason
         }
    	
    }
  }

  /**
   * Calls {@link #resolveElement(Element)} for every element in the Buffer and write 
   * it to the destination file.
   * @param textFile - textFile to be solved and written to the destination.
   * @param destination - destination file for the resolved content of the textFile.
   */
  private void resolveVariables(BufferedReader buffer, File destination) {
	  
	  String strLine;
	  PrintWriter out = null;
	  BufferedWriter bw = null;
	  FileWriter fw = null;
	  
	  try{
		   while ((strLine = buffer.readLine()) != null) {
			   String resolvedLine = resolver.resolveVariables(strLine);
			   try {
			    	fw = new FileWriter(destination);
			        bw = new BufferedWriter(fw);
			        out = new PrintWriter(bw);
			    	out.println(resolvedLine);
				}catch (Exception e) {
			        System.err.println("Error: " + e.getMessage());
		  		}
		   } 
		}catch (IOException e) {
	        System.err.println(e);
	    }finally{
	    	try{
		    	if(out != null){
		            out.close();
		        }
		        if(bw != null){
		        	bw.close();
		        }
		        if(fw != null){
		        	fw.close();
		        }
	    	}catch( IOException e ){
	            // Closing the file writers failed for some obscure reason
	        } 
		}
  }
  
  
  

}

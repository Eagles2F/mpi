/*
 * This is the class to load and parse the DNAStrand input txt file
 * @Author: Yifan Li
 * @Date: 12/1/2014
 */
package utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DNAStrandDataLoader {
	private String  file;
	
	public DNAStrandDataLoader(String file){
		this.file = file;
	}
	
	public ArrayList<DNAStrand> loadData(){
		ArrayList<DNAStrand> dna = new ArrayList<DNAStrand>();
		
		try(BufferedReader br= new BufferedReader(new FileReader(this.file))){
			String line;
			while((line = br.readLine())!= null){	
				dna.add(new DNAStrand(line));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dna;
	}
}

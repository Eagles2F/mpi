/*
 * This is the class to load and parse the 2d points input csv file
 * @Author: Yifan Li
 * @Date: 12/1/2014
 */
package utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TwoDpointsDataLoader {
	private String  file;
	
	public TwoDpointsDataLoader(String file){
		this.file = file;
	}
	
	public ArrayList<TwoDPoint> loadData(){
		ArrayList<TwoDPoint> points = new ArrayList<TwoDPoint>();
		
		try(BufferedReader br= new BufferedReader(new FileReader(this.file))){
			String splitBy = ",";
			String line;
			while((line = br.readLine())!= null){
				String [] b = line.split(splitBy);
				points.add(new TwoDPoint(Double.valueOf(b[0]),Double.valueOf(b[1])));

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return points;
	}
}

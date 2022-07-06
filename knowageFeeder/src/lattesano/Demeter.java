package lattesano;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class Demeter {
	
	private static ResourceBundle properties = ResourceBundle.getBundle("resources/configuration");
	private static String separator = properties.getString("demeter.csv-separator");
	private static String csvFolderPath = properties.getString("demeter.path");
	private static String milkAnalysisPrefixFileName = properties.getString("demeter.milkAnalysisPrefixFileName");
	private static String sampleIdName = properties.getString("demeter.sampleId");
	private static String commentsName = properties.getString("demeter.comments");
	private static String fatName = properties.getString("demeter.fat");
	private static String wholeTankName = properties.getString("demeter.wholeTank");
	private static String freezingPointmCName = properties.getString("demeter.freezingPointmC");
	private static String lactoseName = properties.getString("demeter.lactose");
	private static String proteinName = properties.getString("demeter.protein");
	private static String snfName = properties.getString("demeter.snf");
	private static String tsName = properties.getString("demeter.ts");
	private static String lastFarmer = properties.getString("demeter.lastFarmer");
	
	private static DecimalFormat df = new DecimalFormat("0.00");
	
	@GET
    @Path("/milkAnalysis/v1/MilkAnalysis")
	public Response getMilkAnalysis(@Context HttpHeaders headers) throws IOException {
		JSONArray jsonArray = new JSONArray();
		File csvFolder = new File(csvFolderPath + milkAnalysisPrefixFileName);
        
		File csvFolderFileList = new File(csvFolder.getPath());
		File[] csvListOfFiles = csvFolderFileList.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        
        try {
			Arrays.sort(csvListOfFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		} catch (NullPointerException e1) {
			e1.printStackTrace();
		}
        
    	BufferedReader br = null;
    	try {
            br = new BufferedReader(new FileReader(csvListOfFiles[0].getPath()));
	        String line = ""; 
	        double totalFat = 0;
	        double twoRLFat = 0;
	        double totalProtein = 0;
	        double twoRLProtein = 0;
	        double totalFreezingPointmC = 0;
	        double twoRLFreezingPointmC = 0;
	        double totalLactose = 0;
	        double twoRLLactose = 0;
	        double totalSnf = 0;
	        double twoRLSnf = 0;
	        double totalTs = 0;
	        double twoRLTs = 0;
	        double tankFat = 0;
	        double tankProtein = 0;
	        double tankFreezingPointmC = 0;
	        double tankLactose = 0;
	        double tankSnf = 0;
	        double tankTs = 0;
	        int tankLiters = 0;
	        int twoRLliters = 0; 
	        double avgFatDeviation = 0; 
	        
	    	JSONObject jsonObject = new JSONObject();
	    	/*Recupero i nomi dei campi*/
	   	 	String[] fieldsNames = br.readLine().split(separator);
	        while ((line = br.readLine()) != null) {
	        	   String fieldName = "";
	        	   String sampleId = "";
	        	   String liters = "";
	        	   String fat = "";
	        	   String protein = "";
	        	   String freezingPointmC = "";
	        	   String lactose = "";
	        	   String snf = "";
	        	   String ts = "";
	        	   
	       	       /*Recupero i valori dei campi*/
	       	       String[] fieldsValues = line.split(separator);   
	       	       for (int l = 0; l < fieldsValues.length; l++) {
	       	    	   fieldName = fieldsNames[l].replace("\"", "").replace("/", "").replace(" ", "").replaceAll("[^\\p{ASCII}]", "");
	       	    	   if (fieldName.equalsIgnoreCase(sampleIdName)){
		            	   sampleId = fieldsValues[l].replace("\"", "");
		               }
	       	    	   if (fieldName.equalsIgnoreCase(commentsName)){
		            	   liters = fieldsValues[l].replace("\"", "");
		               }
		               if (fieldName.equalsIgnoreCase(fatName)){
		            	   fat = fieldsValues[l].replace("\"", "");
		               }
		               if (fieldName.equalsIgnoreCase(proteinName)){
		            	   protein = fieldsValues[l].replace("\"", "");
		               }
		               if (fieldName.equalsIgnoreCase(freezingPointmCName)){
		            	   freezingPointmC = fieldsValues[l].replace("\"", "");
		               }
		               if (fieldName.equalsIgnoreCase(lactoseName)){
		            	   lactose = fieldsValues[l].replace("\"", "");
		               }
		               if (fieldName.equalsIgnoreCase(snfName)){
		            	   snf = fieldsValues[l].replace("\"", "");
		               }
		               if (fieldName.equalsIgnoreCase(tsName)){
		            	   ts = fieldsValues[l].replace("\"", "");
		               }
	       	    	   jsonObject.put(fieldsNames[l].replace("\"", "").replace("/", "").replace(" ", "").replaceAll("[^\\p{ASCII}]", ""), fieldsValues[l].replace("\"", "").replace(",", "."));
	       	       }
	       	       if (sampleId.equalsIgnoreCase(wholeTankName)){
	       	    	   tankLiters = Integer.parseInt(liters);
	       	    	   tankFat = Double.parseDouble(fat.replace(",", "."));
	       	    	   tankProtein = Double.parseDouble(protein.replace(",", "."));
	       	    	   tankFreezingPointmC = Double.parseDouble(freezingPointmC.replace(",", "."));
	       	    	   tankLactose = Double.parseDouble(lactose.replace(",", "."));
	       	    	   tankSnf = Double.parseDouble(snf.replace(",", "."));
	       	    	   tankTs = Double.parseDouble(ts.replace(",", "."));
	       	       }
       	    	   if (!sampleId.equalsIgnoreCase(wholeTankName)){
	       	    	   totalFat = totalFat + (Integer.parseInt(liters) * Double.parseDouble(fat.replace(",", ".")));
	       	    	   totalProtein = totalProtein + (Integer.parseInt(liters) * Double.parseDouble(protein.replace(",", ".")));
	       	    	   totalFreezingPointmC = totalFreezingPointmC + (Integer.parseInt(liters) * Double.parseDouble(freezingPointmC.replace(",", ".")));
	       	    	   totalLactose = totalLactose + (Integer.parseInt(liters) * Double.parseDouble(lactose.replace(",", ".")));
	       	    	   totalSnf = totalSnf + (Integer.parseInt(liters) * Double.parseDouble(snf.replace(",", ".")));
	       	    	   totalTs = totalTs + (Integer.parseInt(liters) * Double.parseDouble(ts.replace(",", ".")));
	       	    	   twoRLliters = tankLiters;
	       	    	   twoRLFat = tankFat;
	       	    	   twoRLProtein = tankProtein;
	       	    	   twoRLFreezingPointmC = tankFreezingPointmC;
	       	    	   twoRLLactose = tankLactose;
	       	    	   twoRLSnf = tankSnf;
	       	    	   twoRLTs = tankTs;
	       	       }else {
	       	    	   totalFat = 0;
	       	    	   twoRLFat = 0;
	       	    	   totalProtein = 0;
	       	    	   twoRLProtein = 0;
	       	    	   totalFreezingPointmC = 0;
	       	    	   twoRLFreezingPointmC = 0;
	       	    	   totalLactose = 0;
	       	    	   twoRLLactose = 0;
	       	    	   totalSnf = 0;
	       	    	   twoRLSnf = 0;
	       	    	   totalTs = 0;
	       	    	   twoRLTs = 0;
	       	       }
       	    	   if (twoRLliters > 0 && sampleId.equalsIgnoreCase(lastFarmer)) { 
       	    		   jsonObject.put("weightedAvgFat", df.format(totalFat/twoRLliters).replace(",", "."));
       	    		   jsonObject.put("2rlFat", df.format(twoRLFat).replace(",", "."));
       	    		   jsonObject.put("fatDeviation", df.format(Math.abs(twoRLFat - (totalFat/twoRLliters))).replace(",", "."));
       	    		   jsonObject.put("weightedAvgProtein", df.format(totalProtein/twoRLliters).replace(",", "."));
    	    		   jsonObject.put("2rlProtein", df.format(twoRLProtein).replace(",", "."));
    	    		   jsonObject.put("proteinDeviation", df.format(Math.abs(twoRLProtein - (totalProtein/twoRLliters))).replace(",", "."));
       	    		   jsonObject.put("weightedAvgFreezingPoint", df.format(totalFreezingPointmC/twoRLliters).replace(",", "."));
    	    		   jsonObject.put("2rlFreezingPoint", df.format(twoRLFreezingPointmC).replace(",", "."));
    	    		   jsonObject.put("freezingPointDeviation", df.format(Math.abs(twoRLFreezingPointmC - (totalFreezingPointmC/twoRLliters))).replace(",", "."));
    	    		   jsonObject.put("weightedAvgLactose", df.format(totalLactose/twoRLliters).replace(",", "."));
    	    		   jsonObject.put("2rlLactose", df.format(twoRLLactose).replace(",", "."));
    	    		   jsonObject.put("lactoseDeviation", df.format(Math.abs(twoRLLactose - (totalLactose/twoRLliters))).replace(",", "."));
    	    		   jsonObject.put("weightedAvgSNF", df.format(totalSnf/twoRLliters).replace(",", "."));
    	    		   jsonObject.put("2rlSNF", df.format(twoRLSnf).replace(",", "."));
    	    		   jsonObject.put("SNFDeviation", df.format(Math.abs(twoRLSnf - (totalSnf/twoRLliters))).replace(",", "."));
    	    		   jsonObject.put("weightedAvgTS", df.format(totalTs/twoRLliters).replace(",", "."));
    	    		   jsonObject.put("2rlTS", df.format(twoRLTs).replace(",", "."));
    	    		   jsonObject.put("TSDeviation", df.format(Math.abs(twoRLTs - (totalTs/twoRLliters))).replace(",", "."));
       	    	   }else {
       	    		   jsonObject.put("weightedAvgFat", 0);
       	    		   jsonObject.put("2rlFat", 0);
       	    		   jsonObject.put("fatDeviation", 0);
       	    		   jsonObject.put("weightedAvgProtein", 0);
    	    		   jsonObject.put("2rlProtein", 0);
    	    		   jsonObject.put("proteinDeviation", 0);
       	    		   jsonObject.put("weightedAvgFreezingPoint", 0);
 	    		       jsonObject.put("2rlFreezingPoint", 0);
 	    		       jsonObject.put("freezingPointDeviation", 0);
 	    		       jsonObject.put("weightedAvgLactose", 0);
	    		       jsonObject.put("2rlLactose", 0);
	    		       jsonObject.put("lactoseDeviation", 0);
	    		       jsonObject.put("weightedAvgSNF", 0);
	    		       jsonObject.put("2rlSNF", 0);
	    		       jsonObject.put("SNFDeviation", 0);
	    		       jsonObject.put("weightedAvgTS", 0);
	    		       jsonObject.put("2rlTS", 0);
	    		       jsonObject.put("TSDeviation", 0);
       	    	   }
	       	       jsonArray.put(jsonObject);
	    		   jsonObject = new JSONObject();		
	        }
	        
	        br.close();
	   } catch (FileNotFoundException e) {
			e.printStackTrace();
	   }
       return Response.ok(jsonArray.toString().replace("\\", ""), MediaType.APPLICATION_JSON).build();
	}
}

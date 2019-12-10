package com.va;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.va.Departamento;
import com.va.Grupo;
import com.va.Frutales;

/**
 * Hello world!
 *
 */
public class App 
{
	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String FILENAME = "arbolSiembraD3.xlsx";
	
	public static double contadorGeneral = 0;
	public static double areaTotalColombia = 114200000;
	public static double areaTemporal = 0;
	public static double areaTemporalDep = 0;
    /**
     * @param args
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static void main( String[] args ) throws IOException, InvalidFormatException
    {
    	
    	File currentDirFile = new File("..");    
    	String ruta = Paths.get( currentDirFile.getCanonicalPath() ).getParent().toString();    	

        Workbook workbook = WorkbookFactory.create(new File( ruta +fileSeparator+"dataset"+ fileSeparator +FILENAME));
        System.out.println( "Inicio de generación de json!" );
        
        Sheet sheet = workbook.getSheetAt(0);        
        DataFormatter dataFormatter = new DataFormatter();
        
        JSONObject dataJson = new JSONObject();
        dataJson.put("name", "Héctareas sembradas");
            
        
        List<Departamento> departamentos = new ArrayList<Departamento>();
                       
        JSONArray childrenGenerico = new JSONArray();
		
        
        double totalHa = 0F;  
        sheet.forEach( row ->  {
   
        	if( row.getRowNum() > 0) {
	        	String nombreCelda = dataFormatter.formatCellValue(row.getCell(0)).toString();
	        	Optional<Departamento> departamentoOptional = departamentos.stream().filter(p -> p.getNombre().equals( nombreCelda )).findFirst();
	        	Departamento departamentoFind = null;
	        	//System.out.println("departamentoOptional-->"+departamentoOptional.isPresent());
	        	//System.out.println("nombreCelda-->"+nombreCelda);
	        	if( departamentoOptional.isPresent()  ) {        	
	        		departamentoFind = departamentoOptional.get();
	        	}
	        		        		        
	        	if( departamentoFind != null) {
	        
	        		List<Grupo> gruposDep = departamentoFind.getGrupos();
		        			        		        	
	    			String nombreGrupo = dataFormatter.formatCellValue(row.getCell(1)).toString();
	            	Optional<Grupo> grupoOptional = gruposDep.stream().filter(p -> p.getNombre().equals( nombreGrupo )).findFirst();
	            	
	            	Grupo grupoFind = null;
	            	
	            	if(grupoOptional.isPresent()) {	  	            		
	            		grupoFind = grupoOptional.get();
	            		//System.out.println("Nombre Actual grupo -->"+grupoFind.getNombre());
	            	} 
	            		            	
	            	String nombreFrutal = dataFormatter.formatCellValue(row.getCell(2)).toString();
	            	double valorFrutal = Float.parseFloat(dataFormatter.formatCellValue(row.getCell(3)).toString().replace(",","."));
	            	contadorGeneral +=valorFrutal;
	            	Frutales frutal =  new Frutales( nombreFrutal , valorFrutal); 
	            		            	
	            	if( grupoFind == null) {            	
	            		List<Frutales> frutales = new ArrayList<Frutales>();   
	            		frutales.add(frutal);
	            		grupoFind = new Grupo( nombreGrupo , frutales);
	            		gruposDep.add( grupoFind);
	            	}else {          	            		
	            		grupoFind.getFrutales().add(frutal);            		
	            	}
	        	}else {
	        		
	        		String nombreFrutal = dataFormatter.formatCellValue(row.getCell(2)).toString();
	        		double valorFrutal = Float.parseFloat(dataFormatter.formatCellValue(row.getCell(3)).toString().replace(",","."));
	            	Frutales frutal =  new Frutales( nombreFrutal , valorFrutal); 
	            	contadorGeneral +=valorFrutal;
	        		List<Grupo> gruposDep = new ArrayList<Grupo>();
	        		List<Frutales> frutales = new ArrayList<Frutales>();   
	        		frutales.add(frutal);
	        		String nombreGrupo = dataFormatter.formatCellValue(row.getCell(1)).toString();
	        		Grupo grupoFind = new Grupo( nombreGrupo , frutales); 
	        		gruposDep.add(grupoFind);
	        		
	        		String nombreDepartamento = dataFormatter.formatCellValue(row.getCell(0)).toString();
	        		departamentoFind = new Departamento( nombreDepartamento , gruposDep );
	        		
	        		departamentos.add( departamentoFind);
	        	}
        	}                  
        });
        
        
                
        departamentos.forEach(dep->{  
        	JSONObject depJson = new JSONObject();
        	depJson.put("name", dep.getNombre() );
        	
        	
        	JSONArray grupos = new JSONArray();
			
        	areaTemporalDep = 0;
        	dep.getGrupos().forEach(grupo->{
        		JSONObject grupoJson = new JSONObject();
        		grupoJson.put("name", grupo.getNombre() );
        		        		  
        		JSONArray frutalesArray = new JSONArray();
        		areaTemporal = 0;
        		grupo.getFrutales().forEach(frutal->{  
        			double rate = frutal.getValor() / contadorGeneral;
            		JSONObject frutalJson = new JSONObject();
            		frutalJson.put("name", frutal.getNombre() );
            		frutalJson.put("rate", rate);
            		frutalJson.put("value", frutal.getValor());    
            		areaTemporal+=frutal.getValor();
            		frutalesArray.add( frutalJson );
            	});
        	
        		double rateGrupo = areaTemporal / contadorGeneral; 
        		areaTemporalDep +=areaTemporal;
        		grupoJson.put("children", frutalesArray);
        		grupoJson.put("rate", rateGrupo);
        		grupos.add( grupoJson);
        	});
        	double rateDep = areaTemporalDep / contadorGeneral; 
        	depJson.put("children", grupos);
        	depJson.put("rate", rateDep);
                
        	childrenGenerico.add( depJson );
        });

        double porcetanjeTotal = contadorGeneral /areaTotalColombia;
        System.out.println("Valor total = " + porcetanjeTotal);
        dataJson.put("rate", "0.03" );   
        dataJson.put("children", childrenGenerico);
		// try-with-resources statement based on post comment below :)
		try (FileWriter file = new FileWriter(ruta + fileSeparator +"front"+fileSeparator+"assets"+fileSeparator+"data"+fileSeparator+"arbolSiembraD3.json")) {
			file.write(dataJson.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + dataJson);
		}

        // Closing the workbook
        workbook.close();
    }
}

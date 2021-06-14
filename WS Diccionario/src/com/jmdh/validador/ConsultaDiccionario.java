package com.jmdh.validador;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConsultaDiccionario {
	
	
	public static void main(String[] args) {
		
		if (args.length == 0) {
			System.out.println("Falta la palabra como parámetro");
			System.exit(0);
		}
		
		
		
		
		String palabra = args[0];
		
		try {
			
			String json = llamaServicioHTTP(palabra);
			
			System.out.println("JSON RESPUESTA:");
			System.out.println(json);
			
			extraeInforJSON(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void extraeInforJSON (String respuestaHTTP) throws JSONException {
		
	    //Read JSON response and print
		
	    JSONObject jsonRespuesta = new JSONObject(respuestaHTTP);
	    	    
	    String[] nombres = JSONObject.getNames(jsonRespuesta);
	    
	    System.out.println("******** getNames ********");
	    
	    for (String nombre: nombres) {
	    	System.out.print(nombre + ",");
	    }
	    
	    System.out.println("\n***************");
	    
	    
	    
	    /*
		   * noun
		   * 	syn
		   * 	ant
		   * 	rel
		   * verb
		   * 	syn
		   * 	ant
		   * 	rel
		   * 
		   */
	    
	    JSONArray  sinomNombres = ((JSONObject) jsonRespuesta.get("noun")).getJSONArray("syn");
	    
	    System.out.println("Sinnomimos:");
	    System.out.println("    Sustantivos:");

	    for(int i =0; i< sinomNombres.length(); i++) {
	    	System.out.println("        " + sinomNombres.get(i));
	    }
	    
	    sinomNombres = ((JSONObject) jsonRespuesta.get("verb")).getJSONArray("syn");
	    
	    System.out.println("    Verbos:");

	    for(int i = 0; i< sinomNombres.length(); i++) {
	    	System.out.println("        " + sinomNombres.get(i));
	    }
	    
	   	
	   JSONObject obj = (JSONObject) jsonRespuesta.get("verb");
	   
	   if (obj.has("rel")) {
		   sinomNombres = obj.getJSONArray("rel");
		   System.out.println("           Relacionados:");
		   for(int i = 0; i< sinomNombres.length(); i++) {
	    	 System.out.println("             " + sinomNombres.get(i));
	   }
	    }

	}
	
	
	public static String llamaServicioHTTP(String palabra) throws Exception {
		
	     //String url = "http://api.apalabrados.com/api/dictionaries/ES?words=" + palabra;
		 String url = "https://words.bighugelabs.com/api/2/c5ba0873c73451fbe9c06c9d32768000/" + palabra +"/json";
	     URL obj = new URL(url);
	     HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	     // optional default is GET
	     con.setRequestMethod("GET");
	     //add request header
	     con.setRequestProperty("User-Agent", "Mozilla/5.0");
	     int responseCode = con.getResponseCode();

	     if (responseCode != 200){
	    	 System.out.println("Código: " + responseCode);
	    	 switch (responseCode) {
			case 404:
				System.out.println("Palabra no encontrada en el diccionario");
				break;
			case 500:
				System.out.println("Se ha producido alguna de estas situaciones:");
				System.out.println("- Excedido el límite de uso del webservice. No usar la aplaicación hasta pasado un día");
				System.out.println("- Key de uso no activa");
				System.out.println("- No se envió ninguna palabra al webservice");
				System.out.println("- La IP ha sido bloqueada");
		
				break;

			default:
				System.out.println("Se ha producido un error en la respuesta");
			}
	    	
	    	 System.exit(0);
	     }
	     System.out.println("Response Code : " + responseCode);
	     BufferedReader in = new BufferedReader(
	             new InputStreamReader(con.getInputStream()));
	     String inputLine;
	     StringBuffer response = new StringBuffer();
	     
	     while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	     } 
	     
     
	     in.close();
	     
	     return response.toString();
	}
	    
}

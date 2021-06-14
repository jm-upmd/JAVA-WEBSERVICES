package com.jmdh.validador;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConsultaDiccionarioV2 {

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Falta la palabra como parámetro");

			System.exit(0);
		}

		String palabra = args[0];

		try {

			String respuestaHttp = llamaServicioHTTP(palabra);

			System.out.println(respuestaHttp);

			Object jsonObject = creaJSON(respuestaHttp);

			sacaDatosJSON(jsonObject, 0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int nivel = 0;

	// Método recursivo que extrae todos los elementos del JSON
	static void sacaDatosJSON(Object obj, int nivel) throws JSONException {
		nivel ++;
		JSONObject oJSON;
		JSONArray aJSON;
		final int espacios = 4;

		if (obj instanceof JSONObject) {
			oJSON = (JSONObject) obj;

			String[] nombreElementos = JSONObject.getNames(oJSON);
			for (int i = 0; i < nombreElementos.length; i++) { // Etiqueta (key) del objeto
				System.out.println(espacios(nivel* espacios) + etiqueta(nombreElementos[i]));
				if (esObjetoJSON(oJSON, nombreElementos[i])) { // Si es de un Objeto
					sacaDatosJSON(oJSON.getJSONObject(nombreElementos[i]), nivel);
				} else if (esArrayJSON(oJSON, nombreElementos[i])) { // Si es de un Array de Objetos
					JSONArray a = oJSON.getJSONArray(nombreElementos[i]);
					for (int j = 0; j < a.length(); j++) {
						if (esObjetoJSON(a, j))
							sacaDatosJSON(a.getJSONObject(j), nivel);
						else
							System.out.println(espacios(nivel *  espacios + espacios) + a.getString(j));
					}
				} else { // Si es un dato simple
					System.out.println(espacios(nivel *  espacios + espacios) + oJSON.get(nombreElementos[i]));
				}
			}
		} else if (obj instanceof JSONArray) {
			aJSON = (JSONArray) obj;
			System.out.println("**** " + aJSON.toString());
			for (int i = 0; i < aJSON.length(); i++) {
				System.out.print(aJSON.getJSONObject(i).getString("chapter") + ".");
				System.out.print(aJSON.getJSONObject(i).getString("verse") + ": ");
				System.out.println(aJSON.getJSONObject(i).getString("text"));
			}
		}
	}

	/** Informa si una etiqueta de un objeto JSON corresponde a otro objeto JSON **/
	static boolean esArrayJSON(JSONObject obj, String key) {
		@SuppressWarnings("unused")
		JSONArray o;
		try {
			o = obj.getJSONArray(key);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	/** Informa si una etiqueta de un objeto JSON corresponde a un arrayJSON **/

	static boolean esObjetoJSON(JSONObject obj, String key) {
		@SuppressWarnings("unused")
		JSONObject o;
		try {
			o = obj.getJSONObject(key);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	static boolean esObjetoJSON(JSONArray obj, int pos) {
		@SuppressWarnings("unused")
		JSONObject o;
		try {
			o = obj.getJSONObject(pos);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	static String espacios(int n) {

		if (n == 0)
			return "";

		StringBuilder st = new StringBuilder(n);
		for (int i = 0; i < n; i++)
			st.append(" ");
		return st.toString();
	}

	static Object creaJSON(String respuestaHTTP) throws JSONException {

		// Read JSON response and print
		char primerCaracter = respuestaHTTP.charAt(0);

		if (primerCaracter == '{')
			return new JSONObject(respuestaHTTP);
		else if (primerCaracter == '[')
			return new JSONArray(respuestaHTTP);

		return null;
	}

	public static String llamaServicioHTTP(String palabra) throws Exception {

		// String url = "http://api.apalabrados.com/api/dictionaries/ES?words=" + palabra;
		String url = "https://words.bighugelabs.com/api/2/c5ba0873c73451fbe9c06c9d32768000/" + palabra + "/json";
		 //String url = "https://api.setlist.fm/rest/1.0/search/artists?artistName=ALBORAN";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		// Neceario para setlist.fm
		if (url.startsWith("https://api.setlist.fm")) {
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("x-api-key", "2zcWQhXScoh6wpIOn-lF9mI4LGDY4HvcAHj6");
			con.setRequestProperty("Accept-Language", "es");
		}

		int responseCode = con.getResponseCode();

		if (responseCode != 200) {
			System.out.println("Error.  Código: " + responseCode);
			System.out.println("No hay respuesta");
			System.exit(0);
		}
		// System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();

		return response.toString();
	}

	static String etiqueta(String s) {
		return "[" + s + "]";
	}

}

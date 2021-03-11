package com.mclaren.forms.i18ntranslator;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.i18n.I18n;
import com.google.gson.Gson;

@SlingServlet(paths = { "/bin/mclaren/getI18nText" }, methods = { "GET", "POST" }, metatype = true)
public class LanguageModalI18nTextServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(LanguageModalI18nTextServlet.class);
	private Locale[] myLocales;

	public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		LOG.debug("*** doPost");

		// Get the request parameters.
		String basename = request.getParameter("basename");
		String language = request.getParameter("language");
		String textData = request.getParameter("text");

		LOG.debug("doPost request =" + basename + "|" + language + "|" + textData);

		// Get the required resourcebundle and I18n translator.
		Locale locale = new Locale(language);
		ResourceBundle resourceBundle = request.getResourceBundle(basename, locale);
		I18n i18n = new I18n(resourceBundle);

		// Cleanup data input
		if (textData.startsWith("["))
			textData = textData.substring(1, textData.length());
		if (textData.endsWith("]"))
			textData = textData.substring(0, textData.length() - 1);

		JSONArray myJsonArray = new JSONArray();
		ArrayList<String> list = new ArrayList<String>();
		
		// Split the data by comma
		String parts[] = textData.split(",");

		// Iterate the parts and process.
		for (String part : parts) {

			// Split the key and value pair.
			String keydata[] = part.split("=");

			// Trim the parts.
			String strId = keydata[0].trim();
			String strName = keydata[1].trim();

			LOG.debug("doPost strName = " + strName);

			String keyName = "fd_" + strName;
					
			// Translate the display value.
			String translatedText = i18n.get(keyName);
			
			if (translatedText.startsWith("fd_")) {
				translatedText = strName;
			}
			
			LOG.debug("doPost translatedText = " + translatedText);
			
			list.add(strId + "=" + translatedText);
		}
		
        LOG.debug("doPost list = " + list.toString());
        
        String responseData = new Gson().toJson(list);

		LOG.debug("doPost responseData = " + responseData);

		response.setContentType("application/json");
		
		try {
			response.getWriter().write(responseData);
		} catch (IOException e) {
			LOG.error("Error while getting i18n text for component." + e);
		}
	}

	/*
	 * basename = /content/forms/af/mclaren/enquire-230221-moris-pers/jcr:content/
	 * guideContainer/assets/dictionary 
	 * language = 'fr' for French 
	 * textData
	 */
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

		String basename = request.getParameter("basename");
		String language = request.getParameter("language");
		String textData = request.getParameter("text");

		LOG.debug("doGet request =" + basename + "|" + language + "|" + textData);

		Locale locale = new Locale(language);
		ResourceBundle resourceBundle = request.getResourceBundle(basename, locale);
		I18n i18n = new I18n(resourceBundle);

		// Cleanup data input
		if (textData.startsWith("["))
			textData = textData.substring(1, textData.length());
		if (textData.endsWith("]"))
			textData = textData.substring(0, textData.length() - 1);

		String translatedText = i18n.get(textData);

		LOG.debug("doGet translatedText = " + translatedText);

        String responseData = new Gson().toJson(translatedText);

		LOG.debug("doPost responseData = " + responseData);

		response.setContentType("application/json");

		try {
			response.getWriter().write(responseData);
		} catch (IOException e) {
			LOG.error("Error while getting i18n text for component." + e);
		}
	}
}
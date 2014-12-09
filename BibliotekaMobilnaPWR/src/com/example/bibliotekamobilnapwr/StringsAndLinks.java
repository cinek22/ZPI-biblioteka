package com.example.bibliotekamobilnapwr;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StringsAndLinks {
//	public static String COOKIE_STRING = "__utma=84080792.895347480.1394980478.1401651913.1412192255.5; __utmz=84080792.1412192255.5.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); ALEPH_SESSION_ID=";
	public static String COOKIE_STRING = "ALEPH_SESSION_ID=";
	public static String SESSION_ID = "";
	public static String REFERER = "";
	public static final String MAIN_PAGE = "http://aleph.bg.pwr.wroc.pl/F/";
	public static final String LOGIN_ADDRESS = "?func=file&file_name=login-session";
	public static final String SEARCH_TITLE_NOLOGGED = "http://aleph.bg.pwr.wroc.pl/F/?func=find-a&find_code=WSU&request=";
	public static final String SEARCH_AUTHOR_NOLOGGED = "&request_op=AND&find_code=WAU&request=";
	public static final String SEARCH_BASE_NOLOGGED = "&request_op=AND&find_code=TIT&request=&request_op=AND&find_code=WTI&request=&request_op=AND&find_code=WYR&request=&request_op=AND&find_code=WPU&request=&adjacent=N&local_base=";
	public static final String SEARCH_END_NOLOGGED = "&x=0&y=0&filter_code_1=WLN&filter_request_1=&filter_code_2=WYR&filter_request_2=&filter_code_3=WYR&filter_request_3=&filter_code_4=WFT&filter_request_4=&filter_code_5=WSB&filter_request_5=&SHORT_NO_LINES=99999";
	public static final String CHANGE_PASSWORD = "?func=file&file_name=bor-update-password";
	public static final String MY_ACCOUNT = "?func=bor-info";
	public static final String RENT ="?func=bor-loan&adm_library=TUR50";
	public static final String RENT_HISTORY="?func=bor-history-loan&adm_library=TUR50";
	public static final String ORDERS = "?func=bor-hold";
	public static final String LAST_SEARCH  = "?func=history";
	public static final String MESSAGES = "?func=bor-info#messages";
	public static final String FINANCIAL_TRANS = "?func=bor-cash&adm_library=TUR50";
	

	
	
	/*
	 * 
	 * Params
	 * 
	 * */
	
	public static String PARAM_FUNC = "";
	public static String PARAM_DOC_LIBRARY = "";
	public static String PARAM_ADM_DOC_NUMBER = "";
	public static String PARAM_LOGIN_SOURCE = "";
	public static String PARAM_BIB_LIBRARY = "";		
	public static String PARAM_ITEM_SEQUENCE = "";
	public static String PARAM_BIB_REQUEST = "";
	public static String PARAM_PICKUP = "";
	public static String PARAM_FROM = "";
	public static String PARAM_TO = "";
	
	
	public static String SEARCH_URL = "";
	public static String BOOKING_URL = "";
	/*
	 * 
	 * ConfirmationActivity
	 * 
	 * */
	public static String REFERER_CONFIRMATION = "";
	public static String SESSION_CONFIRMATION = "";
	public static String URL_CONFIRMATION = "";
	public static String BUTTON_CONFIRMATION = "";
	
	public static String isLoged = "";
	
	public static String NEW_MY_ACCOUNT = "";
	
	public static void parseLinks(String resp){
		Document document =  Jsoup.parse(resp);
		Elements description = document.select("body table[cellpadding=0] tr.middlebar");
		
		String str = null;
		for (Element desc : description) {
			Element a = desc.select("a").get(3);
			str = a.attr("href").toString();
			NEW_MY_ACCOUNT = str;
		}
	}
	
}
package com.company;
import java.net.*;  //для использования сокетов


public class URLDepthPair {     //Класс для представления пар [URL, depth]

    private int currentDepth;       //поле для глубины
    private String currentURL;      //поле для адреса


    public URLDepthPair(String URL, int depth) {        //конструктор задает текущий url-адрес и глубину
        currentDepth = depth;
        currentURL = URL;
    }

    public String getURL() {
        return currentURL;
    }       //возвращает текущий url-адрес

    public int getDepth() {
        return currentDepth;
    }       //возвращает текущую глубину

    public String toString() {
        String stringDepth = Integer.toString(currentDepth);    //возвращает глубину и url-адрес в виде строки
        return stringDepth + '\t' + currentURL;
    }

    public String getDocPath() {    //Возвращает путь данного url-адреса иначе вывод сообщения об ошибке
        try {
            URL url = new URL(currentURL);
            return url.getPath();
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }

    public String getWebHost() {        //возвращает хост для url-адреса или сообщение при ошибке
        try {
            URL url = new URL(currentURL);
            return url.getHost();
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }


}
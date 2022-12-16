package com.company;
import java.net.*;
import java.util.*;
import java.io.*;

public class Crawlers {


    public static void main(String[] args) {
        args = new String[]{"http://go.com", "20"};

        int depth = 0;      //текущая глубина


        if (args.length != 2) {     //проверяет на корректность длины входных данных
            System.out.println("usage: java Crawler <URL> <depth>");
            System.exit(1);
        }
        else {      //исключение при невыполнении условия
            try {
                depth = Integer.parseInt(args[1]);  //значение глубины из строки в целое значение
            }
            catch (NumberFormatException nfe) {

                System.out.println("usage: java Crawler <URL> <depth>");
                System.exit(1);
            }
        }

        // список для представления ожидающих URL-адресов
        LinkedList<URLDepthPair> pendingURLs = new LinkedList<URLDepthPair>();

        // список для обработанных url-адресов
        LinkedList<URLDepthPair> processedURLs = new LinkedList<URLDepthPair>();

        // Пара URL-адресов с глубиной для представления веб-сайта, который пользователь ввел с глубиной 0
        URLDepthPair currentDepthPair = new URLDepthPair(args[0], 0);

        // добавление url-адреса в список ожидающих
        pendingURLs.add(currentDepthPair);

        // Динамический массив в который добавляется текущий url-адрес
        ArrayList<String> seenURLs = new ArrayList<String>();
        seenURLs.add(currentDepthPair.getURL());

        // проверка каждого адреса пока список ожидающих непустой
        while (pendingURLs.size() != 0) {

            // получение следующего адреса из ожидающих и добавление к обработанным, сохранение его глубины
            URLDepthPair depthPair = pendingURLs.pop();
            processedURLs.add(depthPair);
            int myDepth = depthPair.getDepth();

            // получение всех ссылок с сайта и сохранение в новом списке ссылок
            LinkedList<String> linksList = new LinkedList<String>();
            linksList = Crawlers.getAllLinks(depthPair);

            // проверка глубины данной и заданной
            if (myDepth < depth) {
                // перебор ссылок с сайта
                for (int i=0;i<linksList.size();i++) {
                    String newURL = linksList.get(i);
                    // если ссылка уже встречалась
                    if (seenURLs.contains(newURL)) {
                        continue;
                    }
                    else {
                        //создание новой пары и добавление адреса в списки
                        URLDepthPair newDepthPair = new URLDepthPair(newURL, myDepth + 1);
                        pendingURLs.add(newDepthPair);
                        seenURLs.add(newURL);
                    }
                }
            }
        }
        // вывод обработанных ссылок с глубиной
        Iterator<URLDepthPair> iter = processedURLs.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }
    // метод который принимает пару адрес и глубина и возвращает список
    private static LinkedList<String> getAllLinks(URLDepthPair myDepthPair) {

        // инициализация списка
        LinkedList<String> URLs = new LinkedList<String>();

        // инициализация сокета
        Socket sock;

        // создание нового сокета с адресом, парой и портом 80
        try {
            sock = new Socket(myDepthPair.getWebHost(), 80);
        }

        catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
            return URLs;
        }
        // возвращает пустой список
        catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
            return URLs;
        }

        // Установка времени ожидания сокета
        try {
            sock.setSoTimeout(3000);
        }
        catch (SocketException exc) {
            System.err.println("SocketException: " + exc.getMessage());
            return URLs;
        }

        // строки для пути адреса из пары и для хоста
        String docPath = myDepthPair.getDocPath();
        String webHost = myDepthPair.getWebHost();

        // Инициализация OutputStream позволяет сокету отправлять данные на другую стороны соединения
        OutputStream outStream;


        try {
            outStream = sock.getOutputStream();
        }

        catch (IOException exce) {
            System.err.println("IOException: " + exce.getMessage());
            return URLs;
        }

        // инициализация PrintWriter, сброс после каждого вывода
        PrintWriter myWriter = new PrintWriter(outStream, true);

        // Отправка запроса на сервер
        myWriter.println("GET " + docPath + "HTTP/1.1");
        myWriter.println("Host: " + webHost);
        myWriter.println("Connection: close");
        myWriter.println();

        // Инициализация InputStream, позволяет получать данные с другой стороны
        InputStream inStream;


        try {
            inStream = sock.getInputStream();
        }

        catch (IOException excep){
            System.err.println("IOException: " + excep.getMessage());
            return URLs;
        }
        // Создание новых InputStreamReader и BufferedReader для чтения строк с сервера
        InputStreamReader inStreamReader = new InputStreamReader(inStream);
        BufferedReader BuffReader = new BufferedReader(inStreamReader);



        //чтение строк
        while (true) {
            String line;
            try {
                line = BuffReader.readLine();
            }

            catch (IOException except) {
                System.err.println("IOException: " + except.getMessage());
                return URLs;
            }
            // строки закончились
            if (line == null)
                break;


            // переменные начального, конечного и текущего индекса ссылки
            int beginIndex = 0;
            int endIndex = 0;
            int index = 0;

            while (true) {

                //константа для строки указывающей на ссылку
                String URL_INDICATOR = "href=\"";

                //строка указывающая конец хоста
                String END_URL = "\"";


                // индекс начала ссылки
                index = line.indexOf(URL_INDICATOR, index);
                if (index == -1)
                    break;

                // изменение текущего индекса и задание начального индекса
                index += URL_INDICATOR.length();
                beginIndex = index;

                // нахождение конца хоста(веб-узла) и присвоение текущему индексу значение конечного
                endIndex = line.indexOf(END_URL, index);
                index = endIndex;

                // установка ссылки меду начальным и конечным индексом и добавление адреса в список
                String newLink = line.substring(beginIndex, endIndex);
                URLs.add(newLink);
            }

        }
        // возвращение списка
        return URLs;
    }

}

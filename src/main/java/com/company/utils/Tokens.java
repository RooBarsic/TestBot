package com.company.utils;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class for storing and accessing tokens
 *
 * @author Farrukh Karimov
 * */
public class Tokens {

    private String SERVER_PORT = "8080";
    private String PRODUCTION_BOT_TOKEN = "null";
    private String PRODUCTION_BOT_NAME = "null";
    private String TESTING_TELEGRAM_BOT_TOKEN = "null";
    private String TESTING_TELEGRAM_BOT_NAME = "null";
    private String TESTING_MAIL_RU_AGENT_BOT_TOKEN = "null";
    private String TESTING_MAIL_RU_AGENT_BOT_NAME = "null";
    private String TESTING_VK_BOT_TOKEN = "null";
    private String TESTING_VK_BOT_ID = "null";
    private String TESTING_FACEBOOK_BOT_VERIFY_TOKEN = "null";
    private String TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN = "null";
    private String TESTING_FACEBOOK_BOT_BASE_URL = "null";
    private String TELEGRAM_FEEDBACK_GROUP_ID = "null";
    private String TELEGRAM_FEEDBACK_GROUP_LOGIN = "null";
    private String AWS_DB_W3_URL = "null";
    private String AWS_DB_W3_ROOT_USER = "null";
    private String AWS_DB_W3_ROOT_PASSWORD = "null";
    private String APP_SECRET_KEY = "null";
    private String TESTING_TELEGRAM_BOT_HEROKU_URL = "null";
    private String TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL = "null";
    private String TESTING_FACEBOOK_BOT_HEROKU_URL = "null";
    private String PING_DELAY = "null";

    /** add parsing tokens from file or System environments */
    public void addTokens() {
        File directory = new File("./");
        String[] flist = directory.list();
        int flag = 0;
        if (flist == null) {
            System.out.println("Empty directory.");
        }
        else {
            for (int i = 0; i < flist.length; i++) {
                String filename = flist[i];
                if (filename.equalsIgnoreCase(".env")) {
                    flag = 1;
                    break;
                }
            }
        }
        if (flag == 1) {
            try(Scanner scanner = new Scanner(new File (".env"))) {
                while (scanner.hasNextLine()) {
                    String str = scanner.nextLine();
                    String buff[] = str.split("=", 2);
                    if (buff.length == 2)
                        setToken(buff[0], buff[1]);
                    else
                        System.out.println("Error with .env file, wrong token format : " + str);
                }

                System.out.println("ENVS: \n" +
                        "SERVER_PORT = " + SERVER_PORT + "\n" +
                        "PRODUCTION_BOT_TOKEN = " + PRODUCTION_BOT_TOKEN + "\n" +
                        "PRODUCTION_BOT_NAME = " + PRODUCTION_BOT_NAME + "\n" +
                        "TESTING_TELEGRAM_BOT_TOKEN = " + TESTING_TELEGRAM_BOT_TOKEN + "\n" +
                        "TESTING_TELEGRAM_BOT_NAME = " + TESTING_TELEGRAM_BOT_NAME + "\n" +
                        "TESTING_MAIL_RU_AGENT_BOT_TOKEN = " + TESTING_MAIL_RU_AGENT_BOT_TOKEN + "\n" +
                        "TESTING_MAIL_RU_AGENT_BOT_NAME = " + TESTING_MAIL_RU_AGENT_BOT_NAME + "\n" +
                        "TESTING_FACEBOOK_BOT_VERIFY_TOKEN = " + TESTING_FACEBOOK_BOT_VERIFY_TOKEN + "\n" +
                        "TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN = " + TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN + "\n" +
                        "TESTING_FACEBOOK_BOT_BASE_URL = " + TESTING_FACEBOOK_BOT_BASE_URL + "\n" +
                        "TELEGRAM_FEEDBACK_GROUP_ID = " + TELEGRAM_FEEDBACK_GROUP_ID + "\n" +
                        "TELEGRAM_FEEDBACK_GROUP_LOGIN = " + TELEGRAM_FEEDBACK_GROUP_LOGIN + "\n" +
                        "TESTING_FACEBOOK_BOT_BASE_URL = " + TESTING_FACEBOOK_BOT_BASE_URL  + "\n" +
                        "AWS_DB_W3_URL = " + AWS_DB_W3_URL + "\n" +
                        "AWS_DB_W3_ROOT_USER = " + AWS_DB_W3_ROOT_USER + "\n" +
                        "AWS_DB_W3_ROOT_PASSWORD = " + AWS_DB_W3_ROOT_PASSWORD + "\n" +
                        "APP_SECRET_KEY = " + APP_SECRET_KEY + "\n" +
                        "TESTING_TELEGRAM_BOT_HEROKU_URL = " + TESTING_TELEGRAM_BOT_HEROKU_URL + "\n" +
                        "TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL = " + TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL + "\n" +
                        "TESTING_FACEBOOK_BOT_HEROKU_URL = " + TESTING_FACEBOOK_BOT_HEROKU_URL + "\n" +
                        "PING_DELAY = " + PING_DELAY + "\n\n");

/*
					String str1 = scanner.nextLine();
					String buff1[] = str1.split("=", 2);
					//System.out.println(buff1[0]);
					PRODUCTION_BOT_TOKEN = buff1[1];

					String str2 = scanner.nextLine();
					String buff2[] = str2.split("=", 2);
					//System.out.println(buff2[0]);
					PRODUCTION_BOT_NAME = buff2[1];

					String str3 = scanner.nextLine();
					String buff3[] = str3.split("=", 2);
					//System.out.println(buff3[0]);
					TESTING_BOT_TOKEN = buff3[1];

					String str4 = scanner.nextLine();
					String buff4[] = str4.split("=", 2);
					//System.out.println(buff4[0]);
					TESTING_BOT_NAME = buff4[1];

					String str5 = scanner.nextLine();
					String buff5[] = str5.split("=", 2);
					//System.out.println(buff5[0]);
					TESTING_MAIL_RU_AGENT_BOT_TOKEN = buff5[1];

					String str6 = scanner.nextLine();
					String buff6[] = str6.split("=", 2);
					//System.out.println(buff6[0]);
					TESTING_MAIL_RU_AGENT_BOT_NAME = buff6[1];

					String str7 = scanner.nextLine();
					String buff7[] = str7.split("=", 2);
					//System.out.println(buff7[0]);
					TESTING_VK_BOT_TOKEN = buff7[1];

					String str8 = scanner.nextLine();
					String buff8[] = str8.split("=", 2);
					//System.out.println(buff8[0]);
					TESTING_VK_BOT_ID = buff8[1];

					String str9 = scanner.nextLine();
					String buff9[] = str9.split("=", 2);
					//System.out.println(buff9[0]);
					TESTING_FACEBOOK_BOT_VERIFY_TOKEN = buff9[1];

					String str10 = scanner.nextLine();
					String buff10[] = str10.split("=", 2);
					//System.out.println(buff10[0]);
					TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN = buff10[1];

					String str11 = scanner.nextLine();
					String buff11[] = str11.split("=", 2);
					//System.out.println(buff11[0]);
					TESTING_FACEBOOK_BOT_BASE_URL = buff11[1];
					*/


            }
            catch (FileNotFoundException e) {
                System.out.println(" ----------- file not found ------------- ");
                e.printStackTrace();
            }
        }
        else if (flag == 0) {
            SERVER_PORT = System.getenv("PORT");  // heroku gives available port by calling "PORT", not "SERVER_PORT"

            PRODUCTION_BOT_TOKEN = System.getenv("PRODUCTION_BOT_TOKEN");
            PRODUCTION_BOT_NAME = System.getenv("PRODUCTION_BOT_NAME");

            TESTING_TELEGRAM_BOT_TOKEN = System.getenv("TESTING_TELEGRAM_BOT_TOKEN");
            TESTING_TELEGRAM_BOT_NAME = System.getenv("TESTING_TELEGRAM_BOT_NAME");

            TESTING_MAIL_RU_AGENT_BOT_TOKEN = System.getenv("TESTING_MAIL_RU_AGENT_BOT_TOKEN");
            TESTING_MAIL_RU_AGENT_BOT_NAME = System.getenv("TESTING_MAIL_RU_AGENT_BOT_NAME");

            TESTING_VK_BOT_ID = System.getenv("TESTING_VK_BOT_ID");
            TESTING_VK_BOT_TOKEN = System.getenv("TESTING_VK_BOT_TOKEN");

            TESTING_FACEBOOK_BOT_VERIFY_TOKEN = System.getenv("TESTING_FACEBOOK_BOT_VERIFY_TOKEN");
            TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN = System.getenv("TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN");
            TESTING_FACEBOOK_BOT_BASE_URL = System.getenv("TESTING_FACEBOOK_BOT_BASE_URL");

            TELEGRAM_FEEDBACK_GROUP_ID = System.getenv("TELEGRAM_FEEDBACK_GROUP_ID");
            TELEGRAM_FEEDBACK_GROUP_LOGIN = System.getenv("TELEGRAM_FEEDBACK_GROUP_LOGIN");

            // config vars for connecting to remote database in AmazonWebServices
            AWS_DB_W3_URL = System.getenv("AWS_DB_W3_URL");
            AWS_DB_W3_ROOT_USER = System.getenv("AWS_DB_W3_ROOT_USER");
            AWS_DB_W3_ROOT_PASSWORD = System.getenv("AWS_DB_W3_ROOT_PASSWORD");

            APP_SECRET_KEY = System.getenv("APP_SECRET_KEY");
            TESTING_TELEGRAM_BOT_HEROKU_URL = System.getenv("TESTING_TELEGRAM_BOT_HEROKU_URL");
            TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL = System.getenv("TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL");
            TESTING_FACEBOOK_BOT_HEROKU_URL = System.getenv("TESTING_FACEBOOK_BOT_HEROKU_URL");
            PING_DELAY = System.getenv("PING_DELAY");

        }
    }

    private void setToken(@NotNull final String tokenName, @NotNull final String tokenValue) {
        switch(tokenName) {
            case "PORT" :  // because heroku gives available port by calling "PORT", not "SERVER_PORT"
            case "SERVER_PORT" :
                SERVER_PORT = tokenValue;
                break;
            case "PRODUCTION_BOT_TOKEN" :
                PRODUCTION_BOT_TOKEN = tokenValue;
                break;
            case "PRODUCTION_BOT_NAME" :
                PRODUCTION_BOT_NAME = tokenValue;
                break;
            case "TESTING_TELEGRAM_BOT_TOKEN" :
                TESTING_TELEGRAM_BOT_TOKEN = tokenValue;
                break;
            case "TESTING_TELEGRAM_BOT_NAME" :
                TESTING_TELEGRAM_BOT_NAME = tokenValue;
                break;
            case "TESTING_MAIL_RU_AGENT_BOT_TOKEN" :
                TESTING_MAIL_RU_AGENT_BOT_TOKEN = tokenValue;
                break;
            case "TESTING_MAIL_RU_AGENT_BOT_NAME" :
                TESTING_MAIL_RU_AGENT_BOT_NAME = tokenValue;
                break;
            case "TESTING_VK_BOT_TOKEN" :
                TESTING_VK_BOT_TOKEN = tokenValue;
                break;
            case "TESTING_VK_BOT_ID" :
                TESTING_VK_BOT_ID = tokenValue;
                break;
            case "TESTING_FACEBOOK_BOT_VERIFY_TOKEN" :
                TESTING_FACEBOOK_BOT_VERIFY_TOKEN = tokenValue;
                break;
            case "TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN" :
                TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN = tokenValue;
                break;
            case "TESTING_FACEBOOK_BOT_BASE_URL" :
                TESTING_FACEBOOK_BOT_BASE_URL = tokenValue;
                break;
            case "TELEGRAM_FEEDBACK_GROUP_ID" :
                TELEGRAM_FEEDBACK_GROUP_ID = tokenValue;
                break;
            case "TELEGRAM_FEEDBACK_GROUP_LOGIN" :
                TELEGRAM_FEEDBACK_GROUP_LOGIN = tokenValue;
                break;
            case "AWS_DB_W3_URL" :
                AWS_DB_W3_URL = tokenValue;
                break;
            case "AWS_DB_W3_ROOT_USER" :
                AWS_DB_W3_ROOT_USER = tokenValue;
                break;
            case "AWS_DB_W3_ROOT_PASSWORD" :
                AWS_DB_W3_ROOT_PASSWORD = tokenValue;
                break;
            case "APP_SECRET_KEY" :
                APP_SECRET_KEY = tokenValue;
                break;
            case "TESTING_TELEGRAM_BOT_HEROKU_URL" :
                TESTING_TELEGRAM_BOT_HEROKU_URL = tokenValue;
                break;
            case "TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL" :
                TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL = tokenValue;
                break;
            case "TESTING_FACEBOOK_BOT_HEROKU_URL" :
                TESTING_FACEBOOK_BOT_HEROKU_URL = tokenValue;
                break;
            case "PING_DELAY" :
                PING_DELAY = tokenValue;
                break;
        }
    }

    /** find and return the needed token */
    public String getTokens(String token) {
        String temp = "null";
        switch (token) {
            case "PORT" : // because heroku gives available port by calling "PORT", not "SERVER_PORT"
            case "SERVER_PORT" :
                temp = SERVER_PORT;
                break;
            case "PRODUCTION_BOT_TOKEN" :
                temp = PRODUCTION_BOT_TOKEN;
                break;
            case "PRODUCTION_BOT_NAME" :
                temp = PRODUCTION_BOT_NAME;
                break;
            case "TESTING_TELEGRAM_BOT_TOKEN" :
                temp = TESTING_TELEGRAM_BOT_TOKEN;
                break;
            case "TESTING_TELEGRAM_BOT_NAME" :
                temp = TESTING_TELEGRAM_BOT_NAME;
                break;
            case "TESTING_MAIL_RU_AGENT_BOT_TOKEN" :
                temp = TESTING_MAIL_RU_AGENT_BOT_TOKEN;
                break;
            case "TESTING_MAIL_RU_AGENT_BOT_NAME" :
                temp = TESTING_MAIL_RU_AGENT_BOT_NAME;
                break;
            case "TESTING_VK_BOT_TOKEN" :
                temp = TESTING_VK_BOT_TOKEN;
                break;
            case "TESTING_VK_BOT_ID" :
                temp = TESTING_VK_BOT_ID;
                break;
            case "TESTING_FACEBOOK_BOT_VERIFY_TOKEN" :
                temp = TESTING_FACEBOOK_BOT_VERIFY_TOKEN;
                break;
            case "TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN" :
                temp = TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN;
                break;
            case "TESTING_FACEBOOK_BOT_BASE_URL" :
                temp = TESTING_FACEBOOK_BOT_BASE_URL;
                break;
            case "TELEGRAM_FEEDBACK_GROUP_ID" :
                temp = TELEGRAM_FEEDBACK_GROUP_ID;
                break;
            case "TELEGRAM_FEEDBACK_GROUP_LOGIN" :
                temp = TELEGRAM_FEEDBACK_GROUP_LOGIN;
                break;
            case "AWS_DB_W3_URL" :
                temp = AWS_DB_W3_URL;
                break;
            case "AWS_DB_W3_ROOT_USER" :
                temp = AWS_DB_W3_ROOT_USER;
                break;
            case "AWS_DB_W3_ROOT_PASSWORD" :
                temp = AWS_DB_W3_ROOT_PASSWORD;
                break;
            case "APP_SECRET_KEY" :
                temp = APP_SECRET_KEY;
                break;
            case "TESTING_TELEGRAM_BOT_HEROKU_URL" :
                temp = TESTING_TELEGRAM_BOT_HEROKU_URL;
                break;
            case "TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL" :
                temp = TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL;
                break;
            case "TESTING_FACEBOOK_BOT_HEROKU_URL" :
                temp = TESTING_FACEBOOK_BOT_HEROKU_URL;
                break;
            case "PING_DELAY" :
                temp = PING_DELAY;
                break;
        }
        return temp;
    }

    /**Will print values for all tokens that have*/
    public void showTokens() {
        System.out.println("token :::: SERVER_PORT = " + SERVER_PORT);
        System.out.println("token :::: PRODUCTION_BOT_TOKEN = " + PRODUCTION_BOT_TOKEN);
        System.out.println("token :::: PRODUCTION_BOT_NAME = " + PRODUCTION_BOT_NAME);
        System.out.println("token :::: TESTING_TELEGRAM_BOT_TOKEN = " + TESTING_TELEGRAM_BOT_TOKEN);
        System.out.println("token :::: TESTING_TELEGRAM_BOT_NAME = " + TESTING_TELEGRAM_BOT_NAME);
        System.out.println("token :::: TESTING_MAIL_RU_AGENT_BOT_TOKEN = " + TESTING_MAIL_RU_AGENT_BOT_TOKEN);
        System.out.println("token :::: TESTING_MAIL_RU_AGENT_BOT_NAME = " + TESTING_MAIL_RU_AGENT_BOT_NAME);
        System.out.println("token :::: TESTING_VK_BOT_TOKEN = " + TESTING_VK_BOT_TOKEN);
        System.out.println("token :::: TESTING_VK_BOT_ID = " + TESTING_VK_BOT_ID);
        System.out.println("token :::: TESTING_FACEBOOK_BOT_VERIFY_TOKEN = " + TESTING_FACEBOOK_BOT_VERIFY_TOKEN);
        System.out.println("token :::: TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN = " + TESTING_FACEBOOK_BOT_PAGE_ACCESS_TOKEN);
        System.out.println("token :::: TESTING_FACEBOOK_BOT_BASE_URL = " + TESTING_FACEBOOK_BOT_BASE_URL);
        System.out.println("token :::: TELEGRAM_FEEDBACK_GROUP_ID = " + TELEGRAM_FEEDBACK_GROUP_ID);
        System.out.println("token :::: TELEGRAM_FEEDBACK_GROUP_LOGIN = " + TELEGRAM_FEEDBACK_GROUP_LOGIN);

        System.out.println("token :::: AWS_DB_W3_URL = " + AWS_DB_W3_URL);
        System.out.println("token :::: AWS_DB_W3_ROOT_USER = " + AWS_DB_W3_ROOT_USER);
        System.out.println("token :::: AWS_DB_W3_ROOT_PASSWORD = " + AWS_DB_W3_ROOT_PASSWORD);

        System.out.println("token :::: APP_SECRET_KEY = " + APP_SECRET_KEY);
        System.out.println("token :::: TESTING_TELEGRAM_BOT_HEROKU_URL = " + TESTING_TELEGRAM_BOT_HEROKU_URL);
        System.out.println("token :::: TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL = " + TESTING_MAIL_RU_AGENT_BOT_HEROKU_URL);
        System.out.println("token :::: TESTING_FACEBOOK_BOT_HEROKU_URL = " + TESTING_FACEBOOK_BOT_HEROKU_URL);
        System.out.println("token :::: PING_DELAY =  " + PING_DELAY);
    }
}

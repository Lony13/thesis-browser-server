package com.koczy.kurek.mizera.thesisbrowser.model;

public class Constants {
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5*60*60;
    public static final int SCRAPER_TIMEOUT = 30*1000;
    public static final String SIGNING_KEY = "devglan123r";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "scopes";

    public static final long LDA_SEED = 100L;
    public static final int LDA_NUM_ITERATION = 10;
    public static final String LDA_VOCAB_PATH = "src/main/resources/vocab.kos.txt";
    public static final int LDA_NUM_TOPICS = 10;
}
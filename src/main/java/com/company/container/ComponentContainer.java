package com.company.container;

import com.company.bot.MyBot;

import java.util.HashMap;
import java.util.Map;

public interface ComponentContainer {
    String BOT_TOKEN = "";//bot's token here
    String BOT_USERNAME = "";//bot's username here
    MyBot MY_BOT = new MyBot();
    Map<Long, String> adminStatus = new HashMap<>();
    Map<Long, String> convertFromTo = new HashMap<>();


}

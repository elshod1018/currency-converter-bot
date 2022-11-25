package com.company.container;

import com.company.bot.MyBot;

import java.util.HashMap;
import java.util.Map;

public interface ComponentContainer {
    String BOT_TOKEN = "5918964524:AAH8tUqdqeXC24PP6sB01BrISfONahzCieE";
    String BOT_USERNAME = "currency1_converterBot";
    MyBot MY_BOT=new MyBot();
    Map<Long, String > adminStatus=new HashMap<>();
    Map<Long, String> convertFromTo=new HashMap<>();



}

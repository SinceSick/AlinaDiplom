package com.example.alinadiplom

import com.example.alinadiplom.database.Analysis

object MainAnalyzes {
    val list: ArrayList<Analysis> = arrayListOf(
        Analysis("WBC",4.5f,11f,"Лейкоциты"),
        Analysis("RBC",3.8f,5.8f,"Эритроциты"),
        Analysis("HGB",126f,174f,"Гемоглобин"),
        Analysis("HCT",0.36f,0.48f,"Гематокрит"),
        //Analysis("HCT%",37f,51f,"Гематокрит"),
        Analysis("MCV",81f,102f,"Средний.объем.эритроцитов"),
        Analysis("MCH",27f,34f,"Содержание гемоглобина в эритроците"),
        Analysis("MCHC",320f,360f,"Концентрация гемоглобина в эритроците"),
        Analysis("PLT",0f,3f,"Тромбоциты"),
        //Analysis("RDW-SD",0f,1f),
        //Analysis("RDW-CV",0f,1f),
        //Analysis("PDW",0f,1f),
        Analysis("MPV",0f,3f,"Средний.объем.тромбоцитов"),
        //Analysis("P-LCR",0f,1f),
        Analysis("PCT",0.108f,0.282f,"Тромбокрит"),
        Analysis("NEUT",1.8f,7.7f,"Нейтрофилы"),
        Analysis("LYMPH",1f,3f,"Лимфоциты"),
        Analysis("MONO",1f,4f,"Моноциты"),
        Analysis("EO",1f,4f,"Эозинофилы"),
        Analysis("BASO",1f,4f,"Базофилы")
    )
}
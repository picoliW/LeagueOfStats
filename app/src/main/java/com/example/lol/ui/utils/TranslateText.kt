package com.example.lol.ui.utils

import com.example.lol.BuildConfig
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions


fun translateText(text: String, targetLanguage: String): String {

    val apiKey = BuildConfig.GOOGLE_API_KEY

    val translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().service

    val translation = translate.translate(
        text,
        Translate.TranslateOption.targetLanguage(targetLanguage)
    )
    return translation.translatedText
}
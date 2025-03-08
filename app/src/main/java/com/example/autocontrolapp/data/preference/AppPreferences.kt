package com.example.autocontrolapp.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AppPreferences(context: Context) {
    
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    
    // Configurações gerais
    var isFirstLaunch: Boolean by BooleanPreference(prefs, KEY_FIRST_LAUNCH, true)
    var temaDark: Boolean by BooleanPreference(prefs, KEY_TEMA_DARK, false)
    var notificacoesAtivas: Boolean by BooleanPreference(prefs, KEY_NOTIFICACOES_ATIVAS, true)
    var intervaloPadrao: Int by IntPreference(prefs, KEY_INTERVALO_PADRAO, 7) // em dias
    var ultimaSincronizacao: Long by LongPreference(prefs, KEY_ULTIMA_SINCRONIZACAO, 0L)
    var idUsuario: String? by StringPreference(prefs, KEY_ID_USUARIO, null)
    var nomeUsuario: String? by StringPreference(prefs, KEY_NOME_USUARIO, null)
    
    // Preferências de notificação
    var horaNotificacaoDiaria: Int by IntPreference(prefs, KEY_HORA_NOTIFICACAO, 9) // 9h por padrão
    var minutoNotificacaoDiaria: Int by IntPreference(prefs, KEY_MINUTO_NOTIFICACAO, 0)
    var notificacaoSom: Boolean by BooleanPreference(prefs, KEY_NOTIFICACAO_SOM, true)
    var notificacaoVibracao: Boolean by BooleanPreference(prefs, KEY_NOTIFICACAO_VIBRACAO, true)
    
    // Configurações de monitoramento
    var monitoramentoAuto: Boolean by BooleanPreference(prefs, KEY_MONITORAMENTO_AUTO, true)
    var lembreteVerificacao: Boolean by BooleanPreference(prefs, KEY_LEMBRETE_VERIFICACAO, true)
    
    // Métodos auxiliares
    fun limparPreferencias() {
        prefs.edit {
            clear()
        }
    }
    
    fun reiniciarPreferencias() {
        isFirstLaunch = true
        temaDark = false
        notificacoesAtivas = true
        intervaloPadrao = 7
        ultimaSincronizacao = 0L
        idUsuario = null
        nomeUsuario = null
        horaNotificacaoDiaria = 9
        minutoNotificacaoDiaria = 0
        notificacaoSom = true
        notificacaoVibracao = true
        monitoramentoAuto = true
        lembreteVerificacao = true
    }
    
    companion object {
        // Chaves das preferências
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_TEMA_DARK = "tema_dark"
        private const val KEY_NOTIFICACOES_ATIVAS = "notificacoes_ativas"
        private const val KEY_INTERVALO_PADRAO = "intervalo_padrao"
        private const val KEY_ULTIMA_SINCRONIZACAO = "ultima_sincronizacao"
        private const val KEY_ID_USUARIO = "id_usuario"
        private const val KEY_NOME_USUARIO = "nome_usuario"
        private const val KEY_HORA_NOTIFICACAO = "hora_notificacao"
        private const val KEY_MINUTO_NOTIFICACAO = "minuto_notificacao"
        private const val KEY_NOTIFICACAO_SOM = "notificacao_som"
        private const val KEY_NOTIFICACAO_VIBRACAO = "notificacao_vibracao"
        private const val KEY_MONITORAMENTO_AUTO = "monitoramento_auto"
        private const val KEY_LEMBRETE_VERIFICACAO = "lembrete_verificacao"
    }
}

// Classes delegadas para propriedades de preferências
class BooleanPreference(private val preferences: SharedPreferences, private val name: String, private val defaultValue: Boolean) : ReadWriteProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.edit { putBoolean(name, value) }
    }
}

class StringPreference(private val preferences: SharedPreferences, private val name: String, private val defaultValue: String?) : ReadWriteProperty<Any, String?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.edit { putString(name, value) }
    }
}

class IntPreference(private val preferences: SharedPreferences, private val name: String, private val defaultValue: Int) : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.getInt(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        preferences.edit { putInt(name, value) }
    }
}

class LongPreference(private val preferences: SharedPreferences, private val name: String, private val defaultValue: Long) : ReadWriteProperty<Any, Long> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.edit { putLong(name, value) }
    }
}
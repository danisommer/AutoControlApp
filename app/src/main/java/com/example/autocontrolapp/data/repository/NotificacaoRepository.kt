package com.example.autocontrolapp.data.repository

import com.example.autocontrolapp.data.database.dao.NotificacaoDao
import com.example.autocontrolapp.data.database.entity.Notificacao
import java.util.*

class NotificacaoRepository(
    private val notificacaoDao: NotificacaoDao
) {
    fun obterTodasNotificacoes() = notificacaoDao.getAll()

    suspend fun obterNotificacaoPorId(id: Long) = notificacaoDao.getById(id)
    
    suspend fun obterNotificacoesAtivas() = notificacaoDao.getAtivas()
    
    suspend fun obterNotificacoesNaoLidas() = notificacaoDao.getNaoLidas()
    
    suspend fun obterNotificacoesPorCategoria(categoriaId: Long) = 
        notificacaoDao.getByCategoria(categoriaId)

    suspend fun adicionarNotificacao(notificacao: Notificacao): Long {
        return notificacaoDao.insert(notificacao)
    }

    suspend fun atualizarNotificacao(notificacao: Notificacao) {
        notificacaoDao.update(notificacao)
    }

    suspend fun excluirNotificacao(notificacao: Notificacao) {
        notificacaoDao.delete(notificacao)
    }
    
    suspend fun marcarComoLida(id: Long) {
        notificacaoDao.marcarComoLida(id, Date())
    }
    
    suspend fun marcarTodasComoLidas() {
        notificacaoDao.marcarTodasComoLidas(Date())
    }
    
    suspend fun agendarNotificacao(categoriaId: Long, data: Date, mensagem: String) {
        val notificacao = Notificacao(
            categoriaId = categoriaId,
            dataAgendamento = data,
            mensagem = mensagem,
            lida = false,
            dataLeitura = null
        )
        notificacaoDao.insert(notificacao)
    }
}
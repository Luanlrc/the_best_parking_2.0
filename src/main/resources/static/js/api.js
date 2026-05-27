/*
 * api.js — camada de comunicação com o backend REST.
 * Tudo que fala com a API passa por aqui. O resto do app só chama essas funções.
 */

const API_BASE = ''; // mesma origem (Spring serve o front e a API juntos)

async function request(method, path, body) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' },
    };
    if (body !== undefined) options.body = JSON.stringify(body);

    const res = await fetch(API_BASE + path, options);

    // 204 No Content (ex: DELETE) não tem body
    if (res.status === 204) return null;

    let data = null;
    try { data = await res.json(); } catch (_) { /* sem body */ }

    if (!res.ok) {
        // o GlobalExceptionHandler do backend devolve { message, ... }
        const msg = (data && data.message) ? data.message : `Erro ${res.status}`;
        throw new Error(msg);
    }
    return data;
}

const api = {
    // Clientes
    listarClientes:   ()       => request('GET', '/api/clientes'),
    criarCliente:     (dto)    => request('POST', '/api/clientes', dto),
    atualizarCliente: (id,dto) => request('PUT', `/api/clientes/${id}`, dto),
    deletarCliente:   (id)     => request('DELETE', `/api/clientes/${id}`),

    // Tipos de automóvel
    listarTipos:   ()       => request('GET', '/api/tipos-automovel'),
    criarTipo:     (dto)    => request('POST', '/api/tipos-automovel', dto),
    atualizarTipo: (id,dto) => request('PUT', `/api/tipos-automovel/${id}`, dto),
    deletarTipo:   (id)     => request('DELETE', `/api/tipos-automovel/${id}`),

    // Automóveis
    listarAutomoveis:   ()       => request('GET', '/api/automoveis'),
    criarAutomovel:     (dto)    => request('POST', '/api/automoveis', dto),
    atualizarAutomovel: (id,dto) => request('PUT', `/api/automoveis/${id}`, dto),
    deletarAutomovel:   (id)     => request('DELETE', `/api/automoveis/${id}`),

    // Vagas
    listarVagas:      ()       => request('GET', '/api/vagas'),
    listarVagasLivres:()       => request('GET', '/api/vagas/livres'),
    criarVaga:        (dto)    => request('POST', '/api/vagas', dto),
    atualizarVaga:    (id,dto) => request('PUT', `/api/vagas/${id}`, dto),
    deletarVaga:      (id)     => request('DELETE', `/api/vagas/${id}`),

    // Movimentações
    listarMovimentacoes: ()    => request('GET', '/api/movimentacoes'),
    listarAbertas:       ()    => request('GET', '/api/movimentacoes/abertas'),
    checkin:  (dto) => request('POST', '/api/movimentacoes/checkin', dto),
    checkout: (dto) => request('POST', '/api/movimentacoes/checkout', dto),
};

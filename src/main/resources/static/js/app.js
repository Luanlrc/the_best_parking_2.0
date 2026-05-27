/*
 * app.js — lógica da SPA: navegação, render das tabelas, CRUD via modal,
 * check-in/check-out e dashboard. Consome a API via o objeto `api` (api.js).
 */

// ====================== UTILIDADES ======================

const $  = (sel, ctx = document) => ctx.querySelector(sel);
const $$ = (sel, ctx = document) => [...ctx.querySelectorAll(sel)];

function toast(message, type = 'info') {
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.textContent = message;
    $('#toast-container').appendChild(el);
    setTimeout(() => el.remove(), 3500);
}

function fmtMoney(v) {
    if (v === null || v === undefined) return '—';
    return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function fmtDateTime(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    return d.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' });
}

function tempoDecorrido(iso) {
    if (!iso) return '—';
    const ms = Date.now() - new Date(iso).getTime();
    const min = Math.max(0, Math.floor(ms / 60000));
    const h = Math.floor(min / 60);
    const m = min % 60;
    return h > 0 ? `${h}h ${m}min` : `${m}min`;
}

function escapeHtml(s) {
    if (s === null || s === undefined) return '';
    return String(s).replace(/[&<>"']/g, c => (
        { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]
    ));
}

// ====================== NAVEGAÇÃO ======================

const VIEW_TITLES = {
    dashboard: 'Dashboard',
    patio: 'Pátio (entrada/saída)',
    clientes: 'Clientes',
    automoveis: 'Automóveis',
    vagas: 'Vagas',
    tipos: 'Tipos de automóvel',
};

// botão de ação no topo (ex: "+ Novo cliente") por view
const TOPBAR_ACTIONS = {
    clientes:   { label: '+ Novo cliente',   handler: () => openClienteModal() },
    automoveis: { label: '+ Novo automóvel', handler: () => openAutomovelModal() },
    vagas:      { label: '+ Nova vaga',      handler: () => openVagaModal() },
    tipos:      { label: '+ Novo tipo',      handler: () => openTipoModal() },
};

function switchView(view) {
    $$('.nav-item').forEach(n => n.classList.toggle('active', n.dataset.view === view));
    $$('.view').forEach(v => v.hidden = (v.id !== `view-${view}`));
    $('#view-title').textContent = VIEW_TITLES[view] || view;

    const action = TOPBAR_ACTIONS[view];
    const btn = $('#topbar-action');
    if (action) {
        btn.hidden = false;
        btn.textContent = action.label;
        btn.onclick = action.handler;
    } else {
        btn.hidden = true;
    }

    loadView(view);
}

function loadView(view) {
    switch (view) {
        case 'dashboard':  renderDashboard(); break;
        case 'patio':      renderPatio(); break;
        case 'clientes':   renderClientes(); break;
        case 'automoveis': renderAutomoveis(); break;
        case 'vagas':      renderVagas(); break;
        case 'tipos':      renderTipos(); break;
    }
}

// ====================== MODAL GENÉRICO ======================

let modalSubmitHandler = null;

function openModal(title, fieldsHtml, onSubmit) {
    $('#modal-title').textContent = title;
    $('#modal-form').innerHTML = fieldsHtml + `
        <div class="modal-actions">
            <button type="button" class="btn btn-ghost" id="modal-cancel">Cancelar</button>
            <button type="submit" class="btn btn-primary">Salvar</button>
        </div>`;
    $('#modal-backdrop').hidden = false;
    modalSubmitHandler = onSubmit;
    $('#modal-cancel').onclick = closeModal;
}

function closeModal() {
    $('#modal-backdrop').hidden = true;
    modalSubmitHandler = null;
}

$('#modal-close').onclick = closeModal;
$('#modal-backdrop').onclick = (e) => { if (e.target.id === 'modal-backdrop') closeModal(); };
$('#modal-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!modalSubmitHandler) return;
    const data = Object.fromEntries(new FormData(e.target).entries());
    try {
        await modalSubmitHandler(data);
        closeModal();
    } catch (err) {
        toast(err.message, 'error');
    }
});

// helper de confirmação de exclusão
async function confirmDelete(label, fn) {
    if (!confirm(`Tem certeza que deseja excluir ${label}?`)) return;
    try {
        await fn();
        toast('Excluído com sucesso', 'success');
    } catch (err) {
        toast(err.message, 'error');
    }
}

function tableEmpty(tbody, cols, msg = 'Nenhum registro.') {
    tbody.innerHTML = `<tr class="empty-row"><td colspan="${cols}">${msg}</td></tr>`;
}

// ====================== DASHBOARD ======================

async function renderDashboard() {
    try {
        const [vagas, abertas, automoveis] = await Promise.all([
            api.listarVagas(),
            api.listarAbertas(),
            api.listarAutomoveis(),
        ]);
        const livres = vagas.filter(v => (v.status || '').toUpperCase() === 'LIVRE').length;
        const ocupadas = vagas.length - livres;

        $('#dash-cards').innerHTML = `
            <div class="card"><div class="card-label">Vagas livres</div><div class="card-value accent-green">${livres}</div></div>
            <div class="card"><div class="card-label">Vagas ocupadas</div><div class="card-value accent-red">${ocupadas}</div></div>
            <div class="card"><div class="card-label">Veículos no pátio</div><div class="card-value accent-blue">${abertas.length}</div></div>
            <div class="card"><div class="card-label">Automóveis cadastrados</div><div class="card-value">${automoveis.length}</div></div>
        `;

        const tbody = $('#dash-table tbody');
        if (!abertas.length) { tableEmpty(tbody, 4, 'Nenhum veículo no pátio.'); return; }
        tbody.innerHTML = abertas.map(m => `
            <tr>
                <td><strong>${escapeHtml(m.placa)}</strong></td>
                <td>${escapeHtml(m.numeroVaga)}</td>
                <td>${fmtDateTime(m.entrada)}</td>
                <td>${tempoDecorrido(m.entrada)}</td>
            </tr>`).join('');
    } catch (err) {
        toast(err.message, 'error');
    }
}

// ====================== PÁTIO (check-in / check-out) ======================

async function renderPatio() {
    try {
        const [automoveis, vagasLivres, abertas] = await Promise.all([
            api.listarAutomoveis(),
            api.listarVagasLivres(),
            api.listarAbertas(),
        ]);

        const placasNoPatio = new Set(abertas.map(m => m.placa));
        const foraDoPatio = automoveis.filter(a => !placasNoPatio.has(a.placa));

        // select de placas para check-in (só as que NÃO estão no pátio)
        $('#form-checkin select[name=placa]').innerHTML =
            foraDoPatio.length
                ? foraDoPatio.map(a => `<option value="${escapeHtml(a.placa)}">${escapeHtml(a.placa)} — ${escapeHtml(a.modelo || '')}</option>`).join('')
                : '<option value="">Nenhum automóvel disponível</option>';

        // select de vagas livres
        $('#form-checkin select[name=idVaga]').innerHTML =
            vagasLivres.length
                ? vagasLivres.map(v => `<option value="${v.id}">${escapeHtml(v.numero)}</option>`).join('')
                : '<option value="">Nenhuma vaga livre</option>';

        // select de placas para check-out (só as que ESTÃO no pátio)
        $('#form-checkout select[name=placa]').innerHTML =
            abertas.length
                ? abertas.map(m => `<option value="${escapeHtml(m.placa)}">${escapeHtml(m.placa)} — vaga ${escapeHtml(m.numeroVaga)}</option>`).join('')
                : '<option value="">Nenhum veículo no pátio</option>';

        // tabela de abertas
        const tbody = $('#patio-table tbody');
        if (!abertas.length) { tableEmpty(tbody, 5, 'Nenhuma movimentação aberta.'); return; }
        tbody.innerHTML = abertas.map(m => `
            <tr>
                <td><strong>${escapeHtml(m.placa)}</strong></td>
                <td>${escapeHtml(m.numeroVaga)}</td>
                <td>${fmtDateTime(m.entrada)}</td>
                <td>${tempoDecorrido(m.entrada)}</td>
                <td class="row-actions">
                    <button class="btn btn-icon btn-edit" data-checkout="${escapeHtml(m.placa)}">Saída ⤴</button>
                </td>
            </tr>`).join('');

        $$('#patio-table [data-checkout]').forEach(btn => {
            btn.onclick = () => doCheckout(btn.dataset.checkout);
        });
    } catch (err) {
        toast(err.message, 'error');
    }
}

$('#form-checkin').addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target).entries());
    if (!data.placa || !data.idVaga) { toast('Selecione placa e vaga.', 'error'); return; }
    try {
        await api.checkin({ placa: data.placa, idVaga: Number(data.idVaga) });
        toast(`Entrada registrada: ${data.placa}`, 'success');
        renderPatio();
    } catch (err) {
        toast(err.message, 'error');
    }
});

$('#form-checkout').addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target).entries());
    if (!data.placa) { toast('Selecione a placa.', 'error'); return; }
    doCheckout(data.placa);
});

async function doCheckout(placa) {
    try {
        const mov = await api.checkout({ placa });
        const box = $('#checkout-result');
        box.hidden = false;
        box.innerHTML = `
            Saída registrada para <strong>${escapeHtml(mov.placa)}</strong>.<br>
            Entrada: ${fmtDateTime(mov.entrada)} · Saída: ${fmtDateTime(mov.saida)}<br>
            Valor cobrado: <strong>${fmtMoney(mov.valorCobrado)}</strong>`;
        toast(`Saída registrada: ${placa} — ${fmtMoney(mov.valorCobrado)}`, 'success');
        renderPatio();
    } catch (err) {
        toast(err.message, 'error');
    }
}

// ====================== CLIENTES ======================

async function renderClientes() {
    const tbody = $('#clientes-table tbody');
    try {
        const clientes = await api.listarClientes();
        if (!clientes.length) { tableEmpty(tbody, 5); return; }
        tbody.innerHTML = clientes.map(c => `
            <tr>
                <td>${c.id}</td>
                <td>${escapeHtml(c.nome)}</td>
                <td>${escapeHtml(c.cpf)}</td>
                <td>${escapeHtml(c.telefonePrincipal)}</td>
                <td class="row-actions">
                    <button class="btn btn-icon btn-edit" data-edit="${c.id}">Editar</button>
                    <button class="btn btn-icon btn-del" data-del="${c.id}">Excluir</button>
                </td>
            </tr>`).join('');

        $$('#clientes-table [data-edit]').forEach(b => b.onclick = () => {
            const c = clientes.find(x => x.id == b.dataset.edit);
            openClienteModal(c);
        });
        $$('#clientes-table [data-del]').forEach(b => b.onclick = () => {
            const c = clientes.find(x => x.id == b.dataset.del);
            confirmDelete(`o cliente "${c.nome}"`, () => api.deletarCliente(c.id).then(renderClientes));
        });
    } catch (err) {
        toast(err.message, 'error');
    }
}

function openClienteModal(cliente = null) {
    const editing = !!cliente;
    openModal(editing ? 'Editar cliente' : 'Novo cliente', `
        <label>Nome <input name="nome" required value="${escapeHtml(cliente?.nome)}"></label>
        <label>CPF <input name="cpf" maxlength="14" value="${escapeHtml(cliente?.cpf)}"></label>
        <label>Telefone <input name="telefonePrincipal" value="${escapeHtml(cliente?.telefonePrincipal)}"></label>
    `, async (data) => {
        if (editing) {
            await api.atualizarCliente(cliente.id, data);
            toast('Cliente atualizado', 'success');
        } else {
            await api.criarCliente(data);
            toast('Cliente criado', 'success');
        }
        renderClientes();
    });
}

// ====================== TIPOS ======================

async function renderTipos() {
    const tbody = $('#tipos-table tbody');
    try {
        const tipos = await api.listarTipos();
        if (!tipos.length) { tableEmpty(tbody, 4); return; }
        tbody.innerHTML = tipos.map(t => `
            <tr>
                <td>${t.id}</td>
                <td>${escapeHtml(t.nome)}</td>
                <td>${fmtMoney(t.valorHora)}</td>
                <td class="row-actions">
                    <button class="btn btn-icon btn-edit" data-edit="${t.id}">Editar</button>
                    <button class="btn btn-icon btn-del" data-del="${t.id}">Excluir</button>
                </td>
            </tr>`).join('');

        $$('#tipos-table [data-edit]').forEach(b => b.onclick = () => {
            const t = tipos.find(x => x.id == b.dataset.edit);
            openTipoModal(t);
        });
        $$('#tipos-table [data-del]').forEach(b => b.onclick = () => {
            const t = tipos.find(x => x.id == b.dataset.del);
            confirmDelete(`o tipo "${t.nome}"`, () => api.deletarTipo(t.id).then(renderTipos));
        });
    } catch (err) {
        toast(err.message, 'error');
    }
}

function openTipoModal(tipo = null) {
    const editing = !!tipo;
    openModal(editing ? 'Editar tipo' : 'Novo tipo', `
        <label>Nome <input name="nome" required value="${escapeHtml(tipo?.nome)}"></label>
        <label>Valor por hora (R$) <input name="valorHora" type="number" step="0.01" min="0" required value="${tipo?.valorHora ?? ''}"></label>
    `, async (data) => {
        const dto = { nome: data.nome, valorHora: Number(data.valorHora) };
        if (editing) {
            await api.atualizarTipo(tipo.id, dto);
            toast('Tipo atualizado', 'success');
        } else {
            await api.criarTipo(dto);
            toast('Tipo criado', 'success');
        }
        renderTipos();
    });
}

// ====================== VAGAS ======================

async function renderVagas() {
    const tbody = $('#vagas-table tbody');
    try {
        const vagas = await api.listarVagas();
        if (!vagas.length) { tableEmpty(tbody, 4); return; }
        tbody.innerHTML = vagas.map(v => {
            const livre = (v.status || '').toUpperCase() === 'LIVRE';
            return `
            <tr>
                <td>${v.id}</td>
                <td>${escapeHtml(v.numero)}</td>
                <td><span class="badge ${livre ? 'badge-livre' : 'badge-ocupada'}">${escapeHtml(v.status)}</span></td>
                <td class="row-actions">
                    <button class="btn btn-icon btn-edit" data-edit="${v.id}">Editar</button>
                    <button class="btn btn-icon btn-del" data-del="${v.id}">Excluir</button>
                </td>
            </tr>`;
        }).join('');

        $$('#vagas-table [data-edit]').forEach(b => b.onclick = () => {
            const v = vagas.find(x => x.id == b.dataset.edit);
            openVagaModal(v);
        });
        $$('#vagas-table [data-del]').forEach(b => b.onclick = () => {
            const v = vagas.find(x => x.id == b.dataset.del);
            confirmDelete(`a vaga "${v.numero}"`, () => api.deletarVaga(v.id).then(renderVagas));
        });
    } catch (err) {
        toast(err.message, 'error');
    }
}

function openVagaModal(vaga = null) {
    const editing = !!vaga;
    const status = vaga?.status || 'LIVRE';
    openModal(editing ? 'Editar vaga' : 'Nova vaga', `
        <label>Número <input name="numero" required maxlength="10" value="${escapeHtml(vaga?.numero)}"></label>
        <label>Status
            <select name="status">
                <option value="LIVRE"   ${status === 'LIVRE' ? 'selected' : ''}>LIVRE</option>
                <option value="OCUPADA" ${status === 'OCUPADA' ? 'selected' : ''}>OCUPADA</option>
            </select>
        </label>
    `, async (data) => {
        if (editing) {
            await api.atualizarVaga(vaga.id, data);
            toast('Vaga atualizada', 'success');
        } else {
            await api.criarVaga(data);
            toast('Vaga criada', 'success');
        }
        renderVagas();
    });
}

// ====================== AUTOMÓVEIS ======================

async function renderAutomoveis() {
    const tbody = $('#automoveis-table tbody');
    try {
        const [automoveis, clientes, tipos] = await Promise.all([
            api.listarAutomoveis(), api.listarClientes(), api.listarTipos(),
        ]);
        const clienteNome = id => clientes.find(c => c.id === id)?.nome || '—';
        const tipoNome    = id => tipos.find(t => t.id === id)?.nome || '—';

        if (!automoveis.length) { tableEmpty(tbody, 8); return; }
        tbody.innerHTML = automoveis.map(a => `
            <tr>
                <td>${a.id}</td>
                <td><strong>${escapeHtml(a.placa)}</strong></td>
                <td>${escapeHtml(a.modelo)}</td>
                <td>${escapeHtml(a.cor)}</td>
                <td>${escapeHtml(tipoNome(a.idTipo))}</td>
                <td>${escapeHtml(clienteNome(a.idCliente))}</td>
                <td><span class="badge ${a.mensalista ? 'badge-yes' : 'badge-no'}">${a.mensalista ? 'Sim' : 'Não'}</span></td>
                <td class="row-actions">
                    <button class="btn btn-icon btn-edit" data-edit="${a.id}">Editar</button>
                    <button class="btn btn-icon btn-del" data-del="${a.id}">Excluir</button>
                </td>
            </tr>`).join('');

        $$('#automoveis-table [data-edit]').forEach(b => b.onclick = () => {
            const a = automoveis.find(x => x.id == b.dataset.edit);
            openAutomovelModal(a, clientes, tipos);
        });
        $$('#automoveis-table [data-del]').forEach(b => b.onclick = () => {
            const a = automoveis.find(x => x.id == b.dataset.del);
            confirmDelete(`o automóvel "${a.placa}"`, () => api.deletarAutomovel(a.id).then(renderAutomoveis));
        });
    } catch (err) {
        toast(err.message, 'error');
    }
}

async function openAutomovelModal(automovel = null, clientes = null, tipos = null) {
    // garante listas de clientes e tipos para os selects
    if (!clientes || !tipos) {
        try {
            [clientes, tipos] = await Promise.all([api.listarClientes(), api.listarTipos()]);
        } catch (err) { toast(err.message, 'error'); return; }
    }
    if (!tipos.length) { toast('Cadastre um tipo de automóvel primeiro.', 'error'); return; }

    const editing = !!automovel;
    const optClientes = `<option value="">— sem cliente —</option>` + clientes.map(c =>
        `<option value="${c.id}" ${automovel?.idCliente === c.id ? 'selected' : ''}>${escapeHtml(c.nome)}</option>`).join('');
    const optTipos = tipos.map(t =>
        `<option value="${t.id}" ${automovel?.idTipo === t.id ? 'selected' : ''}>${escapeHtml(t.nome)}</option>`).join('');

    openModal(editing ? 'Editar automóvel' : 'Novo automóvel', `
        <label>Placa <input name="placa" required maxlength="8" value="${escapeHtml(automovel?.placa)}"></label>
        <label>Modelo <input name="modelo" maxlength="80" value="${escapeHtml(automovel?.modelo)}"></label>
        <label>Cor <input name="cor" maxlength="30" value="${escapeHtml(automovel?.cor)}"></label>
        <label>Tipo <select name="idTipo" required>${optTipos}</select></label>
        <label>Cliente <select name="idCliente">${optClientes}</select></label>
        <label class="checkbox-row"><input type="checkbox" name="mensalista" ${automovel?.mensalista ? 'checked' : ''}> Mensalista (não paga por hora)</label>
    `, async (data) => {
        const dto = {
            placa: data.placa,
            modelo: data.modelo || null,
            cor: data.cor || null,
            mensalista: data.mensalista === 'on',
            idTipo: Number(data.idTipo),
            idCliente: data.idCliente ? Number(data.idCliente) : null,
        };
        if (editing) {
            await api.atualizarAutomovel(automovel.id, dto);
            toast('Automóvel atualizado', 'success');
        } else {
            await api.criarAutomovel(dto);
            toast('Automóvel criado', 'success');
        }
        renderAutomoveis();
    });
}

// ====================== BOOT ======================

$$('.nav-item').forEach(item => {
    item.addEventListener('click', () => switchView(item.dataset.view));
});

switchView('dashboard');

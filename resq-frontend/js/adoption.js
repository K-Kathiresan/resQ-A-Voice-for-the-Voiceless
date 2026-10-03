// resQ Adoption module — vanilla JS. Relies on js/api.js for BASE_URL; auth follows the existing
// pattern (token + role in localStorage, "Authorization: Bearer <token>").
(() => {
  'use strict';

  const API = typeof BASE_URL !== 'undefined' ? BASE_URL : 'http://localhost:8080/api';
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');
  if (!token) { location.replace('login.html'); return; }
  const IS_CITIZEN = role === 'CITIZEN';

  const $ = id => document.getElementById(id);
  const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

  const ANIMAL_STATUS = {
    READY_FOR_ADOPTION: { label: 'Available', tone: 'teal' },
    NOT_READY: { label: 'Not ready yet', tone: 'muted' },
    ADOPTED: { label: 'Adopted', tone: 'success' }
  };
  const APP_STATUS = {
    SUBMITTED: { label: 'Submitted', tone: 'info', note: "We've received your application." },
    UNDER_REVIEW: { label: 'Under Review', tone: 'amber', note: 'Our team is reviewing your application.' },
    APPROVED: { label: 'Approved', tone: 'success', note: 'Congratulations, your application was approved.' },
    REJECTED: { label: 'Rejected', tone: 'danger', note: "This application wasn't selected this time." }
  };

  const state = { animals: [], animalsLoaded: false, applied: new Map(), current: null, detailReq: 0 };

  /* ---------- API layer ---------- */
  class ApiError extends Error { constructor(message, status) { super(message); this.status = status; } }

  async function request(path, { method = 'GET', params } = {}) {
    let url = API + path;
    if (params) url += '?' + new URLSearchParams(Object.entries(params).map(([k, v]) => [k, String(v).trim()]));
    let res;
    try { res = await fetch(url, { method, headers: { Authorization: `Bearer ${token}` } }); }
    catch { throw new ApiError('', 0); }
    if (res.status === 401) { localStorage.clear(); location.replace('login.html'); throw new ApiError('', 401); }
    // Controllers return ResponseEntity directly: the body IS the entity / list (no wrapper).
    let body = null;
    const raw = await res.text().catch(() => '');
    try { body = raw ? JSON.parse(raw) : null; } catch { body = raw; } // plain-text errors stay as strings
    if (!res.ok) {
      const msg = typeof body === 'string' ? body : (body && typeof body.message === 'string' ? body.message : '');
      throw new ApiError(msg, res.status);
    }
    return body;
  }

  const api = {
    listAnimals: () => request('/adoptions'),
    getAnimal: id => request(`/adoptions/${encodeURIComponent(id)}`),
    apply: params => request('/adoption-applications/apply', { method: 'POST', params }),
    myApplications: () => request('/adoption-applications/my')
  };

  /* ---------- helpers ---------- */
  const toList = d => (Array.isArray(d) ? d : []);
  const toDate = v => {
    if (!v) return null;
    const d = Array.isArray(v) ? new Date(v[0], v[1] - 1, v[2], v[3] || 0, v[4] || 0) : new Date(v);
    return isNaN(d) ? null : d;
  };
  const formatDate = v => { const d = toDate(v); return d ? d.toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' }) : ''; };
  const title = a => a?.breed || a?.report?.animalType || 'Rescued companion';
  const humanize = s => String(s || '').toLowerCase().replace(/_/g, ' ').replace(/^\w/, c => c.toUpperCase());
  const badge = (map, key) => { const s = map[key] || { label: humanize(key), tone: 'muted' }; return `<span class="ad-badge ad-badge--${s.tone}">${esc(s.label)}</span>`; };
  const scrollToId = id => $(id)?.scrollIntoView({ behavior: matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth' });

  const ICON = {
    heart: '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>',
    alert: '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>',
    home: '<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>'
  };
  const icon = (n, s = 28) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${ICON[n]}</svg>`;

  const media = (url, alt, cls = '') =>
    `<div class="ad-media ${cls}"><div class="ad-media-ph">${icon('home', 40)}</div>${url ? `<img src="${esc(url)}" alt="${esc(alt)}" loading="lazy">` : ''}</div>`;
  // Missing/broken images fall back to the placeholder behind them (no layout shift).
  document.addEventListener('error', e => { if (e.target.tagName === 'IMG' && e.target.closest('.ad-media')) e.target.remove(); }, true);

  const stateBlock = ({ tone = '', ic, heading, text, action = '' }) =>
    `<div class="ad-state ${tone ? 'ad-state--' + tone : ''}"><div class="ad-state-icon">${icon(ic)}</div><h3>${esc(heading)}</h3><p>${esc(text)}</p>${action}</div>`;

  const skeletonCards = n => Array.from({ length: n }, () =>
    '<div class="ad-skel-card" aria-hidden="true"><div class="ad-skel"></div><div class="ad-skel-lines"><div class="ad-skel"></div><div class="ad-skel" style="width:60%"></div></div></div>').join('');

  function toast(message, { type = '', actionLabel, onAction, duration = 6500 } = {}) {
    const el = document.createElement('div');
    el.className = `ad-toast ${type ? 'ad-toast--' + type : ''}`;
    const span = document.createElement('span'); span.textContent = message; el.append(span);
    if (actionLabel) {
      const b = document.createElement('button'); b.type = 'button'; b.textContent = actionLabel;
      b.addEventListener('click', () => { onAction?.(); el.remove(); }); el.append(b);
    }
    $('toasts').append(el);
    setTimeout(() => el.remove(), duration);
  }

  // Never surface raw server/Java text; only short, clean backend messages pass through.
  const isCleanMessage = m => m && m.length <= 160 && !/exception|java\.|org\.|stack|\bnull\b/i.test(m);

  function applyError(err) {
    const m = err.message || '';
    if (err.status === 0) return { kind: 'network', text: "We couldn't reach the server. Check your connection and try again." };
    if (/already (applied|submitted)|duplicate|more than once|twice/i.test(m) || (err.status === 409 && !/availab|adopted/i.test(m)))
      return { kind: 'duplicate', text: "You've already applied for this animal. You can track it under My Applications." };
    if (/availab|no longer|not ready|adopted/i.test(m))
      return { kind: 'unavailable', text: 'Sorry, this animal is no longer available for adoption.' };
    if (err.status === 403) return { kind: 'other', text: "Your account doesn't have permission to apply for adoption." };
    if (err.status >= 500) return { kind: 'other', text: 'Something went wrong on our side. Please try again in a moment.' };
    return { kind: 'other', text: isCleanMessage(m) ? m : "We couldn't submit your application. Please check your answers and try again." };
  }

  /* ---------- animals ---------- */
  function animalCard(a) {
    const kind = a.report?.animalType;
    const applied = state.applied.get(Number(a.id));
    return `<article class="ad-card">
      ${media(a.report?.imageUrl, `Photo of ${title(a)}`)}
      ${kind && kind !== title(a) ? `<span class="ad-media-pill">${esc(kind)}</span>` : ''}
      <div class="ad-card-body">
        <h3 class="ad-card-title">${esc(title(a))}</h3>
        <dl class="ad-facts"><div><dt>Age</dt><dd>${esc(a.age || '—')}</dd></div><div><dt>Gender</dt><dd>${esc(a.gender || '—')}</dd></div></dl>
        <div class="ad-chips">${a.healthStatus ? `<span class="ad-chip">${esc(a.healthStatus)}</span>` : ''}${a.temperament ? `<span class="ad-chip ad-chip--warm">${esc(a.temperament)}</span>` : ''}</div>
        ${a.description ? `<p class="ad-card-desc">${esc(a.description)}</p>` : ''}
        ${applied ? `<div>${badge(APP_STATUS, applied)}</div>` : ''}
        <button class="ad-btn ad-btn--primary" type="button" data-action="view-details" data-id="${esc(a.id)}">View Details</button>
      </div></article>`;
  }

  function renderAnimals() {
    const grid = $('animalsGrid');
    $('animalsCount').textContent = state.animals.length ? `${state.animals.length} waiting` : '';
    grid.innerHTML = state.animals.length
      ? state.animals.map(animalCard).join('')
      : stateBlock({ ic: 'heart', heading: 'No animals are currently looking for a home.', text: 'New companions appear here as soon as they finish their recovery. Please check back soon.' });
  }

  async function loadAnimals({ silent = false } = {}) {
    const grid = $('animalsGrid');
    if (!silent) { grid.innerHTML = skeletonCards(6); grid.setAttribute('aria-busy', 'true'); }
    try {
      state.animals = toList(await api.listAnimals());
      state.animalsLoaded = true;
      renderAnimals();
    } catch (err) {
      if (!silent) grid.innerHTML = stateBlock({ tone: 'error', ic: 'alert', heading: "We couldn't load the animals", text: 'Please check your connection and try again.', action: '<button class="ad-btn ad-btn--ghost" type="button" data-action="retry-animals">Try again</button>' });
    } finally { grid.setAttribute('aria-busy', 'false'); }
  }

  /* ---------- details ---------- */
  function detailCTA(a) {
    if (a.status !== 'READY_FOR_ADOPTION') return '<p class="ad-note">This animal isn\'t open for applications right now.</p>';
    if (!IS_CITIZEN) return '';
    const applied = state.applied.get(Number(a.id));
    if (applied) return `<p class="ad-note">You've already applied for this animal. ${badge(APP_STATUS, applied)}</p>
      <button class="ad-btn ad-btn--ghost" type="button" data-action="goto-apps">View My Applications</button>`;
    return '<button class="ad-btn ad-btn--primary" type="button" data-action="open-apply">Apply for Adoption</button>';
  }

  function detailHTML(a) {
    const fact = (k, v) => `<div><dt>${k}</dt><dd>${esc(v || '—')}</dd></div>`;
    return `<div class="ad-detail">
      ${media(a.report?.imageUrl, `Photo of ${title(a)}`)}
      <div class="ad-detail-info">
        <div>${badge(ANIMAL_STATUS, a.status)}</div>
        <h2>${esc(title(a))}</h2>
        <dl class="ad-facts">${fact('Age', a.age)}${fact('Gender', a.gender)}${fact('Health status', a.healthStatus)}${fact('Temperament', a.temperament)}</dl>
        ${a.description ? `<div><h3>About</h3><p class="ad-muted">${esc(a.description)}</p></div>` : ''}
        ${detailCTA(a)}
      </div></div>`;
  }

  async function openDetails(id) {
    const dlg = $('detailDialog'), body = $('detailBody'), req = ++state.detailReq;
    body.innerHTML = `<div class="ad-detail"><div class="ad-media"><div class="ad-skel" style="position:absolute;inset:0;border-radius:0"></div></div><div class="ad-detail-info"><div class="ad-skel" style="height:28px;width:60%"></div><div class="ad-skel" style="height:90px"></div><div class="ad-skel" style="height:60px"></div></div></div>`;
    if (!dlg.open) dlg.showModal();
    try {
      const a = await api.getAnimal(id);
      if (req !== state.detailReq) return;
      state.current = a;
      body.innerHTML = detailHTML(a);
    } catch {
      if (req !== state.detailReq) return;
      body.innerHTML = `<div style="padding:24px">${stateBlock({ tone: 'error', ic: 'alert', heading: "We couldn't load this animal", text: 'It may have just been adopted. Close this window and try again.', action: '<button class="ad-btn ad-btn--ghost" type="button" data-action="close-dialog">Close</button>' })}</div>`;
    }
  }

  /* ---------- application form ---------- */
  const form = $('applyForm');

  function openApply() {
    const a = state.current; if (!a) return;
    $('detailDialog').close();
    form.reset(); $('reasonCount').textContent = '0';
    form.querySelectorAll('[aria-invalid]').forEach(el => el.removeAttribute('aria-invalid'));
    form.querySelectorAll('.ad-error').forEach(el => { el.textContent = ''; });
    $('applyBanner').hidden = true;
    $('applySubmit').disabled = false;
    $('applySummary').innerHTML = `${media(a.report?.imageUrl, '', 'ad-media--thumb')}<span>Adopting: ${esc(title(a))}</span>`;
    $('applyDialog').showModal();
  }

  function setError(el, msg) {
    if (el.type === 'radio') $('contactPreferenceError').textContent = msg;
    else { el.setAttribute('aria-invalid', msg ? 'true' : 'false'); $(el.id + 'Error').textContent = msg; }
  }

  function validate() {
    let first = null;
    form.querySelectorAll('select[required],textarea[required],input[type=text][required]').forEach(el => {
      const bad = !el.value.trim();
      setError(el, bad ? 'Please fill this in.' : '');
      if (bad && !first) first = el;
    });
    const radios = form.querySelectorAll('input[name=contactPreference]');
    const missing = ![...radios].some(r => r.checked);
    setError(radios[0], missing ? 'Please choose how we should reach you.' : '');
    if (missing && !first) first = radios[0];
    first?.focus();
    return !first;
  }

  async function submitApplication(e) {
    e.preventDefault();
    const btn = $('applySubmit'), banner = $('applyBanner');
    if (btn.disabled || !validate()) return;
    banner.hidden = true;
    btn.disabled = true; btn.classList.add('is-busy'); btn.textContent = 'Submitting…';
    const v = Object.fromEntries(new FormData(form));
    try {
      await api.apply({ animalId: state.current.id, housingType: v.housingType, animalExperience: v.animalExperience, reason: v.reason, otherPets: v.otherPets, contactPreference: v.contactPreference });
      $('applyDialog').close();
      toast("Application submitted. We'll be in touch soon.", { type: 'success', actionLabel: 'View My Applications', onAction: () => scrollToId('applications') });
      await loadMyApplications({ silent: true });
      renderAnimals();
    } catch (err) {
      const { kind, text } = applyError(err);
      banner.textContent = text; banner.hidden = false;
      if (kind === 'duplicate') { await loadMyApplications({ silent: true }); renderAnimals(); }
      if (kind === 'unavailable') { await loadAnimals({ silent: true }); btn.textContent = 'Submit Adoption Application'; btn.classList.remove('is-busy'); return; }
      btn.disabled = false;
    } finally {
      btn.classList.remove('is-busy'); btn.textContent = 'Submit Adoption Application';
    }
  }

  /* ---------- my applications ---------- */
  function appCard(app) {
    const a = app.adoptionAnimal || app.animal || {};
    const st = APP_STATUS[app.status];
    const date = formatDate(app.createdAt);
    return `<article class="ad-app">
      ${media(a.report?.imageUrl, `Photo of ${title(a)}`, 'ad-media--thumb')}
      <div class="ad-app-main"><h3>${esc(title(a))}</h3>
        <p>${date ? `Applied on ${esc(date)}` : ''}</p>${st ? `<p>${esc(st.note)}</p>` : ''}</div>
      <div class="ad-app-side">${badge(APP_STATUS, app.status)}
        ${a.id ? `<button class="ad-btn ad-btn--ghost" type="button" data-action="view-details" data-id="${esc(a.id)}">View animal</button>` : ''}</div>
    </article>`;
  }

  async function loadMyApplications({ silent = false } = {}) {
    if (!IS_CITIZEN) return;
    const list = $('appsList');
    if (!silent) list.innerHTML = skeletonCards(1).replace('ad-skel-card', 'ad-skel-card" style="max-width:100%');
    try {
      const apps = toList(await api.myApplications()).sort((a, b) => (toDate(b.createdAt) || 0) - (toDate(a.createdAt) || 0));
      state.applied = new Map(apps.filter(x => (x.adoptionAnimal || x.animal)?.id != null).map(x => [Number((x.adoptionAnimal || x.animal).id), x.status]));
      list.innerHTML = apps.length ? apps.map(appCard).join('')
        : stateBlock({ ic: 'heart', heading: "You haven't applied yet", text: 'When you apply for an animal, you can follow its progress here.', action: '<a class="ad-btn ad-btn--primary" href="#animals">Find a Companion</a>' });
      if (state.animalsLoaded) renderAnimals();
    } catch {
      if (!silent) list.innerHTML = stateBlock({ tone: 'error', ic: 'alert', heading: "We couldn't load your applications", text: 'Please try again in a moment.', action: '<button class="ad-btn ad-btn--ghost" type="button" data-action="retry-apps">Try again</button>' });
    }
  }

  /* ---------- wiring ---------- */
  document.addEventListener('click', e => {
    const el = e.target.closest('[data-action]');
    if (!el) return;
    switch (el.dataset.action) {
      case 'view-details': $('applyDialog').open || openDetails(el.dataset.id); break;
      case 'open-apply': openApply(); break;
      case 'close-dialog': el.closest('dialog').close(); break;
      case 'goto-apps': $('detailDialog').close(); scrollToId('applications'); break;
      case 'retry-animals': loadAnimals(); break;
      case 'retry-apps': loadMyApplications(); break;
    }
  });

  document.querySelectorAll('dialog').forEach(d => d.addEventListener('click', e => { if (e.target === d) d.close(); }));
  $('applyDialog').addEventListener('close', () => document.querySelector(`[data-action="view-details"][data-id="${state.current?.id}"]`)?.focus());
  form.addEventListener('submit', submitApplication);
  form.addEventListener('input', e => {
    if (e.target.id === 'reason') $('reasonCount').textContent = e.target.value.length;
    if (e.target.value?.trim() || e.target.type === 'radio') setError(e.target, '');
  });

  document.querySelectorAll('[data-role]').forEach(el => { el.hidden = !el.dataset.role.split(' ').includes(role); });
  $('backLink').href = { VOLUNTEER: 'volunteer-dashboard.html', ADMIN: 'admin-dashboard.html' }[role] || 'my-reports.html';
  $('logoutBtn').addEventListener('click', () => { localStorage.clear(); location.href = 'login.html'; });

  loadAnimals();
  loadMyApplications();
})();
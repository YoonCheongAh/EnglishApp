/**
 * --------------------------------------------------------------------------
 * CoreUI Bootstrap Admin Template config.js
 * Licensed under MIT
 * --------------------------------------------------------------------------
 */
(() => {
  const THEME = 'coreui-free-bootstrap-admin-template-theme'
  const urlParams = new URLSearchParams(window.location.href.split('?')[1])

  if (urlParams.get('theme') && ['auto', 'dark', 'light'].includes(urlParams.get('theme'))) {
    localStorage.setItem(THEME, urlParams.get('theme'))
  }
})();

/**
 * --------------------------------------------------------------------------
 *
 * --------------------------------------------------------------------------
 */
window.API_BASE = window.API_BASE || 'http://localhost:5000';

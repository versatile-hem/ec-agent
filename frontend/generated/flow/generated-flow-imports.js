import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/theme/lumo/vaadin-login-form.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '7b9e8774abf41b05b5102fa342a9e563069a97106da2d88a54f793c09f864a33') {
    pending.push(import('./chunks/chunk-b65dbc689c4517be77b3c09c723b6cc5fb3f78cc6a90bd566a3078c8ecfa28bc.js'));
  }
  if (key === '1d68c5c254ee6cf30f0f66d25c77800e8ca297f9920b8f4ed87ad150c0896436') {
    pending.push(import('./chunks/chunk-b740b605f4eb17f387f27f4a6e8201c9b736a75b04aaf11e6592358cf87c506e.js'));
  }
  if (key === '325c90b0e5c05fe4d61dd250318834b7e1ae6aba8c75461a951913dc9f773d33') {
    pending.push(import('./chunks/chunk-351f77fe79e240a6734b80eca9cca6a782ba09870883e900015aec888248e278.js'));
  }
  if (key === 'ecaf98c9dddc7d41a962e963985f3da0c6d29541c9010fef2661e2facbdc390a') {
    pending.push(import('./chunks/chunk-ff28e534aa58e90185586b55a61e05187ffb9dcf1e6476fdb386c5c49fe0bb3d.js'));
  }
  if (key === '3518336327790701705179fd0c876aaa041f812b3f0c6caa8ca5471b657bda58') {
    pending.push(import('./chunks/chunk-351f77fe79e240a6734b80eca9cca6a782ba09870883e900015aec888248e278.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}
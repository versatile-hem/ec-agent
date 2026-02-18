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
  if (key === '3518336327790701705179fd0c876aaa041f812b3f0c6caa8ca5471b657bda58') {
    pending.push(import('./chunks/chunk-72ddee93b75b7ca4f44dbd820b6b52a226dd7046e32bad20927ec7e8d9c17766.js'));
  }
  if (key === '325c90b0e5c05fe4d61dd250318834b7e1ae6aba8c75461a951913dc9f773d33') {
    pending.push(import('./chunks/chunk-72ddee93b75b7ca4f44dbd820b6b52a226dd7046e32bad20927ec7e8d9c17766.js'));
  }
  if (key === '1d68c5c254ee6cf30f0f66d25c77800e8ca297f9920b8f4ed87ad150c0896436') {
    pending.push(import('./chunks/chunk-65272d3e5ba06d27e1f5e6726520bf5823b949fd2e077c5300494ceca4ed7dd2.js'));
  }
  if (key === 'e397f42e5b8c4d94f2c81a462da07e5ac6fd02be25aa2f5ba7c905a094a48e6e') {
    pending.push(import('./chunks/chunk-87b0c2603d7e8b7888729f5fa4e439f37dac3176860f00cd07fec9333531547d.js'));
  }
  if (key === '41d2198a91bdcae19e2b0c31bc27be537f32de2daece49bddf230bea6b94aa9b') {
    pending.push(import('./chunks/chunk-f8a8cd987aff39f8e5c70c37261ad504237f1c62216693f3bfeaedd442eb4033.js'));
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
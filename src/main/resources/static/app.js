/**
 * Communicates with Spring Boot endpoints safely via JSON payloads
 */
async function apiPost(path, body) {
  const opts = { method: 'POST', headers: { 'Accept': 'application/json' } };
  if (body !== null && body !== undefined) {
    opts.headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(body);
  }
  return fetch(path, opts);
}

function fmt(v) { 
  const numericValue = Number(v) || 0;
  return '₱' + numericValue.toFixed(2);
}

function playScanBeep() {
    try {
        const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioCtx.createOscillator();
        const gainNode = audioCtx.createGain();

        oscillator.connect(gainNode);
        gainNode.connect(audioCtx.destination);

        oscillator.type = 'sine'; 
        oscillator.frequency.value = 440; // Frequency in Hz (higher = higher pitch)
        gainNode.gain.setValueAtTime(0.1, audioCtx.currentTime); // Volume control (0.0 to 1.0)

        oscillator.start();
        // Stop the sound after 100 milliseconds
        oscillator.stop(audioCtx.currentTime + 0.1); 
    } catch (e) {
        console.warn("Audio Context failed to play beep: ", e);
    }
}

// Chart.js global theme overrides — use Inter and project palette
(function() {
  if (!window.Chart) return;
  const root = getComputedStyle(document.documentElement);
  const textColor = (root.getPropertyValue('--text') || '#2b2b2b').trim();
  const primary = (root.getPropertyValue('--primary-pink') || '#F4C6D6').trim();
  const primaryDark = (root.getPropertyValue('--dark-pink') || '#D88C9F').trim();

  Chart.defaults.font.family = "'Inter', system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial";
  Chart.defaults.color = textColor;
  if (!Chart.defaults.plugins) Chart.defaults.plugins = {};
  if (!Chart.defaults.plugins.legend) Chart.defaults.plugins.legend = {};
  if (!Chart.defaults.plugins.tooltip) Chart.defaults.plugins.tooltip = {};

  Chart.defaults.plugins.legend.labels = Chart.defaults.plugins.legend.labels || {};
  Chart.defaults.plugins.legend.labels.color = textColor;

  Chart.defaults.elements = Chart.defaults.elements || {};
  Chart.defaults.elements.rectangle = Chart.defaults.elements.rectangle || {};
  Chart.defaults.elements.rectangle.backgroundColor = primary;
  Chart.defaults.elements.rectangle.borderColor = primaryDark;

  Chart.defaults.plugins.tooltip.backgroundColor = '#ffffff';
  Chart.defaults.plugins.tooltip.titleColor = textColor;
  Chart.defaults.plugins.tooltip.bodyColor = textColor;
})();

function updateCheckoutSummary(totalPrice, itemsCount, cashPaid) {
    document.getElementById('totalItems').value = itemsCount;
    document.getElementById('totalPayable').value = totalPrice.toFixed(2);
    document.getElementById('cartTotal').innerText = '₱' + totalPrice.toFixed(2);
    
    // Calculate Change
    let change = cashPaid - totalPrice;
    document.getElementById('customerChange').value = change >= 0 ? change.toFixed(2) : '0.00';
}

document.getElementById('cashReceived').addEventListener('keydown', function(e) {
    const allowedControls = ['Backspace', 'Delete', 'Tab', 'Escape', 'Enter', '.', 'ArrowLeft', 'ArrowRight'];
    if (allowedControls.includes(e.key)) {
        return; 
    }
    const isNumber = /^[0-9]$/.test(e.key);
    
    if (!isNumber) {
        e.preventDefault();
    }
});

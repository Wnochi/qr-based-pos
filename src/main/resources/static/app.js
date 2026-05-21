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
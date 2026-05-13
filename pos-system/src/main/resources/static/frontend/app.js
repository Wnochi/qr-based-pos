async function apiPost(path, body) {
  const opts = { method: 'POST', headers: { 'Accept': 'application/json' } };
  if (body !== null && body !== undefined) {
    opts.headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(body);
  }
  return fetch(path, opts);
}

// helper to format numbers
function fmt(v) { return (Number(v)||0).toFixed(2); }

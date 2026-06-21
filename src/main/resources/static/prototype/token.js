(function () {
  const tokenKey = "meowKanban.token";

  function getToken() {
    return window.localStorage.getItem(tokenKey) || "";
  }

  function setToken(token) {
    if (token) {
      window.localStorage.setItem(tokenKey, token);
    }
  }

  function clearToken() {
    window.localStorage.removeItem(tokenKey);
  }

  function redirectTarget() {
    const params = new URLSearchParams(window.location.search);
    const target = params.get("redirect") || "/boards";
    return target.startsWith("/") && !target.startsWith("//") ? target : "/boards";
  }

  function currentPath() {
    return `${window.location.pathname}${window.location.search}`;
  }

  function redirectToLogin() {
    if (window.location.pathname === "/login") {
      return;
    }
    window.location.href = `/login?redirect=${encodeURIComponent(currentPath())}`;
  }

  function buildHeaders(headers) {
    const nextHeaders = new Headers(headers || {});
    const token = getToken();
    if (token) {
      nextHeaders.set("Authorization", `Bearer ${token}`);
    }
    return nextHeaders;
  }

  async function authFetch(url, options = {}) {
    const response = await fetch(url, {
      ...options,
      headers: buildHeaders(options.headers)
    });
    if (response.status === 401) {
      clearToken();
      redirectToLogin();
    }
    return response;
  }

  function requireToken() {
    if (!getToken()) {
      redirectToLogin();
    }
  }

  window.MeowKanbanAuth = {
    getToken,
    setToken,
    clearToken,
    redirectTarget,
    fetch: authFetch,
    requireToken
  };
}());

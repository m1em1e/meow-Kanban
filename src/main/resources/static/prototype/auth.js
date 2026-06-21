document.querySelectorAll("[data-password-toggle]").forEach((button) => {
  button.addEventListener("click", () => {
    const input = document.querySelector(`#${button.dataset.passwordToggle}`);
    const shouldShow = input.type === "password";
    input.type = shouldShow ? "text" : "password";
    button.textContent = shouldShow ? "隐藏" : "显示";
  });
});

const loginForm = document.querySelector("#loginForm");

function getLoginMessage() {
  let message = document.querySelector("#loginMessage");
  if (!message && loginForm) {
    message = document.createElement("p");
    message.id = "loginMessage";
    message.className = "form-message error";
    loginForm.querySelector(".auth-submit").before(message);
  }
  return message;
}

function showLoginError(text) {
  const message = getLoginMessage();
  if (message) {
    message.textContent = text;
    message.className = "form-message error";
  }
}

async function readLoginResult(response) {
  let result = null;
  try {
    result = await response.json();
  } catch (error) {
    result = null;
  }

  if (!response.ok || !result || result.code !== 1) {
    throw new Error(result?.msg || "登录失败");
  }
  return result.data;
}

loginForm?.addEventListener("submit", async (event) => {
  event.preventDefault();
  const formData = new FormData(loginForm);
  const payload = {
    username: String(formData.get("username") || "").trim(),
    password: String(formData.get("password") || "")
  };

  try {
    const response = await fetch(loginForm.action, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
      body: JSON.stringify(payload)
    });
    const data = await readLoginResult(response);
    window.MeowKanbanAuth?.setToken(data.token);
    window.location.href = window.MeowKanbanAuth?.redirectTarget() || "/boards";
  } catch (error) {
    window.MeowKanbanAuth?.clearToken();
    showLoginError(error.message || "登录失败");
  }
});

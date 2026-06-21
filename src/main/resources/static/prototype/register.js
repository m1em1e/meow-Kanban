const steps = Array.from(document.querySelectorAll(".register-step"));
const dots = Array.from(document.querySelectorAll("[data-step-dot]"));
const stage = document.querySelector(".register-stage");
const progressFill = document.querySelector("#progressFill");
const progressLabel = document.querySelector("#progressLabel");
const progressTitle = document.querySelector("#progressTitle");
const registerPassword = document.querySelector("#registerPassword");
const confirmPassword = document.querySelector("#confirmPassword");
const passwordMessage = document.querySelector("#passwordMessage");
const titles = ["基础信息", "邮箱验证", "设置密码"];

let currentStep = 0;

function setStep(nextStep) {
  const direction = nextStep > currentStep ? "next" : "prev";
  currentStep = Math.max(0, Math.min(steps.length - 1, nextStep));
  stage.dataset.direction = direction;

  steps.forEach((step, index) => {
    step.classList.toggle("active", index === currentStep);
    step.setAttribute("aria-hidden", String(index !== currentStep));
  });

  dots.forEach((dot, index) => {
    dot.classList.toggle("active", index === currentStep);
    dot.classList.toggle("complete", index < currentStep);
  });

  progressFill.style.width = `${((currentStep + 1) / steps.length) * 100}%`;
  progressLabel.textContent = `步骤 ${currentStep + 1} / ${steps.length}`;
  progressTitle.textContent = titles[currentStep];
}

function updatePasswordMessage() {
  const password = registerPassword.value;
  const confirm = confirmPassword.value;

  if (!password && !confirm) {
    passwordMessage.textContent = "";
    passwordMessage.className = "form-message";
    return false;
  }

  if (password.length < 6) {
    passwordMessage.textContent = "密码至少需要 6 位";
    passwordMessage.className = "form-message error";
    return false;
  }

  if (confirm && password !== confirm) {
    passwordMessage.textContent = "两次输入的密码不一致";
    passwordMessage.className = "form-message error";
    return false;
  }

  if (confirm && password === confirm) {
    passwordMessage.textContent = "密码已匹配";
    passwordMessage.className = "form-message success";
    return true;
  }

  passwordMessage.textContent = "";
  passwordMessage.className = "form-message";
  return false;
}

function showRegisterMessage(message, type = "error") {
  passwordMessage.textContent = message;
  passwordMessage.className = `form-message ${type}`;
}

async function readRegisterResult(response) {
  let result = null;
  try {
    result = await response.json();
  } catch (error) {
    result = null;
  }

  if (!response.ok || !result || result.code !== 1) {
    throw new Error(result?.msg || "注册失败");
  }

  return result;
}

document.querySelectorAll("[data-next]").forEach((button) => {
  button.addEventListener("click", () => setStep(currentStep + 1));
});

document.querySelectorAll("[data-prev]").forEach((button) => {
  button.addEventListener("click", () => setStep(currentStep - 1));
});

document.querySelectorAll("[data-password-toggle]").forEach((button) => {
  button.addEventListener("click", () => {
    const input = document.querySelector(`#${button.dataset.passwordToggle}`);
    const shouldShow = input.type === "password";
    input.type = shouldShow ? "text" : "password";
    button.textContent = shouldShow ? "隐藏" : "显示";
  });
});

document.querySelector("#finishRegister").addEventListener("click", async () => {
  if (!updatePasswordMessage()) {
    return;
  }

  const payload = {
    username: document.querySelector('[name="username"]').value,
    nickname: document.querySelector('[name="nickname"]').value,
    email: document.querySelector('[name="email"]').value,
    password: registerPassword.value
  };

  try {
    const response = await fetch("/api/v1/auth/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
      body: JSON.stringify(payload)
    });

    await readRegisterResult(response);
    window.location.href = "/login?registered";
  } catch (error) {
    showRegisterMessage(error.message || "注册失败");
  }
});

registerPassword.addEventListener("input", updatePasswordMessage);
confirmPassword.addEventListener("input", updatePasswordMessage);
setStep(0);

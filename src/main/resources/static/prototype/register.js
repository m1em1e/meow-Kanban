const steps = Array.from(document.querySelectorAll(".register-step"));
const dots = Array.from(document.querySelectorAll("[data-step-dot]"));
const stage = document.querySelector(".register-stage");
const progressFill = document.querySelector("#progressFill");
const progressLabel = document.querySelector("#progressLabel");
const progressTitle = document.querySelector("#progressTitle");
const registerPassword = document.querySelector("#registerPassword");
const confirmPassword = document.querySelector("#confirmPassword");
const passwordMessage = document.querySelector("#passwordMessage");
const emailInput = document.querySelector('[name="email"]');
const captchaInput = document.querySelector('[name="captcha"]');
const captchaMessage = document.querySelector("#captchaMessage");
const sendCaptchaButton = document.querySelector("#sendCaptcha");
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

function showCaptchaMessage(message, type = "error") {
  captchaMessage.textContent = message;
  captchaMessage.className = `form-message ${type}`;
}

function validateCaptchaStep() {
  const email = emailInput.value.trim();
  const captcha = captchaInput.value.trim();

  if (!email) {
    showCaptchaMessage("请先输入邮箱");
    return false;
  }

  if (!emailInput.checkValidity()) {
    showCaptchaMessage("邮箱格式不正确");
    return false;
  }

  if (!captcha) {
    showCaptchaMessage("请输入验证码");
    return false;
  }

  if (!/^\d{6}$/.test(captcha)) {
    showCaptchaMessage("验证码必须是 6 位数字");
    return false;
  }

  showCaptchaMessage("", "");
  return true;
}

async function readApiResult(response, fallbackMessage) {
  let result = null;
  try {
    result = await response.json();
  } catch (error) {
    result = null;
  }

  if (!response.ok || !result || result.code !== 1) {
    throw new Error(result?.msg || fallbackMessage);
  }

  return result;
}

document.querySelectorAll("[data-next]").forEach((button) => {
  button.addEventListener("click", () => {
    if (currentStep === 1 && !validateCaptchaStep()) {
      return;
    }
    setStep(currentStep + 1);
  });
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

sendCaptchaButton.addEventListener("click", async () => {
  const mail = emailInput.value.trim();

  if (!mail) {
    showCaptchaMessage("请先输入邮箱");
    return;
  }

  if (!emailInput.checkValidity()) {
    showCaptchaMessage("邮箱格式不正确");
    return;
  }

  sendCaptchaButton.disabled = true;
  sendCaptchaButton.textContent = "发送中";
  showCaptchaMessage("");

  try {
    const response = await fetch("/api/v1/auth/mail-captcha/send", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
      body: JSON.stringify({ mail })
    });

    await readApiResult(response, "验证码发送失败");
    showCaptchaMessage("验证码已发送，5 分钟内有效", "success");
  } catch (error) {
    showCaptchaMessage(error.message || "验证码发送失败");
  } finally {
    sendCaptchaButton.disabled = false;
    sendCaptchaButton.textContent = "发送";
  }
});

document.querySelector("#finishRegister").addEventListener("click", async () => {
  if (!updatePasswordMessage()) {
    return;
  }

  const payload = {
    username: document.querySelector('[name="username"]').value,
    nickname: document.querySelector('[name="nickname"]').value,
    email: emailInput.value.trim(),
    captcha: captchaInput.value.trim(),
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

    await readApiResult(response, "注册失败");
    window.location.href = "/login?registered";
  } catch (error) {
    showRegisterMessage(error.message || "注册失败");
  }
});

registerPassword.addEventListener("input", updatePasswordMessage);
confirmPassword.addEventListener("input", updatePasswordMessage);
setStep(0);

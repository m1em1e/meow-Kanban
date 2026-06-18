document.querySelectorAll("[data-password-toggle]").forEach((button) => {
  button.addEventListener("click", () => {
    const input = document.querySelector(`#${button.dataset.passwordToggle}`);
    const shouldShow = input.type === "password";
    input.type = shouldShow ? "text" : "password";
    button.textContent = shouldShow ? "隐藏" : "显示";
  });
});

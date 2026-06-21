(function () {
  const useMockHeaderUser = window.location.protocol === "file:"
    || window.location.pathname.includes("/prototype/");

  function shortName(name) {
    return Array.from(String(name || "我").trim() || "我").slice(0, 1).join("").toUpperCase();
  }

  function renderAvatar(profile) {
    const name = profile?.nickname || profile?.username || "我";
    const avatarResourceId = profile?.avatarResourceId;
    document.querySelectorAll("[data-current-user-avatar]").forEach((avatar) => {
      avatar.textContent = "";
      avatar.classList.toggle("has-image", Boolean(avatarResourceId));

      if (avatarResourceId) {
        const image = document.createElement("img");
        image.src = `/api/v1/resource/${encodeURIComponent(avatarResourceId)}`;
        image.alt = "";
        avatar.append(image);
        return;
      }

      avatar.textContent = shortName(name);
    });
  }

  async function loadHeaderUser() {
    if (useMockHeaderUser) {
      renderAvatar({ nickname: "林夏" });
      return;
    }

    const token = window.MeowKanbanAuth?.getToken();
    if (!token) {
      return;
    }

    try {
      const response = await (window.MeowKanbanAuth?.fetch || fetch)("/api/v1/user/profile", {
        headers: { Accept: "application/json" }
      });
      const result = await response.json();
      if (response.ok && result.code === 1) {
        renderAvatar(result.data);
      }
    } catch (error) {
      return;
    }
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", loadHeaderUser, { once: true });
  } else {
    loadHeaderUser();
  }
}());

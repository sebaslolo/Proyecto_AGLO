document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".admin-table .btn").forEach((button) => {
        button.addEventListener("focus", () => button.classList.add("shadow-sm"));
        button.addEventListener("blur", () => button.classList.remove("shadow-sm"));
    });

    const confirmDeleteModal = document.getElementById("confirmDeleteModal");
    confirmDeleteModal?.addEventListener("show.bs.modal", (event) => {
        const button = event.relatedTarget;
        const deleteId = document.getElementById("deleteId");
        const deleteName = document.getElementById("deleteName");
        if (button && deleteId) {
            deleteId.value = button.dataset.deleteId || "";
        }
        if (button && deleteName) {
            deleteName.textContent = button.dataset.deleteName || "este registro";
        }
    });
});

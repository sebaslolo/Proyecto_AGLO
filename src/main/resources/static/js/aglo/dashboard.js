document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".admin-table .btn").forEach((button) => {
        button.addEventListener("focus", () => button.classList.add("shadow-sm"));
        button.addEventListener("blur", () => button.classList.remove("shadow-sm"));
    });

    const confirmDeleteModal = document.getElementById("confirmDeleteModal");
    confirmDeleteModal?.addEventListener("show.bs.modal", (event) => {
        const button = event.relatedTarget;
        const deleteForm = document.getElementById("confirmDeleteForm");
        const deleteId = document.getElementById("deleteId");
        const deleteName = document.getElementById("deleteName");
        const deleteExtra = document.getElementById("deleteExtra");

        if (button && deleteForm && button.dataset.deleteAction) {
            deleteForm.action = button.dataset.deleteAction;
        }
        if (button && deleteId) {
            deleteId.value = button.dataset.deleteId || "";
            deleteId.name = button.dataset.deleteField || "";
        }
        if (button && deleteExtra) {
            deleteExtra.value = button.dataset.deleteExtraValue || "";
            deleteExtra.name = button.dataset.deleteExtraField || "";
        }
        if (button && deleteName) {
            deleteName.textContent = button.dataset.deleteName || "este registro";
        }
    });

    confirmDeleteModal?.addEventListener("hidden.bs.modal", () => {
        const deleteForm = document.getElementById("confirmDeleteForm");
        const deleteId = document.getElementById("deleteId");
        const deleteExtra = document.getElementById("deleteExtra");

        deleteForm?.removeAttribute("action");
        if (deleteId) {
            deleteId.value = "";
            deleteId.name = "";
        }
        if (deleteExtra) {
            deleteExtra.value = "";
            deleteExtra.name = "";
        }
    });
});

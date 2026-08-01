const formLogin = document.getElementById("formLogin");
const username = document.getElementById("username");
const password = document.getElementById("password");
const errorUsername = document.getElementById("errorUsername");
const errorPassword = document.getElementById("errorPassword");
const ojo = document.querySelector(".ojo");
// ojito de contraseña

ojo?.addEventListener("click", function () {
    if (password.type === "password") {
        password.type = "text";
        ojo.classList.remove("fa-eye-slash");
        ojo.classList.add("fa-eye");
    } else {
        password.type = "password";
        ojo.classList.remove("fa-eye");
        ojo.classList.add("fa-eye-slash");
    }
});

//validacion forms
formLogin?.addEventListener("submit", function (e) {
    let valido = true;
    // limpiar campos
    errorUsername.textContent = "";
    errorPassword.textContent = "";
    username.classList.remove("inputError", "inputCorrecto");
    password.classList.remove("inputError", "inputCorrecto");
    // validar usuario
    if (username.value.trim() === "") {
        errorUsername.textContent = "El usuario es obligatorio";
        username.classList.add("inputError");
        valido = false;
    } else {
        username.classList.add("inputCorrecto");
    }
    //validacion de contraseña
    if (password.value.trim() === "") {
        errorPassword.textContent = "La contraseña es obligatoria";
        password.classList.add("inputError");
        valido = false;
    } else if (password.value.length < 6) {
        errorPassword.textContent = "Mínimo 6 caracteres";
        password.classList.add("inputError");
        valido = false;
    } else {
        password.classList.add("inputCorrecto");
    }
    if (!valido) {
        e.preventDefault();
    }
});

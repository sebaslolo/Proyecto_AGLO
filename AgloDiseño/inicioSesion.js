const formLogin = document.getElementById("formLogin");
const correo = document.getElementById("correo");
const password = document.getElementById("password");
const errorCorreo = document.getElementById("errorCorreo");
const errorPassword = document.getElementById("errorPassword");
const ojo = document.querySelector(".ojo");
// ojito de contraseña

ojo.addEventListener("click", function () {
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
formLogin.addEventListener("submit", function (e) {
    e.preventDefault();
    let valido = true;
    // limpiar campos
    errorCorreo.textContent = "";
    errorPassword.textContent = "";
    correo.classList.remove("inputError", "inputCorrecto");
    password.classList.remove("inputError", "inputCorrecto");
    // validar correo
    const regexCorreo = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (correo.value.trim() === "") {
        errorCorreo.textContent = "El correo es obligatorio";
        correo.classList.add("inputError");
        valido = false;
    } else if (!regexCorreo.test(correo.value)) {
        errorCorreo.textContent = "Correo inválido";
        correo.classList.add("inputError");
        valido = false;
    } else {
        correo.classList.add("inputCorrecto");
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
    // alerta
    if (valido) {
        alert("inicio de sesion correcto");
        formLogin.reset();
        correo.classList.remove("inputCorrecto");
        password.classList.remove("inputCorrecto");

    }

});
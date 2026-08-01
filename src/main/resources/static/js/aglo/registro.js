//formulario js 

const formulario = document.getElementById("formRegistro");
const username = document.getElementById("username");
const nombre = document.getElementById("nombre");
const apellidoPaterno = document.getElementById("apellidoPaterno");
const apellidoMaterno = document.getElementById("apellidoMaterno");
const correo = document.getElementById("correo");
const telefono = document.getElementById("telefono");
const password = document.getElementById("password");
const confirmar = document.getElementById("confirmar");
const errorUsername = document.getElementById("errorUsername");
const errorNombre = document.getElementById("errorNombre");
const errorApellidoPaterno = document.getElementById("errorApellidoPaterno");
const errorApellidoMaterno = document.getElementById("errorApellidoMaterno");
const errorCorreo = document.getElementById("errorCorreo");
const errorTelefono = document.getElementById("errorTelefono");
const errorPassword = document.getElementById("errorPassword");
const errorConfirmar = document.getElementById("errorConfirmar");

//validacion
formulario?.addEventListener("submit", function(e){
    let valido = true;
    limpiarMensajes();
    limpiarClases();

    if(username.value.trim() == ""){
        errorUsername.textContent = "Ingrese un usuario.";
        username.classList.add("inputError");
        valido = false;
    }else if(username.value.trim().length > 30){
        errorUsername.textContent = "Máximo 30 caracteres.";
        username.classList.add("inputError");
        valido = false;
    }else{
        username.classList.add("inputCorrecto");
    }

    //nombre
    if(nombre.value.trim() == ""){
        errorNombre.textContent = "Ingrese su nombre.";
        nombre.classList.add("inputError");
        valido = false;
    }else if(nombre.value.trim().length > 20){
        errorNombre.textContent = "Máximo 20 caracteres.";
        nombre.classList.add("inputError");
        valido = false;
    }else{
        nombre.classList.add("inputCorrecto");
    }

    if(apellidoPaterno.value.trim() == ""){
        errorApellidoPaterno.textContent = "Ingrese su apellido paterno.";
        apellidoPaterno.classList.add("inputError");
        valido = false;
    }else if(apellidoPaterno.value.trim().length > 30){
        errorApellidoPaterno.textContent = "Máximo 30 caracteres.";
        apellidoPaterno.classList.add("inputError");
        valido = false;
    }else{
        apellidoPaterno.classList.add("inputCorrecto");
    }

    if(apellidoMaterno.value.trim().length > 30){
        errorApellidoMaterno.textContent = "Máximo 30 caracteres.";
        apellidoMaterno.classList.add("inputError");
        valido = false;
    }else if(apellidoMaterno.value.trim() !== ""){
        apellidoMaterno.classList.add("inputCorrecto");
    }

    //correo
    const expresion = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if(!expresion.test(correo.value)){
        errorCorreo.textContent = "Correo electrónico inválido.";
        correo.classList.add("inputError");
        valido = false;
    }else if(correo.value.trim().length > 75){
        errorCorreo.textContent = "Máximo 75 caracteres.";
        correo.classList.add("inputError");
        valido = false;
    }else{
        correo.classList.add("inputCorrecto");
    }

    if(telefono.value.trim().length > 25){
        errorTelefono.textContent = "Máximo 25 caracteres.";
        telefono.classList.add("inputError");
        valido = false;
    }else if(telefono.value.trim() !== ""){
        telefono.classList.add("inputCorrecto");
    }

    //contraseña
    if(password.value.length < 8){
        errorPassword.textContent = "Debe tener mínimo 8 caracteres.";
        password.classList.add("inputError");
        valido = false;
    }else if(!/\d/.test(password.value)){
        errorPassword.textContent = "Debe contener al menos un número.";
        password.classList.add("inputError");
        valido = false;
    }else if(password.value.length > 512){
        errorPassword.textContent = "Máximo 512 caracteres.";
        password.classList.add("inputError");
        valido = false;
    }else{
        password.classList.add("inputCorrecto");
    }

    //confirmacion
    if(confirmar.value != password.value){
        errorConfirmar.textContent = "Las contraseñas no coinciden.";
        confirmar.classList.add("inputError");
        valido = false;
    }else{
        confirmar.classList.add("inputCorrecto");
    }
    if(!valido){
        e.preventDefault();
    }
});

//limpiar 
function limpiarMensajes(){
    errorUsername.textContent = "";
    errorNombre.textContent = "";
    errorApellidoPaterno.textContent = "";
    errorApellidoMaterno.textContent = "";
    errorCorreo.textContent = "";
    errorTelefono.textContent = "";
    errorPassword.textContent = "";
    errorConfirmar.textContent = "";
}
function limpiarClases(){
    [username, nombre, apellidoPaterno, apellidoMaterno, correo, telefono, password, confirmar].forEach(function(input){
        input.classList.remove("inputError", "inputCorrecto");
    });
}

//mostrar contraseña 

const ojos = document.querySelectorAll(".ojo");
ojos.forEach(function(ojo){
    ojo.addEventListener("click", function(){
        const input = this.previousElementSibling;
        if(input.type === "password"){
            input.type = "text";
            this.classList.remove("fa-eye-slash");
            this.classList.add("fa-eye");
        }else{
            input.type = "password";
            this.classList.remove("fa-eye");
            this.classList.add("fa-eye-slash");
        }
    });
});

//formulario js 

const formulario = document.getElementById("formRegistro");
const nombre = document.getElementById("nombre");
const correo = document.getElementById("correo");
const password = document.getElementById("password");
const confirmar = document.getElementById("confirmar");
const errorNombre = document.getElementById("errorNombre");
const errorCorreo = document.getElementById("errorCorreo");
const errorPassword = document.getElementById("errorPassword");
const errorConfirmar = document.getElementById("errorConfirmar");

//validacion
formulario.addEventListener("submit", function(e){
    e.preventDefault();
    let valido = true;
    limpiarMensajes();

    //nombre
    if(nombre.value.trim() == ""){
        errorNombre.textContent = "Ingrese su nombre.";
        nombre.classList.add("inputError");
        valido = false;
    }else{
        nombre.classList.remove("inputError");
        nombre.classList.add("inputCorrecto");
    }
    //correo
    const expresion = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if(!expresion.test(correo.value)){
        errorCorreo.textContent = "Correo electrónico inválido.";
        correo.classList.add("inputError");
        valido = false;
    }else{
        correo.classList.remove("inputError");
        correo.classList.add("inputCorrecto");
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
    }else{
        password.classList.remove("inputError");
        password.classList.add("inputCorrecto");
    }

    //confirmacion
    if(confirmar.value != password.value){
        errorConfirmar.textContent = "Las contraseñas no coinciden.";
        confirmar.classList.add("inputError");
        valido = false;
    }else{
        confirmar.classList.remove("inputError");
        confirmar.classList.add("inputCorrecto");
    }
    //validaciom
    if(valido){
        alert("¡Cuenta creada correctamente!");
        formulario.reset();
        quitarClases();
    }
});

//limpiar 
function limpiarMensajes(){
    errorNombre.textContent = "";
    errorCorreo.textContent = "";
    errorPassword.textContent = "";
    errorConfirmar.textContent = "";
}
function quitarClases(){
    nombre.classList.remove("inputCorrecto");
    correo.classList.remove("inputCorrecto");
    password.classList.remove("inputCorrecto");
    confirmar.classList.remove("inputCorrecto");
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
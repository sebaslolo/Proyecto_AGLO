/* barrita de progreso  */

const progreso = document.querySelector(".progreso");
window.addEventListener("scroll", () => {
    const scrollActual = window.scrollY;
    const alturaPagina = document.documentElement.scrollHeight - window.innerHeight;

    const porcentaje = (scrollActual / alturaPagina) * 100;

    if (progreso) {
        progreso.style.width = porcentaje + "%";
    }
});

/* video */

const abrirVideo = document.getElementById("abrirVideo");
const cerrarVideo = document.getElementById("cerrarVideo");
const modalVideo = document.getElementById("videoModal");
const video = document.querySelector("#videoModal iframe");
const linkVideo = "https://www.youtube.com/embed/KEQqgQ42OIM?autoplay=1&start=45";
// abrir video
abrirVideo?.addEventListener("click", function (e) {
    e.preventDefault();

    modalVideo.style.display = "flex";
    video.src = linkVideo;
});

// cerrar video
cerrarVideo?.addEventListener("click", function () {
    modalVideo.style.display = "none";
    video.src = "";
});

// cerrar 
window.addEventListener("click", function (e) {
    if (e.target === modalVideo) {
        modalVideo.style.display = "none";
        video.src = "";
    }
});


/* tarjetas der -izq*/

const cards = document.querySelector(".cards");
const btnAnterior = document.querySelector(".anterior");
const btnSiguiente = document.querySelector(".siguiente");
const card = document.querySelector(".card");
let mover = 0;
if (card) {
    mover = card.offsetWidth + 30;
    window.addEventListener("resize", function () {
        mover = card.offsetWidth + 30;
    });
}
btnSiguiente?.addEventListener("click", function () {
    cards.scrollBy({ left: mover, behavior: "smooth" });
});
btnAnterior?.addEventListener("click", function () {
    cards.scrollBy({ left: -mover, behavior: "smooth" });
});

/* ventana  */
const botones = document.querySelectorAll(".card button");
const modal = document.getElementById("modal");
const titulo = document.getElementById("modalTitulo");
const texto = document.getElementById("modalTexto");
const cerrar = document.querySelector(".cerrar");
const info = [
    "Liberación de tortugas en Playa Ostional.",
    "Tour guiado por la biodiversidad.",
    "Experiencia gastronómica frente al mar.",
    "Actividad artística ecológica.",
    "Observación astronómica con telescopios.",
    "Experiencia de surf para todos los niveles.",
    "Jornada de limpieza de playa.",
    "Charla educativa sobre conservación."
];
// texto inicial botones
botones.forEach(b => {
    b.textContent = "Reserva";
});
// abrir modal
botones.forEach((b, i) => {
    b.addEventListener("click", function () {
        const card = b.closest(".card");
        const nombre = card.querySelector("h3").textContent;
        modal.style.display = "flex";
        titulo.textContent = nombre;
        texto.textContent = info[i];
        b.classList.add("activo");
        b.textContent = "Ver más";
    });
});
// cerrar modal
function cerrarModal() {
    modal.style.display = "none";
    botones.forEach(b => {
        b.classList.remove("activo");
        b.textContent = "Reserva";
    });
}
cerrar?.addEventListener("click", cerrarModal);
window.addEventListener("click", function (e) {
    if (e.target === modal) {
        cerrarModal();
    }
});
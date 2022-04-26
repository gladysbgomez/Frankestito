const joystick = createJoystick(document.getElementById("joystickWrapper"));

// setInterval(() => console.log(joystick.getPosition()), 16);

/* ¿Como mandar la información del joystick?
 * Opcion 1: Calcular un "desiredVector", entonces el servidor al recibirlo calcularia el "steeringVector"
 * Opcion 2: Enviar las coordenadas viejas y las nuevas del joystick, y que el servidor de alguna forma calcule la direccion de desplazamiento
 */

function createJoystick(parent) {
    const maxDiff = 50;
    const stick = document.createElement("div");
    stick.classList.add("joystick");

    stick.addEventListener("mousedown", handleMouseDown, false);
    stick.addEventListener("mousemove", handleMouseMove, false);
    stick.addEventListener("mouseup", handleMouseUp, false);
    stick.addEventListener("touchstart", handleMouseDown, false);
    stick.addEventListener("touchmove", handleMouseMove, false);
    stick.addEventListener("touchend", handleMouseUp, false);

    let dragStart = null;
    let currentPos = { x: 0, y: 0 };

    function handleMouseDown(event) {
        stick.style.transition = "0s";
        if (event.changedTouches) {
            dragStart = {
                x: event.changedTouches[0].clientX,
                y: event.changedTouches[0].clientY
            };
            return;
        }
        dragStart = {
            x: event.clientX,
            y: event.clientY
        };
    }

    function handleMouseMove(event) {
        if (dragStart === null)
            return;
        event.preventDefault();
        if (event.changedTouches) {
            event.clientX = (event.changedTouches[0].clientX);
            event.clientY = (event.changedTouches[0].clientY);
        }
        const xDiff = (event.clientX - dragStart.x);
        const yDiff = (event.clientY - dragStart.y);
        const angle = Math.atan2(yDiff, xDiff);
        const distance = Math.min(maxDiff, Math.hypot(xDiff, yDiff));
        const xNew = (distance * Math.cos(angle))*0.2;
        const yNew = (distance * Math.sin(angle))*0.2;
        stick.style.transform = `translate3d(${xNew}px, ${yNew}px, 0px)`; // Actualizo la posicion
        currentPos = { x: xNew, y: yNew };
        //posSend = {name: "move", x: yNew, y: -xNew};
        //jsonPos = JSON.stringify(posSend);
        //console.log(jsonPos);
        // Los x/y estan cambiados aproposito
        socket.send('{"name": "move", "priority": "0","parameters": [{"name": "x", "value": "' + xNew + '"},\n\
                                                                     {"name": "y", "value": "' + yNew + '"}]}');

    }

    function handleMouseUp(event) {
        if (dragStart === null)
            return;
        stick.style.transition = ".2s";
        stick.style.transform = `translate3d(0px, 0px, 0px)`;
        dragStart = null;
        currentPos = { x: 0, y: 0 };
        socket.send("stop");
    }
    parent.appendChild(stick);
    return {
        getPosition: () => currentPos
    };
}

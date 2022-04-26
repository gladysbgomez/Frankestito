const button = createAttackButton(
  document.getElementById("attackButtonWrapper")
);

// setInterval(() => console.log(joyattackButton.getPosition()), 16);

// Eventos a considerar:
//  touchstart, touchend, mousedown, mouseup, click

function createAttackButton(parent) {
  const attackButton = document.createElement("div");
  attackButton.classList.add("attackButton");

  attackButton.addEventListener("mousedown", handlePress, false);
  attackButton.addEventListener("touchstart", handlePress, false);
  // document.addEventListener("touchstart", handlePress);

  // Los handle probablemente no sean necesarios para el boton de ataque
  function handlePress(event) {
    event.preventDefault();
    //console.log("ataque");
    socket.send("fire");
    socket.send('{"name": "fire", "priority": "1"}');

  }

  function handleRelease(event) {}

  parent.appendChild(attackButton);
  return {
    getPosition: () => currentPos
  };
}

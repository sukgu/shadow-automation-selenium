(function() {
    /* origin: https://embed.plnkr.co/fVIYs97WzjwjYnuDE75u/ */
    var root = container.attachShadow({
      mode: "open"
    })

    //Inside element
    var e = document.createElement("h2");
    e.textContent = "Inside Shadow DOM";
    e.id = "inside";
    root.appendChild(e);
    //Access inside element
    // console.log(container.shadowRoot.querySelector("#inside"));
})()

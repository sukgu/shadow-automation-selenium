(function() {
    /* origin: https://embed.plnkr.co/fVIYs97WzjwjYnuDE75u/ */
    var root = container.attachShadow({mode: "open"});
    var root1 = dTest.attachShadow({mode: "open"});
    
    root1.innerHTML='<p>Hi!</p>';
    document.body.appendChild(document.createTextNode(root1.childNodes.length));
    
    //Inside element
    var data = document.createElement("data-dom");
    data.textContent = "Data DOM";
    data.innerHTML = "<p>HTML Data DOM</p>";
    data.id = "data";
    root1.appendChild(data);
    var e = document.createElement("h2");
    e.textContent = "Inside Shadow DOM";
    e.id = "inside";
    e.className = "inside";
    root.appendChild(e);
    var e = document.createElement("h2");
    e.textContent = "Inside Shadow DOM";
    e.id = "inside";
    e.className = "inside1";
    root.appendChild(e);
    
    var root2 = data.attachShadow({mode: "open"});
    var child2 = document.createElement("data-dom-ele");
    child2.textContent = "data ele";
    child2.id = "data-ele";
    root2.appendChild(child2);
    
    var d = document.querySelector("#dTest").shadowRoot.querySelector("#data").shadowRoot.querySelector("data-dom-ele#data-ele");
    var root3 = d.attachShadow({mode: "open"});
    var child3 = document.createElement("data-dom-ele-child3");
    child3.textContent = "child3 ele";
    child3.innerHTML='<p>Hi! from child3 ele</p>';
    child3.id = "child3-ele";
    root3.appendChild(child3);
    
    
    var d = document.querySelector("#dTest").shadowRoot.querySelector("#data").shadowRoot.querySelector("data-dom-ele#data-ele").shadowRoot.querySelector("#child3-ele");
    var root4 = d.attachShadow({mode: "open"});
    var child4 = document.createElement("data-dom-ele-child4");
    child4.textContent = "child4 ele";
    child4.id = "child4-ele";
    root4.appendChild(child4);
    // console.log(container.shadowRoot.querySelector("#inside"));
})()

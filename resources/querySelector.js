var getShadowElement = function getShadowElement(object,selector) {
	if (object.shadowRoot !=null) {
		return object.shadowRoot.querySelector(selector);
	} else {
		return null;
	}
};

var getAllShadowElement = function getAllShadowElement(object,selector) {
	return object.shadowRoot.querySelectorAll(selector);
};

var getAttribute = function getAttribute(object,attribute) {
	return object.getAttribute(attribute);
};

var isVisible = function isVisible(object) {
	var visible = object.offsetWidth;
	if(visible>0) {
		return true;
	} else {
		return false;
	}
};

var scrollTo = function scrollTo(object) {
	object.scrollIntoView({block: "center", inline: "nearest"});
};

var getParentElement = function getParentElement(object) {
	if(object.parentNode.nodeName=="#document-fragment") {
		return object.parentNode.host;
	} else if(object.nodeName=="#document-fragment") {
		return object.host;
	} else {
		return object.parentElement;
	}
};

var getChildElements = function getChildElements(object) {
	elements = null;
	if(object.nodeName=="#document-fragment") {
		elements = object.children;
	} else {
		elements = object.children;
	}
	if (object.shadowRoot!=null && elements.length==0){
		elements = object.shadowRoot.children;
	}
	return elements;
};

var getSiblingElements = function getSiblingElements(object) {
	if(object.nodeName == "#document-fragment") {
		return object.host.children;
	} else {
		if(object.parentNode.nodeName=="#document-fragment") {
			return object.parentNode.children;
		} else {
			return object.parentElement.children;
		}
	}
};

var getSiblingElement = function getSiblingElement(object, selector) {
	if(object.nodeName=="#document-fragment") {
		return object.host.querySelector(selector);
	} else {
		if(object.parentNode.nodeName=="#document-fragment") {
			return object.parentNode.querySelector(selector);
		} else {
			return object.parentElement.querySelector(selector);
		}
	}
};

var getNextSiblingElement = function getNextSiblingElement(object) {
	if(object.nodeName=="#document-fragment") {
		return object.host.firstElementChild.nextElementSibling;
	} else {
		return object.nextElementSibling;
	}
};

var getPreviousSiblingElement = function getPreviousSiblingElement(object) {
	if(object.nodeName=="#document-fragment") {
		return null;
	} else {
		return object.previousElementSibling;
	}
};

var isChecked = function isChecked(object) {
	return object.checked;
};

var isDisabled = function isDisabled(object) {
	return object.disabled;
};

var findCheckboxWithLabel = function findCheckboxWithLabel(label, root=document) {
	if(root.nodeName=="PAPER-CHECKBOX") {
		if(root.childNodes[0].data.trimStart().trimEnd()==label) {
			return root;
		}
	} else {
		let all_checkbox = getAllObject("paper-checkbox", root);
		for (let checkbox of all_checkbox) {
			if(checkbox.childNodes[0].data.trimStart().trimEnd()==label) {
				return checkbox;
			}
		}
	}
};

var findRadioWithLabel = function findRadioWithLabel(label, root=document) {
	if(root.nodeName=="PAPER-RADIO-BUTTON") {
		if(root.childNodes[0].data.trimStart().trimEnd()==label) {
			return root;
		}
	} else {
		let all_radio = getAllObject("paper-radio-button", root);
		for (let radio of all_radio) {
			if(radio.childNodes[0].data.trimStart().trimEnd()==label) {
				return radio;
			}
		}
	}
};

var selectCheckbox = function selectCheckbox(label, root=document) {
	let checkbox = findCheckboxWithLabel(label, root);
	if(!checkbox.checked) {
		checbox.click();
	}
};

var selectRadio = function selectRadio(label, root=document) {
	let radio = findCheckboxWithLabel(label, root);
	if(!radio.checked) {
		radio.click();
	}
};

var selectDropdown = function selectDropdown(label, root=document) {
	if(root.nodeName=="PAPER-LISTBOX") {
		root.select(label);
	} else {
		let listbox = getAllObject("paper-listbox", root);
		listbox.select(label);
	}
};

var evaluateAllDeep = function evaluateAllDeep(selector, root) {
	if(root==undefined) {
		return collectAllElementsEvaluateDeep(selector, document);
	} else {
		return collectAllElementsEvaluateDeep(selector, root);
	}
};

var evaluateDeep = function evaluateDeep(selector, root) {
	if(root==undefined) {
		return collectElementEvaluateDeep(selector, document);
	} else {
		return collectElementEvaluateDeep(selector, root);
	}
};

var getXPathObject = function getXPathObject(selector, root = document) {
	while (selector.indexOf('/')==0 && selector.search('/') != -1) {
    	selector = selector.replace('/','');
    }
	splitedSelectors = selector.split('//');
	if (splitedSelectors.length == 1) {
		return evaluateDeep(selector, root);
	}
	if (splitedSelectors.length == 2) {
		parent = evaluateDeep(splitedSelectors[0], root);
	    return evaluateDeep(splitedSelectors[1], parent);
	}
	if (splitedSelectors.length == 3) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    return evaluateDeep(splitedSelectors[2], parent_1);
	}
	if (splitedSelectors.length == 4) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    parent_2 = evaluateDeep(splitedSelectors[2], parent_1);
	    return evaluateDeep(splitedSelectors[3], parent_2);
	}
	if (splitedSelectors.length == 5) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    parent_2 = evaluateDeep(splitedSelectors[2], parent_1);
	    parent_3 = evaluateDeep(splitedSelectors[3], parent_2);
	    return evaluateDeep(splitedSelectors[4], parent_3);
	}
	if (splitedSelectors.length == 6) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    parent_2 = evaluateDeep(splitedSelectors[2], parent_1);
	    parent_3 = evaluateDeep(splitedSelectors[3], parent_2);
	    parent_4 = evaluateDeep(splitedSelectors[4], parent_3);
	    return evaluateDeep(splitedSelectors[5], parent_4);
	}
	if (splitedSelectors.length == 7) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    parent_2 = evaluateDeep(splitedSelectors[2], parent_1);
	    parent_3 = evaluateDeep(splitedSelectors[3], parent_2);
	    parent_4 = evaluateDeep(splitedSelectors[4], parent_3);
	    parent_5 = evaluateDeep(splitedSelectors[5], parent_4);
	    return evaluateDeep(splitedSelectors[6], parent_5);
	}
	
};

var getXPathAllObject = function getXPathAllObject(selector, root = document) {
	while (selector.indexOf('/')==0 && selector.search('/') != -1) {
    	selector = selector.replace('/','');
    }
	splitedSelectors = selector.split('//');
	if (splitedSelectors.length == 1) {
		return evaluateAllDeep(selector, root);
	}
	if (splitedSelectors.length == 2) {
		parent = evaluateDeep(splitedSelectors[0], root);
	    return evaluateAllDeep(splitedSelectors[1], parent);
	}
	if (splitedSelectors.length == 3) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    return evaluateAllDeep(splitedSelectors[2], parent_1);
	}
	if (splitedSelectors.length == 4) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    parent_2 = evaluateDeep(splitedSelectors[2], parent_1);
	    return evaluateAllDeep(splitedSelectors[3], parent_2);
	}
	if (splitedSelectors.length == 5) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    parent_2 = evaluateDeep(splitedSelectors[2], parent_1);
	    parent_3 = evaluateDeep(splitedSelectors[3], parent_2);
	    return evaluateAllDeep(splitedSelectors[4], parent_3);
	}
	if (splitedSelectors.length == 6) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    parent_2 = evaluateDeep(splitedSelectors[2], parent_1);
	    parent_3 = evaluateDeep(splitedSelectors[3], parent_2);
	    parent_4 = evaluateDeep(splitedSelectors[4], parent_3);
	    return evaluateAllDeep(splitedSelectors[5], parent_4);
	}
	if (splitedSelectors.length == 7) {
		parent = evaluateDeep(splitedSelectors[0], root);
		parent_1 = evaluateDeep(splitedSelectors[1], parent);
	    parent_2 = evaluateDeep(splitedSelectors[2], parent_1);
	    parent_3 = evaluateDeep(splitedSelectors[3], parent_2);
	    parent_4 = evaluateDeep(splitedSelectors[4], parent_3);
	    parent_5 = evaluateDeep(splitedSelectors[5], parent_4);
	    return evaluateAllDeep(splitedSelectors[6], parent_5);
	}
};

var querySelectorAllDeep = function querySelectorAllDeep(selector, root) {
	if(root==undefined) {
		return _querySelectorDeep(selector, true, document);
	} else {
		return _querySelectorDeep(selector, true, root);
	}
};

var querySelectorDeep = function querySelectorDeep(selector, root) {
	if(root==undefined) {
		return _querySelectorDeep(selector, false, document);
	} else {
		return _querySelectorDeep(selector, false, root);
	}
};

var ElementNotFoundException = function ElementNotFoundException(message = "Not found") {
  this.message = message;
  this.name = 'ElementNotFoundException';
};

var getObject = function getObject(selector, root = document) {
    const multiLevelSelectors = splitByCharacterUnlessQuoted(selector, '>');
	if (multiLevelSelectors.length == 1) {
		return querySelectorDeep(multiLevelSelectors[0], root);
	} else if (multiLevelSelectors.length == 2) {
	    parent = querySelectorDeep(multiLevelSelectors[0]);
        if (parent === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[0]+" couldn't be found.");
        }
		return querySelectorDeep(multiLevelSelectors[1], parent);
	} else if (multiLevelSelectors.length == 3) {
	    parent_1 = querySelectorDeep(multiLevelSelectors[0]);
        if (parent_1 === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[0]+" couldn't be found.");
        }
        parent_2 = querySelectorDeep(multiLevelSelectors[1], parent_1);
        if (parent_2 === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[1]+" couldn't be found.");
        }
		return querySelectorDeep(multiLevelSelectors[2], parent_2);
	} else if (multiLevelSelectors.length == 4) {
	    parent_1 = querySelectorDeep(multiLevelSelectors[0]);
        if (parent_1 === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[0]+" couldn't be found.");
        }
        parent_2 = querySelectorDeep(multiLevelSelectors[1], parent_1);
        if (parent_2 === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[1]+" couldn't be found.");
        }
        parent_3 = querySelectorDeep(multiLevelSelectors[2], parent_2);
        if (parent_3 === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[2]+" couldn't be found.");
        }
		return querySelectorDeep(multiLevelSelectors[3], parent_3);
	} else if (multiLevelSelectors.length == 5) {
	    parent_1 = querySelectorDeep(multiLevelSelectors[0]);
        if (parent_1 === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[0]+" couldn't be found.");
        }
        parent_2 = querySelectorDeep(multiLevelSelectors[1], parent_1);
        if (parent_2 === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[1]+" couldn't be found.");
        }
        parent_3 = querySelectorDeep(multiLevelSelectors[2], parent_2);
        if (parent_3 === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[2]+" couldn't be found.");
        }
        parent_4 = querySelectorDeep(multiLevelSelectors[3], parent_3);
        if (parent_4 === undefined) {
            throw new ElementNotFoundException("Element with CSS "+multiLevelSelectors[3]+" couldn't be found.");
        }
		return querySelectorDeep(multiLevelSelectors[4], parent_4);
	}
};

var getAllObject = function getAllObject(selector, root = document) {
    const multiLevelSelectors = splitByCharacterUnlessQuoted(selector, '>');
    if (multiLevelSelectors.length == 1) {
        return querySelectorAllDeep(multiLevelSelectors[0], root);
    } else if (multiLevelSelectors.length == 2) {
        parent = querySelectorDeep(multiLevelSelectors[0]);
        if (parent === undefined) {
            return [];
        }
        return querySelectorAllDeep(multiLevelSelectors[1], parent);
    } else if (multiLevelSelectors.length == 3) {
        parent_1 = querySelectorDeep(multiLevelSelectors[0]);
        if (parent_1 === undefined) {
            return [];
        }
        parent_2 = querySelectorDeep(multiLevelSelectors[1], parent_1);
        if (parent_2 === undefined) {
            return [];
        }
        return querySelectorAllDeep(multiLevelSelectors[2], parent_2);
    } else if (multiLevelSelectors.length == 4) {
        parent_1 = querySelectorDeep(multiLevelSelectors[0]);
        if (parent_1 === undefined) {
            return [];
        }
        parent_2 = querySelectorDeep(multiLevelSelectors[1], parent_1);
        if (parent_2 === undefined) {
            return [];
        }
        parent_3 = querySelectorDeep(multiLevelSelectors[2], parent_2);
        if (parent_3 === undefined) {
            return [];
        }
		return querySelectorAllDeep(multiLevelSelectors[3], parent_3);
	} else if (multiLevelSelectors.length == 5) {
	    parent_1 = querySelectorDeep(multiLevelSelectors[0]);
        if (parent_1 === undefined) {
            return [];
        }
        parent_2 = querySelectorDeep(multiLevelSelectors[1], parent_1);
        if (parent_2 === undefined) {
            return [];
        }
        parent_3 = querySelectorDeep(multiLevelSelectors[2], parent_2);
        if (parent_3 === undefined) {
            return [];
        }
        parent_4 = querySelectorDeep(multiLevelSelectors[3], parent_3);
        if (parent_4 === undefined) {
            return [];
        }
		return querySelectorAllDeep(multiLevelSelectors[4], parent_4);
	}

};

function _querySelectorDeep(selector, findMany, root) {
    let lightElement = root.querySelector(selector);

    if (document.head.createShadowRoot || document.head.attachShadow) {
        if (!findMany && lightElement) {
            return lightElement;
        }

        const selectionsToMake = splitByCharacterUnlessQuoted(selector, ',');

        return selectionsToMake.reduce((acc, minimalSelector) => {
            if (!findMany && acc) {
                return acc;
            }
            const splitSelector = splitByCharacterUnlessQuoted(minimalSelector
                    .replace(/^\s+/g, '')
                    .replace(/\s*([>+~]+)\s*/g, '$1'), ' ')
                .filter((entry) => !!entry);
            const possibleElementsIndex = splitSelector.length - 1;
            const possibleElements = collectAllElementsQuerySelectorDeep(splitSelector[possibleElementsIndex], root);
            const findElements = findMatchingElement(splitSelector, possibleElementsIndex, root);
            if (findMany) {
                acc = acc.concat(possibleElements.filter(findElements));
                return acc;
            } else {
                acc = possibleElements.find(findElements);
                return acc;
            }
        }, findMany ? [] : null);


    } else {
        if (!findMany) {
            return lightElement;
        } else {
            return root.querySelectorAll(selector);
        }
    }

}

function findMatchingElement(splitSelector, possibleElementsIndex, root) {
    return (element) => {
        let position = possibleElementsIndex;
        let parent = element;
        let foundElement = false;
        while (parent) {
            const foundMatch = parent.matches(splitSelector[position]);
            if (foundMatch && position === 0) {
                foundElement = true;
                break;
            }
            if (foundMatch) {
                position--;
            }
            parent = findParentOrHost(parent, root);
        }
        return foundElement;
    };

}

function splitByCharacterUnlessQuoted(selector, character) {
    return selector.match(/\\?.|^$/g).reduce((p, c) => {
        if (c === '"' && !p.sQuote) {
            p.quote ^= 1;
            p.a[p.a.length - 1] += c;
        } else if (c === '\'' && !p.quote) {
            p.sQuote ^= 1;
            p.a[p.a.length - 1] += c;

        } else if (!p.quote && !p.sQuote && c === character) {
            p.a.push('');
        } else {
            p.a[p.a.length - 1] += c;
        }
        return p;
    }, { a: [''] }).a;
}


function findParentOrHost(element, root) {
    const parentNode = element.parentNode;
    return (parentNode && parentNode.host && parentNode.nodeType === 11) ? parentNode.host : parentNode === root ? null : parentNode;
}


function collectAllElementsQuerySelectorDeep(selector = null, root) {
    const allElements = [];

    const findAllElements = function(nodes) {
        for (let i = 0, el; el = nodes[i]; ++i) {
            allElements.push(el);
            if (el.shadowRoot) {
                findAllElements(el.shadowRoot.querySelectorAll('*'));
            }
        }
    };

	if(root.shadowRoot != null) {
		findAllElements(root.shadowRoot.querySelectorAll('*'));
	}

    findAllElements(root.querySelectorAll('*'));

    return selector ? allElements.filter(el => el.matches(selector)) : allElements;
}


function collectAllElementsEvaluateDeep(selector, root) {
    var allElements = [];
    while (selector.indexOf('/')==0 && selector.search('/') != -1) {
    	selector = selector.replace('/','');
    }
    
    allElementsInDocument = collectAllElementsQuerySelectorDeep('*', root);

    const findAllElements = function(nodes) {
        for (i=0; i<nodes.length; i++) {
        	test_node = document.createElement('test-node');
        	parent_node = nodes[i].parentNode;
        	if (parent_node != null && parent_node.nodeName != 'HTML' && parent_node.nodeName != '#document') {
        		cloned_node = nodes[i].cloneNode();
        		if (nodes[i].textContent != "") {
        			cloned_node.textContent = nodes[i].textContent; 
        		}
        		test_node.append(cloned_node);
            	elements = document.evaluate(".//"+selector, test_node, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE);
            	while ((element=elements.iterateNext()) != null) {
					if (!allElements.filter((value) => value == nodes[i]).length > 0) {
						allElements.push(nodes[i]);
					}
                }
        	}
        	elements = document.evaluate(".//"+selector, nodes[i], null, XPathResult.ORDERED_NODE_ITERATOR_TYPE);
            while ((element=elements.iterateNext()) != null) {
            	if (!allElements.filter((value) => value == element).length > 0) {
					allElements.push(element);
				}
            }
        }
    };

    findAllElements(allElementsInDocument);

    return allElements;
}


function collectElementEvaluateDeep(selector, root) {
    var element = null;
    while (selector.indexOf('/')==0 && selector.search('/') != -1) {
    	selector = selector.replace('/','');
    }
    
    allElementsInDocument = collectAllElementsQuerySelectorDeep('*', root);

    const findAllElements = function(nodes) {
        for (i=0; i<nodes.length; i++) {
        	test_node = document.createElement('test-node');
        	parent_node = nodes[i].parentNode;
        	if (parent_node != null && parent_node.nodeName != 'HTML' && parent_node.nodeName != '#document') {
        		cloned_node = nodes[i].cloneNode();
        		if (nodes[i].textContent != "") {
        			cloned_node.textContent = nodes[i].textContent; 
        		}
        		test_node.append(cloned_node);
            	elements = document.evaluate('.//'+selector, test_node, null, XPathResult.FIRST_ORDERED_NODE_TYPE);
            	value = elements.singleNodeValue;
            	if (value!=null) {
            		element = nodes[i];
            		break;
            	}
        	}
        	elements = document.evaluate('.//'+selector, nodes[i], null, XPathResult.FIRST_ORDERED_NODE_TYPE);
        	value = elements.singleNodeValue;
        	if (value!=null) {
        		element = elements.singleNodeValue;
        		break;
        	}
        }
    };

    findAllElements(allElementsInDocument);

    return element;
}
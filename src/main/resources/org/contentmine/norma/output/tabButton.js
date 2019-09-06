function openTab(evt, currentTabId, tabClass) {
    var i, element, tablinks;

    element = document.getElementsByClassName(tabClass);
    for (i = 0; i < element.length; i++) {
        element[i].style.display = "none";
    }

    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    document.getElementById(currentTabId).style.display = "block";
    evt.currentTarget.className += " active";
    
}

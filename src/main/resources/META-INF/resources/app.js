// Einfache JavaScript-Datei für dynamische Interaktion
window.addEventListener('DOMContentLoaded', function () {

	// Erfolgsmeldung nach Hinzufügen einer Website anzeigen
	if (localStorage.getItem('websiteAdded') === '1') {
		// Popup-Element erzeugen
		var popup = document.createElement('div');
		popup.className = 'success-popup';
		popup.textContent = 'Website erfolgreich hinzugefügt!';
		document.body.appendChild(popup);
		// Nach 3 Sekunden ausblenden
		setTimeout(function () {
			popup.classList.add('hide');
			setTimeout(function () {
				popup.remove();
			}, 500); // Zeit für Ausblend-Animation
		}, 3000);
		localStorage.removeItem('websiteAdded');
	}
});

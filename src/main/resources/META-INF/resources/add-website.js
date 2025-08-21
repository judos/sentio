// JavaScript für das Hinzufügen einer überwachten Website
window.addEventListener('DOMContentLoaded', function () {
	const form = document.getElementById('websiteForm');
	const msg = document.getElementById('formMsg');
	form.addEventListener('submit', async function (e) {
		e.preventDefault();
		msg.textContent = '';
		const name = document.getElementById('name').value;
		const url = document.getElementById('url').value;
		const response = await fetch('/api/websites', {
			method: 'POST',
			headers: {'Content-Type': 'application/json'},
			body: JSON.stringify({name, url})
		});
		if (response.ok) {
			localStorage.setItem('websiteAdded', '1');
			window.location.href = '/hello';
		} else {
			const error = await response.text();
			msg.textContent = 'Fehler: ' + error;
		}
	});
});

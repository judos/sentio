async function removeMonitor(id, monitorKey) {
	const response = await fetch('/api/website-monitors/' + id + '/' + monitorKey, {
		method: 'DELETE',
		headers: {'Content-Type': 'application/json'},
	});
	await response.text()
	if (response.ok) {
		window.location.href = '/website/' + id;
	} else {
		const error = await response.text();
		console.error(error);
	}
}

async function saveConfig(id, monitorKey) {
	const response = await fetch('/api/website-monitors/' + id + '/' + monitorKey, {
		method: 'POST',
		headers: {'Content-Type': 'application/json'},
		body: form2Json('configForm')
	});
	await response.text()
	if (response.ok) {
		window.location.href = '/website/' + id;
	} else {
		const error = await response.text();
		console.error(error);
	}
}

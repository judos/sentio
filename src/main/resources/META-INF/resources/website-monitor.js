// TODO: rename parameters
async function removeMonitored(id, monitoredId, monitorName) {
	popupQueueDelete('Do you want to delete the monitored "' + monitorName + '"?', async function () {
		const response = await fetch('/api/monitored/' + monitoredId, {
			method: 'DELETE',
			headers: {'Content-Type': 'application/json'},
		});
		await response.text()
		if (response.ok) {
			// TODO: update link
			window.location.href = '/website/' + id;
		} else {
			const error = await response.text();
			console.error(error);
		}
	});
}

// TODO: update params
async function saveConfig(id, monitoredId) {
	const response = await fetch('/api/monitored/' + monitoredId, {
		method: 'POST',
		headers: {'Content-Type': 'application/json'},
		body: form2Json('configForm')
	});
	await response.text()
	if (response.ok) {
		// TODO: update link
		window.location.href = '/website/' + id;
	} else {
		const error = await response.text();
		console.error(error);
	}
}

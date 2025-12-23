async function removeMonitored(monitoredId, monitorName) {
	popupQueueDelete('Do you want to delete the monitored "' + monitorName + '"?', async function () {
		const response = await fetch('/api/monitored/' + monitoredId, {
			method: 'DELETE',
			headers: {'Content-Type': 'application/json'},
		});
		await response.text()
		if (response.ok) {
			window.location.href = '/monitored';
		} else {
			const error = await response.text();
			console.error(error);
		}
	});
}

async function saveMonitored(monitoredId) {
	fetchJson('/api/monitored/' + (monitoredId ?? ''), {
		method: 'POST',
		body: form2Json('monitored')
	}).then(async function (response) {
		window.location.href = '/monitored/' + response.id;
	}).catch(function (/** @type {JsonFetchError} */ error) {
		popupQueueText({text: error.message, danger: true});
	});
}

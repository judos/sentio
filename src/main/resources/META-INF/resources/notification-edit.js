
function setupBot() {
	var token = document.getElementById('token').value;

	fetch('/api/notification/', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
	 	body: JSON.stringify({ token: token })
	}).then(async function (response) {
		const text = await response.text();
		popupQueueText('Status: ' + response.status + '<br>Response: ' + text);
	}).catch(function (error) {
		popupQueueText('Network error');
		console.error('Network error:', error);
	});
}

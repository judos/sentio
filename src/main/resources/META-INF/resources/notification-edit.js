
function test() {
	fetch('/api/notification/', {
		method: 'POST'
	}).then(async function (response) {
		const text = await response.text();
		popupQueueText('Status: ' + response.status + '<br>Response: ' + text);
	}).catch(function (error) {
		popupQueueText('Network error');
		console.error('Network error:', error);
	});
}

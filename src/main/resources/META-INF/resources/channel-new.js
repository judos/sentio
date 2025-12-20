
function setupBot() {
	const token = document.getElementById('token').value;
	const button = document.getElementById('setup-button');
	setButtonLoading(button, true);

	fetchJson('/api/channel/', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
	 	body: JSON.stringify({ token: token })
	}).then(async function (response) {
		setButtonLoading(button, false);
		localStorage.setItem('popup', JSON.stringify({text: 'Setup successful'}));
		window.location.href = '/channel';
	}).catch(function (/** @type {JsonFetchError} */ error) {
		setButtonLoading(button, false);
		popupQueueText({text: error.message, danger: true});
	});
}

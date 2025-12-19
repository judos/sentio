
function setupBot() {
	const token = document.getElementById('token').value;
	const button = document.getElementById('setup-button');
	setButtonLoading(button, true);

	fetch('/api/notification/', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
	 	body: JSON.stringify({ token: token })
	}).then(async function (response) {
		setButtonLoading(button, false);
		const text = JSON.parse(await response.text());
		if (response.ok) {
			localStorage.setItem('popup', JSON.stringify({text: 'Setup successful'}));
			window.location.href = '/notification';
		} else {
			popupQueueText({text: text.message ?? 'Setup failed', danger: true});
			console.warn(text);
		}
	}).catch(function (error) {
		setButtonLoading(button, false);
		popupQueueText({text: 'Network error', danger: true});
		console.error('Network error:', error);
	});
}

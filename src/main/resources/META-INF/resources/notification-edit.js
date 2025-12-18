
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
			popupQueueText('Setup successful');
		} else {
			popupQueueText(text.error ?? 'Setup failed');
			console.warn(text);
		}
	}).catch(function (error) {
		setButtonLoading(button, false);
		popupQueueText('Network error');
		console.error('Network error:', error);
	});
}

function setupBotTest() {
	const button = document.getElementById('setup-button2');
	setButtonLoading(button, true);
	fetch('/api/notification/test', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
	 	body: JSON.stringify({ token: token })
	}).then(async function (response) {
		setButtonLoading(button, false);
		const text = await response.text();
		console.info(text);
		popupQueueText('Status: ' + response.status);
	}).catch(function (error) {
		setButtonLoading(button, false);
		popupQueueText('Network error');
		console.error('Network error:', error);
	});
}

function setupBot1() {
	console.info('click');
	const button = document.getElementById('setup-button1');
	setButtonLoading(button, true);
	setTimeout(function () {
		setButtonLoading(button, false);
	}, 2500)
}

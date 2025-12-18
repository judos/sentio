
function setupBot() {
	var token = document.getElementById('token').value;
	var button = document.getElementById('setup-button');
	var button1 = document.getElementById('setup-button1');
	var button2 = document.getElementById('setup-button2');
	var loading = document.getElementById('setup-loading');
	button.classList.add('loading');
	changeBgAlpha(button, 0.6);
	button1.classList.add('loading');
	changeBgAlpha(button1, 0.6);
	button2.classList.add('loading');
	changeBgAlpha(button2, 0.6);

	setTimeout(function() {
		// button.classList.remove('loading');
	}, 10000);

	// fetch('/api/notification/', {
	// 	method: 'POST',
	// 	headers: { 'Content-Type': 'application/json' },
	//  	body: JSON.stringify({ token: token })
	// }).then(async function (response) {
	// 	const text = await response.text();
	// 	popupQueueText('Status: ' + response.status + '<br>Response: ' + text);
	// }).catch(function (error) {
	// 	popupQueueText('Network error');
	// 	console.error('Network error:', error);
	// });
}

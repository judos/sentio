function deleteChannel(id, name) {
	popupQueueDelete('Are you sure you want to delete the channel "' + name + '"?', function () {
		fetchJson('/api/channel/' + id, {method: 'DELETE'}
		).then(async function (response) {
			localStorage.setItem('popup', JSON.stringify({text: 'Website deleted'}));
			window.location.href = '/';
		}).catch(function (/** @type {JsonFetchError} */ error) {
			popupQueueText({text: error.message, danger: true});
		});
	})
}

function sendTestMessage(id) {
	fetchJson('/api/channel/' + id + '/test', {method: 'POST'}
	).then(async function (response) {
		popupQueueText({text: 'Test message sent'});
	}).catch(function (/** @type {JsonFetchError} */ error) {
		popupQueueText({text: error.message, danger: true});
	});
}

function deleteWebsite(id, name) {
	popupQueueDelete('Are you sure you want to delete the website "' + name + '"?', function () {
		fetch('/api/websites/' + id, {
			method: 'DELETE'
		}).then(async function (response) {
			if (response.ok) {
				localStorage.setItem('popup', JSON.stringify({text:'Website deleted'}));
				window.location.href = '/';
			} else {
				const text = await response.text();
				popupQueueText({text: 'Error deleting website: ' + (text || response.status), danger: true});
				console.error('Delete error:', response.status, text);
			}
		}).catch(function (error) {
			popupQueueText({text: 'Network error while deleting website.', danger: true});
			console.error('Network error:', error);
		});
	})
}

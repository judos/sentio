function deleteWebsite(id, name) {
	popupQueueDelete('Are you sure you want to delete the website "' + name + '"?', function () {
		fetch('/api/websites/' + id, {
			method: 'DELETE'
		}).then(async function (response) {
			if (response.ok) {
				localStorage.setItem('popup', 'Website deleted');
				window.location.href = '/';
			} else {
				const text = await response.text();
				popupQueueText('Error deleting website: ' + (text || response.status));
				console.error('Delete error:', response.status, text);
			}
		}).catch(function (error) {
			popupQueueText('Network error while deleting website.');
			console.error('Network error:', error);
		});
	})
}

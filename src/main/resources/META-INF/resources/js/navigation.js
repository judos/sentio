// TODO: move this into user settings
function handleDateRangeChange() {
	const select = document.getElementById('dateRangeSelect');
	setConfig('dateRange', select.value);
	location.reload();
}

// Beim Laden die Auswahl aus localStorage setzen
window.addEventListener('DOMContentLoaded', function () {
	const select = document.getElementById('dateRangeSelect');
	const stored = getConfig('dateRange');
	if (select && stored) {
		select.value = stored;
	}
});

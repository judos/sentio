function setButtonLoading(element, isLoading) {
	if (isLoading) {
		element.disabled = true;
		element.classList.add('loading');
		changeBgAlpha(element, 0.6);
	} else {
		element.disabled = false;
		element.classList.remove('loading');
		changeBgAlpha(element, 1);
	}
}

function setButtonLoading(element, isLoading) {
	if (isLoading) {
		element.classList.add('loading');
		changeBgAlpha(element, 0.6);
	} else {
		element.classList.remove('loading');
		changeBgAlpha(element, 1);
	}
}

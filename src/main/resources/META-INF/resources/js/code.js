function enableCodeCopy() {
  const codeBlocks = document.querySelectorAll('code');
  codeBlocks.forEach(code => {
		code.title ="Click to copy";
    code.addEventListener('click', () => {
      navigator.clipboard.writeText(code.innerHTML)
        .then(() => {
          popupQueueText({text: 'copied to clipboard', showMs: 1000});
          code.style.backgroundColor = '#e0ffe0';
          setTimeout(() => code.style.backgroundColor = '', 500);
        })
        .catch(err => {
          console.error('Failed to copy:', err);
        });
    });
  });
}

// Call the function once the DOM is ready
document.addEventListener('DOMContentLoaded', enableCodeCopy);

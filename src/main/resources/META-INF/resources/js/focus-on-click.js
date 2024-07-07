document.addEventListener('DOMContentLoaded', function() {
    // Function to add focus and blur event listeners to elements with a specific class
    function makeFocusableOnClick(className) {
        const elements = document.querySelectorAll(`.${className}`);
        elements.forEach(element => {
            element.setAttribute('tabindex', '0'); // Ensure the element is focusable
            element.addEventListener('click', function() {
                this.focus();
                this.classList.add('focused'); // Add class for visual feedback
            });
            element.addEventListener('blur', function() {
                this.classList.remove('focused'); // Remove class when focus is lost
            });
        });
    }

    // Call the function with the desired class name
    makeFocusableOnClick('focusable');
});

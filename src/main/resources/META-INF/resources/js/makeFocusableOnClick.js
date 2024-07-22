


document.addEventListener('htmx:afterSettle', function(evn) {
        // console.log(evn.details.target) // TODO: IF list item, give it focus.

    // Function to add focus and blur event listeners to elements with a specific class
    function makeFocusableOnClick(focusClassName, ignoreClassName) {
        const elementsToFocus = document.querySelectorAll(`.${focusClassName}`);

        elementsToFocus.forEach(element => {
            element.setAttribute('tabindex', '0'); // Ensure the element is focusable
            element.addEventListener('click', function() {
                this.focus();
                this.classList.add('focused'); // Add class for visual feedback
            });

            element.addEventListener('blur', function() {
                setTimeout(() => { // needed to push the check of activeElement to end of event cycle, so it has been updated.
                    if (!document.activeElement.classList.contains(ignoreClassName)) { // Make exception for elements that should not remove focus.
                        this.classList.remove('focused'); // Remove class when focus is lost
                    }
                 }, 0)
            });
        });

    }

    // Call the function with the desired class name
    makeFocusableOnClick('focusable', 'preserveFocusedClass');
});

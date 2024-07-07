// Function to calculate and update the sum of integer values
const updateRatioSum = () => {
	const ratioElements = document.querySelectorAll('#meals-list .table-entry.ratio .value');
	const sumElement = document.querySelector('.table-entry.ratio-sum .value');

	let sum = 0;

	ratioElements.forEach(element => {
		const value = parseInt(element.value || element.textContent, 10);
		if (!isNaN(value) && Number.isInteger(value)) {
			sum += value;
		}
	});

	if (sumElement) {
		sumElement.textContent = sum.toString(); // Display sum as an integer
	}


	if (sum > 100) {
		sumElement.classList.add("overshoot");
	} else if (sum < 100) {
		sumElement.classList.add("undershoot");
	} else if (sum == 100) {
		sumElement.classList.add("whole");
	}
};

// Function to set up event listener on the meals-list container
const setupMealsListListener = () => {
	const mealsList = document.getElementById('meals-list');

	if (mealsList) {
		mealsList.addEventListener('input', (event) => {
			if (event.target.matches('.table-entry.ratio .value')) {
				updateRatioSum();
			}
		});
	} else {
		console.error('Element with id "meals-list" not found');
	}
};

// Initial calculation and set up listener when the DOM is fully loaded
document.addEventListener('DOMContentLoaded', () => {
	updateRatioSum();
	setupMealsListListener();
});

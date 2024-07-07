/**
 * @element prov-button
 * 
 * @description
 * A customizable button component for the Provisioner application.
 * This button supports different styles and can include an optional icon.
 * 
 * @attribute text string - The text to display on the button. Default is "Button".
 * @attribute type enum - The style variant of the button. Can be "default", "primary", or "warning". Default is "default".
 * @attribute icon string - Optional. SVG path data for an icon to display before the button text.
 * 
 * @firesEvent button-click - Dispatched when the button is clicked. Event detail includes the button type.
 * 
 * @example
 * <prov-button text="Click me!"></prov-button>
 * <prov-button text="Primary Action" type="primary"></prov-button>
 * <prov-button text="Warning" type="warning" icon="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15h2v-2h-2v2zm0-4h2V7h-2v6z"></prov-button>
 */
class ProvisionerButton extends HTMLElement {
	constructor() {
		super();
		this.attachShadow({ mode: 'open' });
	}

	connectedCallback() {
		this.render();
	}

	/**
	 * Renders the button based on the current attributes.
	 * @private
	 */
	render() {
		const text = this.getAttribute('text') || 'Button';
		const type = this.getAttribute('type') || 'default';
		const icon = this.getAttribute('icon');



		this.shadowRoot.innerHTML = `
            <style>
                @import 'prov-button.css';
            </style>
            <button class="custom-btn ${type}" part="button">
                ${icon ? `<svg class="icon-btn" viewBox="0 0 24 24" part="icon"><path d="${icon}"/></svg>` : ''}
                <span>${text}</span>
            </button>
        `;

		this.shadowRoot.querySelector('button').addEventListener('click', this.handleClick.bind(this));
	}

	/**
	 * Handles the click event on the button.
	 * @private
	 */
	handleClick() {
		this.dispatchEvent(new CustomEvent('button-click', {
			bubbles: true,
			composed: true,
			detail: { type: this.getAttribute('type') || 'default' }
		}));
	}
}

customElements.define('prov-button', ProvisionerButton);